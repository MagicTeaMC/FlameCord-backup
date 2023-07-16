package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Xpp3DomBuilder {
  private static final boolean DEFAULT_TRIM = true;
  
  public static Xpp3Dom build(Reader reader) throws XmlPullParserException, IOException {
    return build(reader, (InputLocationBuilder)null);
  }
  
  public static Xpp3Dom build(Reader reader, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
    return build(reader, true, locationBuilder);
  }
  
  public static Xpp3Dom build(InputStream is, String encoding) throws XmlPullParserException, IOException {
    return build(is, encoding, true);
  }
  
  public static Xpp3Dom build(InputStream is, String encoding, boolean trim) throws XmlPullParserException, IOException {
    try {
      MXParser mXParser = new MXParser();
      mXParser.setInput(is, encoding);
      Xpp3Dom xpp3Dom = build((XmlPullParser)mXParser, trim);
      is.close();
      is = null;
      return xpp3Dom;
    } finally {
      IOUtil.close(is);
    } 
  }
  
  public static Xpp3Dom build(Reader reader, boolean trim) throws XmlPullParserException, IOException {
    return build(reader, trim, (InputLocationBuilder)null);
  }
  
  public static Xpp3Dom build(Reader reader, boolean trim, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
    try {
      MXParser mXParser = new MXParser();
      mXParser.setInput(reader);
      Xpp3Dom xpp3Dom = build((XmlPullParser)mXParser, trim, locationBuilder);
      reader.close();
      reader = null;
      return xpp3Dom;
    } finally {
      IOUtil.close(reader);
    } 
  }
  
  public static Xpp3Dom build(XmlPullParser parser) throws XmlPullParserException, IOException {
    return build(parser, true);
  }
  
  public static Xpp3Dom build(XmlPullParser parser, boolean trim) throws XmlPullParserException, IOException {
    return build(parser, trim, (InputLocationBuilder)null);
  }
  
  public static Xpp3Dom build(XmlPullParser parser, boolean trim, InputLocationBuilder locationBuilder) throws XmlPullParserException, IOException {
    List<Xpp3Dom> elements = new ArrayList<Xpp3Dom>();
    List<StringBuilder> values = new ArrayList<StringBuilder>();
    int eventType = parser.getEventType();
    boolean spacePreserve = false;
    while (eventType != 1) {
      if (eventType == 2) {
        spacePreserve = false;
        String rawName = parser.getName();
        Xpp3Dom childConfiguration = new Xpp3Dom(rawName);
        if (locationBuilder != null)
          childConfiguration.setInputLocation(locationBuilder.toInputLocation(parser)); 
        int depth = elements.size();
        if (depth > 0) {
          Xpp3Dom parent = elements.get(depth - 1);
          parent.addChild(childConfiguration);
        } 
        elements.add(childConfiguration);
        if (parser.isEmptyElementTag()) {
          values.add(null);
        } else {
          values.add(new StringBuilder());
        } 
        int attributesSize = parser.getAttributeCount();
        for (int i = 0; i < attributesSize; i++) {
          String name = parser.getAttributeName(i);
          String value = parser.getAttributeValue(i);
          childConfiguration.setAttribute(name, value);
          spacePreserve = (spacePreserve || ("xml:space".equals(name) && "preserve".equals(value)));
        } 
      } else if (eventType == 4) {
        int depth = values.size() - 1;
        StringBuilder valueBuffer = values.get(depth);
        String text = parser.getText();
        if (trim && !spacePreserve)
          text = text.trim(); 
        valueBuffer.append(text);
      } else if (eventType == 3) {
        int depth = elements.size() - 1;
        Xpp3Dom finishedConfiguration = elements.remove(depth);
        Object accumulatedValue = values.remove(depth);
        if (finishedConfiguration.getChildCount() == 0)
          if (accumulatedValue == null) {
            finishedConfiguration.setValue(null);
          } else {
            finishedConfiguration.setValue(accumulatedValue.toString());
          }  
        if (depth == 0)
          return finishedConfiguration; 
      } 
      eventType = parser.next();
    } 
    throw new IllegalStateException("End of document found before returning to 0 depth");
  }
  
  public static interface InputLocationBuilder {
    Object toInputLocation(XmlPullParser param1XmlPullParser);
  }
}
