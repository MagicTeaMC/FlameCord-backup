package org.apache.logging.log4j.core.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLSocketFactory;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.util.CyclicBuffer;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.util.PropertiesUtil;

public class SmtpManager extends MailManager {
  public static final SMTPManagerFactory FACTORY = new SMTPManagerFactory();
  
  private final Session session;
  
  private final CyclicBuffer<LogEvent> buffer;
  
  private volatile MimeMessage message;
  
  private final MailManager.FactoryData data;
  
  private static MimeMessage createMimeMessage(MailManager.FactoryData data, Session session, LogEvent appendEvent) throws MessagingException {
    return (new MimeMessageBuilder(session)).setFrom(data.getFrom())
      .setReplyTo(data.getReplyTo())
      .setRecipients(Message.RecipientType.TO, data.getTo())
      .setRecipients(Message.RecipientType.CC, data.getCc())
      .setRecipients(Message.RecipientType.BCC, data.getBcc())
      .setSubject(data.getSubjectSerializer().toSerializable(appendEvent))
      .build();
  }
  
  protected SmtpManager(String name, Session session, MimeMessage message, MailManager.FactoryData data) {
    super((LoggerContext)null, name);
    this.session = session;
    this.message = message;
    this.data = data;
    this.buffer = new CyclicBuffer(LogEvent.class, data.getBufferSize());
  }
  
  public void add(LogEvent event) {
    this.buffer.add(event.toImmutable());
  }
  
  @Deprecated
  public static SmtpManager getSmtpManager(Configuration config, String to, String cc, String bcc, String from, String replyTo, String subject, String protocol, String host, int port, String username, String password, boolean isDebug, String filterName, int numElements, SslConfiguration sslConfiguration) {
    AbstractStringLayout.Serializer subjectSerializer = PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(subject).build();
    MailManager.FactoryData data = new MailManager.FactoryData(to, cc, bcc, from, replyTo, subject, subjectSerializer, protocol, host, port, username, password, isDebug, numElements, sslConfiguration, filterName);
    return (SmtpManager)getManager(data.getManagerName(), FACTORY, data);
  }
  
  public void sendEvents(Layout<?> layout, LogEvent appendEvent) {
    if (this.message == null)
      connect(appendEvent); 
    try {
      LogEvent[] priorEvents = removeAllBufferedEvents();
      byte[] rawBytes = formatContentToBytes(priorEvents, appendEvent, layout);
      String contentType = layout.getContentType();
      String encoding = getEncoding(rawBytes, contentType);
      byte[] encodedBytes = encodeContentToBytes(rawBytes, encoding);
      InternetHeaders headers = getHeaders(contentType, encoding);
      MimeMultipart mp = getMimeMultipart(encodedBytes, headers);
      String subject = this.data.getSubjectSerializer().toSerializable(appendEvent);
      sendMultipartMessage(this.message, mp, subject);
    } catch (MessagingException|IOException|RuntimeException e) {
      logError("Caught exception while sending e-mail notification.", e);
      throw new LoggingException("Error occurred while sending email", e);
    } 
  }
  
  LogEvent[] removeAllBufferedEvents() {
    return (LogEvent[])this.buffer.removeAll();
  }
  
  protected byte[] formatContentToBytes(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout) throws IOException {
    ByteArrayOutputStream raw = new ByteArrayOutputStream();
    writeContent(priorEvents, appendEvent, layout, raw);
    return raw.toByteArray();
  }
  
  private void writeContent(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout, ByteArrayOutputStream out) throws IOException {
    writeHeader(layout, out);
    writeBuffer(priorEvents, appendEvent, layout, out);
    writeFooter(layout, out);
  }
  
  protected void writeHeader(Layout<?> layout, OutputStream out) throws IOException {
    byte[] header = layout.getHeader();
    if (header != null)
      out.write(header); 
  }
  
  protected void writeBuffer(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout, OutputStream out) throws IOException {
    for (LogEvent priorEvent : priorEvents) {
      byte[] arrayOfByte = layout.toByteArray(priorEvent);
      out.write(arrayOfByte);
    } 
    byte[] bytes = layout.toByteArray(appendEvent);
    out.write(bytes);
  }
  
  protected void writeFooter(Layout<?> layout, OutputStream out) throws IOException {
    byte[] footer = layout.getFooter();
    if (footer != null)
      out.write(footer); 
  }
  
