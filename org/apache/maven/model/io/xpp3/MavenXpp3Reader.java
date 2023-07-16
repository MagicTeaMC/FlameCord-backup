package org.apache.maven.model.io.xpp3;

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
import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.ConfigurationContainer;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.FileSet;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.ModelBase;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.PatternSet;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginConfiguration;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryBase;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.pull.EntityReplacementMap;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class MavenXpp3Reader {
  private boolean addDefaultEntities = true;
  
  public final ContentTransformer contentTransformer;
  
  public MavenXpp3Reader() {
    this(new ContentTransformer() {
          public String transform(String source, String fieldName) {
            return source;
          }
        });
  }
  
  public static interface ContentTransformer {
    String transform(String param1String1, String param1String2);
  }
  
  public MavenXpp3Reader(ContentTransformer contentTransformer) {
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
  
  public Model read(Reader reader, boolean strict) throws IOException, XmlPullParserException {
    MXParser mXParser = this.addDefaultEntities ? new MXParser(EntityReplacementMap.defaultEntityReplacementMap) : new MXParser();
    mXParser.setInput(reader);
    return read((XmlPullParser)mXParser, strict);
  }
  
  public Model read(Reader reader) throws IOException, XmlPullParserException {
    return read(reader, true);
  }
  
  public Model read(InputStream in, boolean strict) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in), strict);
  }
  
  public Model read(InputStream in) throws IOException, XmlPullParserException {
    return read((Reader)ReaderFactory.newXmlReader(in));
  }
  
  private Activation parseActivation(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Activation activation = new Activation();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "activeByDefault", null, parsed)) {
        activation.setActiveByDefault(getBooleanValue(interpolatedTrimmed(parser.nextText(), "activeByDefault"), "activeByDefault", parser, "false"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "jdk", null, parsed)) {
        activation.setJdk(interpolatedTrimmed(parser.nextText(), "jdk"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "os", null, parsed)) {
        activation.setOs(parseActivationOS(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "property", null, parsed)) {
        activation.setProperty(parseActivationProperty(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "file", null, parsed)) {
        activation.setFile(parseActivationFile(parser, strict));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return activation;
  }
  
  private ActivationFile parseActivationFile(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ActivationFile activationFile = new ActivationFile();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "missing", null, parsed)) {
        activationFile.setMissing(interpolatedTrimmed(parser.nextText(), "missing"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "exists", null, parsed)) {
        activationFile.setExists(interpolatedTrimmed(parser.nextText(), "exists"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return activationFile;
  }
  
  private ActivationOS parseActivationOS(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ActivationOS activationOS = new ActivationOS();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        activationOS.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "family", null, parsed)) {
        activationOS.setFamily(interpolatedTrimmed(parser.nextText(), "family"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "arch", null, parsed)) {
        activationOS.setArch(interpolatedTrimmed(parser.nextText(), "arch"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        activationOS.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return activationOS;
  }
  
  private ActivationProperty parseActivationProperty(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ActivationProperty activationProperty = new ActivationProperty();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        activationProperty.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "value", null, parsed)) {
        activationProperty.setValue(interpolatedTrimmed(parser.nextText(), "value"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return activationProperty;
  }
  
  private Build parseBuild(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Build build = new Build();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "sourceDirectory", null, parsed)) {
        build.setSourceDirectory(interpolatedTrimmed(parser.nextText(), "sourceDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "scriptSourceDirectory", null, parsed)) {
        build.setScriptSourceDirectory(interpolatedTrimmed(parser.nextText(), "scriptSourceDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "testSourceDirectory", null, parsed)) {
        build.setTestSourceDirectory(interpolatedTrimmed(parser.nextText(), "testSourceDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
        build.setOutputDirectory(interpolatedTrimmed(parser.nextText(), "outputDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "testOutputDirectory", null, parsed)) {
        build.setTestOutputDirectory(interpolatedTrimmed(parser.nextText(), "testOutputDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
        List<Extension> extensions = new ArrayList<>();
        build.setExtensions(extensions);
        while (parser.nextTag() == 2) {
          if ("extension".equals(parser.getName())) {
            extensions.add(parseExtension(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
        build.setDefaultGoal(interpolatedTrimmed(parser.nextText(), "defaultGoal"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
        List<Resource> resources = new ArrayList<>();
        build.setResources(resources);
        while (parser.nextTag() == 2) {
          if ("resource".equals(parser.getName())) {
            resources.add(parseResource(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
        List<Resource> testResources = new ArrayList<>();
        build.setTestResources(testResources);
        while (parser.nextTag() == 2) {
          if ("testResource".equals(parser.getName())) {
            testResources.add(parseResource(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
        build.setDirectory(interpolatedTrimmed(parser.nextText(), "directory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
        build.setFinalName(interpolatedTrimmed(parser.nextText(), "finalName"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
        List<String> filters = new ArrayList<>();
        build.setFilters(filters);
        while (parser.nextTag() == 2) {
          if ("filter".equals(parser.getName())) {
            filters.add(interpolatedTrimmed(parser.nextText(), "filters"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
        build.setPluginManagement(parsePluginManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        build.setPlugins(plugins);
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
    return build;
  }
  
  private BuildBase parseBuildBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    BuildBase buildBase = new BuildBase();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "defaultGoal", null, parsed)) {
        buildBase.setDefaultGoal(interpolatedTrimmed(parser.nextText(), "defaultGoal"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "resources", null, parsed)) {
        List<Resource> resources = new ArrayList<>();
        buildBase.setResources(resources);
        while (parser.nextTag() == 2) {
          if ("resource".equals(parser.getName())) {
            resources.add(parseResource(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "testResources", null, parsed)) {
        List<Resource> testResources = new ArrayList<>();
        buildBase.setTestResources(testResources);
        while (parser.nextTag() == 2) {
          if ("testResource".equals(parser.getName())) {
            testResources.add(parseResource(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
        buildBase.setDirectory(interpolatedTrimmed(parser.nextText(), "directory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "finalName", null, parsed)) {
        buildBase.setFinalName(interpolatedTrimmed(parser.nextText(), "finalName"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "filters", null, parsed)) {
        List<String> filters = new ArrayList<>();
        buildBase.setFilters(filters);
        while (parser.nextTag() == 2) {
          if ("filter".equals(parser.getName())) {
            filters.add(interpolatedTrimmed(parser.nextText(), "filters"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
        buildBase.setPluginManagement(parsePluginManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        buildBase.setPlugins(plugins);
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
    return buildBase;
  }
  
  private CiManagement parseCiManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    CiManagement ciManagement = new CiManagement();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
        ciManagement.setSystem(interpolatedTrimmed(parser.nextText(), "system"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        ciManagement.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "notifiers", null, parsed)) {
        List<Notifier> notifiers = new ArrayList<>();
        ciManagement.setNotifiers(notifiers);
        while (parser.nextTag() == 2) {
          if ("notifier".equals(parser.getName())) {
            notifiers.add(parseNotifier(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return ciManagement;
  }
  
  private ConfigurationContainer parseConfigurationContainer(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ConfigurationContainer configurationContainer = new ConfigurationContainer();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
        configurationContainer.setInherited(interpolatedTrimmed(parser.nextText(), "inherited"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        configurationContainer.setConfiguration(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return configurationContainer;
  }
  
  private Contributor parseContributor(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Contributor contributor = new Contributor();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        contributor.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
        contributor.setEmail(interpolatedTrimmed(parser.nextText(), "email"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        contributor.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
        contributor.setOrganization(interpolatedTrimmed(parser.nextText(), "organization"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
        contributor.setOrganizationUrl(interpolatedTrimmed(parser.nextText(), "organizationUrl"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
        List<String> roles = new ArrayList<>();
        contributor.setRoles(roles);
        while (parser.nextTag() == 2) {
          if ("role".equals(parser.getName())) {
            roles.add(interpolatedTrimmed(parser.nextText(), "roles"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
        contributor.setTimezone(interpolatedTrimmed(parser.nextText(), "timezone"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          contributor.addProperty(key, value);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return contributor;
  }
  
  private Dependency parseDependency(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Dependency dependency = new Dependency();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        dependency.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        dependency.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        dependency.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
        dependency.setType(interpolatedTrimmed(parser.nextText(), "type"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "classifier", null, parsed)) {
        dependency.setClassifier(interpolatedTrimmed(parser.nextText(), "classifier"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "scope", null, parsed)) {
        dependency.setScope(interpolatedTrimmed(parser.nextText(), "scope"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "systemPath", null, parsed)) {
        dependency.setSystemPath(interpolatedTrimmed(parser.nextText(), "systemPath"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "exclusions", null, parsed)) {
        List<Exclusion> exclusions = new ArrayList<>();
        dependency.setExclusions(exclusions);
        while (parser.nextTag() == 2) {
          if ("exclusion".equals(parser.getName())) {
            exclusions.add(parseExclusion(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "optional", null, parsed)) {
        dependency.setOptional(interpolatedTrimmed(parser.nextText(), "optional"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return dependency;
  }
  
  private DependencyManagement parseDependencyManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    DependencyManagement dependencyManagement = new DependencyManagement();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
        List<Dependency> dependencies = new ArrayList<>();
        dependencyManagement.setDependencies(dependencies);
        while (parser.nextTag() == 2) {
          if ("dependency".equals(parser.getName())) {
            dependencies.add(parseDependency(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return dependencyManagement;
  }
  
  private DeploymentRepository parseDeploymentRepository(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    DeploymentRepository deploymentRepository = new DeploymentRepository();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "uniqueVersion", null, parsed)) {
        deploymentRepository.setUniqueVersion(getBooleanValue(interpolatedTrimmed(parser.nextText(), "uniqueVersion"), "uniqueVersion", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
        deploymentRepository.setReleases(parseRepositoryPolicy(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
        deploymentRepository.setSnapshots(parseRepositoryPolicy(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        deploymentRepository.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        deploymentRepository.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        deploymentRepository.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
        deploymentRepository.setLayout(interpolatedTrimmed(parser.nextText(), "layout"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return deploymentRepository;
  }
  
  private Developer parseDeveloper(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Developer developer = new Developer();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        developer.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        developer.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "email", null, parsed)) {
        developer.setEmail(interpolatedTrimmed(parser.nextText(), "email"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        developer.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
        developer.setOrganization(interpolatedTrimmed(parser.nextText(), "organization"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "organizationUrl", "organisationUrl", parsed)) {
        developer.setOrganizationUrl(interpolatedTrimmed(parser.nextText(), "organizationUrl"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "roles", null, parsed)) {
        List<String> roles = new ArrayList<>();
        developer.setRoles(roles);
        while (parser.nextTag() == 2) {
          if ("role".equals(parser.getName())) {
            roles.add(interpolatedTrimmed(parser.nextText(), "roles"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "timezone", null, parsed)) {
        developer.setTimezone(interpolatedTrimmed(parser.nextText(), "timezone"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          developer.addProperty(key, value);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return developer;
  }
  
  private DistributionManagement parseDistributionManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    DistributionManagement distributionManagement = new DistributionManagement();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "repository", null, parsed)) {
        distributionManagement.setRepository(parseDeploymentRepository(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "snapshotRepository", null, parsed)) {
        distributionManagement.setSnapshotRepository(parseDeploymentRepository(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "site", null, parsed)) {
        distributionManagement.setSite(parseSite(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "downloadUrl", null, parsed)) {
        distributionManagement.setDownloadUrl(interpolatedTrimmed(parser.nextText(), "downloadUrl"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "relocation", null, parsed)) {
        distributionManagement.setRelocation(parseRelocation(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "status", null, parsed)) {
        distributionManagement.setStatus(interpolatedTrimmed(parser.nextText(), "status"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return distributionManagement;
  }
  
  private Exclusion parseExclusion(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Exclusion exclusion = new Exclusion();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        exclusion.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        exclusion.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return exclusion;
  }
  
  private Extension parseExtension(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Extension extension = new Extension();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        extension.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        extension.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        extension.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return extension;
  }
  
  private FileSet parseFileSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    FileSet fileSet = new FileSet();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
        fileSet.setDirectory(interpolatedTrimmed(parser.nextText(), "directory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
        List<String> includes = new ArrayList<>();
        fileSet.setIncludes(includes);
        while (parser.nextTag() == 2) {
          if ("include".equals(parser.getName())) {
            includes.add(interpolatedTrimmed(parser.nextText(), "includes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
        List<String> excludes = new ArrayList<>();
        fileSet.setExcludes(excludes);
        while (parser.nextTag() == 2) {
          if ("exclude".equals(parser.getName())) {
            excludes.add(interpolatedTrimmed(parser.nextText(), "excludes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return fileSet;
  }
  
  private IssueManagement parseIssueManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    IssueManagement issueManagement = new IssueManagement();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "system", null, parsed)) {
        issueManagement.setSystem(interpolatedTrimmed(parser.nextText(), "system"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        issueManagement.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return issueManagement;
  }
  
  private License parseLicense(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    License license = new License();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        license.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        license.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "distribution", null, parsed)) {
        license.setDistribution(interpolatedTrimmed(parser.nextText(), "distribution"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "comments", null, parsed)) {
        license.setComments(interpolatedTrimmed(parser.nextText(), "comments"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return license;
  }
  
  private MailingList parseMailingList(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    MailingList mailingList = new MailingList();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        mailingList.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "subscribe", null, parsed)) {
        mailingList.setSubscribe(interpolatedTrimmed(parser.nextText(), "subscribe"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "unsubscribe", null, parsed)) {
        mailingList.setUnsubscribe(interpolatedTrimmed(parser.nextText(), "unsubscribe"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "post", null, parsed)) {
        mailingList.setPost(interpolatedTrimmed(parser.nextText(), "post"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "archive", null, parsed)) {
        mailingList.setArchive(interpolatedTrimmed(parser.nextText(), "archive"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "otherArchives", null, parsed)) {
        List<String> otherArchives = new ArrayList<>();
        mailingList.setOtherArchives(otherArchives);
        while (parser.nextTag() == 2) {
          if ("otherArchive".equals(parser.getName())) {
            otherArchives.add(interpolatedTrimmed(parser.nextText(), "otherArchives"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return mailingList;
  }
  
  private Model parseModel(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Model model = new Model();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        if (!"xmlns".equals(name))
          if ("child.project.url.inherit.append.path".equals(name)) {
            model.setChildProjectUrlInheritAppendPath(interpolatedTrimmed(value, "child.project.url.inherit.append.path"));
          } else {
            checkUnknownAttribute(parser, name, tagName, strict);
          }   
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "modelVersion", null, parsed)) {
        model.setModelVersion(interpolatedTrimmed(parser.nextText(), "modelVersion"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "parent", null, parsed)) {
        model.setParent(parseParent(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        model.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        model.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        model.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "packaging", null, parsed)) {
        model.setPackaging(interpolatedTrimmed(parser.nextText(), "packaging"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        model.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "description", null, parsed)) {
        model.setDescription(interpolatedTrimmed(parser.nextText(), "description"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        model.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "inceptionYear", null, parsed)) {
        model.setInceptionYear(interpolatedTrimmed(parser.nextText(), "inceptionYear"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "organization", "organisation", parsed)) {
        model.setOrganization(parseOrganization(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "licenses", null, parsed)) {
        List<License> licenses = new ArrayList<>();
        model.setLicenses(licenses);
        while (parser.nextTag() == 2) {
          if ("license".equals(parser.getName())) {
            licenses.add(parseLicense(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "developers", null, parsed)) {
        List<Developer> developers = new ArrayList<>();
        model.setDevelopers(developers);
        while (parser.nextTag() == 2) {
          if ("developer".equals(parser.getName())) {
            developers.add(parseDeveloper(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "contributors", null, parsed)) {
        List<Contributor> contributors = new ArrayList<>();
        model.setContributors(contributors);
        while (parser.nextTag() == 2) {
          if ("contributor".equals(parser.getName())) {
            contributors.add(parseContributor(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "mailingLists", null, parsed)) {
        List<MailingList> mailingLists = new ArrayList<>();
        model.setMailingLists(mailingLists);
        while (parser.nextTag() == 2) {
          if ("mailingList".equals(parser.getName())) {
            mailingLists.add(parseMailingList(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "prerequisites", null, parsed)) {
        model.setPrerequisites(parsePrerequisites(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
        List<String> modules = new ArrayList<>();
        model.setModules(modules);
        while (parser.nextTag() == 2) {
          if ("module".equals(parser.getName())) {
            modules.add(interpolatedTrimmed(parser.nextText(), "modules"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "scm", null, parsed)) {
        model.setScm(parseScm(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "issueManagement", null, parsed)) {
        model.setIssueManagement(parseIssueManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "ciManagement", null, parsed)) {
        model.setCiManagement(parseCiManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
        model.setDistributionManagement(parseDistributionManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          model.addProperty(key, value);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
        model.setDependencyManagement(parseDependencyManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
        List<Dependency> dependencies = new ArrayList<>();
        model.setDependencies(dependencies);
        while (parser.nextTag() == 2) {
          if ("dependency".equals(parser.getName())) {
            dependencies.add(parseDependency(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
        List<Repository> repositories = new ArrayList<>();
        model.setRepositories(repositories);
        while (parser.nextTag() == 2) {
          if ("repository".equals(parser.getName())) {
            repositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
        List<Repository> pluginRepositories = new ArrayList<>();
        model.setPluginRepositories(pluginRepositories);
        while (parser.nextTag() == 2) {
          if ("pluginRepository".equals(parser.getName())) {
            pluginRepositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
        model.setBuild(parseBuild(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
        model.setReports(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
        model.setReporting(parseReporting(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "profiles", null, parsed)) {
        List<Profile> profiles = new ArrayList<>();
        model.setProfiles(profiles);
        while (parser.nextTag() == 2) {
          if ("profile".equals(parser.getName())) {
            profiles.add(parseProfile(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return model;
  }
  
  private ModelBase parseModelBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ModelBase modelBase = new ModelBase();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
        List<String> modules = new ArrayList<>();
        modelBase.setModules(modules);
        while (parser.nextTag() == 2) {
          if ("module".equals(parser.getName())) {
            modules.add(interpolatedTrimmed(parser.nextText(), "modules"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
        modelBase.setDistributionManagement(parseDistributionManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          modelBase.addProperty(key, value);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
        modelBase.setDependencyManagement(parseDependencyManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
        List<Dependency> dependencies = new ArrayList<>();
        modelBase.setDependencies(dependencies);
        while (parser.nextTag() == 2) {
          if ("dependency".equals(parser.getName())) {
            dependencies.add(parseDependency(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
        List<Repository> repositories = new ArrayList<>();
        modelBase.setRepositories(repositories);
        while (parser.nextTag() == 2) {
          if ("repository".equals(parser.getName())) {
            repositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
        List<Repository> pluginRepositories = new ArrayList<>();
        modelBase.setPluginRepositories(pluginRepositories);
        while (parser.nextTag() == 2) {
          if ("pluginRepository".equals(parser.getName())) {
            pluginRepositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
        modelBase.setReports(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
        modelBase.setReporting(parseReporting(parser, strict));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return modelBase;
  }
  
  private Notifier parseNotifier(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Notifier notifier = new Notifier();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "type", null, parsed)) {
        notifier.setType(interpolatedTrimmed(parser.nextText(), "type"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "sendOnError", null, parsed)) {
        notifier.setSendOnError(getBooleanValue(interpolatedTrimmed(parser.nextText(), "sendOnError"), "sendOnError", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "sendOnFailure", null, parsed)) {
        notifier.setSendOnFailure(getBooleanValue(interpolatedTrimmed(parser.nextText(), "sendOnFailure"), "sendOnFailure", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "sendOnSuccess", null, parsed)) {
        notifier.setSendOnSuccess(getBooleanValue(interpolatedTrimmed(parser.nextText(), "sendOnSuccess"), "sendOnSuccess", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "sendOnWarning", null, parsed)) {
        notifier.setSendOnWarning(getBooleanValue(interpolatedTrimmed(parser.nextText(), "sendOnWarning"), "sendOnWarning", parser, "true"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "address", null, parsed)) {
        notifier.setAddress(interpolatedTrimmed(parser.nextText(), "address"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          notifier.addConfiguration(key, value);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return notifier;
  }
  
  private Organization parseOrganization(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Organization organization = new Organization();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        organization.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        organization.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return organization;
  }
  
  private Parent parseParent(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Parent parent = new Parent();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        parent.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        parent.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        parent.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "relativePath", null, parsed)) {
        parent.setRelativePath(interpolatedTrimmed(parser.nextText(), "relativePath"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return parent;
  }
  
  private PatternSet parsePatternSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    PatternSet patternSet = new PatternSet();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
        List<String> includes = new ArrayList<>();
        patternSet.setIncludes(includes);
        while (parser.nextTag() == 2) {
          if ("include".equals(parser.getName())) {
            includes.add(interpolatedTrimmed(parser.nextText(), "includes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
        List<String> excludes = new ArrayList<>();
        patternSet.setExcludes(excludes);
        while (parser.nextTag() == 2) {
          if ("exclude".equals(parser.getName())) {
            excludes.add(interpolatedTrimmed(parser.nextText(), "excludes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return patternSet;
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
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        plugin.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        plugin.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        plugin.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "extensions", null, parsed)) {
        plugin.setExtensions(interpolatedTrimmed(parser.nextText(), "extensions"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "executions", null, parsed)) {
        List<PluginExecution> executions = new ArrayList<>();
        plugin.setExecutions(executions);
        while (parser.nextTag() == 2) {
          if ("execution".equals(parser.getName())) {
            executions.add(parsePluginExecution(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
        List<Dependency> dependencies = new ArrayList<>();
        plugin.setDependencies(dependencies);
        while (parser.nextTag() == 2) {
          if ("dependency".equals(parser.getName())) {
            dependencies.add(parseDependency(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
        plugin.setGoals(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
        plugin.setInherited(interpolatedTrimmed(parser.nextText(), "inherited"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        plugin.setConfiguration(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return plugin;
  }
  
  private PluginConfiguration parsePluginConfiguration(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    PluginConfiguration pluginConfiguration = new PluginConfiguration();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "pluginManagement", null, parsed)) {
        pluginConfiguration.setPluginManagement(parsePluginManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        pluginConfiguration.setPlugins(plugins);
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
    return pluginConfiguration;
  }
  
  private PluginContainer parsePluginContainer(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    PluginContainer pluginContainer = new PluginContainer();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        pluginContainer.setPlugins(plugins);
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
    return pluginContainer;
  }
  
  private PluginExecution parsePluginExecution(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    PluginExecution pluginExecution = new PluginExecution();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        pluginExecution.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "phase", null, parsed)) {
        pluginExecution.setPhase(interpolatedTrimmed(parser.nextText(), "phase"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "goals", null, parsed)) {
        List<String> goals = new ArrayList<>();
        pluginExecution.setGoals(goals);
        while (parser.nextTag() == 2) {
          if ("goal".equals(parser.getName())) {
            goals.add(interpolatedTrimmed(parser.nextText(), "goals"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
        pluginExecution.setInherited(interpolatedTrimmed(parser.nextText(), "inherited"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        pluginExecution.setConfiguration(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return pluginExecution;
  }
  
  private PluginManagement parsePluginManagement(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    PluginManagement pluginManagement = new PluginManagement();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<Plugin> plugins = new ArrayList<>();
        pluginManagement.setPlugins(plugins);
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
    return pluginManagement;
  }
  
  private Prerequisites parsePrerequisites(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Prerequisites prerequisites = new Prerequisites();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "maven", null, parsed)) {
        prerequisites.setMaven(interpolatedTrimmed(parser.nextText(), "maven"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return prerequisites;
  }
  
  private Profile parseProfile(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Profile profile = new Profile();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        profile.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "activation", null, parsed)) {
        profile.setActivation(parseActivation(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "build", null, parsed)) {
        profile.setBuild(parseBuildBase(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "modules", null, parsed)) {
        List<String> modules = new ArrayList<>();
        profile.setModules(modules);
        while (parser.nextTag() == 2) {
          if ("module".equals(parser.getName())) {
            modules.add(interpolatedTrimmed(parser.nextText(), "modules"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "distributionManagement", null, parsed)) {
        profile.setDistributionManagement(parseDistributionManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "properties", null, parsed)) {
        while (parser.nextTag() == 2) {
          String key = parser.getName();
          String value = parser.nextText().trim();
          profile.addProperty(key, value);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencyManagement", null, parsed)) {
        profile.setDependencyManagement(parseDependencyManagement(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "dependencies", null, parsed)) {
        List<Dependency> dependencies = new ArrayList<>();
        profile.setDependencies(dependencies);
        while (parser.nextTag() == 2) {
          if ("dependency".equals(parser.getName())) {
            dependencies.add(parseDependency(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "repositories", null, parsed)) {
        List<Repository> repositories = new ArrayList<>();
        profile.setRepositories(repositories);
        while (parser.nextTag() == 2) {
          if ("repository".equals(parser.getName())) {
            repositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "pluginRepositories", null, parsed)) {
        List<Repository> pluginRepositories = new ArrayList<>();
        profile.setPluginRepositories(pluginRepositories);
        while (parser.nextTag() == 2) {
          if ("pluginRepository".equals(parser.getName())) {
            pluginRepositories.add(parseRepository(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
        profile.setReports(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reporting", null, parsed)) {
        profile.setReporting(parseReporting(parser, strict));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return profile;
  }
  
  private Relocation parseRelocation(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Relocation relocation = new Relocation();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        relocation.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        relocation.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        relocation.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "message", null, parsed)) {
        relocation.setMessage(interpolatedTrimmed(parser.nextText(), "message"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return relocation;
  }
  
  private ReportPlugin parseReportPlugin(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ReportPlugin reportPlugin = new ReportPlugin();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "groupId", null, parsed)) {
        reportPlugin.setGroupId(interpolatedTrimmed(parser.nextText(), "groupId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "artifactId", null, parsed)) {
        reportPlugin.setArtifactId(interpolatedTrimmed(parser.nextText(), "artifactId"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "version", null, parsed)) {
        reportPlugin.setVersion(interpolatedTrimmed(parser.nextText(), "version"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reportSets", null, parsed)) {
        List<ReportSet> reportSets = new ArrayList<>();
        reportPlugin.setReportSets(reportSets);
        while (parser.nextTag() == 2) {
          if ("reportSet".equals(parser.getName())) {
            reportSets.add(parseReportSet(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
        reportPlugin.setInherited(interpolatedTrimmed(parser.nextText(), "inherited"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        reportPlugin.setConfiguration(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return reportPlugin;
  }
  
  private ReportSet parseReportSet(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    ReportSet reportSet = new ReportSet();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        reportSet.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "reports", null, parsed)) {
        List<String> reports = new ArrayList<>();
        reportSet.setReports(reports);
        while (parser.nextTag() == 2) {
          if ("report".equals(parser.getName())) {
            reports.add(interpolatedTrimmed(parser.nextText(), "reports"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "inherited", null, parsed)) {
        reportSet.setInherited(interpolatedTrimmed(parser.nextText(), "inherited"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "configuration", null, parsed)) {
        reportSet.setConfiguration(Xpp3DomBuilder.build(parser, true));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return reportSet;
  }
  
  private Reporting parseReporting(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Reporting reporting = new Reporting();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "excludeDefaults", null, parsed)) {
        reporting.setExcludeDefaults(interpolatedTrimmed(parser.nextText(), "excludeDefaults"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "outputDirectory", null, parsed)) {
        reporting.setOutputDirectory(interpolatedTrimmed(parser.nextText(), "outputDirectory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "plugins", null, parsed)) {
        List<ReportPlugin> plugins = new ArrayList<>();
        reporting.setPlugins(plugins);
        while (parser.nextTag() == 2) {
          if ("plugin".equals(parser.getName())) {
            plugins.add(parseReportPlugin(parser, strict));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return reporting;
  }
  
  private Repository parseRepository(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Repository repository = new Repository();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "releases", null, parsed)) {
        repository.setReleases(parseRepositoryPolicy(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "snapshots", null, parsed)) {
        repository.setSnapshots(parseRepositoryPolicy(parser, strict));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        repository.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        repository.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        repository.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
        repository.setLayout(interpolatedTrimmed(parser.nextText(), "layout"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return repository;
  }
  
  private RepositoryBase parseRepositoryBase(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    RepositoryBase repositoryBase = new RepositoryBase();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        repositoryBase.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        repositoryBase.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        repositoryBase.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "layout", null, parsed)) {
        repositoryBase.setLayout(interpolatedTrimmed(parser.nextText(), "layout"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return repositoryBase;
  }
  
  private RepositoryPolicy parseRepositoryPolicy(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    RepositoryPolicy repositoryPolicy = new RepositoryPolicy();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "enabled", null, parsed)) {
        repositoryPolicy.setEnabled(interpolatedTrimmed(parser.nextText(), "enabled"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "updatePolicy", null, parsed)) {
        repositoryPolicy.setUpdatePolicy(interpolatedTrimmed(parser.nextText(), "updatePolicy"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "checksumPolicy", null, parsed)) {
        repositoryPolicy.setChecksumPolicy(interpolatedTrimmed(parser.nextText(), "checksumPolicy"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return repositoryPolicy;
  }
  
  private Resource parseResource(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Resource resource = new Resource();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        checkUnknownAttribute(parser, name, tagName, strict); 
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "targetPath", null, parsed)) {
        resource.setTargetPath(interpolatedTrimmed(parser.nextText(), "targetPath"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "filtering", null, parsed)) {
        resource.setFiltering(interpolatedTrimmed(parser.nextText(), "filtering"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "directory", null, parsed)) {
        resource.setDirectory(interpolatedTrimmed(parser.nextText(), "directory"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "includes", null, parsed)) {
        List<String> includes = new ArrayList<>();
        resource.setIncludes(includes);
        while (parser.nextTag() == 2) {
          if ("include".equals(parser.getName())) {
            includes.add(interpolatedTrimmed(parser.nextText(), "includes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "excludes", null, parsed)) {
        List<String> excludes = new ArrayList<>();
        resource.setExcludes(excludes);
        while (parser.nextTag() == 2) {
          if ("exclude".equals(parser.getName())) {
            excludes.add(interpolatedTrimmed(parser.nextText(), "excludes"));
            continue;
          } 
          checkUnknownElement(parser, strict);
        } 
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return resource;
  }
  
  private Scm parseScm(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Scm scm = new Scm();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        if ("child.scm.connection.inherit.append.path".equals(name)) {
          scm.setChildScmConnectionInheritAppendPath(interpolatedTrimmed(value, "child.scm.connection.inherit.append.path"));
        } else if ("child.scm.developerConnection.inherit.append.path".equals(name)) {
          scm.setChildScmDeveloperConnectionInheritAppendPath(interpolatedTrimmed(value, "child.scm.developerConnection.inherit.append.path"));
        } else if ("child.scm.url.inherit.append.path".equals(name)) {
          scm.setChildScmUrlInheritAppendPath(interpolatedTrimmed(value, "child.scm.url.inherit.append.path"));
        } else {
          checkUnknownAttribute(parser, name, tagName, strict);
        }  
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "connection", null, parsed)) {
        scm.setConnection(interpolatedTrimmed(parser.nextText(), "connection"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "developerConnection", null, parsed)) {
        scm.setDeveloperConnection(interpolatedTrimmed(parser.nextText(), "developerConnection"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "tag", null, parsed)) {
        scm.setTag(interpolatedTrimmed(parser.nextText(), "tag"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        scm.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return scm;
  }
  
  private Site parseSite(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    String tagName = parser.getName();
    Site site = new Site();
    for (int i = parser.getAttributeCount() - 1; i >= 0; i--) {
      String name = parser.getAttributeName(i);
      String value = parser.getAttributeValue(i);
      if (name.indexOf(':') < 0)
        if ("child.site.url.inherit.append.path".equals(name)) {
          site.setChildSiteUrlInheritAppendPath(interpolatedTrimmed(value, "child.site.url.inherit.append.path"));
        } else {
          checkUnknownAttribute(parser, name, tagName, strict);
        }  
    } 
    Set parsed = new HashSet();
    while ((strict ? parser.nextTag() : nextTag(parser)) == 2) {
      if (checkFieldWithDuplicate(parser, "id", null, parsed)) {
        site.setId(interpolatedTrimmed(parser.nextText(), "id"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "name", null, parsed)) {
        site.setName(interpolatedTrimmed(parser.nextText(), "name"));
        continue;
      } 
      if (checkFieldWithDuplicate(parser, "url", null, parsed)) {
        site.setUrl(interpolatedTrimmed(parser.nextText(), "url"));
        continue;
      } 
      checkUnknownElement(parser, strict);
    } 
    return site;
  }
  
  private Model read(XmlPullParser parser, boolean strict) throws IOException, XmlPullParserException {
    Model model = null;
    int eventType = parser.getEventType();
    boolean parsed = false;
    while (eventType != 1) {
      if (eventType == 2) {
        if (strict && !"project".equals(parser.getName()))
          throw new XmlPullParserException("Expected root element 'project' but found '" + parser.getName() + "'", parser, null); 
        if (parsed)
          throw new XmlPullParserException("Duplicated tag: 'project'", parser, null); 
        model = parseModel(parser, strict);
        model.setModelEncoding(parser.getInputEncoding());
        parsed = true;
      } 
      eventType = parser.next();
    } 
    if (parsed)
      return model; 
    throw new XmlPullParserException("Expected root element 'project' but found no element at all: invalid XML document", parser, null);
  }
  
  public void setAddDefaultEntities(boolean addDefaultEntities) {
    this.addDefaultEntities = addDefaultEntities;
  }
}
