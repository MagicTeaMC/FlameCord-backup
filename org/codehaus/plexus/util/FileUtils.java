package org.codehaus.plexus.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.codehaus.plexus.util.io.InputStreamFacade;
import org.codehaus.plexus.util.io.URLInputStreamFacade;

public class FileUtils {
  public static final int ONE_KB = 1024;
  
  public static final int ONE_MB = 1048576;
  
  public static final int ONE_GB = 1073741824;
  
  private static final long FILE_COPY_BUFFER_SIZE = 31457280L;
  
  public static String FS = System.getProperty("file.separator");
  
  private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new String[] { ":", "*", "?", "\"", "<", ">", "|" };
  
  public static String[] getDefaultExcludes() {
    return DirectoryScanner.DEFAULTEXCLUDES;
  }
  
  public static List<String> getDefaultExcludesAsList() {
    return Arrays.asList(getDefaultExcludes());
  }
  
  public static String getDefaultExcludesAsString() {
    return StringUtils.join((Object[])DirectoryScanner.DEFAULTEXCLUDES, ",");
  }
  
  public static String byteCountToDisplaySize(int size) {
    String displaySize;
    if (size / 1073741824 > 0) {
      displaySize = String.valueOf(size / 1073741824) + " GB";
    } else if (size / 1048576 > 0) {
      displaySize = String.valueOf(size / 1048576) + " MB";
    } else if (size / 1024 > 0) {
      displaySize = String.valueOf(size / 1024) + " KB";
    } else {
      displaySize = String.valueOf(size) + " bytes";
    } 
    return displaySize;
  }
  
