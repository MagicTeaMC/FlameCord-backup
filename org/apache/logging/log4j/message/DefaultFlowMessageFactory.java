package org.apache.logging.log4j.message;

import java.io.Serializable;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

public class DefaultFlowMessageFactory implements FlowMessageFactory, Serializable {
  private static final String EXIT_DEFAULT_PREFIX = "Exit";
  
  private static final String ENTRY_DEFAULT_PREFIX = "Enter";
  
  private static final long serialVersionUID = 8578655591131397576L;
  
  private final String entryText;
  
  private final String exitText;
  
  private final MessageFactory messageFactory;
  
  public DefaultFlowMessageFactory() {
    this("Enter", "Exit");
  }
  
  public DefaultFlowMessageFactory(String entryText, String exitText) {
    this.entryText = entryText;
    this.exitText = exitText;
    this.messageFactory = createDefaultMessageFactory();
  }
  
  private static MessageFactory createDefaultMessageFactory() {
    try {
      return AbstractLogger.DEFAULT_MESSAGE_FACTORY_CLASS.newInstance();
    } catch (InstantiationException|IllegalAccessException e) {
      throw new IllegalStateException(e);
    } 
  }
  
  private static class AbstractFlowMessage implements FlowMessage, StringBuilderFormattable {
    private static final long serialVersionUID = 1L;
    
    private final Message message;
    
    private final String text;
    
    AbstractFlowMessage(String text, Message message) {
      this.message = message;
      this.text = text;
    }
    
    public String getFormattedMessage() {
      if (this.message != null)
        return this.text + " " + this.message.getFormattedMessage(); 
      return this.text;
    }
    
    public String getFormat() {
      if (this.message != null)
        return this.text + " " + this.message.getFormat(); 
      return this.text;
    }
    
    public Object[] getParameters() {
      if (this.message != null)
        return this.message.getParameters(); 
      return null;
    }
    
    public Throwable getThrowable() {
      if (this.message != null)
        return this.message.getThrowable(); 
      return null;
    }
    
    public Message getMessage() {
      return this.message;
    }
    
    public String getText() {
      return this.text;
    }
    
    public void formatTo(StringBuilder buffer) {
      buffer.append(this.text);
      if (this.message != null) {
        buffer.append(" ");
        StringBuilders.appendValue(buffer, this.message);
      } 
    }
  }
  
  private static final class SimpleEntryMessage extends AbstractFlowMessage implements EntryMessage {
    private static final long serialVersionUID = 1L;
    
    SimpleEntryMessage(String entryText, Message message) {
      super(entryText, message);
    }
  }
  
  private static final class SimpleExitMessage extends AbstractFlowMessage implements ExitMessage {
    private static final long serialVersionUID = 1L;
    
    private final Object result;
    
    private final boolean isVoid;
    
    SimpleExitMessage(String exitText, EntryMessage message) {
      this(exitText, message.getMessage());
    }
    
    SimpleExitMessage(String exitText, Message message) {
      super(exitText, message);
      this.result = null;
      this.isVoid = true;
    }
    
    SimpleExitMessage(String exitText, Object result, EntryMessage message) {
      this(exitText, result, message.getMessage());
    }
    
    SimpleExitMessage(String exitText, Object result, Message message) {
      super(exitText, message);
      this.result = result;
      this.isVoid = false;
    }
    
    public String getFormattedMessage() {
      String formattedMessage = super.getFormattedMessage();
      if (this.isVoid)
        return formattedMessage; 
      return formattedMessage + ": " + this.result;
    }
  }
  
  public String getEntryText() {
    return this.entryText;
  }
  
  public String getExitText() {
    return this.exitText;
  }
  
  public EntryMessage newEntryMessage(String format, Object... params) {
    Message message;
    boolean hasFormat = Strings.isNotEmpty(format);
    if (params == null || params.length == 0) {
      message = hasFormat ? this.messageFactory.newMessage(format) : null;
    } else if (hasFormat) {
      message = this.messageFactory.newMessage(format, params);
    } else {
      StringBuilder sb = new StringBuilder("params(");
      for (int i = 0; i < params.length; i++) {
        if (i > 0)
          sb.append(", "); 
        sb.append("{}");
      } 
      sb.append(")");
      message = this.messageFactory.newMessage(sb.toString(), params);
    } 
    return newEntryMessage(message);
  }
  
  public EntryMessage newEntryMessage(Message message) {
    return new SimpleEntryMessage(this.entryText, makeImmutable(message));
  }
  
  private Message makeImmutable(Message message) {
    if (message instanceof ReusableMessage)
      return ((ReusableMessage)message).memento(); 
    return message;
  }
  
  public ExitMessage newExitMessage(String format, Object result) {
    Message message;
    boolean hasFormat = Strings.isNotEmpty(format);
    if (result == null) {
      message = hasFormat ? this.messageFactory.newMessage(format) : null;
    } else {
      message = this.messageFactory.newMessage(hasFormat ? format : "with({})", new Object[] { result });
    } 
    return newExitMessage(message);
  }
  
  public ExitMessage newExitMessage(Message message) {
    return new SimpleExitMessage(this.exitText, message);
  }
  
  public ExitMessage newExitMessage(EntryMessage message) {
    return new SimpleExitMessage(this.exitText, message);
  }
  
  public ExitMessage newExitMessage(Object result, EntryMessage message) {
    return new SimpleExitMessage(this.exitText, result, message);
  }
  
  public ExitMessage newExitMessage(Object result, Message message) {
    return new SimpleExitMessage(this.exitText, result, message);
  }
}
