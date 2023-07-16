package org.eclipse.sisu.bean;

import java.lang.reflect.Member;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class DeclaredMembers implements Iterable<Member> {
  private final Class<?> clazz;
  
  private final View[] views;
  
  public DeclaredMembers(Class<?> clazz, View... views) {
    this.clazz = clazz;
    this.views = (views.length == 0) ? View.values() : views;
  }
  
  public Iterator<Member> iterator() {
    return new MemberIterator(this.clazz, this.views);
  }
  
  private static final class MemberIterator implements Iterator<Member> {
    private static final Member[] NO_MEMBERS = new Member[0];
    
    private Class<?> clazz;
    
    private final DeclaredMembers.View[] views;
    
    private int viewIndex;
    
    private Member[] members = NO_MEMBERS;
    
    private int memberIndex;
    
    MemberIterator(Class<?> clazz, DeclaredMembers.View[] views) {
      this.clazz = clazz;
      this.views = views;
    }
    
    public boolean hasNext() {
      while (this.memberIndex <= 0) {
        if (this.viewIndex >= this.views.length) {
          this.clazz = this.clazz.getSuperclass();
          this.viewIndex = 0;
        } 
        if (this.clazz == null || this.clazz == Object.class)
          return false; 
        int index = this.viewIndex++;
        this.members = this.views[index].members(this.clazz);
        this.memberIndex = this.members.length;
      } 
      return true;
    }
    
    public Member next() {
      if (hasNext())
        return this.members[--this.memberIndex]; 
      throw new NoSuchElementException();
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public enum View {
    CONSTRUCTORS {
      Member[] members(Class<?> clazz) {
        return (Member[])clazz.getDeclaredConstructors();
      }
    },
    METHODS {
      Member[] members(Class<?> clazz) {
        return (Member[])clazz.getDeclaredMethods();
      }
    },
    FIELDS {
      Member[] members(Class<?> clazz) {
        return (Member[])clazz.getDeclaredFields();
      }
    };
    
    abstract Member[] members(Class<?> param1Class);
  }
}
