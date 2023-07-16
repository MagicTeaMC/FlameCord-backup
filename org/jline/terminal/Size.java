package org.jline.terminal;

public class Size {
  private int rows;
  
  private int cols;
  
  public Size() {}
  
  public Size(int columns, int rows) {
    this();
    setColumns(columns);
    setRows(rows);
  }
  
  public int getColumns() {
    return this.cols;
  }
  
  public void setColumns(int columns) {
    this.cols = (short)columns;
  }
  
  public int getRows() {
    return this.rows;
  }
  
  public void setRows(int rows) {
    this.rows = (short)rows;
  }
  
  public int cursorPos(int row, int col) {
    return row * (this.cols + 1) + col;
  }
  
  public void copy(Size size) {
    setColumns(size.getColumns());
    setRows(size.getRows());
  }
  
  public boolean equals(Object o) {
    if (o instanceof Size) {
      Size size = (Size)o;
      return (this.rows == size.rows && this.cols == size.cols);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.rows * 31 + this.cols;
  }
  
  public String toString() {
    return "Size[cols=" + this.cols + ", rows=" + this.rows + ']';
  }
}
