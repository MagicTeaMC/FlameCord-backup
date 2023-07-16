package org.apache.logging.log4j.core.appender.mom.kafka;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.util.Integers;

@Plugin(name = "Kafka", category = "Core", elementType = "appender", printObject = true)
public final class KafkaAppender extends AbstractAppender {
  public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B> implements org.apache.logging.log4j.core.util.Builder<KafkaAppender> {
    @PluginAttribute("retryCount")
    private int retryCount;
    
    @PluginAttribute("topic")
    private String topic;
    
    @PluginAttribute("key")
    private String key;
    
    @PluginAttribute(value = "syncSend", defaultBoolean = true)
    private boolean syncSend;
    
    @PluginAttribute(value = "sendEventTimestamp", defaultBoolean = false)
    private boolean sendEventTimestamp;
    
    public KafkaAppender build() {
      Layout<? extends Serializable> layout = getLayout();
      if (layout == null) {
        KafkaAppender.LOGGER.error("No layout provided for KafkaAppender");
        return null;
      } 
      KafkaManager kafkaManager = KafkaManager.getManager(getConfiguration().getLoggerContext(), getName(), this.topic, this.syncSend, this.sendEventTimestamp, 
          getPropertyArray(), this.key);
      return new KafkaAppender(getName(), layout, getFilter(), isIgnoreExceptions(), kafkaManager, 
          getPropertyArray(), getRetryCount().intValue());
    }
    
    public Integer getRetryCount() {
      Integer intRetryCount = null;
      try {
        intRetryCount = Integer.valueOf(this.retryCount);
      } catch (NumberFormatException numberFormatException) {}
      return intRetryCount;
    }
    
    public String getTopic() {
      return this.topic;
    }
    
    public boolean isSendEventTimestamp() {
      return this.sendEventTimestamp;
    }
    
    public boolean isSyncSend() {
      return this.syncSend;
    }
    
    public B setKey(String key) {
      this.key = key;
      return (B)asBuilder();
    }
    
    @Deprecated
    public B setRetryCount(String retryCount) {
      this.retryCount = Integers.parseInt(retryCount, 0);
      return (B)asBuilder();
    }
    
    public B setRetryCount(int retryCount) {
      this.retryCount = retryCount;
      return (B)asBuilder();
    }
    
    public B setSendEventTimestamp(boolean sendEventTimestamp) {
      this.sendEventTimestamp = sendEventTimestamp;
      return (B)asBuilder();
    }
    
    public B setSyncSend(boolean syncSend) {
      this.syncSend = syncSend;
      return (B)asBuilder();
    }
    
    public B setTopic(String topic) {
      this.topic = topic;
      return (B)asBuilder();
    }
  }
  
  private static final String[] KAFKA_CLIENT_PACKAGES = new String[] { "org.apache.kafka.common", "org.apache.kafka.clients" };
  
  private final Integer retryCount;
  
  private final KafkaManager manager;
  
  @Deprecated
  public static KafkaAppender createAppender(Layout<? extends Serializable> layout, Filter filter, String name, boolean ignoreExceptions, String topic, Property[] properties, Configuration configuration, String key) {
    if (layout == null) {
      AbstractLifeCycle.LOGGER.error("No layout provided for KafkaAppender");
      return null;
    } 
    KafkaManager kafkaManager = KafkaManager.getManager(configuration.getLoggerContext(), name, topic, true, properties, key);
    return new KafkaAppender(name, layout, filter, ignoreExceptions, kafkaManager, null, 0);
  }
  
  private static boolean isRecursive(LogEvent event) {
    return Stream.<String>of(KAFKA_CLIENT_PACKAGES).anyMatch(prefix -> event.getLoggerName().startsWith(prefix));
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder<>()).asBuilder();
  }
  
  private KafkaAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions, KafkaManager manager, Property[] properties, int retryCount) {
    super(name, filter, layout, ignoreExceptions, properties);
    this.manager = Objects.<KafkaManager>requireNonNull(manager, "manager");
    this.retryCount = Integer.valueOf(retryCount);
  }
  
  public void append(LogEvent event) {
    if (event.getLoggerName() != null && isRecursive(event)) {
      LOGGER.warn("Recursive logging from [{}] for appender [{}].", event.getLoggerName(), getName());
    } else {
      try {
        tryAppend(event);
      } catch (Exception e) {
        if (this.retryCount != null) {
          int currentRetryAttempt = 0;
          while (currentRetryAttempt < this.retryCount.intValue()) {
            currentRetryAttempt++;
            try {
              tryAppend(event);
              break;
            } catch (Exception exception) {}
          } 
        } 
        error("Unable to write to Kafka in appender [" + getName() + "]", event, e);
      } 
    } 
  }
  
  public void start() {
    super.start();
    this.manager.startup();
  }
  
  public boolean stop(long timeout, TimeUnit timeUnit) {
    setStopping();
    boolean stopped = stop(timeout, timeUnit, false);
    stopped &= this.manager.stop(timeout, timeUnit);
    setStopped();
    return stopped;
  }
  
  public String toString() {
    return "KafkaAppender{name=" + getName() + ", state=" + getState() + ", topic=" + this.manager.getTopic() + '}';
  }
  
  private void tryAppend(LogEvent event) throws ExecutionException, InterruptedException, TimeoutException {
    byte[] data;
    Layout<? extends Serializable> layout = getLayout();
    if (layout instanceof org.apache.logging.log4j.core.layout.SerializedLayout) {
      byte[] header = layout.getHeader();
      byte[] body = layout.toByteArray(event);
      data = new byte[header.length + body.length];
      System.arraycopy(header, 0, data, 0, header.length);
      System.arraycopy(body, 0, data, header.length, body.length);
    } else {
      data = layout.toByteArray(event);
    } 
    this.manager.send(data, Long.valueOf(event.getTimeMillis()));
  }
}
