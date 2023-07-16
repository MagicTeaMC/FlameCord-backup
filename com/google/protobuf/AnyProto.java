package com.google.protobuf;

public final class AnyProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_Any_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Any_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\031google/protobuf/any.proto\022\017google.protobuf\"&\n\003Any\022\020\n\btype_url\030\001 \001(\t\022\r\n\005value\030\002 \001(\fBv\n\023com.google.protobufB\bAnyProtoP\001Z,google.golang.org/protobuf/types/known/anypb¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    internal_static_google_protobuf_Any_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_Any_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Any_descriptor, new String[] { "TypeUrl", "Value" });
  }
}
