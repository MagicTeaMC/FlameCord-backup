package org.codehaus.plexus.util.xml.pull;

public class EntityReplacementMap {
  final String[] entityName;
  
  final char[][] entityNameBuf;
  
  final String[] entityReplacement;
  
  final char[][] entityReplacementBuf;
  
  int entityEnd;
  
  final int[] entityNameHash;
  
  public EntityReplacementMap(String[][] replacements) {
    int length = replacements.length;
    this.entityName = new String[length];
    this.entityNameBuf = new char[length][];
    this.entityReplacement = new String[length];
    this.entityReplacementBuf = new char[length][];
    this.entityNameHash = new int[length];
    for (String[] replacement : replacements)
      defineEntityReplacementText(replacement[0], replacement[1]); 
  }
  
  private void defineEntityReplacementText(String entityName, String replacementText) {
    if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
      String tmp = replacementText.substring(1, replacementText.length() - 1);
      for (int i = 0; i < this.entityName.length; i++) {
        if (this.entityName[i] != null && this.entityName[i].equals(tmp))
          replacementText = this.entityReplacement[i]; 
      } 
    } 
    char[] entityNameCharData = entityName.toCharArray();
    this.entityName[this.entityEnd] = newString(entityNameCharData, 0, entityName.length());
    this.entityNameBuf[this.entityEnd] = entityNameCharData;
    this.entityReplacement[this.entityEnd] = replacementText;
    this.entityReplacementBuf[this.entityEnd] = replacementText.toCharArray();
    this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, (this.entityNameBuf[this.entityEnd]).length);
    this.entityEnd++;
  }
  
  private String newString(char[] cbuf, int off, int len) {
    return new String(cbuf, off, len);
  }
  
  private static int fastHash(char[] ch, int off, int len) {
    if (len == 0)
      return 0; 
    int hash = ch[off];
    hash = (hash << 7) + ch[off + len - 1];
    if (len > 16)
      hash = (hash << 7) + ch[off + len / 4]; 
    if (len > 8)
      hash = (hash << 7) + ch[off + len / 2]; 
    return hash;
  }
  
  public static final EntityReplacementMap defaultEntityReplacementMap = new EntityReplacementMap(new String[][] { 
        { "nbsp", " " }, { "iexcl", "¡" }, { "cent", "¢" }, { "pound", "£" }, { "curren", "¤" }, { "yen", "¥" }, { "brvbar", "¦" }, { "sect", "§" }, { "uml", "¨" }, { "copy", "©" }, 
        { "ordf", "ª" }, { "laquo", "«" }, { "not", "¬" }, { "shy", "­" }, { "reg", "®" }, { "macr", "¯" }, { "deg", "°" }, { "plusmn", "±" }, { "sup2", "²" }, { "sup3", "³" }, 
        { "acute", "´" }, { "micro", "µ" }, { "para", "¶" }, { "middot", "·" }, { "cedil", "¸" }, { "sup1", "¹" }, { "ordm", "º" }, { "raquo", "»" }, { "frac14", "¼" }, { "frac12", "½" }, 
        { "frac34", "¾" }, { "iquest", "¿" }, { "Agrave", "À" }, { "Aacute", "Á" }, { "Acirc", "Â" }, { "Atilde", "Ã" }, { "Auml", "Ä" }, { "Aring", "Å" }, { "AElig", "Æ" }, { "Ccedil", "Ç" }, 
        { "Egrave", "È" }, { "Eacute", "É" }, { "Ecirc", "Ê" }, { "Euml", "Ë" }, { "Igrave", "Ì" }, { "Iacute", "Í" }, { "Icirc", "Î" }, { "Iuml", "Ï" }, { "ETH", "Ð" }, { "Ntilde", "Ñ" }, 
        { "Ograve", "Ò" }, { "Oacute", "Ó" }, { "Ocirc", "Ô" }, { "Otilde", "Õ" }, { "Ouml", "Ö" }, { "times", "×" }, { "Oslash", "Ø" }, { "Ugrave", "Ù" }, { "Uacute", "Ú" }, { "Ucirc", "Û" }, 
        { "Uuml", "Ü" }, { "Yacute", "Ý" }, { "THORN", "Þ" }, { "szlig", "ß" }, { "agrave", "à" }, { "aacute", "á" }, { "acirc", "â" }, { "atilde", "ã" }, { "auml", "ä" }, { "aring", "å" }, 
        { "aelig", "æ" }, { "ccedil", "ç" }, { "egrave", "è" }, { "eacute", "é" }, { "ecirc", "ê" }, { "euml", "ë" }, { "igrave", "ì" }, { "iacute", "í" }, { "icirc", "î" }, { "iuml", "ï" }, 
        { "eth", "ð" }, { "ntilde", "ñ" }, { "ograve", "ò" }, { "oacute", "ó" }, { "ocirc", "ô" }, { "otilde", "õ" }, { "ouml", "ö" }, { "divide", "÷" }, { "oslash", "ø" }, { "ugrave", "ù" }, 
        { "uacute", "ú" }, { "ucirc", "û" }, { "uuml", "ü" }, { "yacute", "ý" }, { "thorn", "þ" }, { "yuml", "ÿ" }, { "OElig", "Œ" }, { "oelig", "œ" }, { "Scaron", "Š" }, { "scaron", "š" }, 
        { "Yuml", "Ÿ" }, { "circ", "ˆ" }, { "tilde", "˜" }, { "ensp", " " }, { "emsp", " " }, { "thinsp", " " }, { "zwnj", "‌" }, { "zwj", "‍" }, { "lrm", "‎" }, { "rlm", "‏" }, 
        { "ndash", "–" }, { "mdash", "—" }, { "lsquo", "‘" }, { "rsquo", "’" }, { "sbquo", "‚" }, { "ldquo", "“" }, { "rdquo", "”" }, { "bdquo", "„" }, { "dagger", "†" }, { "Dagger", "‡" }, 
        { "permil", "‰" }, { "lsaquo", "‹" }, { "rsaquo", "›" }, { "euro", "€" }, { "fnof", "ƒ" }, { "Alpha", "Α" }, { "Beta", "Β" }, { "Gamma", "Γ" }, { "Delta", "Δ" }, { "Epsilon", "Ε" }, 
        { "Zeta", "Ζ" }, { "Eta", "Η" }, { "Theta", "Θ" }, { "Iota", "Ι" }, { "Kappa", "Κ" }, { "Lambda", "Λ" }, { "Mu", "Μ" }, { "Nu", "Ν" }, { "Xi", "Ξ" }, { "Omicron", "Ο" }, 
        { "Pi", "Π" }, { "Rho", "Ρ" }, { "Sigma", "Σ" }, { "Tau", "Τ" }, { "Upsilon", "Υ" }, { "Phi", "Φ" }, { "Chi", "Χ" }, { "Psi", "Ψ" }, { "Omega", "Ω" }, { "alpha", "α" }, 
        { "beta", "β" }, { "gamma", "γ" }, { "delta", "δ" }, { "epsilon", "ε" }, { "zeta", "ζ" }, { "eta", "η" }, { "theta", "θ" }, { "iota", "ι" }, { "kappa", "κ" }, { "lambda", "λ" }, 
        { "mu", "μ" }, { "nu", "ν" }, { "xi", "ξ" }, { "omicron", "ο" }, { "pi", "π" }, { "rho", "ρ" }, { "sigmaf", "ς" }, { "sigma", "σ" }, { "tau", "τ" }, { "upsilon", "υ" }, 
        { "phi", "φ" }, { "chi", "χ" }, { "psi", "ψ" }, { "omega", "ω" }, { "thetasym", "ϑ" }, { "upsih", "ϒ" }, { "piv", "ϖ" }, { "bull", "•" }, { "hellip", "…" }, { "prime", "′" }, 
        { "Prime", "″" }, { "oline", "‾" }, { "frasl", "⁄" }, { "weierp", "℘" }, { "image", "ℑ" }, { "real", "ℜ" }, { "trade", "™" }, { "alefsym", "ℵ" }, { "larr", "←" }, { "uarr", "↑" }, 
        { "rarr", "→" }, { "darr", "↓" }, { "harr", "↔" }, { "crarr", "↵" }, { "lArr", "⇐" }, { "uArr", "⇑" }, { "rArr", "⇒" }, { "dArr", "⇓" }, { "hArr", "⇔" }, { "forall", "∀" }, 
        { "part", "∂" }, { "exist", "∃" }, { "empty", "∅" }, { "nabla", "∇" }, { "isin", "∈" }, { "notin", "∉" }, { "ni", "∋" }, { "prod", "∏" }, { "sum", "∑" }, { "minus", "−" }, 
        { "lowast", "∗" }, { "radic", "√" }, { "prop", "∝" }, { "infin", "∞" }, { "ang", "∠" }, { "and", "∧" }, { "or", "∨" }, { "cap", "∩" }, { "cup", "∪" }, { "int", "∫" }, 
        { "there4", "∴" }, { "sim", "∼" }, { "cong", "≅" }, { "asymp", "≈" }, { "ne", "≠" }, { "equiv", "≡" }, { "le", "≤" }, { "ge", "≥" }, { "sub", "⊂" }, { "sup", "⊃" }, 
        { "nsub", "⊄" }, { "sube", "⊆" }, { "supe", "⊇" }, { "oplus", "⊕" }, { "otimes", "⊗" }, { "perp", "⊥" }, { "sdot", "⋅" }, { "lceil", "⌈" }, { "rceil", "⌉" }, { "lfloor", "⌊" }, 
        { "rfloor", "⌋" }, { "lang", "〈" }, { "rang", "〉" }, { "loz", "◊" }, { "spades", "♠" }, { "clubs", "♣" }, { "hearts", "♥" }, { "diams", "♦" } });
}
