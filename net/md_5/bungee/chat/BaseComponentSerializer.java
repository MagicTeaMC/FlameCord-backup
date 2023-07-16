package net.md_5.bungee.chat;

import com.google.common.base.Preconditions;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;

public class BaseComponentSerializer {
  protected void deserialize(JsonObject object, BaseComponent component, JsonDeserializationContext context) {
    // Byte code:
    //   0: aload_1
    //   1: ldc 'bold'
    //   3: invokevirtual has : (Ljava/lang/String;)Z
    //   6: ifeq -> 25
    //   9: aload_2
    //   10: aload_1
    //   11: ldc 'bold'
    //   13: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   16: invokevirtual getAsBoolean : ()Z
    //   19: invokestatic valueOf : (Z)Ljava/lang/Boolean;
    //   22: invokevirtual setBold : (Ljava/lang/Boolean;)V
    //   25: aload_1
    //   26: ldc 'italic'
    //   28: invokevirtual has : (Ljava/lang/String;)Z
    //   31: ifeq -> 50
    //   34: aload_2
    //   35: aload_1
    //   36: ldc 'italic'
    //   38: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   41: invokevirtual getAsBoolean : ()Z
    //   44: invokestatic valueOf : (Z)Ljava/lang/Boolean;
    //   47: invokevirtual setItalic : (Ljava/lang/Boolean;)V
    //   50: aload_1
    //   51: ldc 'underlined'
    //   53: invokevirtual has : (Ljava/lang/String;)Z
    //   56: ifeq -> 75
    //   59: aload_2
    //   60: aload_1
    //   61: ldc 'underlined'
    //   63: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   66: invokevirtual getAsBoolean : ()Z
    //   69: invokestatic valueOf : (Z)Ljava/lang/Boolean;
    //   72: invokevirtual setUnderlined : (Ljava/lang/Boolean;)V
    //   75: aload_1
    //   76: ldc 'strikethrough'
    //   78: invokevirtual has : (Ljava/lang/String;)Z
    //   81: ifeq -> 100
    //   84: aload_2
    //   85: aload_1
    //   86: ldc 'strikethrough'
    //   88: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   91: invokevirtual getAsBoolean : ()Z
    //   94: invokestatic valueOf : (Z)Ljava/lang/Boolean;
    //   97: invokevirtual setStrikethrough : (Ljava/lang/Boolean;)V
    //   100: aload_1
    //   101: ldc 'obfuscated'
    //   103: invokevirtual has : (Ljava/lang/String;)Z
    //   106: ifeq -> 125
    //   109: aload_2
    //   110: aload_1
    //   111: ldc 'obfuscated'
    //   113: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   116: invokevirtual getAsBoolean : ()Z
    //   119: invokestatic valueOf : (Z)Ljava/lang/Boolean;
    //   122: invokevirtual setObfuscated : (Ljava/lang/Boolean;)V
    //   125: aload_1
    //   126: ldc 'color'
    //   128: invokevirtual has : (Ljava/lang/String;)Z
    //   131: ifeq -> 150
    //   134: aload_2
    //   135: aload_1
    //   136: ldc 'color'
    //   138: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   141: invokevirtual getAsString : ()Ljava/lang/String;
    //   144: invokestatic of : (Ljava/lang/String;)Lnet/md_5/bungee/api/ChatColor;
    //   147: invokevirtual setColor : (Lnet/md_5/bungee/api/ChatColor;)V
    //   150: aload_1
    //   151: ldc 'insertion'
    //   153: invokevirtual has : (Ljava/lang/String;)Z
    //   156: ifeq -> 172
    //   159: aload_2
    //   160: aload_1
    //   161: ldc 'insertion'
    //   163: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   166: invokevirtual getAsString : ()Ljava/lang/String;
    //   169: invokevirtual setInsertion : (Ljava/lang/String;)V
    //   172: aload_1
    //   173: ldc 'clickEvent'
    //   175: invokevirtual has : (Ljava/lang/String;)Z
    //   178: ifeq -> 244
    //   181: aload_1
    //   182: ldc 'clickEvent'
    //   184: invokevirtual getAsJsonObject : (Ljava/lang/String;)Lcom/google/gson/JsonObject;
    //   187: astore #4
    //   189: aload_2
    //   190: new net/md_5/bungee/api/chat/ClickEvent
    //   193: dup
    //   194: aload #4
    //   196: ldc 'action'
    //   198: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   201: invokevirtual getAsString : ()Ljava/lang/String;
    //   204: getstatic java/util/Locale.ROOT : Ljava/util/Locale;
    //   207: invokevirtual toUpperCase : (Ljava/util/Locale;)Ljava/lang/String;
    //   210: invokestatic valueOf : (Ljava/lang/String;)Lnet/md_5/bungee/api/chat/ClickEvent$Action;
    //   213: aload #4
    //   215: ldc 'value'
    //   217: invokevirtual has : (Ljava/lang/String;)Z
    //   220: ifeq -> 236
    //   223: aload #4
    //   225: ldc 'value'
    //   227: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   230: invokevirtual getAsString : ()Ljava/lang/String;
    //   233: goto -> 238
    //   236: ldc ''
    //   238: invokespecial <init> : (Lnet/md_5/bungee/api/chat/ClickEvent$Action;Ljava/lang/String;)V
    //   241: invokevirtual setClickEvent : (Lnet/md_5/bungee/api/chat/ClickEvent;)V
    //   244: aload_1
    //   245: ldc 'hoverEvent'
    //   247: invokevirtual has : (Ljava/lang/String;)Z
    //   250: ifeq -> 512
    //   253: aload_1
    //   254: ldc 'hoverEvent'
    //   256: invokevirtual getAsJsonObject : (Ljava/lang/String;)Lcom/google/gson/JsonObject;
    //   259: astore #4
    //   261: aconst_null
    //   262: astore #5
    //   264: aload #4
    //   266: ldc 'action'
    //   268: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   271: invokevirtual getAsString : ()Ljava/lang/String;
    //   274: getstatic java/util/Locale.ROOT : Ljava/util/Locale;
    //   277: invokevirtual toUpperCase : (Ljava/util/Locale;)Ljava/lang/String;
    //   280: invokestatic valueOf : (Ljava/lang/String;)Lnet/md_5/bungee/api/chat/HoverEvent$Action;
    //   283: astore #6
    //   285: iconst_2
    //   286: anewarray java/lang/String
    //   289: dup
    //   290: iconst_0
    //   291: ldc 'value'
    //   293: aastore
    //   294: dup
    //   295: iconst_1
    //   296: ldc 'contents'
    //   298: aastore
    //   299: invokestatic asList : ([Ljava/lang/Object;)Ljava/util/List;
    //   302: invokeinterface iterator : ()Ljava/util/Iterator;
    //   307: astore #7
    //   309: aload #7
    //   311: invokeinterface hasNext : ()Z
    //   316: ifeq -> 501
    //   319: aload #7
    //   321: invokeinterface next : ()Ljava/lang/Object;
    //   326: checkcast java/lang/String
    //   329: astore #8
    //   331: aload #4
    //   333: aload #8
    //   335: invokevirtual has : (Ljava/lang/String;)Z
    //   338: ifne -> 344
    //   341: goto -> 309
    //   344: aload #4
    //   346: aload #8
    //   348: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   351: astore #9
    //   353: aload #9
    //   355: invokevirtual isJsonArray : ()Z
    //   358: ifeq -> 379
    //   361: aload_3
    //   362: aload #9
    //   364: ldc [Lnet/md_5/bungee/api/chat/BaseComponent;
    //   366: invokeinterface deserialize : (Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;)Ljava/lang/Object;
    //   371: checkcast [Lnet/md_5/bungee/api/chat/BaseComponent;
    //   374: astore #10
    //   376: goto -> 401
    //   379: iconst_1
    //   380: anewarray net/md_5/bungee/api/chat/BaseComponent
    //   383: dup
    //   384: iconst_0
    //   385: aload_3
    //   386: aload #9
    //   388: ldc net/md_5/bungee/api/chat/BaseComponent
    //   390: invokeinterface deserialize : (Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;)Ljava/lang/Object;
    //   395: checkcast net/md_5/bungee/api/chat/BaseComponent
    //   398: aastore
    //   399: astore #10
    //   401: new net/md_5/bungee/api/chat/HoverEvent
    //   404: dup
    //   405: aload #6
    //   407: aload #10
    //   409: invokespecial <init> : (Lnet/md_5/bungee/api/chat/HoverEvent$Action;[Lnet/md_5/bungee/api/chat/BaseComponent;)V
    //   412: astore #5
    //   414: goto -> 501
    //   417: astore #10
    //   419: aload #9
    //   421: invokevirtual isJsonArray : ()Z
    //   424: ifeq -> 449
    //   427: aload_3
    //   428: aload #9
    //   430: aload #6
    //   432: iconst_1
    //   433: invokestatic getClass : (Lnet/md_5/bungee/api/chat/HoverEvent$Action;Z)Ljava/lang/Class;
    //   436: invokeinterface deserialize : (Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;)Ljava/lang/Object;
    //   441: checkcast [Lnet/md_5/bungee/api/chat/hover/content/Content;
    //   444: astore #11
    //   446: goto -> 475
    //   449: iconst_1
    //   450: anewarray net/md_5/bungee/api/chat/hover/content/Content
    //   453: dup
    //   454: iconst_0
    //   455: aload_3
    //   456: aload #9
    //   458: aload #6
    //   460: iconst_0
    //   461: invokestatic getClass : (Lnet/md_5/bungee/api/chat/HoverEvent$Action;Z)Ljava/lang/Class;
    //   464: invokeinterface deserialize : (Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;)Ljava/lang/Object;
    //   469: checkcast net/md_5/bungee/api/chat/hover/content/Content
    //   472: aastore
    //   473: astore #11
    //   475: new net/md_5/bungee/api/chat/HoverEvent
    //   478: dup
    //   479: aload #6
    //   481: new java/util/ArrayList
    //   484: dup
    //   485: aload #11
    //   487: invokestatic asList : ([Ljava/lang/Object;)Ljava/util/List;
    //   490: invokespecial <init> : (Ljava/util/Collection;)V
    //   493: invokespecial <init> : (Lnet/md_5/bungee/api/chat/HoverEvent$Action;Ljava/util/List;)V
    //   496: astore #5
    //   498: goto -> 501
    //   501: aload #5
    //   503: ifnull -> 512
    //   506: aload_2
    //   507: aload #5
    //   509: invokevirtual setHoverEvent : (Lnet/md_5/bungee/api/chat/HoverEvent;)V
    //   512: aload_1
    //   513: ldc 'font'
    //   515: invokevirtual has : (Ljava/lang/String;)Z
    //   518: ifeq -> 534
    //   521: aload_2
    //   522: aload_1
    //   523: ldc 'font'
    //   525: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   528: invokevirtual getAsString : ()Ljava/lang/String;
    //   531: invokevirtual setFont : (Ljava/lang/String;)V
    //   534: aload_1
    //   535: ldc 'extra'
    //   537: invokevirtual has : (Ljava/lang/String;)Z
    //   540: ifeq -> 567
    //   543: aload_2
    //   544: aload_3
    //   545: aload_1
    //   546: ldc 'extra'
    //   548: invokevirtual get : (Ljava/lang/String;)Lcom/google/gson/JsonElement;
    //   551: ldc [Lnet/md_5/bungee/api/chat/BaseComponent;
    //   553: invokeinterface deserialize : (Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;)Ljava/lang/Object;
    //   558: checkcast [Ljava/lang/Object;
    //   561: invokestatic asList : ([Ljava/lang/Object;)Ljava/util/List;
    //   564: invokevirtual setExtra : (Ljava/util/List;)V
    //   567: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #25	-> 0
    //   #27	-> 9
    //   #29	-> 25
    //   #31	-> 34
    //   #33	-> 50
    //   #35	-> 59
    //   #37	-> 75
    //   #39	-> 84
    //   #41	-> 100
    //   #43	-> 109
    //   #45	-> 125
    //   #47	-> 134
    //   #49	-> 150
    //   #51	-> 159
    //   #55	-> 172
    //   #57	-> 181
    //   #58	-> 189
    //   #59	-> 198
    //   #60	-> 217
    //   #58	-> 241
    //   #62	-> 244
    //   #64	-> 253
    //   #65	-> 261
    //   #66	-> 264
    //   #68	-> 285
    //   #70	-> 331
    //   #72	-> 341
    //   #74	-> 344
    //   #81	-> 353
    //   #83	-> 361
    //   #86	-> 379
    //   #88	-> 390
    //   #91	-> 401
    //   #106	-> 414
    //   #92	-> 417
    //   #95	-> 419
    //   #97	-> 427
    //   #100	-> 449
    //   #102	-> 461
    //   #105	-> 475
    //   #109	-> 498
    //   #111	-> 501
    //   #113	-> 506
    //   #117	-> 512
    //   #119	-> 521
    //   #121	-> 534
    //   #123	-> 543
    //   #125	-> 567
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   189	55	4	event	Lcom/google/gson/JsonObject;
    //   376	3	10	components	[Lnet/md_5/bungee/api/chat/BaseComponent;
    //   401	13	10	components	[Lnet/md_5/bungee/api/chat/BaseComponent;
    //   446	3	11	list	[Lnet/md_5/bungee/api/chat/hover/content/Content;
    //   475	23	11	list	[Lnet/md_5/bungee/api/chat/hover/content/Content;
    //   419	79	10	ex	Lcom/google/gson/JsonParseException;
    //   353	148	9	contents	Lcom/google/gson/JsonElement;
    //   331	170	8	type	Ljava/lang/String;
    //   261	251	4	event	Lcom/google/gson/JsonObject;
    //   264	248	5	hoverEvent	Lnet/md_5/bungee/api/chat/HoverEvent;
    //   285	227	6	action	Lnet/md_5/bungee/api/chat/HoverEvent$Action;
    //   0	568	0	this	Lnet/md_5/bungee/chat/BaseComponentSerializer;
    //   0	568	1	object	Lcom/google/gson/JsonObject;
    //   0	568	2	component	Lnet/md_5/bungee/api/chat/BaseComponent;
    //   0	568	3	context	Lcom/google/gson/JsonDeserializationContext;
    // Exception table:
    //   from	to	target	type
    //   353	414	417	com/google/gson/JsonParseException
  }
  
