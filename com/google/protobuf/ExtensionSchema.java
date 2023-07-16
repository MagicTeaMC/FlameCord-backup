package com.google.protobuf;

import java.io.IOException;
import java.util.Map;

@CheckReturnValue
abstract class ExtensionSchema<T extends FieldSet.FieldDescriptorLite<T>> {
  abstract boolean hasExtensions(MessageLite paramMessageLite);
  
  abstract FieldSet<T> getExtensions(Object paramObject);
  
  abstract void setExtensions(Object paramObject, FieldSet<T> paramFieldSet);
  
  abstract FieldSet<T> getMutableExtensions(Object paramObject);
  
  abstract void makeImmutable(Object paramObject);
  
  abstract <UT, UB> UB parseExtension(Object paramObject1, Reader paramReader, Object paramObject2, ExtensionRegistryLite paramExtensionRegistryLite, FieldSet<T> paramFieldSet, UB paramUB, UnknownFieldSchema<UT, UB> paramUnknownFieldSchema) throws IOException;
  
  abstract int extensionNumber(Map.Entry<?, ?> paramEntry);
  
  abstract void serializeExtension(Writer paramWriter, Map.Entry<?, ?> paramEntry) throws IOException;
  
  abstract Object findExtensionByNumber(ExtensionRegistryLite paramExtensionRegistryLite, MessageLite paramMessageLite, int paramInt);
  
  abstract void parseLengthPrefixedMessageSetItem(Reader paramReader, Object paramObject, ExtensionRegistryLite paramExtensionRegistryLite, FieldSet<T> paramFieldSet) throws IOException;
  
  abstract void parseMessageSetItem(ByteString paramByteString, Object paramObject, ExtensionRegistryLite paramExtensionRegistryLite, FieldSet<T> paramFieldSet) throws IOException;
}
