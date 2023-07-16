package org.jline.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractTerminal;

public class Status {
  protected final AbstractTerminal terminal;
  
  protected final boolean supported;
  
  protected List<AttributedString> oldLines = Collections.emptyList();
  
  protected List<AttributedString> linesToRestore = Collections.emptyList();
  
  protected int rows;
  
  protected int columns;
  
  protected boolean force;
  
  protected boolean suspended = false;
  
  protected AttributedString borderString;
  
  protected int border = 0;
  
  public static Status getStatus(Terminal terminal) {
    return getStatus(terminal, true);
  }
  
  public static Status getStatus(Terminal terminal, boolean create) {
    return (terminal instanceof AbstractTerminal) ? (
      (AbstractTerminal)terminal).getStatus(create) : 
      null;
  }
  
  public Status(AbstractTerminal terminal) {
    this.terminal = Objects.<AbstractTerminal>requireNonNull(terminal, "terminal can not be null");
    this
      
      .supported = (terminal.getStringCapability(InfoCmp.Capability.change_scroll_region) != null && terminal.getStringCapability(InfoCmp.Capability.save_cursor) != null && terminal.getStringCapability(InfoCmp.Capability.restore_cursor) != null && terminal.getStringCapability(InfoCmp.Capability.cursor_address) != null);
    if (this.supported) {
      char borderChar = '─';
      AttributedStringBuilder bb = new AttributedStringBuilder();
      for (int i = 0; i < 200; i++)
        bb.append(borderChar); 
      this.borderString = bb.toAttributedString();
      resize();
    } 
  }
  
  public void setBorder(boolean border) {
    this.border = border ? 1 : 0;
  }
  
  public void resize() {
    Size size = this.terminal.getSize();
    this.rows = size.getRows();
    this.columns = size.getColumns();
    this.force = true;
  }
  
  public void reset() {
    this.force = true;
  }
  
  public void hardReset() {
    if (this.suspended)
      return; 
    List<AttributedString> lines = new ArrayList<>(this.oldLines);
    int b = this.border;
    update(null);
    this.border = b;
    update(lines);
  }
  
  public void redraw() {
    if (this.suspended)
      return; 
    update(this.oldLines);
  }
  
  public void clear() {
    privateClear(this.oldLines.size());
  }
  
  private void clearAll() {
    int b = this.border;
    this.border = 0;
    privateClear(this.oldLines.size() + b);
  }
  
  private void privateClear(int statusSize) {
    List<AttributedString> as = new ArrayList<>();
    for (int i = 0; i < statusSize; i++)
      as.add(new AttributedString("")); 
    if (!as.isEmpty())
      update(as); 
  }
  
  public void update(List<AttributedString> lines) {
    if (!this.supported)
      return; 
    if (lines == null)
      lines = Collections.emptyList(); 
    if (this.suspended) {
      this.linesToRestore = new ArrayList<>(lines);
      return;
    } 
    if (lines.isEmpty())
      clearAll(); 
    if (this.oldLines.equals(lines) && !this.force)
      return; 
    int statusSize = lines.size() + ((lines.size() == 0) ? 0 : this.border);
    int nb = statusSize - this.oldLines.size() - ((this.oldLines.size() == 0) ? 0 : this.border);
    if (nb > 0) {
      int j;
      for (j = 0; j < nb; j++)
        this.terminal.puts(InfoCmp.Capability.cursor_down, new Object[0]); 
      for (j = 0; j < nb; j++)
        this.terminal.puts(InfoCmp.Capability.cursor_up, new Object[0]); 
    } 
    this.terminal.puts(InfoCmp.Capability.save_cursor, new Object[0]);
    this.terminal.puts(InfoCmp.Capability.cursor_address, new Object[] { Integer.valueOf(this.rows - statusSize), Integer.valueOf(0) });
    if (!this.terminal.puts(InfoCmp.Capability.clr_eos, new Object[0]))
      for (int j = this.rows - statusSize; j < this.rows; j++) {
        this.terminal.puts(InfoCmp.Capability.cursor_address, new Object[] { Integer.valueOf(j), Integer.valueOf(0) });
        this.terminal.puts(InfoCmp.Capability.clr_eol, new Object[0]);
      }  
    if (this.border == 1 && lines.size() > 0) {
      this.terminal.puts(InfoCmp.Capability.cursor_address, new Object[] { Integer.valueOf(this.rows - statusSize), Integer.valueOf(0) });
      this.borderString.columnSubSequence(0, this.columns).print((Terminal)this.terminal);
    } 
    for (int i = 0; i < lines.size(); i++) {
      this.terminal.puts(InfoCmp.Capability.cursor_address, new Object[] { Integer.valueOf(this.rows - lines.size() + i), Integer.valueOf(0) });
      if (((AttributedString)lines.get(i)).length() > this.columns) {
        AttributedStringBuilder asb = new AttributedStringBuilder();
        asb.append(((AttributedString)lines.get(i)).substring(0, this.columns - 3)).append("...", new AttributedStyle(AttributedStyle.INVERSE));
        asb.toAttributedString().columnSubSequence(0, this.columns).print((Terminal)this.terminal);
      } else {
        ((AttributedString)lines.get(i)).columnSubSequence(0, this.columns).print((Terminal)this.terminal);
      } 
    } 
    this.terminal.puts(InfoCmp.Capability.change_scroll_region, new Object[] { Integer.valueOf(0), Integer.valueOf(this.rows - 1 - statusSize) });
    this.terminal.puts(InfoCmp.Capability.restore_cursor, new Object[0]);
    this.terminal.flush();
    this.oldLines = new ArrayList<>(lines);
    this.force = false;
  }
  
  public void suspend() {
    if (this.suspended)
      return; 
    this.linesToRestore = new ArrayList<>(this.oldLines);
    int b = this.border;
    update(null);
    this.border = b;
    this.suspended = true;
  }
  
  public void restore() {
    if (!this.suspended)
      return; 
    this.suspended = false;
    update(this.linesToRestore);
    this.linesToRestore = Collections.emptyList();
  }
  
  public int size() {
    return this.oldLines.size() + this.border;
  }
}
