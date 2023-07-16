package com.mysql.cj.log;

public class BaseMetricsHolder {
  private static final int HISTOGRAM_BUCKETS = 20;
  
  private long longestQueryTimeMs = 0L;
  
  private long maximumNumberTablesAccessed = 0L;
  
  private long minimumNumberTablesAccessed = Long.MAX_VALUE;
  
  private long numberOfPreparedExecutes = 0L;
  
  private long numberOfPrepares = 0L;
  
  private long numberOfQueriesIssued = 0L;
  
  private long numberOfResultSetsCreated = 0L;
  
  private long[] numTablesMetricsHistBreakpoints;
  
  private int[] numTablesMetricsHistCounts;
  
  private long[] oldHistBreakpoints = null;
  
  private int[] oldHistCounts = null;
  
  private long shortestQueryTimeMs = Long.MAX_VALUE;
  
  private double totalQueryTimeMs = 0.0D;
  
  private long[] perfMetricsHistBreakpoints;
  
  private int[] perfMetricsHistCounts;
  
  private long queryTimeCount;
  
  private double queryTimeSum;
  
  private double queryTimeSumSquares;
  
  private double queryTimeMean;
  
  private void createInitialHistogram(long[] breakpoints, long lowerBound, long upperBound) {
    double bucketSize = (upperBound - lowerBound) / 20.0D * 1.25D;
    if (bucketSize < 1.0D)
      bucketSize = 1.0D; 
    for (int i = 0; i < 20; i++) {
      breakpoints[i] = lowerBound;
      lowerBound = (long)(lowerBound + bucketSize);
    } 
  }
  
  private void addToHistogram(int[] histogramCounts, long[] histogramBreakpoints, long value, int numberOfTimes, long currentLowerBound, long currentUpperBound) {
    if (histogramCounts == null) {
      createInitialHistogram(histogramBreakpoints, currentLowerBound, currentUpperBound);
    } else {
      for (int i = 0; i < 20; i++) {
        if (histogramBreakpoints[i] >= value) {
          histogramCounts[i] = histogramCounts[i] + numberOfTimes;
          break;
        } 
      } 
    } 
  }
  
  private void addToPerformanceHistogram(long value, int numberOfTimes) {
    checkAndCreatePerformanceHistogram();
    addToHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, value, numberOfTimes, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
  }
  
  private void addToTablesAccessedHistogram(long value, int numberOfTimes) {
    checkAndCreateTablesAccessedHistogram();
    addToHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, value, numberOfTimes, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
  }
  
  private void checkAndCreatePerformanceHistogram() {
    if (this.perfMetricsHistCounts == null)
      this.perfMetricsHistCounts = new int[20]; 
    if (this.perfMetricsHistBreakpoints == null)
      this.perfMetricsHistBreakpoints = new long[20]; 
  }
  
  private void checkAndCreateTablesAccessedHistogram() {
    if (this.numTablesMetricsHistCounts == null)
      this.numTablesMetricsHistCounts = new int[20]; 
    if (this.numTablesMetricsHistBreakpoints == null)
      this.numTablesMetricsHistBreakpoints = new long[20]; 
  }
  
  public void registerQueryExecutionTime(long queryTimeMs) {
    if (queryTimeMs > this.longestQueryTimeMs) {
      this.longestQueryTimeMs = queryTimeMs;
      repartitionPerformanceHistogram();
    } 
    addToPerformanceHistogram(queryTimeMs, 1);
    if (queryTimeMs < this.shortestQueryTimeMs)
      this.shortestQueryTimeMs = (queryTimeMs == 0L) ? 1L : queryTimeMs; 
    this.numberOfQueriesIssued++;
    this.totalQueryTimeMs += queryTimeMs;
  }
  
  private void repartitionHistogram(int[] histCounts, long[] histBreakpoints, long currentLowerBound, long currentUpperBound) {
    if (this.oldHistCounts == null) {
      this.oldHistCounts = new int[histCounts.length];
      this.oldHistBreakpoints = new long[histBreakpoints.length];
    } 
    System.arraycopy(histCounts, 0, this.oldHistCounts, 0, histCounts.length);
    System.arraycopy(histBreakpoints, 0, this.oldHistBreakpoints, 0, histBreakpoints.length);
    createInitialHistogram(histBreakpoints, currentLowerBound, currentUpperBound);
    for (int i = 0; i < 20; i++)
      addToHistogram(histCounts, histBreakpoints, this.oldHistBreakpoints[i], this.oldHistCounts[i], currentLowerBound, currentUpperBound); 
  }
  
  private void repartitionPerformanceHistogram() {
    checkAndCreatePerformanceHistogram();
    repartitionHistogram(this.perfMetricsHistCounts, this.perfMetricsHistBreakpoints, (this.shortestQueryTimeMs == Long.MAX_VALUE) ? 0L : this.shortestQueryTimeMs, this.longestQueryTimeMs);
  }
  
  private void repartitionTablesAccessedHistogram() {
    checkAndCreateTablesAccessedHistogram();
    repartitionHistogram(this.numTablesMetricsHistCounts, this.numTablesMetricsHistBreakpoints, (this.minimumNumberTablesAccessed == Long.MAX_VALUE) ? 0L : this.minimumNumberTablesAccessed, this.maximumNumberTablesAccessed);
  }
  