  protected void serialize(JsonObject object, BaseComponent component, JsonSerializationContext context) {
    boolean first = false;
    if (ComponentSerializer.serializedComponents.get() == null) {
      first = true;
      ComponentSerializer.serializedComponents.set(Collections.newSetFromMap(new IdentityHashMap<>()));
    } 
    try {
      Preconditions.checkArgument(!((Set)ComponentSerializer.serializedComponents.get()).contains(component), "Component loop");
      ((Set<BaseComponent>)ComponentSerializer.serializedComponents.get()).add(component);
      if (component.isBoldRaw() != null)
        object.addProperty("bold", component.isBoldRaw()); 
      if (component.isItalicRaw() != null)
        object.addProperty("italic", component.isItalicRaw()); 
      if (component.isUnderlinedRaw() != null)
        object.addProperty("underlined", component.isUnderlinedRaw()); 
      if (component.isStrikethroughRaw() != null)
        object.addProperty("strikethrough", component.isStrikethroughRaw()); 
      if (component.isObfuscatedRaw() != null)
        object.addProperty("obfuscated", component.isObfuscatedRaw()); 
      if (component.getColorRaw() != null)
        object.addProperty("color", component.getColorRaw().getName()); 
      if (component.getInsertion() != null)
        object.addProperty("insertion", component.getInsertion()); 
      if (component.getClickEvent() != null) {
        JsonObject clickEvent = new JsonObject();
        clickEvent.addProperty("action", component.getClickEvent().getAction().toString().toLowerCase(Locale.ROOT));
        clickEvent.addProperty("value", component.getClickEvent().getValue());
        object.add("clickEvent", (JsonElement)clickEvent);
      } 
      if (component.getHoverEvent() != null) {
        JsonObject hoverEvent = new JsonObject();
        hoverEvent.addProperty("action", component.getHoverEvent().getAction().toString().toLowerCase(Locale.ROOT));
        if (component.getHoverEvent().isLegacy()) {
          hoverEvent.add("value", context.serialize(component.getHoverEvent().getContents().get(0)));
        } else {
          hoverEvent.add("contents", context.serialize((component.getHoverEvent().getContents().size() == 1) ? component
                .getHoverEvent().getContents().get(0) : component.getHoverEvent().getContents()));
        } 
        object.add("hoverEvent", (JsonElement)hoverEvent);
      } 
      if (component.getFontRaw() != null)
        object.addProperty("font", component.getFontRaw()); 
      if (component.getExtra() != null)
        object.add("extra", context.serialize(component.getExtra())); 
    } finally {
      ((Set)ComponentSerializer.serializedComponents.get()).remove(component);
      if (first)
        ComponentSerializer.serializedComponents.set(null); 
    } 
  }
}
