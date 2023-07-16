package org.codehaus.plexus.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class NioFiles {
  public static boolean isSymbolicLink(File file) {
    return Files.isSymbolicLink(file.toPath());
  }
  
  public static void chmod(File file, int mode) throws IOException {
    Path path = file.toPath();
    if (!Files.isSymbolicLink(path))
      Files.setPosixFilePermissions(path, getPermissions(mode)); 
  }
  
  private static Set<PosixFilePermission> getPermissions(int mode) {
    Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
    if ((mode & 0x100) > 0)
      perms.add(PosixFilePermission.OWNER_READ); 
    if ((mode & 0x80) > 0)
      perms.add(PosixFilePermission.OWNER_WRITE); 
    if ((mode & 0x40) > 0)
      perms.add(PosixFilePermission.OWNER_EXECUTE); 
    if ((mode & 0x20) > 0)
      perms.add(PosixFilePermission.GROUP_READ); 
    if ((mode & 0x10) > 0)
      perms.add(PosixFilePermission.GROUP_WRITE); 
    if ((mode & 0x8) > 0)
      perms.add(PosixFilePermission.GROUP_EXECUTE); 
    if ((mode & 0x4) > 0)
      perms.add(PosixFilePermission.OTHERS_READ); 
    if ((mode & 0x2) > 0)
      perms.add(PosixFilePermission.OTHERS_WRITE); 
    if ((mode & 0x1) > 0)
      perms.add(PosixFilePermission.OTHERS_EXECUTE); 
    return perms;
  }
  
  public static long getLastModified(File file) throws IOException {
    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class, new LinkOption[0]);
    return basicFileAttributes.lastModifiedTime().toMillis();
  }
  
  public static File readSymbolicLink(File symlink) throws IOException {
    Path path = Files.readSymbolicLink(symlink.toPath());
    return path.toFile();
  }
  
  public static File createSymbolicLink(File symlink, File target) throws IOException {
    Path link = symlink.toPath();
    if (Files.exists(link, new LinkOption[] { LinkOption.NOFOLLOW_LINKS }))
      Files.delete(link); 
    link = Files.createSymbolicLink(link, target.toPath(), (FileAttribute<?>[])new FileAttribute[0]);
    return link.toFile();
  }
  
  public static boolean deleteIfExists(File file) throws IOException {
    return Files.deleteIfExists(file.toPath());
  }
  
  public static File copy(File source, File target) throws IOException {
    Path copy = Files.copy(source.toPath(), target.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES });
    return copy.toFile();
  }
}