  public static String dirname(String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i >= 0) ? filename.substring(0, i) : "";
  }
  
  public static String filename(String filename) {
    int i = filename.lastIndexOf(File.separator);
    return (i >= 0) ? filename.substring(i + 1) : filename;
  }
  
  public static String basename(String filename) {
    return basename(filename, extension(filename));
  }
  
  public static String basename(String filename, String suffix) {
    int i = filename.lastIndexOf(File.separator) + 1;
    int lastDot = (suffix != null && suffix.length() > 0) ? filename.lastIndexOf(suffix) : -1;
    if (lastDot >= 0)
      return filename.substring(i, lastDot); 
    if (i > 0)
      return filename.substring(i); 
    return filename;
  }
  
  public static String extension(String filename) {
    int lastDot, lastSep = filename.lastIndexOf(File.separatorChar);
    if (lastSep < 0) {
      lastDot = filename.lastIndexOf('.');
    } else {
      lastDot = filename.substring(lastSep + 1).lastIndexOf('.');
      if (lastDot >= 0)
        lastDot += lastSep + 1; 
    } 
    if (lastDot >= 0 && lastDot > lastSep)
      return filename.substring(lastDot + 1); 
    return "";
  }
  
  public static boolean fileExists(String fileName) {
    File file = new File(fileName);
    return file.exists();
  }
  
  public static String fileRead(String file) throws IOException {
    return fileRead(file, (String)null);
  }
  
  public static String fileRead(String file, String encoding) throws IOException {
    return fileRead(new File(file), encoding);
  }
  
  public static String fileRead(File file) throws IOException {
    return fileRead(file, (String)null);
  }
  
  public static String fileRead(File file, String encoding) throws IOException {
    StringBuilder buf = new StringBuilder();
    Reader reader = null;
    try {
      if (encoding != null) {
        reader = new InputStreamReader(new FileInputStream(file), encoding);
      } else {
        reader = new InputStreamReader(new FileInputStream(file));
      } 
      char[] b = new char[512];
      int count;
      while ((count = reader.read(b)) >= 0)
        buf.append(b, 0, count); 
      reader.close();
      reader = null;
    } finally {
      IOUtil.close(reader);
    } 
    return buf.toString();
  }
  
  public static void fileAppend(String fileName, String data) throws IOException {
    fileAppend(fileName, null, data);
  }
  
  public static void fileAppend(String fileName, String encoding, String data) throws IOException {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(fileName, true);
      if (encoding != null) {
        out.write(data.getBytes(encoding));
      } else {
        out.write(data.getBytes());
      } 
      out.close();
      out = null;
    } finally {
      IOUtil.close(out);
    } 
  }
  
  public static void fileWrite(String fileName, String data) throws IOException {
    fileWrite(fileName, (String)null, data);
  }
  
  public static void fileWrite(String fileName, String encoding, String data) throws IOException {
    File file = (fileName == null) ? null : new File(fileName);
    fileWrite(file, encoding, data);
  }
  
  public static void fileWrite(File file, String data) throws IOException {
    fileWrite(file, (String)null, data);
  }
  
  public static void fileWrite(File file, String encoding, String data) throws IOException {
    Writer writer = null;
    try {
      OutputStream out = new FileOutputStream(file);
      if (encoding != null) {
        writer = new OutputStreamWriter(out, encoding);
      } else {
        writer = new OutputStreamWriter(out);
      } 
      writer.write(data);
      writer.close();
      writer = null;
    } finally {
      IOUtil.close(writer);
    } 
  }
  
  public static void fileDelete(String fileName) {
    File file = new File(fileName);
    if (Java7Detector.isJava7()) {
      try {
        NioFiles.deleteIfExists(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } 
    } else {
      file.delete();
    } 
  }
  
  public static boolean waitFor(String fileName, int seconds) {
    return waitFor(new File(fileName), seconds);
  }
  
  public static boolean waitFor(File file, int seconds) {
    int timeout = 0;
    int tick = 0;
    while (!file.exists()) {
      if (tick++ >= 10) {
        tick = 0;
        if (timeout++ > seconds)
          return false; 
      } 
      try {
        Thread.sleep(100L);
      } catch (InterruptedException interruptedException) {}
    } 
    return true;
  }
  
  public static File getFile(String fileName) {
    return new File(fileName);
  }
  
  public static String[] getFilesFromExtension(String directory, String[] extensions) {
    List<String> files = new ArrayList<String>();
    File currentDir = new File(directory);
    String[] unknownFiles = currentDir.list();
    if (unknownFiles == null)
      return new String[0]; 
    for (String unknownFile : unknownFiles) {
      String currentFileName = directory + System.getProperty("file.separator") + unknownFile;
      File currentFile = new File(currentFileName);
      if (currentFile.isDirectory()) {
        if (!currentFile.getName().equals("CVS")) {
          String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
          files = blendFilesToVector(files, fetchFiles);
        } 
      } else {
        String add = currentFile.getAbsolutePath();
        if (isValidFile(add, extensions))
          files.add(add); 
      } 
    } 
    String[] foundFiles = new String[files.size()];
    files.toArray(foundFiles);
    return foundFiles;
  }
  
  private static List<String> blendFilesToVector(List<String> v, String[] files) {
    for (String file : files)
      v.add(file); 
    return v;
  }
  
  private static boolean isValidFile(String file, String[] extensions) {
    String extension = extension(file);
    if (extension == null)
      extension = ""; 
    for (String extension1 : extensions) {
      if (extension1.equals(extension))
        return true; 
    } 
    return false;
  }
  
  public static void mkdir(String dir) {
    File file = new File(dir);
    if (Os.isFamily("windows") && !isValidWindowsFileName(file))
      throw new IllegalArgumentException("The file (" + dir + ") cannot contain any of the following characters: \n" + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " ")); 
    if (!file.exists())
      file.mkdirs(); 
  }
  
  public static boolean contentEquals(File file1, File file2) throws IOException {
    boolean file1Exists = file1.exists();
    if (file1Exists != file2.exists())
      return false; 
    if (!file1Exists)
      return true; 
    if (file1.isDirectory() || file2.isDirectory())
      return false; 
    InputStream input1 = null;
    InputStream input2 = null;
    boolean equals = false;
    try {
      input1 = new FileInputStream(file1);
      input2 = new FileInputStream(file2);
      equals = IOUtil.contentEquals(input1, input2);
      input1.close();
      input1 = null;
      input2.close();
      input2 = null;
    } finally {
      IOUtil.close(input1);
      IOUtil.close(input2);
    } 
    return equals;
  }
  
  public static File toFile(URL url) {
    if (url == null || !url.getProtocol().equalsIgnoreCase("file"))
      return null; 
    String filename = url.getFile().replace('/', File.separatorChar);
    int pos = -1;
    while ((pos = filename.indexOf('%', pos + 1)) >= 0) {
      if (pos + 2 < filename.length()) {
        String hexStr = filename.substring(pos + 1, pos + 3);
        char ch = (char)Integer.parseInt(hexStr, 16);
        filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
      } 
    } 
    return new File(filename);
  }
  
  public static URL[] toURLs(File[] files) throws IOException {
    URL[] urls = new URL[files.length];
    for (int i = 0; i < urls.length; i++)
      urls[i] = files[i].toURI().toURL(); 
    return urls;
  }
  
  public static String removeExtension(String filename) {
    String ext = extension(filename);
    if ("".equals(ext))
      return filename; 
    int index = filename.lastIndexOf(ext) - 1;
    return filename.substring(0, index);
  }
  
  public static String getExtension(String filename) {
    return extension(filename);
  }
  
  public static String removePath(String filepath) {
    return removePath(filepath, File.separatorChar);
  }
  
  public static String removePath(String filepath, char fileSeparatorChar) {
    int index = filepath.lastIndexOf(fileSeparatorChar);
    if (-1 == index)
      return filepath; 
    return filepath.substring(index + 1);
  }
  
  public static String getPath(String filepath) {
    return getPath(filepath, File.separatorChar);
  }
  
  public static String getPath(String filepath, char fileSeparatorChar) {
    int index = filepath.lastIndexOf(fileSeparatorChar);
    if (-1 == index)
      return ""; 
    return filepath.substring(0, index);
  }
  
  public static void copyFileToDirectory(String source, String destinationDirectory) throws IOException {
    copyFileToDirectory(new File(source), new File(destinationDirectory));
  }
  
  public static void copyFileToDirectoryIfModified(String source, String destinationDirectory) throws IOException {
    copyFileToDirectoryIfModified(new File(source), new File(destinationDirectory));
  }
  
  public static void copyFileToDirectory(File source, File destinationDirectory) throws IOException {
    if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
      throw new IllegalArgumentException("Destination is not a directory"); 
    copyFile(source, new File(destinationDirectory, source.getName()));
  }
  
  public static void copyFileToDirectoryIfModified(File source, File destinationDirectory) throws IOException {
    if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
      throw new IllegalArgumentException("Destination is not a directory"); 
    copyFileIfModified(source, new File(destinationDirectory, source.getName()));
  }
  
  public static void mkDirs(File sourceBase, String[] dirs, File destination) throws IOException {
    for (String dir : dirs) {
      File src = new File(sourceBase, dir);
      File dst = new File(destination, dir);
      if (Java7Detector.isJava7() && NioFiles.isSymbolicLink(src)) {
        File target = NioFiles.readSymbolicLink(src);
        NioFiles.createSymbolicLink(dst, target);
      } else {
        dst.mkdirs();
      } 
    } 
  }
  
  public static void copyFile(File source, File destination) throws IOException {
    if (!source.exists()) {
      String message = "File " + source + " does not exist";
      throw new IOException(message);
    } 
    if (source.getCanonicalPath().equals(destination.getCanonicalPath()))
      return; 
    mkdirsFor(destination);
    doCopyFile(source, destination);
    if (source.length() != destination.length()) {
      String message = "Failed to copy full contents from " + source + " to " + destination;
      throw new IOException(message);
    } 
  }
  
  private static void doCopyFile(File source, File destination) throws IOException {
    if (Java7Detector.isJava7()) {
      doCopyFileUsingNewIO(source, destination);
    } else {
      doCopyFileUsingLegacyIO(source, destination);
    } 
  }
  
  private static void doCopyFileUsingLegacyIO(File source, File destination) throws IOException {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    FileChannel input = null;
    FileChannel output = null;
    try {
      fis = new FileInputStream(source);
      fos = new FileOutputStream(destination);
      input = fis.getChannel();
      output = fos.getChannel();
      long size = input.size();
      long pos = 0L;
      long count = 0L;
      while (pos < size) {
        count = (size - pos > 31457280L) ? 31457280L : (size - pos);
        pos += output.transferFrom(input, pos, count);
      } 
      output.close();
      output = null;
      fos.close();
      fos = null;
      input.close();
      input = null;
      fis.close();
      fis = null;
    } finally {
      IOUtil.close(output);
      IOUtil.close(fos);
      IOUtil.close(input);
      IOUtil.close(fis);
    } 
  }
  
  private static void doCopyFileUsingNewIO(File source, File destination) throws IOException {
    NioFiles.copy(source, destination);
  }
  
  public static boolean copyFileIfModified(File source, File destination) throws IOException {
    if (isSourceNewerThanDestination(source, destination)) {
      copyFile(source, destination);
      return true;
    } 
    return false;
  }
  
  public static void copyURLToFile(URL source, File destination) throws IOException {
    copyStreamToFile((InputStreamFacade)new URLInputStreamFacade(source), destination);
  }
  
  public static void copyStreamToFile(InputStreamFacade source, File destination) throws IOException {
    mkdirsFor(destination);
    checkCanWrite(destination);
    InputStream input = null;
    FileOutputStream output = null;
    try {
      input = source.getInputStream();
      output = new FileOutputStream(destination);
      IOUtil.copy(input, output);
      output.close();
      output = null;
      input.close();
      input = null;
    } finally {
      IOUtil.close(input);
      IOUtil.close(output);
    } 
  }
  
  private static void checkCanWrite(File destination) throws IOException {
    if (destination.exists() && !destination.canWrite()) {
      String message = "Unable to open file " + destination + " for writing.";
      throw new IOException(message);
    } 
  }
  
  private static void mkdirsFor(File destination) {
    File parentFile = destination.getParentFile();
    if (parentFile != null && !parentFile.exists())
      parentFile.mkdirs(); 
  }
  
  public static String normalize(String path) {
    String normalized = path;
    while (true) {
      int index = normalized.indexOf("//");
      if (index < 0)
        break; 
      normalized = normalized.substring(0, index) + normalized.substring(index + 1);
    } 
    while (true) {
      int index = normalized.indexOf("/./");
      if (index < 0)
        break; 
      normalized = normalized.substring(0, index) + normalized.substring(index + 2);
    } 
    while (true) {
      int index = normalized.indexOf("/../");
      if (index < 0)
        break; 
      if (index == 0)
        return null; 
      int index2 = normalized.lastIndexOf('/', index - 1);
      normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
    } 
    return normalized;
  }
  
  public static String catPath(String lookupPath, String path) {
    int index = lookupPath.lastIndexOf("/");
    String lookup = lookupPath.substring(0, index);
    String pth = path;
    while (pth.startsWith("../")) {
      if (lookup.length() > 0) {
        index = lookup.lastIndexOf("/");
        lookup = lookup.substring(0, index);
      } else {
        return null;
      } 
      index = pth.indexOf("../") + 3;
      pth = pth.substring(index);
    } 
    return lookup + "/" + pth;
  }
  
  public static File resolveFile(File baseFile, String filename) {
    String filenm = filename;
    if ('/' != File.separatorChar)
      filenm = filename.replace('/', File.separatorChar); 
    if ('\\' != File.separatorChar)
      filenm = filename.replace('\\', File.separatorChar); 
    if (filenm.startsWith(File.separator) || (Os.isFamily("windows") && filenm.indexOf(":") > 0)) {
      File file1 = new File(filenm);
      try {
        file1 = file1.getCanonicalFile();
      } catch (IOException iOException) {}
      return file1;
    } 
    char[] chars = filename.toCharArray();
    StringBuilder sb = new StringBuilder();
    int start = 0;
    if ('\\' == File.separatorChar) {
      sb.append(filenm.charAt(0));
      start++;
    } 
    for (int i = start; i < chars.length; i++) {
      boolean doubleSeparator = (File.separatorChar == chars[i] && File.separatorChar == chars[i - 1]);
      if (!doubleSeparator)
        sb.append(chars[i]); 
    } 
    filenm = sb.toString();
    File file = (new File(baseFile, filenm)).getAbsoluteFile();
    try {
      file = file.getCanonicalFile();
    } catch (IOException iOException) {}
    return file;
  }
  
  public static void forceDelete(String file) throws IOException {
    forceDelete(new File(file));
  }
  
  public static void forceDelete(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectory(file);
    } else {
      boolean filePresent = file.getCanonicalFile().exists();
      if (!deleteFile(file) && filePresent) {
        String message = "File " + file + " unable to be deleted.";
        throw new IOException(message);
      } 
    } 
  }
  
  private static boolean deleteFile(File file) throws IOException {
    if (file.isDirectory())
      throw new IOException("File " + file + " isn't a file."); 
    if (!file.delete()) {
      if (Os.isFamily("windows")) {
        file = file.getCanonicalFile();
        System.gc();
      } 
      try {
        Thread.sleep(10L);
        return file.delete();
      } catch (InterruptedException ignore) {
        return file.delete();
      } 
    } 
    return true;
  }
  
  public static void forceDeleteOnExit(File file) throws IOException {
    if (!file.exists())
      return; 
    if (file.isDirectory()) {
      deleteDirectoryOnExit(file);
    } else {
      file.deleteOnExit();
    } 
  }
  
  private static void deleteDirectoryOnExit(File directory) throws IOException {
    if (!directory.exists())
      return; 
    directory.deleteOnExit();
    cleanDirectoryOnExit(directory);
  }
  
  private static void cleanDirectoryOnExit(File directory) throws IOException {
    if (!directory.exists()) {
      String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    IOException exception = null;
    File[] files = directory.listFiles();
    for (File file : files) {
      try {
        forceDeleteOnExit(file);
      } catch (IOException ioe) {
        exception = ioe;
      } 
    } 
    if (null != exception)
      throw exception; 
  }
  
  public static void forceMkdir(File file) throws IOException {
    if (Os.isFamily("windows"))
      if (!isValidWindowsFileName(file))
        throw new IllegalArgumentException("The file (" + file.getAbsolutePath() + ") cannot contain any of the following characters: \n" + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));  
    if (file.exists()) {
      if (file.isFile()) {
        String message = "File " + file + " exists and is " + "not a directory. Unable to create directory.";
        throw new IOException(message);
      } 
    } else if (false == file.mkdirs()) {
      String message = "Unable to create directory " + file;
      throw new IOException(message);
    } 
  }
  
  public static void deleteDirectory(String directory) throws IOException {
    deleteDirectory(new File(directory));
  }
  
  public static void deleteDirectory(File directory) throws IOException {
    if (!directory.exists())
      return; 
    if (directory.delete())
      return; 
    cleanDirectory(directory);
    if (!directory.delete()) {
      String message = "Directory " + directory + " unable to be deleted.";
      throw new IOException(message);
    } 
  }
  
  public static void cleanDirectory(String directory) throws IOException {
    cleanDirectory(new File(directory));
  }
  
  public static void cleanDirectory(File directory) throws IOException {
    if (!directory.exists()) {
      String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    IOException exception = null;
    File[] files = directory.listFiles();
    if (files == null)
      return; 
    for (File file : files) {
      try {
        forceDelete(file);
      } catch (IOException ioe) {
        exception = ioe;
      } 
    } 
    if (null != exception)
      throw exception; 
  }
  
  public static long sizeOfDirectory(String directory) {
    return sizeOfDirectory(new File(directory));
  }
  
  public static long sizeOfDirectory(File directory) {
    if (!directory.exists()) {
      String message = directory + " does not exist";
      throw new IllegalArgumentException(message);
    } 
    if (!directory.isDirectory()) {
      String message = directory + " is not a directory";
      throw new IllegalArgumentException(message);
    } 
    long size = 0L;
    File[] files = directory.listFiles();
    for (File file : files) {
      if (file.isDirectory()) {
        size += sizeOfDirectory(file);
      } else {
        size += file.length();
      } 
    } 
    return size;
  }
  
  public static List<File> getFiles(File directory, String includes, String excludes) throws IOException {
    return getFiles(directory, includes, excludes, true);
  }
  
  public static List<File> getFiles(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
    List<String> fileNames = getFileNames(directory, includes, excludes, includeBasedir);
    List<File> files = new ArrayList<File>();
    for (String filename : fileNames)
      files.add(new File(filename)); 
    return files;
  }
  
  public static List<String> getFileNames(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
    return getFileNames(directory, includes, excludes, includeBasedir, true);
  }
  
  public static List<String> getFileNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
    return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, true, false);
  }
  
  public static List<String> getDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
    return getDirectoryNames(directory, includes, excludes, includeBasedir, true);
  }
  
  public static List<String> getDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
    return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, false, true);
  }
  
  public static List<String> getFileAndDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive, boolean getFiles, boolean getDirectories) throws IOException {
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(directory);
    if (includes != null)
      scanner.setIncludes(StringUtils.split(includes, ",")); 
    if (excludes != null)
      scanner.setExcludes(StringUtils.split(excludes, ",")); 
    scanner.setCaseSensitive(isCaseSensitive);
    scanner.scan();
    List<String> list = new ArrayList<String>();
    if (getFiles) {
      String[] files = scanner.getIncludedFiles();
      for (String file : files) {
        if (includeBasedir) {
          list.add(directory + FS + file);
        } else {
          list.add(file);
        } 
      } 
    } 
    if (getDirectories) {
      String[] directories = scanner.getIncludedDirectories();
      for (String directory1 : directories) {
        if (includeBasedir) {
          list.add(directory + FS + directory1);
        } else {
          list.add(directory1);
        } 
      } 
    } 
    return list;
  }
  
  public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectory(sourceDirectory, destinationDirectory, "**", null);
  }
  
  public static void copyDirectory(File sourceDirectory, File destinationDirectory, String includes, String excludes) throws IOException {
    if (!sourceDirectory.exists())
      return; 
    List<File> files = getFiles(sourceDirectory, includes, excludes);
    for (File file : files)
      copyFileToDirectory(file, destinationDirectory); 
  }
  
  public static void copyDirectoryLayout(File sourceDirectory, File destinationDirectory, String[] includes, String[] excludes) throws IOException {
    if (sourceDirectory == null)
      throw new IOException("source directory can't be null."); 
    if (destinationDirectory == null)
      throw new IOException("destination directory can't be null."); 
    if (sourceDirectory.equals(destinationDirectory))
      throw new IOException("source and destination are the same directory."); 
    if (!sourceDirectory.exists())
      throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")."); 
    DirectoryScanner scanner = new DirectoryScanner();
    scanner.setBasedir(sourceDirectory);
    if (includes != null && includes.length >= 1) {
      scanner.setIncludes(includes);
    } else {
      scanner.setIncludes(new String[] { "**" });
    } 
    if (excludes != null && excludes.length >= 1)
      scanner.setExcludes(excludes); 
    scanner.addDefaultExcludes();
    scanner.scan();
    List<String> includedDirectories = Arrays.asList(scanner.getIncludedDirectories());
    for (String name : includedDirectories) {
      File source = new File(sourceDirectory, name);
      if (source.equals(sourceDirectory))
        continue; 
      File destination = new File(destinationDirectory, name);
      destination.mkdirs();
    } 
  }
  
  public static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, false);
  }
  
  public static void copyDirectoryStructureIfModified(File sourceDirectory, File destinationDirectory) throws IOException {
    copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, true);
  }
  
  private static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory, File rootDestinationDirectory, boolean onlyModifiedFiles) throws IOException {
    if (sourceDirectory == null)
      throw new IOException("source directory can't be null."); 
    if (destinationDirectory == null)
      throw new IOException("destination directory can't be null."); 
    if (sourceDirectory.equals(destinationDirectory))
      throw new IOException("source and destination are the same directory."); 
    if (!sourceDirectory.exists())
      throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ")."); 
    File[] files = sourceDirectory.listFiles();
    String sourcePath = sourceDirectory.getAbsolutePath();
    for (File file : files) {
      if (!file.equals(rootDestinationDirectory)) {
        String dest = file.getAbsolutePath();
        dest = dest.substring(sourcePath.length() + 1);
        File destination = new File(destinationDirectory, dest);
        if (file.isFile()) {
          destination = destination.getParentFile();
          if (onlyModifiedFiles) {
            copyFileToDirectoryIfModified(file, destination);
          } else {
            copyFileToDirectory(file, destination);
          } 
        } else if (file.isDirectory()) {
          if (!destination.exists() && !destination.mkdirs())
            throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'."); 
          copyDirectoryStructure(file, destination, rootDestinationDirectory, onlyModifiedFiles);
        } else {
          throw new IOException("Unknown file type: " + file.getAbsolutePath());
        } 
      } 
    } 
  }
  
  public static void rename(File from, File to) throws IOException {
    if (to.exists() && !to.delete())
      throw new IOException("Failed to delete " + to + " while trying to rename " + from); 
    File parent = to.getParentFile();
    if (parent != null && !parent.exists() && !parent.mkdirs())
      throw new IOException("Failed to create directory " + parent + " while trying to rename " + from); 
    if (!from.renameTo(to)) {
      copyFile(from, to);
      if (!from.delete())
        throw new IOException("Failed to delete " + from + " while trying to rename it."); 
    } 
  }
  
  public static File createTempFile(String prefix, String suffix, File parentDir) {
    File result = null;
    String parent = System.getProperty("java.io.tmpdir");
    if (parentDir != null)
      parent = parentDir.getPath(); 
    DecimalFormat fmt = new DecimalFormat("#####");
    SecureRandom secureRandom = new SecureRandom();
    long secureInitializer = secureRandom.nextLong();
    Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
    synchronized (rand) {
      while (true) {
        result = new File(parent, prefix + fmt.format(Math.abs(rand.nextInt())) + suffix);
        if (!result.exists())
          return result; 
      } 
    } 
  }
  
  public static void copyFile(File from, File to, String encoding, FilterWrapper[] wrappers) throws IOException {
    copyFile(from, to, encoding, wrappers, false);
  }
  
  public static abstract class FilterWrapper {
    public abstract Reader getReader(Reader param1Reader);
  }
  
  public static void copyFile(File from, File to, String encoding, FilterWrapper[] wrappers, boolean overwrite) throws IOException {
    if (wrappers != null && wrappers.length > 0) {
      Reader fileReader = null;
      Writer fileWriter = null;
      try {
        if (encoding == null || encoding.length() < 1) {
          fileReader = new BufferedReader(new FileReader(from));
          fileWriter = new FileWriter(to);
        } else {
          FileInputStream instream = new FileInputStream(from);
          FileOutputStream outstream = new FileOutputStream(to);
          fileReader = new BufferedReader(new InputStreamReader(instream, encoding));
          fileWriter = new OutputStreamWriter(outstream, encoding);
        } 
        Reader reader = fileReader;
        for (FilterWrapper wrapper : wrappers)
          reader = wrapper.getReader(reader); 
        IOUtil.copy(reader, fileWriter);
        fileWriter.close();
        fileWriter = null;
        fileReader.close();
        fileReader = null;
      } finally {
        IOUtil.close(fileReader);
        IOUtil.close(fileWriter);
      } 
    } else if (isSourceNewerThanDestination(from, to) || overwrite) {
      copyFile(from, to);
    } 
  }
  
  private static boolean isSourceNewerThanDestination(File source, File destination) {
    return ((destination.lastModified() == 0L && source.lastModified() == 0L) || destination.lastModified() < source.lastModified());
  }
  
  public static List<String> loadFile(File file) throws IOException {
    List<String> lines = new ArrayList<String>();
    BufferedReader reader = null;
    try {
      if (file.exists()) {
        reader = new BufferedReader(new FileReader(file));
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
          line = line.trim();
          if (!line.startsWith("#") && line.length() != 0)
            lines.add(line); 
        } 
        reader.close();
        reader = null;
      } 
    } finally {
      IOUtil.close(reader);
    } 
    return lines;
  }
  
  public static boolean isValidWindowsFileName(File f) {
    if (Os.isFamily("windows")) {
      if (StringUtils.indexOfAny(f.getName(), INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME) != -1)
        return false; 
      File parentFile = f.getParentFile();
      if (parentFile != null)
        return isValidWindowsFileName(parentFile); 
    } 
    return true;
  }
}
