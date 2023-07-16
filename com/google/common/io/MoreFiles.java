package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.graph.SuccessorsFunction;
import com.google.common.graph.Traverser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class MoreFiles {
  public static ByteSource asByteSource(Path path, OpenOption... options) {
    return new PathByteSource(path, options);
  }
  
  private static final class PathByteSource extends ByteSource {
    private static final LinkOption[] FOLLOW_LINKS = new LinkOption[0];
    
    private final Path path;
    
    private final OpenOption[] options;
    
    private final boolean followLinks;
    
    private PathByteSource(Path path, OpenOption... options) {
      this.path = (Path)Preconditions.checkNotNull(path);
      this.options = (OpenOption[])options.clone();
      this.followLinks = followLinks(this.options);
    }
    
    private static boolean followLinks(OpenOption[] options) {
      for (OpenOption option : options) {
        if (option == LinkOption.NOFOLLOW_LINKS)
          return false; 
      } 
      return true;
    }
    
    public InputStream openStream() throws IOException {
      return Files.newInputStream(this.path, this.options);
    }
    
    private BasicFileAttributes readAttributes() throws IOException {
      (new LinkOption[1])[0] = LinkOption.NOFOLLOW_LINKS;
      return Files.readAttributes(this.path, BasicFileAttributes.class, this.followLinks ? FOLLOW_LINKS : new LinkOption[1]);
    }
    
    public Optional<Long> sizeIfKnown() {
      BasicFileAttributes attrs;
      try {
        attrs = readAttributes();
      } catch (IOException e) {
        return Optional.absent();
      } 
      if (attrs.isDirectory() || attrs.isSymbolicLink())
        return Optional.absent(); 
      return Optional.of(Long.valueOf(attrs.size()));
    }
    
    public long size() throws IOException {
      BasicFileAttributes attrs = readAttributes();
      if (attrs.isDirectory())
        throw new IOException("can't read: is a directory"); 
      if (attrs.isSymbolicLink())
        throw new IOException("can't read: is a symbolic link"); 
      return attrs.size();
    }
    
    public byte[] read() throws IOException {
      SeekableByteChannel channel = Files.newByteChannel(this.path, this.options);
      try {
        byte[] arrayOfByte = ByteStreams.toByteArray(Channels.newInputStream(channel), channel.size());
        if (channel != null)
          channel.close(); 
        return arrayOfByte;
      } catch (Throwable throwable) {
        if (channel != null)
          try {
            channel.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    }
    
    public CharSource asCharSource(Charset charset) {
      if (this.options.length == 0)
        return new ByteSource.AsCharSource(charset) {
            public Stream<String> lines() throws IOException {
              return Files.lines(MoreFiles.PathByteSource.this.path, this.charset);
            }
          }; 
      return super.asCharSource(charset);
    }
    
    public String toString() {
      String str1 = String.valueOf(this.path), str2 = Arrays.toString((Object[])this.options);
      return (new StringBuilder(26 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("MoreFiles.asByteSource(").append(str1).append(", ").append(str2).append(")").toString();
    }
  }
  
  public static ByteSink asByteSink(Path path, OpenOption... options) {
    return new PathByteSink(path, options);
  }
  
  private static final class PathByteSink extends ByteSink {
    private final Path path;
    
    private final OpenOption[] options;
    
    private PathByteSink(Path path, OpenOption... options) {
      this.path = (Path)Preconditions.checkNotNull(path);
      this.options = (OpenOption[])options.clone();
    }
    
    public OutputStream openStream() throws IOException {
      return Files.newOutputStream(this.path, this.options);
    }
    
    public String toString() {
      String str1 = String.valueOf(this.path), str2 = Arrays.toString((Object[])this.options);
      return (new StringBuilder(24 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("MoreFiles.asByteSink(").append(str1).append(", ").append(str2).append(")").toString();
    }
  }
  
  public static CharSource asCharSource(Path path, Charset charset, OpenOption... options) {
    return asByteSource(path, options).asCharSource(charset);
  }
  
  public static CharSink asCharSink(Path path, Charset charset, OpenOption... options) {
    return asByteSink(path, options).asCharSink(charset);
  }
  
  public static ImmutableList<Path> listFiles(Path dir) throws IOException {
    try {
      DirectoryStream<Path> stream = Files.newDirectoryStream(dir);
      try {
        ImmutableList<Path> immutableList = ImmutableList.copyOf(stream);
        if (stream != null)
          stream.close(); 
        return immutableList;
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (DirectoryIteratorException e) {
      throw e.getCause();
    } 
  }
  
  public static Traverser<Path> fileTraverser() {
    return Traverser.forTree(FILE_TREE);
  }
  
  private static final SuccessorsFunction<Path> FILE_TREE = new SuccessorsFunction<Path>() {
      public Iterable<Path> successors(Path path) {
        return MoreFiles.fileTreeChildren(path);
      }
    };
  
  private static Iterable<Path> fileTreeChildren(Path dir) {
    if (Files.isDirectory(dir, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      try {
        return (Iterable<Path>)listFiles(dir);
      } catch (IOException e) {
        throw new DirectoryIteratorException(e);
      }  
    return (Iterable<Path>)ImmutableList.of();
  }
  
  public static Predicate<Path> isDirectory(LinkOption... options) {
    final LinkOption[] optionsCopy = (LinkOption[])options.clone();
    return new Predicate<Path>() {
        public boolean apply(Path input) {
          return Files.isDirectory(input, optionsCopy);
        }
        
        public String toString() {
          String str = Arrays.toString((Object[])optionsCopy);
          return (new StringBuilder(23 + String.valueOf(str).length())).append("MoreFiles.isDirectory(").append(str).append(")").toString();
        }
      };
  }
  
  private static boolean isDirectory(SecureDirectoryStream<Path> dir, Path name, LinkOption... options) throws IOException {
    return ((BasicFileAttributeView)dir.<BasicFileAttributeView>getFileAttributeView(name, BasicFileAttributeView.class, options))
      .readAttributes()
      .isDirectory();
  }
  
  public static Predicate<Path> isRegularFile(LinkOption... options) {
    final LinkOption[] optionsCopy = (LinkOption[])options.clone();
    return new Predicate<Path>() {
        public boolean apply(Path input) {
          return Files.isRegularFile(input, optionsCopy);
        }
        
        public String toString() {
          String str = Arrays.toString((Object[])optionsCopy);
          return (new StringBuilder(25 + String.valueOf(str).length())).append("MoreFiles.isRegularFile(").append(str).append(")").toString();
        }
      };
  }
  
  public static boolean equal(Path path1, Path path2) throws IOException {
    Preconditions.checkNotNull(path1);
    Preconditions.checkNotNull(path2);
    if (Files.isSameFile(path1, path2))
      return true; 
    ByteSource source1 = asByteSource(path1, new OpenOption[0]);
    ByteSource source2 = asByteSource(path2, new OpenOption[0]);
    long len1 = ((Long)source1.sizeIfKnown().or(Long.valueOf(0L))).longValue();
    long len2 = ((Long)source2.sizeIfKnown().or(Long.valueOf(0L))).longValue();
    if (len1 != 0L && len2 != 0L && len1 != len2)
      return false; 
    return source1.contentEquals(source2);
  }
  
  public static void touch(Path path) throws IOException {
    Preconditions.checkNotNull(path);
    try {
      Files.setLastModifiedTime(path, FileTime.fromMillis(System.currentTimeMillis()));
    } catch (NoSuchFileException e) {
      try {
        Files.createFile(path, (FileAttribute<?>[])new FileAttribute[0]);
      } catch (FileAlreadyExistsException fileAlreadyExistsException) {}
    } 
  }
  
  public static void createParentDirectories(Path path, FileAttribute<?>... attrs) throws IOException {
    Path normalizedAbsolutePath = path.toAbsolutePath().normalize();
    Path parent = normalizedAbsolutePath.getParent();
    if (parent == null)
      return; 
    if (!Files.isDirectory(parent, new LinkOption[0])) {
      Files.createDirectories(parent, attrs);
      if (!Files.isDirectory(parent, new LinkOption[0])) {
        String str = String.valueOf(path);
        throw new IOException((new StringBuilder(39 + String.valueOf(str).length())).append("Unable to create parent directories of ").append(str).toString());
      } 
    } 
  }
  
  public static String getFileExtension(Path path) {
    Path name = path.getFileName();
    if (name == null)
      return ""; 
    String fileName = name.toString();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
  }
  
  public static String getNameWithoutExtension(Path path) {
    Path name = path.getFileName();
    if (name == null)
      return ""; 
    String fileName = name.toString();
    int dotIndex = fileName.lastIndexOf('.');
    return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
  }
  
  public static void deleteRecursively(Path path, RecursiveDeleteOption... options) throws IOException {
    Path parentPath = getParentPath(path);
    if (parentPath == null)
      throw new FileSystemException(path.toString(), null, "can't delete recursively"); 
    Collection<IOException> exceptions = null;
    try {
      boolean sdsSupported = false;
      DirectoryStream<Path> parent = Files.newDirectoryStream(parentPath);
      try {
        if (parent instanceof SecureDirectoryStream) {
          sdsSupported = true;
          exceptions = deleteRecursivelySecure((SecureDirectoryStream<Path>)parent, 
              
              Objects.<Path>requireNonNull(path.getFileName()));
        } 
        if (parent != null)
          parent.close(); 
      } catch (Throwable throwable) {
        if (parent != null)
          try {
            parent.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      if (!sdsSupported) {
        checkAllowsInsecure(path, options);
        exceptions = deleteRecursivelyInsecure(path);
      } 
    } catch (IOException e) {
      if (exceptions == null)
        throw e; 
      exceptions.add(e);
    } 
    if (exceptions != null)
      throwDeleteFailed(path, exceptions); 
  }
  
  public static void deleteDirectoryContents(Path path, RecursiveDeleteOption... options) throws IOException {
    Collection<IOException> exceptions = null;
    try {
      DirectoryStream<Path> stream = Files.newDirectoryStream(path);
      try {
        if (stream instanceof SecureDirectoryStream) {
          SecureDirectoryStream<Path> sds = (SecureDirectoryStream<Path>)stream;
          exceptions = deleteDirectoryContentsSecure(sds);
        } else {
          checkAllowsInsecure(path, options);
          exceptions = deleteDirectoryContentsInsecure(stream);
        } 
        if (stream != null)
          stream.close(); 
      } catch (Throwable throwable) {
        if (stream != null)
          try {
            stream.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException e) {
      if (exceptions == null)
        throw e; 
      exceptions.add(e);
    } 
    if (exceptions != null)
      throwDeleteFailed(path, exceptions); 
  }
  
  @CheckForNull
  private static Collection<IOException> deleteRecursivelySecure(SecureDirectoryStream<Path> dir, Path path) {
    Collection<IOException> exceptions = null;
    try {
      if (isDirectory(dir, path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
        SecureDirectoryStream<Path> childDir = dir.newDirectoryStream(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS });
        try {
          exceptions = deleteDirectoryContentsSecure(childDir);
          if (childDir != null)
            childDir.close(); 
        } catch (Throwable throwable) {
          if (childDir != null)
            try {
              childDir.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        if (exceptions == null)
          dir.deleteDirectory(path); 
      } else {
        dir.deleteFile(path);
      } 
      return exceptions;
    } catch (IOException e) {
      return addException(exceptions, e);
    } 
  }
  
  @CheckForNull
  private static Collection<IOException> deleteDirectoryContentsSecure(SecureDirectoryStream<Path> dir) {
    Collection<IOException> exceptions = null;
    try {
      for (Path path : dir)
        exceptions = concat(exceptions, deleteRecursivelySecure(dir, path.getFileName())); 
      return exceptions;
    } catch (DirectoryIteratorException e) {
      return addException(exceptions, e.getCause());
    } 
  }
  
  @CheckForNull
  private static Collection<IOException> deleteRecursivelyInsecure(Path path) {
    Collection<IOException> exceptions = null;
    try {
      if (Files.isDirectory(path, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);
        try {
          exceptions = deleteDirectoryContentsInsecure(stream);
          if (stream != null)
            stream.close(); 
        } catch (Throwable throwable) {
          if (stream != null)
            try {
              stream.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } 
      if (exceptions == null)
        Files.delete(path); 
      return exceptions;
    } catch (IOException e) {
      return addException(exceptions, e);
    } 
  }
  
  @CheckForNull
  private static Collection<IOException> deleteDirectoryContentsInsecure(DirectoryStream<Path> dir) {
    Collection<IOException> exceptions = null;
    try {
      for (Path entry : dir)
        exceptions = concat(exceptions, deleteRecursivelyInsecure(entry)); 
      return exceptions;
    } catch (DirectoryIteratorException e) {
      return addException(exceptions, e.getCause());
    } 
  }
  
  @CheckForNull
  private static Path getParentPath(Path path) {
    Path parent = path.getParent();
    if (parent != null)
      return parent; 
    if (path.getNameCount() == 0)
      return null; 
    return path.getFileSystem().getPath(".", new String[0]);
  }
  
  private static void checkAllowsInsecure(Path path, RecursiveDeleteOption[] options) throws InsecureRecursiveDeleteException {
    if (!Arrays.<RecursiveDeleteOption>asList(options).contains(RecursiveDeleteOption.ALLOW_INSECURE))
      throw new InsecureRecursiveDeleteException(path.toString()); 
  }
  
  private static Collection<IOException> addException(@CheckForNull Collection<IOException> exceptions, IOException e) {
    if (exceptions == null)
      exceptions = new ArrayList<>(); 
    exceptions.add(e);
    return exceptions;
  }
  
  @CheckForNull
  private static Collection<IOException> concat(@CheckForNull Collection<IOException> exceptions, @CheckForNull Collection<IOException> other) {
    if (exceptions == null)
      return other; 
    if (other != null)
      exceptions.addAll(other); 
    return exceptions;
  }
  
  private static void throwDeleteFailed(Path path, Collection<IOException> exceptions) throws FileSystemException {
    NoSuchFileException pathNotFound = pathNotFound(path, exceptions);
    if (pathNotFound != null)
      throw pathNotFound; 
    FileSystemException deleteFailed = new FileSystemException(path.toString(), null, "failed to delete one or more files; see suppressed exceptions for details");
    for (IOException e : exceptions)
      deleteFailed.addSuppressed(e); 
    throw deleteFailed;
  }
  
  @CheckForNull
  private static NoSuchFileException pathNotFound(Path path, Collection<IOException> exceptions) {
    if (exceptions.size() != 1)
      return null; 
    IOException exception = (IOException)Iterables.getOnlyElement(exceptions);
    if (!(exception instanceof NoSuchFileException))
      return null; 
    NoSuchFileException noSuchFileException = (NoSuchFileException)exception;
    String exceptionFile = noSuchFileException.getFile();
    if (exceptionFile == null)
      return null; 
    Path parentPath = getParentPath(path);
    if (parentPath == null)
      return null; 
    Path pathResolvedFromParent = parentPath.resolve(Objects.<Path>requireNonNull(path.getFileName()));
    if (exceptionFile.equals(pathResolvedFromParent.toString()))
      return noSuchFileException; 
    return null;
  }
}
