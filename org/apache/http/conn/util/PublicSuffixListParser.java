package org.apache.http.conn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public final class PublicSuffixListParser {
  public PublicSuffixList parse(Reader reader) throws IOException {
    List<String> rules = new ArrayList<String>();
    List<String> exceptions = new ArrayList<String>();
    BufferedReader r = new BufferedReader(reader);
    String line;
    while ((line = r.readLine()) != null) {
      if (line.isEmpty())
        continue; 
      if (line.startsWith("//"))
        continue; 
      if (line.startsWith("."))
        line = line.substring(1); 
      boolean isException = line.startsWith("!");
      if (isException)
        line = line.substring(1); 
      if (isException) {
        exceptions.add(line);
        continue;
      } 
      rules.add(line);
    } 
    return new PublicSuffixList(DomainType.UNKNOWN, rules, exceptions);
  }
  
  public List<PublicSuffixList> parseByType(Reader reader) throws IOException {
    List<PublicSuffixList> result = new ArrayList<PublicSuffixList>(2);
    BufferedReader r = new BufferedReader(reader);
    StringBuilder sb = new StringBuilder(256);
    DomainType domainType = null;
    List<String> rules = null;
    List<String> exceptions = null;
    String line;
    while ((line = r.readLine()) != null) {
      if (line.isEmpty())
        continue; 
      if (line.startsWith("//")) {
        if (domainType == null) {
          if (line.contains("===BEGIN ICANN DOMAINS===")) {
            domainType = DomainType.ICANN;
            continue;
          } 
          if (line.contains("===BEGIN PRIVATE DOMAINS==="))
            domainType = DomainType.PRIVATE; 
          continue;
        } 
        if (line.contains("===END ICANN DOMAINS===") || line.contains("===END PRIVATE DOMAINS===")) {
          if (rules != null)
            result.add(new PublicSuffixList(domainType, rules, exceptions)); 
          domainType = null;
          rules = null;
          exceptions = null;
        } 
        continue;
      } 
      if (domainType == null)
        continue; 
      if (line.startsWith("."))
        line = line.substring(1); 
      boolean isException = line.startsWith("!");
      if (isException)
        line = line.substring(1); 
      if (isException) {
        if (exceptions == null)
          exceptions = new ArrayList<String>(); 
        exceptions.add(line);
        continue;
      } 
      if (rules == null)
        rules = new ArrayList<String>(); 
      rules.add(line);
    } 
    return result;
  }
}
