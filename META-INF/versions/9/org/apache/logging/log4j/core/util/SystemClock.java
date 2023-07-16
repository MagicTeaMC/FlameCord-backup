package META-INF.versions.9.org.apache.logging.log4j.core.util;

import java.time.Clock;
import java.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;
import org.apache.logging.log4j.core.time.PreciseClock;
import org.apache.logging.log4j.core.util.Clock;

public final class SystemClock implements Clock, PreciseClock {
  public long currentTimeMillis() {
    return System.currentTimeMillis();
  }
  
  public void init(MutableInstant mutableInstant) {
    Instant instant = Clock.systemUTC().instant();
    mutableInstant.initFromEpochSecond(instant.getEpochSecond(), instant.getNano());
  }
}
