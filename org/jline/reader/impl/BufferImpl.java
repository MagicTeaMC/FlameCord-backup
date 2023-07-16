package org.jline.reader.impl;

import java.util.Objects;
import org.jline.reader.Buffer;

public class BufferImpl implements Buffer {
  private int cursor = 0;
  
  private int cursorCol = -1;
  
  private int[] buffer;
  
  private int g0;
  
  private int g1;
  
  public BufferImpl() {
    this(64);
  }
  
  public BufferImpl(int size) {
    this.buffer = new int[size];
    this.g0 = 0;
    this.g1 = this.buffer.length;
  }
  
  private BufferImpl(BufferImpl buffer) {
    this.cursor = buffer.cursor;
    this.cursorCol = buffer.cursorCol;
    this.buffer = (int[])buffer.buffer.clone();
    this.g0 = buffer.g0;
    this.g1 = buffer.g1;
  }
  
  public BufferImpl copy() {
    return new BufferImpl(this);
  }
  
  public int cursor() {
    return this.cursor;
  }
  
  public int length() {
    return this.buffer.length - this.g1 - this.g0;
  }
  
  public boolean currChar(int ch) {
    if (this.cursor == length())
      return false; 
    this.buffer[adjust(this.cursor)] = ch;
    return true;
  }
  
  public int currChar() {
    if (this.cursor == length())
      return 0; 
    return atChar(this.cursor);
  }
  
  public int prevChar() {
    if (this.cursor <= 0)
      return 0; 
    return atChar(this.cursor - 1);
  }
  
  public int nextChar() {
    if (this.cursor >= length() - 1)
      return 0; 
    return atChar(this.cursor + 1);
  }
  
  public int atChar(int i) {
    if (i < 0 || i >= length())
      return 0; 
    return this.buffer[adjust(i)];
  }
  
  private int adjust(int i) {
    return (i >= this.g0) ? (i + this.g1 - this.g0) : i;
  }
  
  public void write(int c) {
    write(new int[] { c });
  }
  
  public void write(int c, boolean overTyping) {
    if (overTyping)
      delete(1); 
    write(new int[] { c });
  }
  
  public void write(CharSequence str) {
    Objects.requireNonNull(str);
    write(str.codePoints().toArray());
  }
  
  public void write(CharSequence str, boolean overTyping) {
    Objects.requireNonNull(str);
    int[] ucps = str.codePoints().toArray();
    if (overTyping)
      delete(ucps.length); 
    write(ucps);
  }
  
  private void write(int[] ucps) {
    moveGapToCursor();
    int len = length() + ucps.length;
    int sz = this.buffer.length;
    if (sz < len) {
      while (sz < len)
        sz *= 2; 
      int[] nb = new int[sz];
      System.arraycopy(this.buffer, 0, nb, 0, this.g0);
      System.arraycopy(this.buffer, this.g1, nb, this.g1 + sz - this.buffer.length, this.buffer.length - this.g1);
      this.g1 += sz - this.buffer.length;
      this.buffer = nb;
    } 
    System.arraycopy(ucps, 0, this.buffer, this.cursor, ucps.length);
    this.g0 += ucps.length;
    this.cursor += ucps.length;
    this.cursorCol = -1;
  }
  
  public boolean clear() {
    if (length() == 0)
      return false; 
    this.g0 = 0;
    this.g1 = this.buffer.length;
    this.cursor = 0;
    this.cursorCol = -1;
    return true;
  }
  
  public String substring(int start) {
    return substring(start, length());
  }
  
  public String substring(int start, int end) {
    if (start >= end || start < 0 || end > length())
      return ""; 
    if (end <= this.g0)
      return new String(this.buffer, start, end - start); 
    if (start > this.g0)
      return new String(this.buffer, this.g1 - this.g0 + start, end - start); 
    int[] b = (int[])this.buffer.clone();
    System.arraycopy(b, this.g1, b, this.g0, b.length - this.g1);
    return new String(b, start, end - start);
  }
  