  public void reportMetrics(Log log) {
    StringBuilder logMessage = new StringBuilder(256);
    logMessage.append("** Performance Metrics Report **\n");
    logMessage.append("\nLongest reported query: " + this.longestQueryTimeMs + " ms");
    logMessage.append("\nShortest reported query: " + this.shortestQueryTimeMs + " ms");
    logMessage.append("\nAverage query execution time: " + (this.totalQueryTimeMs / this.numberOfQueriesIssued) + " ms");
    logMessage.append("\nNumber of statements executed: " + this.numberOfQueriesIssued);
    logMessage.append("\nNumber of result sets created: " + this.numberOfResultSetsCreated);
    logMessage.append("\nNumber of statements prepared: " + this.numberOfPrepares);
    logMessage.append("\nNumber of prepared statement executions: " + this.numberOfPreparedExecutes);
    if (this.perfMetricsHistBreakpoints != null) {
      logMessage.append("\n\n\tTiming Histogram:\n");
      int maxNumPoints = 20;
      int highestCount = Integer.MIN_VALUE;
      int i;
      for (i = 0; i < 20; i++) {
        if (this.perfMetricsHistCounts[i] > highestCount)
          highestCount = this.perfMetricsHistCounts[i]; 
      } 
      if (highestCount == 0)
        highestCount = 1; 
      for (i = 0; i < 19; i++) {
        if (i == 0) {
          logMessage.append("\n\tless than " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
        } else {
          logMessage.append("\n\tbetween " + this.perfMetricsHistBreakpoints[i] + " and " + this.perfMetricsHistBreakpoints[i + 1] + " ms: \t" + this.perfMetricsHistCounts[i]);
        } 
        logMessage.append("\t");
        int numPointsToGraph = (int)(maxNumPoints * this.perfMetricsHistCounts[i] / highestCount);
        for (int j = 0; j < numPointsToGraph; j++)
          logMessage.append("*"); 
        if (this.longestQueryTimeMs < this.perfMetricsHistCounts[i + 1])
          break; 
      } 
      if (this.perfMetricsHistBreakpoints[18] < this.longestQueryTimeMs) {
        logMessage.append("\n\tbetween ");
        logMessage.append(this.perfMetricsHistBreakpoints[18]);
        logMessage.append(" and ");
        logMessage.append(this.perfMetricsHistBreakpoints[19]);
        logMessage.append(" ms: \t");
        logMessage.append(this.perfMetricsHistCounts[19]);
      } 
    } 
    if (this.numTablesMetricsHistBreakpoints != null) {
      logMessage.append("\n\n\tTable Join Histogram:\n");
      int maxNumPoints = 20;
      int highestCount = Integer.MIN_VALUE;
      int i;
      for (i = 0; i < 20; i++) {
        if (this.numTablesMetricsHistCounts[i] > highestCount)
          highestCount = this.numTablesMetricsHistCounts[i]; 
      } 
      if (highestCount == 0)
        highestCount = 1; 
      for (i = 0; i < 19; i++) {
        if (i == 0) {
          logMessage.append("\n\t" + this.numTablesMetricsHistBreakpoints[i + 1] + " tables or less: \t\t" + this.numTablesMetricsHistCounts[i]);
        } else {
          logMessage.append("\n\tbetween " + this.numTablesMetricsHistBreakpoints[i] + " and " + this.numTablesMetricsHistBreakpoints[i + 1] + " tables: \t" + this.numTablesMetricsHistCounts[i]);
        } 
        logMessage.append("\t");
        int numPointsToGraph = (int)(maxNumPoints * this.numTablesMetricsHistCounts[i] / highestCount);
        for (int j = 0; j < numPointsToGraph; j++)
          logMessage.append("*"); 
        if (this.maximumNumberTablesAccessed < this.numTablesMetricsHistBreakpoints[i + 1])
          break; 
      } 
      if (this.numTablesMetricsHistBreakpoints[18] < this.maximumNumberTablesAccessed) {
        logMessage.append("\n\tbetween ");
        logMessage.append(this.numTablesMetricsHistBreakpoints[18]);
        logMessage.append(" and ");
        logMessage.append(this.numTablesMetricsHistBreakpoints[19]);
        logMessage.append(" tables: ");
        logMessage.append(this.numTablesMetricsHistCounts[19]);
      } 
    } 
    log.logInfo(logMessage);
  }
  
  public void reportNumberOfTablesAccessed(int numTablesAccessed) {
    if (numTablesAccessed < this.minimumNumberTablesAccessed)
      this.minimumNumberTablesAccessed = numTablesAccessed; 
    if (numTablesAccessed > this.maximumNumberTablesAccessed) {
      this.maximumNumberTablesAccessed = numTablesAccessed;
      repartitionTablesAccessedHistogram();
    } 
    addToTablesAccessedHistogram(numTablesAccessed, 1);
  }
  
  public void incrementNumberOfPreparedExecutes() {
    this.numberOfPreparedExecutes++;
    this.numberOfQueriesIssued++;
  }
  
  public void incrementNumberOfPrepares() {
    this.numberOfPrepares++;
  }
  
  public void incrementNumberOfResultSetsCreated() {
    this.numberOfResultSetsCreated++;
  }
  
  public void reportQueryTime(long millisOrNanos) {
    this.queryTimeCount++;
    this.queryTimeSum += millisOrNanos;
    this.queryTimeSumSquares += (millisOrNanos * millisOrNanos);
    this.queryTimeMean = (this.queryTimeMean * (this.queryTimeCount - 1L) + millisOrNanos) / this.queryTimeCount;
  }
  
  public boolean checkAbonormallyLongQuery(long millisOrNanos) {
    boolean res = false;
    if (this.queryTimeCount > 14L) {
      double stddev = Math.sqrt((this.queryTimeSumSquares - this.queryTimeSum * this.queryTimeSum / this.queryTimeCount) / (this.queryTimeCount - 1L));
      res = (millisOrNanos > this.queryTimeMean + 5.0D * stddev);
    } 
    reportQueryTime(millisOrNanos);
    return res;
  }
}
