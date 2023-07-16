package com.google.protobuf;

public final class EmptyProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_Empty_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Empty_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\033google/protobuf/empty.proto\022\017google.protobuf\"\007\n\005EmptyB}\n\023com.google.protobufB\nEmptyProtoP\001Z.google.golang.org/protobuf/types/known/emptypbø\001\001¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    internal_static_google_protobuf_Empty_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_Empty_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Empty_descriptor, new String[0]);
  }
}
