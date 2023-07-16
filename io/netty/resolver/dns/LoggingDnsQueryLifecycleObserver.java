package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetSocketAddress;
import java.util.List;

final class LoggingDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver {
  private final InternalLogger logger;
  
  private final InternalLogLevel level;
  
  private final DnsQuestion question;
  
  private InetSocketAddress dnsServerAddress;
  
  LoggingDnsQueryLifecycleObserver(DnsQuestion question, InternalLogger logger, InternalLogLevel level) {
    this.question = (DnsQuestion)ObjectUtil.checkNotNull(question, "question");
    this.logger = (InternalLogger)ObjectUtil.checkNotNull(logger, "logger");
    this.level = (InternalLogLevel)ObjectUtil.checkNotNull(level, "level");
  }
  
  public void queryWritten(InetSocketAddress dnsServerAddress, ChannelFuture future) {
    this.dnsServerAddress = dnsServerAddress;
  }
  
  public void queryCancelled(int queriesRemaining) {
    if (this.dnsServerAddress != null) {
      this.logger.log(this.level, "from {} : {} cancelled with {} queries remaining", new Object[] { this.dnsServerAddress, this.question, 
            Integer.valueOf(queriesRemaining) });
    } else {
      this.logger.log(this.level, "{} query never written and cancelled with {} queries remaining", this.question, 
          Integer.valueOf(queriesRemaining));
    } 
  }
  
  public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> nameServers) {
    this.logger.log(this.level, "from {} : {} redirected", this.dnsServerAddress, this.question);
    return this;
  }
  
  public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion cnameQuestion) {
    this.logger.log(this.level, "from {} : {} CNAME question {}", new Object[] { this.dnsServerAddress, this.question, cnameQuestion });
    return this;
  }
  
  public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode code) {
    this.logger.log(this.level, "from {} : {} no answer {}", new Object[] { this.dnsServerAddress, this.question, code });
    return this;
  }
  
  public void queryFailed(Throwable cause) {
    if (this.dnsServerAddress != null) {
      this.logger.log(this.level, "from {} : {} failure", new Object[] { this.dnsServerAddress, this.question, cause });
    } else {
      this.logger.log(this.level, "{} query never written and failed", this.question, cause);
    } 
  }
  
  public void querySucceed() {}
}