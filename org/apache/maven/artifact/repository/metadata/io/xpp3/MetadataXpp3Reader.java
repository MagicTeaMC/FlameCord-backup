package org.apache.maven.artifact.repository.metadata.io.xpp3;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Plugin;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MetadataXpp3Reader {
  private boolean addDefaultEntities = true;
  
  public final ContentTransformer contentTransformer;
  
  public MetadataXpp3Reader() {
    this(new ContentTransformer() {
          public String transform(String source, String fieldName) {
            return source;
          }
        });
  }
  
  public static interface ContentTransformer {
    String transform(String param1String1, String param1String2);
  }
  
  public MetadataXpp3Reader(ContentTransformer contentTransformer) {
    this.contentTransformer = contentTransformer;
  }
  
  private boolean checkFieldWithDuplicate(XmlPullParser parser, String tagName, String alias, Set<String> parsed) throws XmlPullParserException {
    if (!parser.getName().equals(tagName) && !parser.getName().equals(alias))
      return false; 
    if (!parsed.add(tagName))
      throw new XmlPullParserException("Duplicated tag: '" + tagName + "'", parser, null); 
    return true;
  }
  
  private void checkUnknownAttribute(XmlPullParser parser, String attribute, String tagName, boolean strict) throws XmlPullParserException, IOException {
    if (strict)
      throw new XmlPullParserException("Unknown attribute '" + attribute + "' for tag '" + tagName + "'", parser, null); 
  }
  
  private void checkUnknownElement(XmlPullParser parser, boolean strict) throws XmlPullParserException, IOException {
    if (strict)
      throw new XmlPullParserException("Unrecognised tag: '" + parser.getName() + "'", parser, null); 
    for (int unrecognizedTagCount = 1; unrecognizedTagCount > 0; ) {
      int eventType = parser.next();
      if (eventType == 2) {
        unrecognizedTagCount++;
        continue;
      } 
      if (eventType == 3)
        unrecognizedTagCount--; 
    } 
  }
  
  public boolean getAddDefaultEntities() {
    return this.addDefaultEntities;
  }
  
  private boolean getBooleanValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    return getBooleanValue(s, attribute, parser, null);
  }
  
  private boolean getBooleanValue(String s, String attribute, XmlPullParser parser, String defaultValue) throws XmlPullParserException {
    if (s != null && s.length() != 0)
      return Boolean.valueOf(s).booleanValue(); 
    if (defaultValue != null)
      return Boolean.valueOf(defaultValue).booleanValue(); 
    return false;
  }
  
  private byte getByteValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Byte.valueOf(s).byteValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a byte", parser, nfe); 
      }  
    return 0;
  }
  
  private char getCharacterValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    if (s != null)
      return s.charAt(0); 
    return Character.MIN_VALUE;
  }
  
  private Date getDateValue(String s, String attribute, XmlPullParser parser) throws XmlPullParserException {
    return getDateValue(s, attribute, null, parser);
  }
  
  private Date getDateValue(String s, String attribute, String dateFormat, XmlPullParser parser) throws XmlPullParserException {
    if (s != null) {
      String effectiveDateFormat = dateFormat;
      if (dateFormat == null)
        effectiveDateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"; 
      if ("long".equals(effectiveDateFormat))
        try {
          return new Date(Long.parseLong(s));
        } catch (NumberFormatException e) {
          throw new XmlPullParserException(e.getMessage(), parser, e);
        }  
      try {
        DateFormat dateParser = new SimpleDateFormat(effectiveDateFormat, Locale.US);
        return dateParser.parse(s);
      } catch (ParseException e) {
        throw new XmlPullParserException(e.getMessage(), parser, e);
      } 
    } 
    return null;
  }
  
  private double getDoubleValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Double.valueOf(s).doubleValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe); 
      }  
    return 0.0D;
  }
  
  private float getFloatValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Float.valueOf(s).floatValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a floating point number", parser, nfe); 
      }  
    return 0.0F;
  }
  
  private int getIntegerValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Integer.valueOf(s).intValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be an integer", parser, nfe); 
      }  
    return 0;
  }
  
  private long getLongValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Long.valueOf(s).longValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a long integer", parser, nfe); 
      }  
    return 0L;
  }
  
  private String getRequiredAttributeValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s == null)
      if (strict)
        throw new XmlPullParserException("Missing required value for attribute '" + attribute + "'", parser, null);  
    return s;
  }
  
  private short getShortValue(String s, String attribute, XmlPullParser parser, boolean strict) throws XmlPullParserException {
    if (s != null)
      try {
        return Short.valueOf(s).shortValue();
      } catch (NumberFormatException nfe) {
        if (strict)
          throw new XmlPullParserException("Unable to parse element '" + attribute + "', must be a short integer", parser, nfe); 
      }  
    return 0;
  }
  
  private String getTrimmedValue(String s) {
    if (s != null)
      s = s.trim(); 
    return s;
  }
  
  private String interpolatedTrimmed(String value, String context) {
    return getTrimmedValue(this.contentTransformer.transform(value, context));
  }
  
  private int nextTag(XmlPullParser parser) throws IOException, XmlPullParserException {
    int eventType = parser.next();
    if (eventType == 4)
      eventType = parser.next(); 
    if (eventType != 2 && eventType != 3)
      throw new XmlPullParserException("expected START_TAG or END_TAG not " + XmlPullParser.TYPES[eventType], parser, null); 
    return eventType;
  }
  
  public Metadata read(Reader reader, boolean strict) throws IOException, XmlPullParserException {
    MXParser mXParser = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
    mXParser.setInput(reader);
    return read((XmlPullParser)mXParser, strict);
  }
  
  public Metadata read(Reader reader) throws IOException, XmlPullParserException {
    return read(reader, true);
  }
  
  public Metadata read(InputStream in, boolean strict) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in), strict);
  }
  
  public Metadata read(InputStream in) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in));
  }
  
  private Metadata parseMetadata(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Metadata metadata = new Metadata();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        if (!"xmlns".equals(name))
          if ("modelVersion".equals(name)) {
            metadata.setModelVersion(interpolatedTrimmed(value, "modelVersion"));
          } else {
            checkUnknownAttribute(parser, name, tagName, strict);
          }   
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        metadata.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        metadata.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "versioning", null, parsed)) {
        metadata.setVersioning(parseVersioning(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        metadata.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        metadata.setPlugins(plugins);
        while (parser.nextTag() == 2) {
          if ("plugin".equals(parser.getName())) {
            plugins.add(parsePlugin(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return metadata;
  }
  
  private Plugin parsePlugin(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Plugin plugin = new Plugin();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        plugin.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "prefix", null, parsed)) {
        plugin.setPrefix(interpolatedTrimmed(parser.nextText(), "prefix"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        plugin.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return plugin;
  }
  
  private Snapshot parseSnapshot(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Snapshot snapshot = new Snapshot();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "timestamp", null, parsed)) {
        snapshot.setTimestamp(interpolatedTrimmed(parser.nextText(), "timestamp"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "buildNumber", null, parsed)) {
        snapshot.setBuildNumber(getIntegerValue(interpolatedTrimmed(parser.nextText(), "buildNumber"), "buildNumber", parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "localCopy", null, parsed)) {
        snapshot.setLocalCopy(getBooleanValue(interpolatedTrimmed(parser.nextText(), "localCopy"), "localCopy", parser, "false"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return snapshot;
  }
  
  private SnapshotVersion parseSnapshotVersion(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    SnapshotVersion snapshotVersion = new SnapshotVersion();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "classifier", null, parsed)) {
        snapshotVersion.setClassifier(interpolatedTrimmed(parser.nextText(), "classifier"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "extension", null, parsed)) {
        snapshotVersion.setExtension(interpolatedTrimmed(parser.nextText(), "extension"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "value", null, parsed)) {
        snapshotVersion.setVersion(interpolatedTrimmed(parser.nextText(), "value"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "updated", null, parsed)) {
        snapshotVersion.setUpdated(interpolatedTrimmed(parser.nextText(), "updated"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return snapshotVersion;
  }
  
  private Versioning parseVersioning(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Versioning versioning = new Versioning();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "latest", null, parsed)) {
        versioning.setLatest(interpolatedTrimmed(parser.nextText(), "latest"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "release", null, parsed)) {
        versioning.setRelease(interpolatedTrimmed(parser.nextText(), "release"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "versions", null, parsed)) {
        List<String> versions = new ArrayList<>();
        versioning.setVersions(versions);
        while (parser.nextTag() == 2) {
          if ("version".equals(parser.getName())) {
            versions.add(interpolatedTrimmed(parser.nextText(), "versions"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "lastUpdated", null, parsed)) {
        versioning.setLastUpdated(interpolatedTrimmed(parser.nextText(), "lastUpdated"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "snapshot", null, parsed)) {
        versioning.setSnapshot(parseSnapshot(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "snapshotVersions", null, parsed)) {
        List<SnapshotVersion> snapshotVersions = new ArrayList<>();
        versioning.setSnapshotVersions(snapshotVersions);
        while (parser.nextTag() == 2) {
          if ("snapshotVersion".equals(parser.getName())) {
            snapshotVersions.add(parseSnapshotVersion(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return versioning;
  }
  
  private Metadata read(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    Metadata metadata = null;
    int eventType = parser.getEventType();
    boolean parsed = false;
    while (eventType != 1) {
      if (eventType == 2) {
        if (strict && !"metadata".equals(parser.getName()))
          throw new XmlPullParserException("Expected root element 'metadata' but found '" + parser.getName() + "'", parser, null); 
        if (parsed)
          throw new XmlPullParserException("Duplicated tag: 'metadata'", parser, null); 
        metadata = parseMetadata(parser, strict);
        metadata.setModelEncoding(parser.getInputEncoding());
        parsed = true;
      } 
      eventType = parser.next();
    } 
    if (parsed)
      return metadata; 
    throw new XmlPullParserException("Expected root element 'metadata' but found no element at all: invalid XML document", parser, null);
  }
  
  public void setAddDefaultEntities(boolean addDefaultEntities) {
    this.addDefaultEntities = addDefaultEntities;
  }
}
