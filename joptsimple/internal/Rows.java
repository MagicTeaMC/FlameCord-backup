package joptsimple.internal;

import java.util.ArrayList;
import java.util.List;

public class Rows {
  private final int overallWidth;
  
  private final int columnSeparatorWidth;
  
  private final List<Row> rows = new ArrayList<>();
  
  private int widthOfWidestOption;
  
  private int widthOfWidestDescription;
  
  public Rows(int overallWidth, int columnSeparatorWidth) {
    this.overallWidth = overallWidth;
    this.columnSeparatorWidth = columnSeparatorWidth;
  }
  
  public void add(String option, String description) {
    add(new Row(option, description));
  }
  
  private void add(Row row) {
    this.rows.add(row);
    this.widthOfWidestOption = Math.max(this.widthOfWidestOption, row.option.length());
    this.widthOfWidestDescription = Math.max(this.widthOfWidestDescription, row.description.length());
  }
  
  public void reset() {
    this.rows.clear();
    this.widthOfWidestOption = 0;
    this.widthOfWidestDescription = 0;
  }
  
  public void fitToWidth() {
    Columns columns = new Columns(optionWidth(), descriptionWidth());
    List<Row> fitted = new ArrayList<>();
    for (Row each : this.rows)
      fitted.addAll(columns.fit(each)); 
    reset();
    for (Row each : fitted)
      add(each); 
  }
  
  public String render() {
    StringBuilder buffer = new StringBuilder();
    for (Row each : this.rows) {
      pad(buffer, each.option, optionWidth()).append(Strings.repeat(' ', this.columnSeparatorWidth));
      pad(buffer, each.description, descriptionWidth()).append(Strings.LINE_SEPARATOR);
    } 
    return buffer.toString();
  }
  
  private int optionWidth() {
    return Math.min((this.overallWidth - this.columnSeparatorWidth) / 2, this.widthOfWidestOption);
  }
  
  private int descriptionWidth() {
    return Math.min(this.overallWidth - optionWidth() - this.columnSeparatorWidth, this.widthOfWidestDescription);
  }
  
  private StringBuilder pad(StringBuilder buffer, String s, int length) {
    buffer.append(s).append(Strings.repeat(' ', length - s.length()));
    return buffer;
  }
}
