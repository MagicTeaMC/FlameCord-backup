package com.google.protobuf;

public final class FieldMaskProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_FieldMask_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_FieldMask_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n google/protobuf/field_mask.proto\022\017google.protobuf\"\032\n\tFieldMask\022\r\n\005paths\030\001 \003(\tB\001\n\023com.google.protobufB\016FieldMaskProtoP\001Z2google.golang.org/protobuf/types/known/fieldmaskpbø\001\001¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    internal_static_google_protobuf_FieldMask_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_FieldMask_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_FieldMask_descriptor, new String[] { "Paths" });
  }
}
