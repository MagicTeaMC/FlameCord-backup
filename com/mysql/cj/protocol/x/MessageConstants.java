package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxExpect;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import com.mysql.cj.x.protobuf.MysqlxResultset;
import com.mysql.cj.x.protobuf.MysqlxSession;
import com.mysql.cj.x.protobuf.MysqlxSql;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MessageConstants {
  public static final Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> MESSAGE_CLASS_TO_PARSER;
  
  public static final Map<Class<? extends GeneratedMessageV3>, Integer> MESSAGE_CLASS_TO_TYPE;
  
  public static final Map<Integer, Class<? extends GeneratedMessageV3>> MESSAGE_TYPE_TO_CLASS;
  
  public static final Map<Class<? extends MessageLite>, Integer> MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE;
  
  static {
    Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> messageClassToParser = new HashMap<>();
    Map<Class<? extends GeneratedMessageV3>, Integer> messageClassToType = new HashMap<>();
    Map<Integer, Class<? extends GeneratedMessageV3>> messageTypeToClass = new HashMap<>();
    messageClassToParser.put(Mysqlx.Error.class, Mysqlx.Error.getDefaultInstance().getParserForType());
    messageClassToParser.put(Mysqlx.Ok.class, Mysqlx.Ok.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxSession.AuthenticateContinue.class, MysqlxSession.AuthenticateContinue.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxSession.AuthenticateOk.class, MysqlxSession.AuthenticateOk.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxConnection.Capabilities.class, MysqlxConnection.Capabilities.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxResultset.ColumnMetaData.class, MysqlxResultset.ColumnMetaData.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxResultset.FetchDone.class, MysqlxResultset.FetchDone.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxResultset.FetchDoneMoreResultsets.class, MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxNotice.Frame.class, MysqlxNotice.Frame.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxResultset.Row.class, MysqlxResultset.Row.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxSql.StmtExecuteOk.class, MysqlxSql.StmtExecuteOk.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxConnection.Compression.class, MysqlxConnection.Compression.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxNotice.SessionStateChanged.class, MysqlxNotice.SessionStateChanged.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxNotice.SessionVariableChanged.class, MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParserForType());
    messageClassToParser.put(MysqlxNotice.Warning.class, MysqlxNotice.Warning.getDefaultInstance().getParserForType());
    messageClassToType.put(Mysqlx.Error.class, Integer.valueOf(1));
    messageClassToType.put(Mysqlx.Ok.class, Integer.valueOf(0));
    messageClassToType.put(MysqlxSession.AuthenticateContinue.class, Integer.valueOf(3));
    messageClassToType.put(MysqlxSession.AuthenticateOk.class, Integer.valueOf(4));
    messageClassToType.put(MysqlxConnection.Capabilities.class, Integer.valueOf(2));
    messageClassToType.put(MysqlxResultset.ColumnMetaData.class, Integer.valueOf(12));
    messageClassToType.put(MysqlxResultset.FetchDone.class, Integer.valueOf(14));
    messageClassToType.put(MysqlxResultset.FetchDoneMoreResultsets.class, Integer.valueOf(16));
    messageClassToType.put(MysqlxNotice.Frame.class, Integer.valueOf(11));
    messageClassToType.put(MysqlxResultset.Row.class, Integer.valueOf(13));
    messageClassToType.put(MysqlxSql.StmtExecuteOk.class, Integer.valueOf(17));
    messageClassToType.put(MysqlxConnection.Compression.class, Integer.valueOf(19));
    for (Map.Entry<Class<? extends GeneratedMessageV3>, Integer> entry : messageClassToType.entrySet())
      messageTypeToClass.put(entry.getValue(), entry.getKey()); 
    MESSAGE_CLASS_TO_PARSER = Collections.unmodifiableMap(messageClassToParser);
    MESSAGE_CLASS_TO_TYPE = Collections.unmodifiableMap(messageClassToType);
    MESSAGE_TYPE_TO_CLASS = Collections.unmodifiableMap(messageTypeToClass);
    Map<Class<? extends MessageLite>, Integer> messageClassToClientMessageType = new HashMap<>();
    messageClassToClientMessageType.put(MysqlxSession.AuthenticateStart.class, Integer.valueOf(4));
    messageClassToClientMessageType.put(MysqlxSession.AuthenticateContinue.class, Integer.valueOf(5));
    messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesGet.class, Integer.valueOf(1));
    messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesSet.class, Integer.valueOf(2));
    messageClassToClientMessageType.put(MysqlxSession.Close.class, Integer.valueOf(7));
    messageClassToClientMessageType.put(MysqlxCrud.Delete.class, Integer.valueOf(20));
    messageClassToClientMessageType.put(MysqlxCrud.Find.class, Integer.valueOf(17));
    messageClassToClientMessageType.put(MysqlxCrud.Insert.class, Integer.valueOf(18));
    messageClassToClientMessageType.put(MysqlxSession.Reset.class, Integer.valueOf(6));
    messageClassToClientMessageType.put(MysqlxSql.StmtExecute.class, Integer.valueOf(12));
    messageClassToClientMessageType.put(MysqlxCrud.Update.class, Integer.valueOf(19));
    messageClassToClientMessageType.put(MysqlxCrud.CreateView.class, Integer.valueOf(30));
    messageClassToClientMessageType.put(MysqlxCrud.ModifyView.class, Integer.valueOf(31));
    messageClassToClientMessageType.put(MysqlxCrud.DropView.class, Integer.valueOf(32));
    messageClassToClientMessageType.put(MysqlxExpect.Open.class, Integer.valueOf(24));
    messageClassToClientMessageType.put(MysqlxPrepare.Prepare.class, Integer.valueOf(40));
    messageClassToClientMessageType.put(MysqlxPrepare.Execute.class, Integer.valueOf(41));
    messageClassToClientMessageType.put(MysqlxPrepare.Deallocate.class, Integer.valueOf(42));
    MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE = Collections.unmodifiableMap(messageClassToClientMessageType);
  }
  
  public static int getTypeForMessageClass(Class<? extends MessageLite> msgClass) {
    Integer tag = MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE.get(msgClass);
    if (tag == null)
      throw new WrongArgumentException("No mapping to ClientMessages for message class " + msgClass.getSimpleName()); 
    return tag.intValue();
  }
  
  public static Class<? extends GeneratedMessageV3> getMessageClassForType(int type) {
    Class<? extends GeneratedMessageV3> messageClass = MESSAGE_TYPE_TO_CLASS.get(Integer.valueOf(type));
    if (messageClass == null) {
      Mysqlx.ServerMessages.Type serverMessageMapping = Mysqlx.ServerMessages.Type.forNumber(type);
      throw AssertionFailedException.shouldNotHappen("Unknown message type: " + type + " (server messages mapping: " + serverMessageMapping + ")");
    } 
    return messageClass;
  }
}
