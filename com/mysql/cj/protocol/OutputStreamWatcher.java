package com.mysql.cj.protocol;

public interface OutputStreamWatcher {
  void streamClosed(WatchableStream paramWatchableStream);
}