  public String upToCursor() {
    return substring(0, this.cursor);
  }
  
  public boolean cursor(int position) {
    if (position == this.cursor)
      return true; 
    return (move(position - this.cursor) != 0);
  }
  
  public int move(int num) {
    int where = num;
    if (this.cursor == 0 && where <= 0)
      return 0; 
    if (this.cursor == length() && where >= 0)
      return 0; 
    if (this.cursor + where < 0) {
      where = -this.cursor;
    } else if (this.cursor + where > length()) {
      where = length() - this.cursor;
    } 
    this.cursor += where;
    this.cursorCol = -1;
    return where;
  }
  
  public boolean up() {
    int col = getCursorCol();
    int pnl = this.cursor - 1;
    while (pnl >= 0 && atChar(pnl) != 10)
      pnl--; 
    if (pnl < 0)
      return false; 
    int ppnl = pnl - 1;
    while (ppnl >= 0 && atChar(ppnl) != 10)
      ppnl--; 
    this.cursor = Math.min(ppnl + col + 1, pnl);
    return true;
  }
  
  public boolean down() {
    int col = getCursorCol();
    int nnl = this.cursor;
    while (nnl < length() && atChar(nnl) != 10)
      nnl++; 
    if (nnl >= length())
      return false; 
    int nnnl = nnl + 1;
    while (nnnl < length() && atChar(nnnl) != 10)
      nnnl++; 
    this.cursor = Math.min(nnl + col + 1, nnnl);
    return true;
  }
  
  public boolean moveXY(int dx, int dy) {
    int col = 0;
    while (prevChar() != 10 && move(-1) == -1)
      col++; 
    this.cursorCol = 0;
    while (dy < 0) {
      up();
      dy++;
    } 
    while (dy > 0) {
      down();
      dy--;
    } 
    col = Math.max(col + dx, 0);
    for (int i = 0; i < col && 
      move(1) == 1 && currChar() != 10; i++);
    this.cursorCol = col;
    return true;
  }
  
  private int getCursorCol() {
    if (this.cursorCol < 0) {
      this.cursorCol = 0;
      int pnl = this.cursor - 1;
      while (pnl >= 0 && atChar(pnl) != 10)
        pnl--; 
      this.cursorCol = this.cursor - pnl - 1;
    } 
    return this.cursorCol;
  }
  
  public int backspace(int num) {
    int count = Math.max(Math.min(this.cursor, num), 0);
    moveGapToCursor();
    this.cursor -= count;
    this.g0 -= count;
    this.cursorCol = -1;
    return count;
  }
  
  public boolean backspace() {
    return (backspace(1) == 1);
  }
  
  public int delete(int num) {
    int count = Math.max(Math.min(length() - this.cursor, num), 0);
    moveGapToCursor();
    this.g1 += count;
    this.cursorCol = -1;
    return count;
  }
  
  public boolean delete() {
    return (delete(1) == 1);
  }
  
  public String toString() {
    return substring(0, length());
  }
  
  public void copyFrom(Buffer buf) {
    if (!(buf instanceof BufferImpl))
      throw new IllegalStateException(); 
    BufferImpl that = (BufferImpl)buf;
    this.g0 = that.g0;
    this.g1 = that.g1;
    this.buffer = (int[])that.buffer.clone();
    this.cursor = that.cursor;
    this.cursorCol = that.cursorCol;
  }
  
  private void moveGapToCursor() {
    if (this.cursor < this.g0) {
      int l = this.g0 - this.cursor;
      System.arraycopy(this.buffer, this.cursor, this.buffer, this.g1 - l, l);
      this.g0 -= l;
      this.g1 -= l;
    } else if (this.cursor > this.g0) {
      int l = this.cursor - this.g0;
      System.arraycopy(this.buffer, this.g1, this.buffer, this.g0, l);
      this.g0 += l;
      this.g1 += l;
    } 
  }
}
