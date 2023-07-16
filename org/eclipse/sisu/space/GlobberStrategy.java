package org.eclipse.sisu.space;

import java.util.regex.Pattern;

enum GlobberStrategy {
  ANYTHING {
    final Object compile(String glob) {
      return null;
    }
    
    final boolean matches(Object globPattern, String filename) {
      return true;
    }
  },
  SUFFIX {
    final Object compile(String glob) {
      return glob.substring(1);
    }
    
    final boolean matches(Object globPattern, String filename) {
      return filename.endsWith((String)globPattern);
    }
  },
  PREFIX {
    final Object compile(String glob) {
      return glob.substring(0, glob.length() - 1);
    }
    
    final boolean matches(Object globPattern, String filename) {
      return null.basename(filename).startsWith((String)globPattern);
    }
  },
  EXACT {
    final Object compile(String glob) {
      return glob;
    }
    
    final boolean matches(Object globPattern, String filename) {
      return globPattern.equals(null.basename(filename));
    }
  },
  PATTERN {
    final Object compile(String glob) {
      return Pattern.compile("\\Q" + glob.replaceAll("\\*+", "\\\\E.*\\\\Q") + "\\E");
    }
    
    final boolean matches(Object globPattern, String filename) {
      return ((Pattern)globPattern).matcher(null.basename(filename)).matches();
    }
  };
  
  static final GlobberStrategy selectFor(String glob) {
    if (glob == null || "*".equals(glob))
      return ANYTHING; 
    int firstWildcard = glob.indexOf('*');
    if (firstWildcard < 0)
      return EXACT; 
    int lastWildcard = glob.lastIndexOf('*');
    if (firstWildcard == lastWildcard) {
      if (firstWildcard == 0)
        return SUFFIX; 
      if (lastWildcard == glob.length() - 1)
        return PREFIX; 
    } 
    return PATTERN;
  }
  
  static final String basename(String filename) {
    return filename.substring(1 + filename.lastIndexOf('/'));
  }
  
  abstract Object compile(String paramString);
  
  abstract boolean matches(Object paramObject, String paramString);
}
