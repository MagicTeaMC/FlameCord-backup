package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.core.appender.ManagerFactory;

public interface MailManagerFactory extends ManagerFactory<MailManager, MailManager.FactoryData> {
  MailManager createManager(String paramString, MailManager.FactoryData paramFactoryData);
}
