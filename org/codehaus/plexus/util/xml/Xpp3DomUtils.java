package org.codehaus.plexus.util.xml;

import java.io.IOException;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class Xpp3DomUtils {
  public static final String CHILDREN_COMBINATION_MODE_ATTRIBUTE = "combine.children";
  
  public static final String CHILDREN_COMBINATION_MERGE = "merge";
  
  public static final String CHILDREN_COMBINATION_APPEND = "append";
  
  public static final String DEFAULT_CHILDREN_COMBINATION_MODE = "merge";
  
  public static final String SELF_COMBINATION_MODE_ATTRIBUTE = "combine.self";
  
  public static final String SELF_COMBINATION_OVERRIDE = "override";
  
  public static final String SELF_COMBINATION_MERGE = "merge";
  
  public static final String ID_COMBINATION_MODE_ATTRIBUTE = "combine.id";
  
  public static final String DEFAULT_SELF_COMBINATION_MODE = "merge";
  
  public void writeToSerializer(String namespace, XmlSerializer serializer, Xpp3Dom dom) throws IOException {
    SerializerXMLWriter xmlWriter = new SerializerXMLWriter(namespace, serializer);
    Xpp3DomWriter.write(xmlWriter, dom);
    if (xmlWriter.getExceptions().size() > 0)
      throw (IOException)xmlWriter.getExceptions().get(0); 
  }
  
  private static void mergeIntoXpp3Dom(Xpp3Dom dominant, Xpp3Dom recessive, Boolean childMergeOverride) {
    if (recessive == null)
      return; 
    boolean mergeSelf = true;
    String selfMergeMode = dominant.getAttribute("combine.self");
    if (isNotEmpty(selfMergeMode) && "override".equals(selfMergeMode))
      mergeSelf = false; 
    if (mergeSelf) {
      if (isEmpty(dominant.getValue()) && !isEmpty(recessive.getValue())) {
        dominant.setValue(recessive.getValue());
        dominant.setInputLocation(recessive.getInputLocation());
      } 
      String[] recessiveAttrs = recessive.getAttributeNames();
      for (String attr : recessiveAttrs) {
        if (isEmpty(dominant.getAttribute(attr)))
          dominant.setAttribute(attr, recessive.getAttribute(attr)); 
      } 
      boolean mergeChildren = true;
      if (childMergeOverride != null) {
        mergeChildren = childMergeOverride.booleanValue();
      } else {
        String childMergeMode = dominant.getAttribute("combine.children");
        if (isNotEmpty(childMergeMode) && "append".equals(childMergeMode))
          mergeChildren = false; 
      } 
      Xpp3Dom[] children = recessive.getChildren();
      for (Xpp3Dom recessiveChild : children) {
        String idValue = recessiveChild.getAttribute("combine.id");
        Xpp3Dom childDom = null;
        if (isNotEmpty(idValue)) {
          for (Xpp3Dom dominantChild : dominant.getChildren()) {
            if (idValue.equals(dominantChild.getAttribute("combine.id"))) {
              childDom = dominantChild;
              mergeChildren = true;
            } 
          } 
        } else {
          childDom = dominant.getChild(recessiveChild.getName());
        } 
        if (mergeChildren && childDom != null) {
          mergeIntoXpp3Dom(childDom, recessiveChild, childMergeOverride);
        } else {
          dominant.addChild(new Xpp3Dom(recessiveChild));
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
  
  public static boolean isNotEmpty(String str) {
    return (str != null && str.length() > 0);
  }
  
  public static boolean isEmpty(String str) {
    return (str == null || str.trim().length() == 0);
  }
}