  protected String getEncoding(byte[] rawBytes, String contentType) {
    ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(rawBytes, contentType);
    return MimeUtility.getEncoding((DataSource)byteArrayDataSource);
  }
  
  protected byte[] encodeContentToBytes(byte[] rawBytes, String encoding) throws MessagingException, IOException {
    ByteArrayOutputStream encoded = new ByteArrayOutputStream();
    encodeContent(rawBytes, encoding, encoded);
    return encoded.toByteArray();
  }
  
  protected void encodeContent(byte[] bytes, String encoding, ByteArrayOutputStream out) throws MessagingException, IOException {
    try (OutputStream encoder = MimeUtility.encode(out, encoding)) {
      encoder.write(bytes);
    } 
  }
  
  protected InternetHeaders getHeaders(String contentType, String encoding) {
    InternetHeaders headers = new InternetHeaders();
    headers.setHeader("Content-Type", contentType + "; charset=UTF-8");
    headers.setHeader("Content-Transfer-Encoding", encoding);
    return headers;
  }
  
  protected MimeMultipart getMimeMultipart(byte[] encodedBytes, InternetHeaders headers) throws MessagingException {
    MimeMultipart mp = new MimeMultipart();
    MimeBodyPart part = new MimeBodyPart(headers, encodedBytes);
    mp.addBodyPart((BodyPart)part);
    return mp;
  }
  
  @Deprecated
  protected void sendMultipartMessage(MimeMessage msg, MimeMultipart mp) throws MessagingException {
    synchronized (msg) {
      msg.setContent((Multipart)mp);
      msg.setSentDate(new Date());
      Transport.send((Message)msg);
    } 
  }
  
  protected void sendMultipartMessage(MimeMessage msg, MimeMultipart mp, String subject) throws MessagingException {
    synchronized (msg) {
      msg.setContent((Multipart)mp);
      msg.setSentDate(new Date());
      msg.setSubject(subject);
      Transport.send((Message)msg);
    } 
  }
  
  private synchronized void connect(LogEvent appendEvent) {
    if (this.message != null)
      return; 
    try {
      this.message = createMimeMessage(this.data, this.session, appendEvent);
    } catch (MessagingException e) {
      logError("Could not set SmtpAppender message options", (Throwable)e);
      this.message = null;
    } 
  }
  
  public static class SMTPManagerFactory implements MailManagerFactory {
    public SmtpManager createManager(String name, MailManager.FactoryData data) {
      String smtpProtocol = data.getSmtpProtocol();
      String prefix = "mail." + smtpProtocol;
      Properties properties = PropertiesUtil.getSystemProperties();
      properties.setProperty("mail.transport.protocol", smtpProtocol);
      if (properties.getProperty("mail.host") == null)
        properties.setProperty("mail.host", NetUtils.getLocalHostname()); 
      String smtpHost = data.getSmtpHost();
      if (null != smtpHost)
        properties.setProperty(prefix + ".host", smtpHost); 
      if (data.getSmtpPort() > 0)
        properties.setProperty(prefix + ".port", String.valueOf(data.getSmtpPort())); 
      Authenticator authenticator = buildAuthenticator(data.getSmtpUsername(), data.getSmtpPassword());
      if (null != authenticator)
        properties.setProperty(prefix + ".auth", "true"); 
      if (smtpProtocol.equals("smtps")) {
        SslConfiguration sslConfiguration = data.getSslConfiguration();
        if (sslConfiguration != null) {
          SSLSocketFactory sslSocketFactory = sslConfiguration.getSslSocketFactory();
          properties.put(prefix + ".ssl.socketFactory", sslSocketFactory);
          properties.setProperty(prefix + ".ssl.checkserveridentity", Boolean.toString(sslConfiguration.isVerifyHostName()));
        } 
      } 
      Session session = Session.getInstance(properties, authenticator);
      session.setProtocolForAddress("rfc822", smtpProtocol);
      session.setDebug(data.isSmtpDebug());
      return new SmtpManager(name, session, null, data);
    }
    
    private Authenticator buildAuthenticator(final String username, final String password) {
      if (null != password && null != username)
        return new Authenticator() {
            private final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username, password);
            
            protected PasswordAuthentication getPasswordAuthentication() {
              return this.passwordAuthentication;
            }
          }; 
      return null;
    }
  }
}
