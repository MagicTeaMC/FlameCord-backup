package com.google.protobuf;

public class SingleFieldBuilderV3<MType extends AbstractMessage, BType extends AbstractMessage.Builder, IType extends MessageOrBuilder> implements AbstractMessage.BuilderParent {
  private AbstractMessage.BuilderParent parent;
  
  private BType builder;
  
  private MType message;
  
  private boolean isClean;
  
  public SingleFieldBuilderV3(MType message, AbstractMessage.BuilderParent parent, boolean isClean) {
    this.message = (MType)Internal.<AbstractMessage>checkNotNull((AbstractMessage)message);
    this.parent = parent;
    this.isClean = isClean;
  }
  
  public void dispose() {
    this.parent = null;
  }
  
  public MType getMessage() {
    if (this.message == null)
      this.message = (MType)this.builder.buildPartial(); 
    return this.message;
  }
  
  public MType build() {
    this.isClean = true;
    return getMessage();
  }
  
  public BType getBuilder() {
    if (this.builder == null) {
      this.builder = (BType)this.message.newBuilderForType(this);
      this.builder.mergeFrom((Message)this.message);
      this.builder.markClean();
    } 
    return this.builder;
  }
  
  public IType getMessageOrBuilder() {
    if (this.builder != null)
      return (IType)this.builder; 
    return (IType)this.message;
  }
  
  public SingleFieldBuilderV3<MType, BType, IType> setMessage(MType message) {
    this.message = (MType)Internal.<AbstractMessage>checkNotNull((AbstractMessage)message);
    if (this.builder != null) {
      this.builder.dispose();
      this.builder = null;
    } 
    onChanged();
    return this;
  }
  
  public SingleFieldBuilderV3<MType, BType, IType> mergeFrom(MType value) {
    if (this.builder == null && this.message == this.message.getDefaultInstanceForType()) {
      this.message = value;
    } else {
      getBuilder().mergeFrom((Message)value);
    } 
    onChanged();
    return this;
  }
  
  public SingleFieldBuilderV3<MType, BType, IType> clear() {
    this
      
      .message = (MType)((this.message != null) ? (AbstractMessage)this.message.getDefaultInstanceForType() : (AbstractMessage)this.builder.getDefaultInstanceForType());
    if (this.builder != null) {
      this.builder.dispose();
      this.builder = null;
    } 
    onChanged();
    return this;
  }
  
  private void onChanged() {
    if (this.builder != null)
      this.message = null; 
    if (this.isClean && this.parent != null) {
      this.parent.markDirty();
      this.isClean = false;
    } 
  }
  
  public void markDirty() {
    onChanged();
  }
}
