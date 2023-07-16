package org.eclipse.aether.connector.basic;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.util.ChecksumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ChecksumValidator {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChecksumValidator.class);
  
  private final File dataFile;
  
  private final Collection<File> tempFiles;
  
  private final FileProcessor fileProcessor;
  
  private final ChecksumFetcher checksumFetcher;
  
  private final ChecksumPolicy checksumPolicy;
  
  private final Collection<RepositoryLayout.Checksum> checksums;
  
  private final Map<File, Object> checksumFiles;
  
  ChecksumValidator(File dataFile, FileProcessor fileProcessor, ChecksumFetcher checksumFetcher, ChecksumPolicy checksumPolicy, Collection<RepositoryLayout.Checksum> checksums) {
    this.dataFile = dataFile;
    this.tempFiles = new HashSet<>();
    this.fileProcessor = fileProcessor;
    this.checksumFetcher = checksumFetcher;
    this.checksumPolicy = checksumPolicy;
    this.checksums = checksums;
    this.checksumFiles = new HashMap<>();
  }
  
  public ChecksumCalculator newChecksumCalculator(File targetFile) {
    if (this.checksumPolicy != null)
      return ChecksumCalculator.newInstance(targetFile, this.checksums); 
    return null;
  }
  
  public void validate(Map<String, ?> actualChecksums, Map<String, ?> inlinedChecksums) throws ChecksumFailureException {
    if (this.checksumPolicy == null)
      return; 
    if (inlinedChecksums != null && validateInlinedChecksums(actualChecksums, inlinedChecksums))
      return; 
    if (validateExternalChecksums(actualChecksums))
      return; 
    this.checksumPolicy.onNoMoreChecksums();
  }
  
  private boolean validateInlinedChecksums(Map<String, ?> actualChecksums, Map<String, ?> inlinedChecksums) throws ChecksumFailureException {
    for (Map.Entry<String, ?> entry : inlinedChecksums.entrySet()) {
      String algo = entry.getKey();
      Object calculated = actualChecksums.get(algo);
      if (!(calculated instanceof String))
        continue; 
      String actual = String.valueOf(calculated);
      String expected = entry.getValue().toString();
      this.checksumFiles.put(getChecksumFile(algo), expected);
      if (!isEqualChecksum(expected, actual)) {
        this.checksumPolicy.onChecksumMismatch(algo, 1, new ChecksumFailureException(expected, actual));
        continue;
      } 
      if (this.checksumPolicy.onChecksumMatch(algo, 1))
        return true; 
    } 
    return false;
  }
  
  private boolean validateExternalChecksums(Map<String, ?> actualChecksums) throws ChecksumFailureException {
    for (RepositoryLayout.Checksum checksum : this.checksums) {
      String algo = checksum.getAlgorithm();
      Object calculated = actualChecksums.get(algo);
      if (calculated instanceof Exception) {
        this.checksumPolicy.onChecksumError(algo, 0, new ChecksumFailureException((Exception)calculated));
        continue;
      } 
      try {
        File checksumFile = getChecksumFile(checksum.getAlgorithm());
        File tmp = createTempFile(checksumFile);
        try {
          if (!this.checksumFetcher.fetchChecksum(checksum.getLocation(), tmp))
            continue; 
        } catch (Exception e) {
          this.checksumPolicy.onChecksumError(algo, 0, new ChecksumFailureException(e));
          continue;
        } 
        String actual = String.valueOf(calculated);
        String expected = ChecksumUtils.read(tmp);
        this.checksumFiles.put(checksumFile, tmp);
        if (!isEqualChecksum(expected, actual)) {
          this.checksumPolicy.onChecksumMismatch(algo, 0, new ChecksumFailureException(expected, actual));
          continue;
        } 
        if (this.checksumPolicy.onChecksumMatch(algo, 0))
          return true; 
      } catch (IOException e) {
        this.checksumPolicy.onChecksumError(algo, 0, new ChecksumFailureException(e));
      } 
    } 
    return false;
  }
  
  private static boolean isEqualChecksum(String expected, String actual) {
    return expected.equalsIgnoreCase(actual);
  }
  
  private File getChecksumFile(String algorithm) {
    String ext = algorithm.replace("-", "").toLowerCase(Locale.ENGLISH);
    return new File(this.dataFile.getPath() + '.' + ext);
  }
  
  private File createTempFile(File path) throws IOException {
    File file = File.createTempFile(path.getName() + "-" + 
        UUID.randomUUID().toString().replace("-", "").substring(0, 8), ".tmp", path.getParentFile());
    this.tempFiles.add(file);
    return file;
  }
  
  private void clearTempFiles() {
    for (File file : this.tempFiles) {
      if (!file.delete() && file.exists())
        LOGGER.debug("Could not delete temporary file {}", file); 
    } 
    this.tempFiles.clear();
  }
  
  public void retry() {
    this.checksumPolicy.onTransferRetry();
    this.checksumFiles.clear();
    clearTempFiles();
  }
  
  public boolean handle(ChecksumFailureException exception) {
    return this.checksumPolicy.onTransferChecksumFailure(exception);
  }
  
  public void commit() {
    for (Map.Entry<File, Object> entry : this.checksumFiles.entrySet()) {
      File checksumFile = entry.getKey();
      Object tmp = entry.getValue();
      try {
        if (tmp instanceof File) {
          this.fileProcessor.move((File)tmp, checksumFile);
          this.tempFiles.remove(tmp);
          continue;
        } 
        this.fileProcessor.write(checksumFile, String.valueOf(tmp));
      } catch (IOException e) {
        LOGGER.debug("Failed to write checksum file {}", checksumFile, e);
      } 
    } 
    this.checksumFiles.clear();
  }
  
  public void close() {
    clearTempFiles();
  }
  
  static interface ChecksumFetcher {
    boolean fetchChecksum(URI param1URI, File param1File) throws Exception;
  }
}
