package com.google.protobuf;

public final class SourceContextProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_SourceContext_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_SourceContext_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n$google/protobuf/source_context.proto\022\017google.protobuf\"\"\n\rSourceContext\022\021\n\tfile_name\030\001 \001(\tB\001\n\023com.google.protobufB\022SourceContextProtoP\001Z6google.golang.org/protobuf/types/known/sourcecontextpb¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    internal_static_google_protobuf_SourceContext_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_SourceContext_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_SourceContext_descriptor, new String[] { "FileName" });
  }
}
