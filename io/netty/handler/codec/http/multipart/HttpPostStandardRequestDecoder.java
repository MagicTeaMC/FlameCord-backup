package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder {
  private final HttpDataFactory factory;
  
  private final HttpRequest request;
  
  private final Charset charset;
  
  private boolean isLastChunk;
  
  private final List<InterfaceHttpData> bodyListHttpData = new ArrayList<InterfaceHttpData>();
  
  private final Map<String, List<InterfaceHttpData>> bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
  
  private ByteBuf undecodedChunk;
  
  private int bodyListHttpDataRank;
  
  private HttpPostRequestDecoder.MultiPartStatus currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
  
  private Attribute currentAttribute;
  
  private boolean destroyed;
  
  private int discardThreshold = 10485760;
  
  public HttpPostStandardRequestDecoder(HttpRequest request) {
    this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
  }
  
  public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request) {
    this(factory, request, HttpConstants.DEFAULT_CHARSET);
  }
  
  public HttpPostStandardRequestDecoder(HttpDataFactory factory, HttpRequest request, Charset charset) {
    this.request = (HttpRequest)ObjectUtil.checkNotNull(request, "request");
    this.charset = (Charset)ObjectUtil.checkNotNull(charset, "charset");
    this.factory = (HttpDataFactory)ObjectUtil.checkNotNull(factory, "factory");
    try {
      if (request instanceof HttpContent) {
        offer((HttpContent)request);
      } else {
        parseBody();
      } 
    } catch (Throwable e) {
      destroy();
      PlatformDependent.throwException(e);
    } 
  }
  
  private void checkDestroyed() {
    if (this.destroyed)
      throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already"); 
  }
  
  public boolean isMultipart() {
    checkDestroyed();
    return false;
  }
  
  public void setDiscardThreshold(int discardThreshold) {
    this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
  }
  
  public int getDiscardThreshold() {
    return this.discardThreshold;
  }
  
  public List<InterfaceHttpData> getBodyHttpDatas() {
    checkDestroyed();
    if (!this.isLastChunk)
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(); 
    return this.bodyListHttpData;
  }
  
  public List<InterfaceHttpData> getBodyHttpDatas(String name) {
    checkDestroyed();
    if (!this.isLastChunk)
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(); 
    return this.bodyMapHttpData.get(name);
  }
  
  public InterfaceHttpData getBodyHttpData(String name) {
    checkDestroyed();
    if (!this.isLastChunk)
      throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(); 
    List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
    if (list != null)
      return list.get(0); 
    return null;
  }
  
  public HttpPostStandardRequestDecoder offer(HttpContent content) {
    checkDestroyed();
    if (content instanceof io.netty.handler.codec.http.LastHttpContent)
      this.isLastChunk = true; 
    ByteBuf buf = content.content();
    if (this.undecodedChunk == null) {
      this
        
        .undecodedChunk = buf.alloc().buffer(buf.readableBytes()).writeBytes(buf);
    } else {
      this.undecodedChunk.writeBytes(buf);
    } 
    parseBody();
    if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold)
      if (this.undecodedChunk.refCnt() == 1) {
        this.undecodedChunk.discardReadBytes();
      } else {
        ByteBuf buffer = this.undecodedChunk.alloc().buffer(this.undecodedChunk.readableBytes());
        buffer.writeBytes(this.undecodedChunk);
        this.undecodedChunk.release();
        this.undecodedChunk = buffer;
      }  
    return this;
  }
  
  public boolean hasNext() {
    checkDestroyed();
    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE)
      if (this.bodyListHttpDataRank >= this.bodyListHttpData.size())
        throw new HttpPostRequestDecoder.EndOfDataDecoderException();  
    return (!this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size());
  }
  
  public InterfaceHttpData next() {
    checkDestroyed();
    if (hasNext())
      return this.bodyListHttpData.get(this.bodyListHttpDataRank++); 
    return null;
  }
  
  public InterfaceHttpData currentPartialHttpData() {
    return this.currentAttribute;
  }
  
  private void parseBody() {
    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
      if (this.isLastChunk)
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE; 
      return;
    } 
    parseBodyAttributes();
  }
  
  protected void addHttpData(InterfaceHttpData data) {
    if (data == null)
      return; 
    List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
    if (datas == null) {
      datas = new ArrayList<InterfaceHttpData>(1);
      this.bodyMapHttpData.put(data.getName(), datas);
    } 
    datas.add(data);
    this.bodyListHttpData.add(data);
  }
  
  private void parseBodyAttributesStandard() {
    int firstpos = this.undecodedChunk.readerIndex();
    int currentpos = firstpos;
    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED)
      this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION; 
    boolean contRead = true;
    try {
      while (this.undecodedChunk.isReadable() && contRead) {
        char read = (char)this.undecodedChunk.readUnsignedByte();
        currentpos++;
        switch (this.currentStatus) {
          case DISPOSITION:
            if (read == '=') {
              this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
              int equalpos = currentpos - 1;
              String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
              this.currentAttribute = this.factory.createAttribute(this.request, key);
              firstpos = currentpos;
              continue;
            } 
            if (read == '&') {
              this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
              int ampersandpos = currentpos - 1;
              String key = decodeAttribute(this.undecodedChunk
                  .toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
              if (!key.isEmpty()) {
                this.currentAttribute = this.factory.createAttribute(this.request, key);
                this.currentAttribute.setValue("");
                addHttpData(this.currentAttribute);
              } 
              this.currentAttribute = null;
              firstpos = currentpos;
              contRead = true;
            } 
            continue;
          case FIELD:
            if (read == '&') {
              this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
              int ampersandpos = currentpos - 1;
              setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
              firstpos = currentpos;
              contRead = true;
              continue;
            } 
            if (read == '\r') {
              if (this.undecodedChunk.isReadable()) {
                read = (char)this.undecodedChunk.readUnsignedByte();
                currentpos++;
                if (read == '\n') {
                  this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                  int ampersandpos = currentpos - 2;
                  setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                  firstpos = currentpos;
                  contRead = false;
                  continue;
                } 
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
              } 
              currentpos--;
              continue;
            } 
            if (read == '\n') {
              this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
              int ampersandpos = currentpos - 1;
              setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
              firstpos = currentpos;
              contRead = false;
            } 
            continue;
        } 
        contRead = false;
      } 
      if (this.isLastChunk && this.currentAttribute != null) {
        int ampersandpos = currentpos;
        if (ampersandpos > firstpos) {
          setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
        } else if (!this.currentAttribute.isCompleted()) {
          setFinalBuffer(Unpooled.EMPTY_BUFFER);
        } 
        firstpos = currentpos;
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
      } else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
        this.currentAttribute.addContent(this.undecodedChunk.retainedSlice(firstpos, currentpos - firstpos), false);
        firstpos = currentpos;
      } 
      this.undecodedChunk.readerIndex(firstpos);
    } catch (ErrorDataDecoderException e) {
      this.undecodedChunk.readerIndex(firstpos);
      throw e;
    } catch (IOException e) {
      this.undecodedChunk.readerIndex(firstpos);
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
    } catch (IllegalArgumentException e) {
      this.undecodedChunk.readerIndex(firstpos);
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
    } 
  }
  
  private void parseBodyAttributes() {
    // Byte code:
    //   0: aload_0
    //   1: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   4: ifnonnull -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   12: invokevirtual hasArray : ()Z
    //   15: ifne -> 23
    //   18: aload_0
    //   19: invokespecial parseBodyAttributesStandard : ()V
    //   22: return
    //   23: new io/netty/handler/codec/http/multipart/HttpPostBodyUtil$SeekAheadOptimize
    //   26: dup
    //   27: aload_0
    //   28: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   31: invokespecial <init> : (Lio/netty/buffer/ByteBuf;)V
    //   34: astore_1
    //   35: aload_0
    //   36: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   39: invokevirtual readerIndex : ()I
    //   42: istore_2
    //   43: iload_2
    //   44: istore_3
    //   45: aload_0
    //   46: getfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   49: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.NOTSTARTED : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   52: if_acmpne -> 62
    //   55: aload_0
    //   56: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   59: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   62: iconst_1
    //   63: istore #6
    //   65: aload_1
    //   66: getfield pos : I
    //   69: aload_1
    //   70: getfield limit : I
    //   73: if_icmpge -> 536
    //   76: aload_1
    //   77: getfield bytes : [B
    //   80: aload_1
    //   81: dup
    //   82: getfield pos : I
    //   85: dup_x1
    //   86: iconst_1
    //   87: iadd
    //   88: putfield pos : I
    //   91: baload
    //   92: sipush #255
    //   95: iand
    //   96: i2c
    //   97: istore #7
    //   99: iinc #3, 1
    //   102: getstatic io/netty/handler/codec/http/multipart/HttpPostStandardRequestDecoder$1.$SwitchMap$io$netty$handler$codec$http$multipart$HttpPostRequestDecoder$MultiPartStatus : [I
    //   105: aload_0
    //   106: getfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   109: invokevirtual ordinal : ()I
    //   112: iaload
    //   113: lookupswitch default -> 522, 1 -> 140, 2 -> 311
    //   140: iload #7
    //   142: bipush #61
    //   144: if_icmpne -> 208
    //   147: aload_0
    //   148: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.FIELD : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   151: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   154: iload_3
    //   155: iconst_1
    //   156: isub
    //   157: istore #4
    //   159: aload_0
    //   160: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   163: iload_2
    //   164: iload #4
    //   166: iload_2
    //   167: isub
    //   168: aload_0
    //   169: getfield charset : Ljava/nio/charset/Charset;
    //   172: invokevirtual toString : (IILjava/nio/charset/Charset;)Ljava/lang/String;
    //   175: aload_0
    //   176: getfield charset : Ljava/nio/charset/Charset;
    //   179: invokestatic decodeAttribute : (Ljava/lang/String;Ljava/nio/charset/Charset;)Ljava/lang/String;
    //   182: astore #8
    //   184: aload_0
    //   185: aload_0
    //   186: getfield factory : Lio/netty/handler/codec/http/multipart/HttpDataFactory;
    //   189: aload_0
    //   190: getfield request : Lio/netty/handler/codec/http/HttpRequest;
    //   193: aload #8
    //   195: invokeinterface createAttribute : (Lio/netty/handler/codec/http/HttpRequest;Ljava/lang/String;)Lio/netty/handler/codec/http/multipart/Attribute;
    //   200: putfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   203: iload_3
    //   204: istore_2
    //   205: goto -> 533
    //   208: iload #7
    //   210: bipush #38
    //   212: if_icmpne -> 533
    //   215: aload_0
    //   216: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   219: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   222: iload_3
    //   223: iconst_1
    //   224: isub
    //   225: istore #5
    //   227: aload_0
    //   228: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   231: iload_2
    //   232: iload #5
    //   234: iload_2
    //   235: isub
    //   236: aload_0
    //   237: getfield charset : Ljava/nio/charset/Charset;
    //   240: invokevirtual toString : (IILjava/nio/charset/Charset;)Ljava/lang/String;
    //   243: aload_0
    //   244: getfield charset : Ljava/nio/charset/Charset;
    //   247: invokestatic decodeAttribute : (Ljava/lang/String;Ljava/nio/charset/Charset;)Ljava/lang/String;
    //   250: astore #8
    //   252: aload #8
    //   254: invokevirtual isEmpty : ()Z
    //   257: ifne -> 298
    //   260: aload_0
    //   261: aload_0
    //   262: getfield factory : Lio/netty/handler/codec/http/multipart/HttpDataFactory;
    //   265: aload_0
    //   266: getfield request : Lio/netty/handler/codec/http/HttpRequest;
    //   269: aload #8
    //   271: invokeinterface createAttribute : (Lio/netty/handler/codec/http/HttpRequest;Ljava/lang/String;)Lio/netty/handler/codec/http/multipart/Attribute;
    //   276: putfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   279: aload_0
    //   280: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   283: ldc ''
    //   285: invokeinterface setValue : (Ljava/lang/String;)V
    //   290: aload_0
    //   291: aload_0
    //   292: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   295: invokevirtual addHttpData : (Lio/netty/handler/codec/http/multipart/InterfaceHttpData;)V
    //   298: aload_0
    //   299: aconst_null
    //   300: putfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   303: iload_3
    //   304: istore_2
    //   305: iconst_1
    //   306: istore #6
    //   308: goto -> 533
    //   311: iload #7
    //   313: bipush #38
    //   315: if_icmpne -> 354
    //   318: aload_0
    //   319: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.DISPOSITION : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   322: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   325: iload_3
    //   326: iconst_1
    //   327: isub
    //   328: istore #5
    //   330: aload_0
    //   331: aload_0
    //   332: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   335: iload_2
    //   336: iload #5
    //   338: iload_2
    //   339: isub
    //   340: invokevirtual retainedSlice : (II)Lio/netty/buffer/ByteBuf;
    //   343: invokespecial setFinalBuffer : (Lio/netty/buffer/ByteBuf;)V
    //   346: iload_3
    //   347: istore_2
    //   348: iconst_1
    //   349: istore #6
    //   351: goto -> 533
    //   354: iload #7
    //   356: bipush #13
    //   358: if_icmpne -> 474
    //   361: aload_1
    //   362: getfield pos : I
    //   365: aload_1
    //   366: getfield limit : I
    //   369: if_icmpge -> 461
    //   372: aload_1
    //   373: getfield bytes : [B
    //   376: aload_1
    //   377: dup
    //   378: getfield pos : I
    //   381: dup_x1
    //   382: iconst_1
    //   383: iadd
    //   384: putfield pos : I
    //   387: baload
    //   388: sipush #255
    //   391: iand
    //   392: i2c
    //   393: istore #7
    //   395: iinc #3, 1
    //   398: iload #7
    //   400: bipush #10
    //   402: if_icmpne -> 446
    //   405: aload_0
    //   406: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.PREEPILOGUE : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   409: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   412: iload_3
    //   413: iconst_2
    //   414: isub
    //   415: istore #5
    //   417: aload_1
    //   418: iconst_0
    //   419: invokevirtual setReadPosition : (I)V
    //   422: aload_0
    //   423: aload_0
    //   424: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   427: iload_2
    //   428: iload #5
    //   430: iload_2
    //   431: isub
    //   432: invokevirtual retainedSlice : (II)Lio/netty/buffer/ByteBuf;
    //   435: invokespecial setFinalBuffer : (Lio/netty/buffer/ByteBuf;)V
    //   438: iload_3
    //   439: istore_2
    //   440: iconst_0
    //   441: istore #6
    //   443: goto -> 536
    //   446: aload_1
    //   447: iconst_0
    //   448: invokevirtual setReadPosition : (I)V
    //   451: new io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
    //   454: dup
    //   455: ldc 'Bad end of line'
    //   457: invokespecial <init> : (Ljava/lang/String;)V
    //   460: athrow
    //   461: aload_1
    //   462: getfield limit : I
    //   465: ifle -> 533
    //   468: iinc #3, -1
    //   471: goto -> 533
    //   474: iload #7
    //   476: bipush #10
    //   478: if_icmpne -> 533
    //   481: aload_0
    //   482: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.PREEPILOGUE : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   485: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   488: iload_3
    //   489: iconst_1
    //   490: isub
    //   491: istore #5
    //   493: aload_1
    //   494: iconst_0
    //   495: invokevirtual setReadPosition : (I)V
    //   498: aload_0
    //   499: aload_0
    //   500: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   503: iload_2
    //   504: iload #5
    //   506: iload_2
    //   507: isub
    //   508: invokevirtual retainedSlice : (II)Lio/netty/buffer/ByteBuf;
    //   511: invokespecial setFinalBuffer : (Lio/netty/buffer/ByteBuf;)V
    //   514: iload_3
    //   515: istore_2
    //   516: iconst_0
    //   517: istore #6
    //   519: goto -> 536
    //   522: aload_1
    //   523: iconst_0
    //   524: invokevirtual setReadPosition : (I)V
    //   527: iconst_0
    //   528: istore #6
    //   530: goto -> 536
    //   533: goto -> 65
    //   536: aload_0
    //   537: getfield isLastChunk : Z
    //   540: ifeq -> 609
    //   543: aload_0
    //   544: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   547: ifnull -> 609
    //   550: iload_3
    //   551: istore #5
    //   553: iload #5
    //   555: iload_2
    //   556: if_icmple -> 578
    //   559: aload_0
    //   560: aload_0
    //   561: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   564: iload_2
    //   565: iload #5
    //   567: iload_2
    //   568: isub
    //   569: invokevirtual retainedSlice : (II)Lio/netty/buffer/ByteBuf;
    //   572: invokespecial setFinalBuffer : (Lio/netty/buffer/ByteBuf;)V
    //   575: goto -> 597
    //   578: aload_0
    //   579: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   582: invokeinterface isCompleted : ()Z
    //   587: ifne -> 597
    //   590: aload_0
    //   591: getstatic io/netty/buffer/Unpooled.EMPTY_BUFFER : Lio/netty/buffer/ByteBuf;
    //   594: invokespecial setFinalBuffer : (Lio/netty/buffer/ByteBuf;)V
    //   597: iload_3
    //   598: istore_2
    //   599: aload_0
    //   600: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.EPILOGUE : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   603: putfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   606: goto -> 654
    //   609: iload #6
    //   611: ifeq -> 654
    //   614: aload_0
    //   615: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   618: ifnull -> 654
    //   621: aload_0
    //   622: getfield currentStatus : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   625: getstatic io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus.FIELD : Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$MultiPartStatus;
    //   628: if_acmpne -> 654
    //   631: aload_0
    //   632: getfield currentAttribute : Lio/netty/handler/codec/http/multipart/Attribute;
    //   635: aload_0
    //   636: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   639: iload_2
    //   640: iload_3
    //   641: iload_2
    //   642: isub
    //   643: invokevirtual retainedSlice : (II)Lio/netty/buffer/ByteBuf;
    //   646: iconst_0
    //   647: invokeinterface addContent : (Lio/netty/buffer/ByteBuf;Z)V
    //   652: iload_3
    //   653: istore_2
    //   654: aload_0
    //   655: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   658: iload_2
    //   659: invokevirtual readerIndex : (I)Lio/netty/buffer/ByteBuf;
    //   662: pop
    //   663: goto -> 722
    //   666: astore #7
    //   668: aload_0
    //   669: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   672: iload_2
    //   673: invokevirtual readerIndex : (I)Lio/netty/buffer/ByteBuf;
    //   676: pop
    //   677: aload #7
    //   679: athrow
    //   680: astore #7
    //   682: aload_0
    //   683: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   686: iload_2
    //   687: invokevirtual readerIndex : (I)Lio/netty/buffer/ByteBuf;
    //   690: pop
    //   691: new io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
    //   694: dup
    //   695: aload #7
    //   697: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   700: athrow
    //   701: astore #7
    //   703: aload_0
    //   704: getfield undecodedChunk : Lio/netty/buffer/ByteBuf;
    //   707: iload_2
    //   708: invokevirtual readerIndex : (I)Lio/netty/buffer/ByteBuf;
    //   711: pop
    //   712: new io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
    //   715: dup
    //   716: aload #7
    //   718: invokespecial <init> : (Ljava/lang/Throwable;)V
    //   721: athrow
    //   722: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #526	-> 0
    //   #527	-> 7
    //   #529	-> 8
    //   #530	-> 18
    //   #531	-> 22
    //   #533	-> 23
    //   #534	-> 35
    //   #535	-> 43
    //   #538	-> 45
    //   #539	-> 55
    //   #541	-> 62
    //   #543	-> 65
    //   #544	-> 76
    //   #545	-> 99
    //   #546	-> 102
    //   #548	-> 140
    //   #549	-> 147
    //   #550	-> 154
    //   #551	-> 159
    //   #553	-> 184
    //   #554	-> 203
    //   #555	-> 205
    //   #556	-> 215
    //   #557	-> 222
    //   #558	-> 227
    //   #559	-> 240
    //   #558	-> 247
    //   #564	-> 252
    //   #565	-> 260
    //   #566	-> 279
    //   #567	-> 290
    //   #569	-> 298
    //   #570	-> 303
    //   #571	-> 305
    //   #572	-> 308
    //   #575	-> 311
    //   #576	-> 318
    //   #577	-> 325
    //   #578	-> 330
    //   #579	-> 346
    //   #580	-> 348
    //   #581	-> 354
    //   #582	-> 361
    //   #583	-> 372
    //   #584	-> 395
    //   #585	-> 398
    //   #586	-> 405
    //   #587	-> 412
    //   #588	-> 417
    //   #589	-> 422
    //   #590	-> 438
    //   #591	-> 440
    //   #592	-> 443
    //   #595	-> 446
    //   #596	-> 451
    //   #599	-> 461
    //   #600	-> 468
    //   #603	-> 474
    //   #604	-> 481
    //   #605	-> 488
    //   #606	-> 493
    //   #607	-> 498
    //   #608	-> 514
    //   #609	-> 516
    //   #610	-> 519
    //   #615	-> 522
    //   #616	-> 527
    //   #617	-> 530
    //   #619	-> 533
    //   #620	-> 536
    //   #622	-> 550
    //   #623	-> 553
    //   #624	-> 559
    //   #625	-> 578
    //   #626	-> 590
    //   #628	-> 597
    //   #629	-> 599
    //   #630	-> 609
    //   #632	-> 631
    //   #634	-> 652
    //   #636	-> 654
    //   #649	-> 663
    //   #637	-> 666
    //   #639	-> 668
    //   #640	-> 677
    //   #641	-> 680
    //   #643	-> 682
    //   #644	-> 691
    //   #645	-> 701
    //   #647	-> 703
    //   #648	-> 712
    //   #650	-> 722
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   184	21	8	key	Ljava/lang/String;
    //   159	49	4	equalpos	I
    //   252	56	8	key	Ljava/lang/String;
    //   227	84	5	ampersandpos	I
    //   330	24	5	ampersandpos	I
    //   417	29	5	ampersandpos	I
    //   493	29	5	ampersandpos	I
    //   99	434	7	read	C
    //   553	56	5	ampersandpos	I
    //   668	12	7	e	Lio/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException;
    //   682	19	7	e	Ljava/io/IOException;
    //   703	19	7	e	Ljava/lang/IllegalArgumentException;
    //   0	723	0	this	Lio/netty/handler/codec/http/multipart/HttpPostStandardRequestDecoder;
    //   35	688	1	sao	Lio/netty/handler/codec/http/multipart/HttpPostBodyUtil$SeekAheadOptimize;
    //   43	680	2	firstpos	I
    //   45	678	3	currentpos	I
    //   65	658	6	contRead	Z
    // Exception table:
    //   from	to	target	type
    //   65	663	666	io/netty/handler/codec/http/multipart/HttpPostRequestDecoder$ErrorDataDecoderException
    //   65	663	680	java/io/IOException
    //   65	663	701	java/lang/IllegalArgumentException
  }
  
  private void setFinalBuffer(ByteBuf buffer) throws IOException {
    this.currentAttribute.addContent(buffer, true);
    ByteBuf decodedBuf = decodeAttribute(this.currentAttribute.getByteBuf(), this.charset);
    if (decodedBuf != null)
      this.currentAttribute.setContent(decodedBuf); 
    addHttpData(this.currentAttribute);
    this.currentAttribute = null;
  }
  
  private static String decodeAttribute(String s, Charset charset) {
    try {
      return QueryStringDecoder.decodeComponent(s, charset);
    } catch (IllegalArgumentException e) {
      throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e);
    } 
  }
  
  private static ByteBuf decodeAttribute(ByteBuf b, Charset charset) {
    int firstEscaped = b.forEachByte(new UrlEncodedDetector());
    if (firstEscaped == -1)
      return null; 
    ByteBuf buf = b.alloc().buffer(b.readableBytes());
    UrlDecoder urlDecode = new UrlDecoder(buf);
    int idx = b.forEachByte(urlDecode);
    if (urlDecode.nextEscapedIdx != 0) {
      if (idx == -1)
        idx = b.readableBytes() - 1; 
      idx -= urlDecode.nextEscapedIdx - 1;
      buf.release();
      throw new HttpPostRequestDecoder.ErrorDataDecoderException(
          String.format("Invalid hex byte at index '%d' in string: '%s'", new Object[] { Integer.valueOf(idx), b.toString(charset) }));
    } 
    return buf;
  }
  
  public void destroy() {
    cleanFiles();
    for (InterfaceHttpData httpData : this.bodyListHttpData) {
      if (httpData.refCnt() > 0)
        httpData.release(); 
    } 
    this.destroyed = true;
    if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
      this.undecodedChunk.release();
      this.undecodedChunk = null;
    } 
  }
  
  public void cleanFiles() {
    checkDestroyed();
    this.factory.cleanRequestHttpData(this.request);
  }
  
  public void removeHttpDataFromClean(InterfaceHttpData data) {
    checkDestroyed();
    this.factory.removeHttpDataFromClean(this.request, data);
  }
  
  private static final class UrlEncodedDetector implements ByteProcessor {
    private UrlEncodedDetector() {}
    
    public boolean process(byte value) throws Exception {
      return (value != 37 && value != 43);
    }
  }
  
  private static final class UrlDecoder implements ByteProcessor {
    private final ByteBuf output;
    
    private int nextEscapedIdx;
    
    private byte hiByte;
    
    UrlDecoder(ByteBuf output) {
      this.output = output;
    }
    
    public boolean process(byte value) {
      if (this.nextEscapedIdx != 0) {
        if (this.nextEscapedIdx == 1) {
          this.hiByte = value;
          this.nextEscapedIdx++;
        } else {
          int hi = StringUtil.decodeHexNibble((char)this.hiByte);
          int lo = StringUtil.decodeHexNibble((char)value);
          if (hi == -1 || lo == -1) {
            this.nextEscapedIdx++;
            return false;
          } 
          this.output.writeByte((hi << 4) + lo);
          this.nextEscapedIdx = 0;
        } 
      } else if (value == 37) {
        this.nextEscapedIdx = 1;
      } else if (value == 43) {
        this.output.writeByte(32);
      } else {
        this.output.writeByte(value);
      } 
      return true;
    }
  }
}
