package com.google.protobuf;

public final class ApiProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_Api_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Api_fieldAccessorTable;
  
  static final Descriptors.Descriptor internal_static_google_protobuf_Method_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Method_fieldAccessorTable;
  
  static final Descriptors.Descriptor internal_static_google_protobuf_Mixin_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Mixin_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\031google/protobuf/api.proto\022\017google.protobuf\032$google/protobuf/source_context.proto\032\032google/protobuf/type.proto\"\002\n\003Api\022\f\n\004name\030\001 \001(\t\022(\n\007methods\030\002 \003(\0132\027.google.protobuf.Method\022(\n\007options\030\003 \003(\0132\027.google.protobuf.Option\022\017\n\007version\030\004 \001(\t\0226\n\016source_context\030\005 \001(\0132\036.google.protobuf.SourceContext\022&\n\006mixins\030\006 \003(\0132\026.google.protobuf.Mixin\022'\n\006syntax\030\007 \001(\0162\027.google.protobuf.Syntax\"Õ\001\n\006Method\022\f\n\004name\030\001 \001(\t\022\030\n\020request_type_url\030\002 \001(\t\022\031\n\021request_streaming\030\003 \001(\b\022\031\n\021response_type_url\030\004 \001(\t\022\032\n\022response_streaming\030\005 \001(\b\022(\n\007options\030\006 \003(\0132\027.google.protobuf.Option\022'\n\006syntax\030\007 \001(\0162\027.google.protobuf.Syntax\"#\n\005Mixin\022\f\n\004name\030\001 \001(\t\022\f\n\004root\030\002 \001(\tBv\n\023com.google.protobufB\bApiProtoP\001Z,google.golang.org/protobuf/types/known/apipb¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[] { SourceContextProto.getDescriptor(), 
          TypeProto.getDescriptor() });
    internal_static_google_protobuf_Api_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_Api_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Api_descriptor, new String[] { "Name", "Methods", "Options", "Version", "SourceContext", "Mixins", "Syntax" });
    internal_static_google_protobuf_Method_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_google_protobuf_Method_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Method_descriptor, new String[] { "Name", "RequestTypeUrl", "RequestStreaming", "ResponseTypeUrl", "ResponseStreaming", "Options", "Syntax" });
    internal_static_google_protobuf_Mixin_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_google_protobuf_Mixin_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Mixin_descriptor, new String[] { "Name", "Root" });
    SourceContextProto.getDescriptor();
    TypeProto.getDescriptor();
  }
}
