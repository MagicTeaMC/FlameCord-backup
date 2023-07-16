package com.google.protobuf;

public final class RpcUtil {
  public static <Type extends Message> RpcCallback<Type> specializeCallback(RpcCallback<Message> originalCallback) {
    return (RpcCallback)originalCallback;
  }
  
  public static <Type extends Message> RpcCallback<Message> generalizeCallback(final RpcCallback<Type> originalCallback, final Class<Type> originalClass, final Type defaultInstance) {
    return new RpcCallback<Message>() {
        public void run(Message parameter) {
          Message message;
          try {
            message = originalClass.cast(parameter);
          } catch (ClassCastException ignored) {
            message = (Message)RpcUtil.copyAsType((Type)defaultInstance, parameter);
          } 
          originalCallback.run(message);
        }
      };
  }
  
  private static <Type extends Message> Type copyAsType(Type typeDefaultInstance, Message source) {
    return (Type)typeDefaultInstance.newBuilderForType().mergeFrom(source).build();
  }
  
  public static <ParameterType> RpcCallback<ParameterType> newOneTimeCallback(final RpcCallback<ParameterType> originalCallback) {
    return new RpcCallback<ParameterType>() {
        private boolean alreadyCalled = false;
        
        public void run(ParameterType parameter) {
          synchronized (this) {
            if (this.alreadyCalled)
              throw new RpcUtil.AlreadyCalledException(); 
            this.alreadyCalled = true;
          } 
          originalCallback.run(parameter);
        }
      };
  }
  
  public static final class AlreadyCalledException extends RuntimeException {
    private static final long serialVersionUID = 5469741279507848266L;
    
    public AlreadyCalledException() {
      super("This RpcCallback was already called and cannot be called multiple times.");
    }
  }
}
