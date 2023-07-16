package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3Dom implements Serializable {
  private static final long serialVersionUID = 2567894443061173996L;
  
  protected String name;
  
  protected String value;
  
  protected Map<String, String> attributes;
  
  protected final List<Xpp3Dom> childList;
  
  protected Xpp3Dom parent;
  
  protected Object inputLocation;
  
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  private static final Xpp3Dom[] EMPTY_DOM_ARRAY = new Xpp3Dom[0];
  
  public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
  
  public static final String CHILDREN_COMBINATION_MERGE = "merge";
  
  public static final String CHILDREN_COMBINATION_APPEND = "append";
  
  public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
  
  public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
  
  public static final String SELF_COMBINATION_OVERRIDE = "override";
  
  public static final String SELF_COMBINATION_MERGE = "merge";
  
  public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
  
  public Xpp3Dom(String name) {
    this.name = name;
    this.childList = new ArrayList<Xpp3Dom>();
  }
  
  public Xpp3Dom(String name, Object inputLocation) {
    this(name);
    this.inputLocation = inputLocation;
  }
  
  public Xpp3Dom(Xpp3Dom src) {
    this(src, src.getName());
  }
  
  public Xpp3Dom(Xpp3Dom src, String name) {
    this.name = name;
    this.inputLocation = src.inputLocation;
    int childCount = src.getChildCount();
    this.childList = new ArrayList<Xpp3Dom>(childCount);
    setValue(src.getValue());
    String[] attributeNames = src.getAttributeNames();
    for (String attributeName : attributeNames)
      setAttribute(attributeName, src.getAttribute(attributeName)); 
    for (int i = 0; i < childCount; i++)
      addChild(new Xpp3Dom(src.getChild(i))); 
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public String[] getAttributeNames() {
    if (null == this.attributes || this.attributes.isEmpty())
      return EMPTY_STRING_ARRAY; 
    return (String[])this.attributes.keySet().toArray((Object[])EMPTY_STRING_ARRAY);
  }
  
  public String getAttribute(String name) {
    return (null != this.attributes) ? this.attributes.get(name) : null;
  }
  
  public void setAttribute(String name, String value) {
    if (null == value)
      throw new NullPointerException("Attribute value can not be null"); 
    if (null == name)
      throw new NullPointerException("Attribute name can not be null"); 
    if (null == this.attributes)
      this.attributes = new HashMap<String, String>(); 
    this.attributes.put(name, value);
  }
  
  public Xpp3Dom getChild(int i) {
    return this.childList.get(i);
  }
  
  public Xpp3Dom getChild(String name) {
    if (name != null) {
      ListIterator<Xpp3Dom> it = this.childList.listIterator(this.childList.size());
      while (it.hasPrevious()) {
        Xpp3Dom child = it.previous();
        if (name.equals(child.getName()))
          return child; 
      } 
    } 
    return null;
  }
  
  public void addChild(Xpp3Dom xpp3Dom) {
    xpp3Dom.setParent(this);
    this.childList.add(xpp3Dom);
  }
  
  public Xpp3Dom[] getChildren() {
    if (null == this.childList || this.childList.isEmpty())
      return EMPTY_DOM_ARRAY; 
    return this.childList.<Xpp3Dom>toArray(EMPTY_DOM_ARRAY);
  }
  
  public Xpp3Dom[] getChildren(String name) {
    return getChildrenAsList(name).<Xpp3Dom>toArray(EMPTY_DOM_ARRAY);
  }
  
  private List<Xpp3Dom> getChildrenAsList(String name) {
    if (null == this.childList)
      return Collections.emptyList(); 
    ArrayList<Xpp3Dom> children = null;
    for (Xpp3Dom configuration : this.childList) {
      if (name.equals(configuration.getName())) {
        if (children == null)
          children = new ArrayList<Xpp3Dom>(); 
        children.add(configuration);
      } 
    } 
    if (children != null)
      return children; 
    return Collections.emptyList();
  }
  
  public int getChildCount() {
    if (null == this.childList)
      return 0; 
    return this.childList.size();
  }
  
  public void removeChild(int i) {
    Xpp3Dom child = getChild(i);
    this.childList.remove(i);
    child.setParent(null);
  }
  
  public Xpp3Dom getParent() {
    return this.parent;
  }
  
  public void setParent(Xpp3Dom parent) {
    this.parent = parent;
  }
  
  public Object getInputLocation() {
    return this.inputLocation;
  }
  
  public void setInputLocation(Object inputLocation) {
    this.inputLocation = inputLocation;
  }
  
  public void writeToSerializer(String namespace, XmlSerializer serializer) throws IOException {
    SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
    Xpp3DomWriter.write(xmlWriter, this);
    if (xmlWriter.getExceptions().size() > 0)
      throw (IOException)xmlWriter.getExceptions().get(0); 
  }
  
  private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
    if (recessive == null)
      return; 
    boolean mergeSelf = true;
    String selfMergeMode = dominant.getAttribute("combine.self");
    if ("override".equals(selfMergeMode))
      mergeSelf = false; 
    if (mergeSelf) {
      if (isEmpty(dominant.getValue()) && !isEmpty(recessive.getValue())) {
        dominant.setValue(recessive.getValue());
        dominant.setInputLocation(recessive.getInputLocation());
      } 
      if (recessive.attributes != null)
        for (String attr : recessive.attributes.keySet()) {
          if (isEmpty(dominant.getAttribute(attr)))
            dominant.setAttribute(attr, recessive.getAttribute(attr)); 
        }  
      if (recessive.getChildCount() > 0) {
        boolean mergeChildren = true;
        if (childMergeOverride != null) {
          mergeChildren = childMergeOverride.booleanValue();
        } else {
          String childMergeMode = dominant.getAttribute("combine.children");
          if ("append".equals(childMergeMode))
            mergeChildren = false; 
        } 
        if (!mergeChildren) {
          Xpp3Dom[] dominantChildren = dominant.getChildren();
          dominant.childList.clear();
          for (int i = 0, recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++) {
            Xpp3Dom recessiveChild = recessive.getChild(i);
            dominant.addChild(new Xpp3Dom(recessiveChild));
          } 
          for (Xpp3Dom aDominantChildren : dominantChildren)
            dominant.addChild(aDominantChildren); 
        } else {
          Map<String, Iterator<Xpp3Dom>> commonChildren = new HashMap<String, Iterator<Xpp3Dom>>();
          for (Xpp3Dom recChild : recessive.childList) {
            if (commonChildren.containsKey(recChild.name))
              continue; 
            List<Xpp3Dom> dominantChildren = dominant.getChildrenAsList(recChild.name);
            if (dominantChildren.size() > 0)
              commonChildren.put(recChild.name, dominantChildren.iterator()); 
          } 
          for (int i = 0, recessiveChildCount = recessive.getChildCount(); i < recessiveChildCount; i++) {
            Xpp3Dom recessiveChild = recessive.getChild(i);
            Iterator<Xpp3Dom> it = commonChildren.get(recessiveChild.getName());
            if (it == null) {
              dominant.addChild(new Xpp3Dom(recessiveChild));
            } else if (it.hasNext()) {
              Xpp3Dom dominantChild = it.next();
              mergeIntoXpp3Dom(dominantChild, recessiveChild, childMergeOverride);
            } 
          } 
        } 
      } 
    } 
  }
  
  public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
    if (dominant != null) {
      mergeIntoXpp3Dom(dominant, recessive, childMergeOverride);
      return dominant;
    } 
    return recessive;
  }
  
  public static Xpp3Dom mergeXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive) {
    if (dominant != null) {
      mergeIntoXpp3Dom(dominant, recessive, null);
      return dominant;
    } 
    return recessive;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Xpp3Dom))
      return false; 
    Xpp3Dom dom = (Xpp3Dom)obj;
    if ((this.name == null) ? (dom.name != null) : !this.name.equals(dom.name))
      return false; 
    if ((this.value == null) ? (dom.value != null) : !this.value.equals(dom.value))
      return false; 
    if ((this.attributes == null) ? (dom.attributes != null) : !this.attributes.equals(dom.attributes))
      return false; 
    if ((this.childList == null) ? (dom.childList != null) : !this.childList.equals(dom.childList))
      return false; 
    return true;
  }
  
  public int hashCode() {
    int result = 17;
    result = 37 * result + ((this.name != null) ? this.name.hashCode() : 0);
    result = 37 * result + ((this.value != null) ? this.value.hashCode() : 0);
    result = 37 * result + ((this.attributes != null) ? this.attributes.hashCode() : 0);
    result = 37 * result + ((this.childList != null) ? this.childList.hashCode() : 0);
    return result;
  }
  
  public String toString() {
    StringWriter writer = new StringWriter();
    XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
    Xpp3DomWriter.write(xmlWriter, this);
    return writer.toString();
  }
  
  public String toUnescapedString() {
    StringWriter writer = new StringWriter();
    XMLWriter xmlWriter = new PrettyPrintXMLWriter(writer, "UTF-8", null);
    Xpp3DomWriter.write(xmlWriter, this, false);
    return writer.toString();
  }
  
  public static boolean isNotEmpty(String str) {
    return (str != null && str.length() > 0);
  }
  
  public static boolean isEmpty(String str) {
    return (str == null || str.trim().length() == 0);
  }
}
