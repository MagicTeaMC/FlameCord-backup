package com.google.protobuf;

public final class StructProto {
  static final Descriptors.Descriptor internal_static_google_protobuf_Struct_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Struct_fieldAccessorTable;
  
  static final Descriptors.Descriptor internal_static_google_protobuf_Struct_FieldsEntry_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Struct_FieldsEntry_fieldAccessorTable;
  
  static final Descriptors.Descriptor internal_static_google_protobuf_Value_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_Value_fieldAccessorTable;
  
  static final Descriptors.Descriptor internal_static_google_protobuf_ListValue_descriptor;
  
  static final GeneratedMessageV3.FieldAccessorTable internal_static_google_protobuf_ListValue_fieldAccessorTable;
  
  private static Descriptors.FileDescriptor descriptor;
  
  public static void registerAllExtensions(ExtensionRegistryLite registry) {}
  
  public static void registerAllExtensions(ExtensionRegistry registry) {
    registerAllExtensions(registry);
  }
  
  public static Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }
  
  static {
    String[] descriptorData = { "\n\034google/protobuf/struct.proto\022\017google.protobuf\"\001\n\006Struct\0223\n\006fields\030\001 \003(\0132#.google.protobuf.Struct.FieldsEntry\032E\n\013FieldsEntry\022\013\n\003key\030\001 \001(\t\022%\n\005value\030\002 \001(\0132\026.google.protobuf.Value:\0028\001\"ê\001\n\005Value\0220\n\nnull_value\030\001 \001(\0162\032.google.protobuf.NullValueH\000\022\026\n\fnumber_value\030\002 \001(\001H\000\022\026\n\fstring_value\030\003 \001(\tH\000\022\024\n\nbool_value\030\004 \001(\bH\000\022/\n\fstruct_value\030\005 \001(\0132\027.google.protobuf.StructH\000\0220\n\nlist_value\030\006 \001(\0132\032.google.protobuf.ListValueH\000B\006\n\004kind\"3\n\tListValue\022&\n\006values\030\001 \003(\0132\026.google.protobuf.Value*\033\n\tNullValue\022\016\n\nNULL_VALUE\020\000B\n\023com.google.protobufB\013StructProtoP\001Z/google.golang.org/protobuf/types/known/structpbø\001\001¢\002\003GPBª\002\036Google.Protobuf.WellKnownTypesb\006proto3" };
    descriptor = Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData, new Descriptors.FileDescriptor[0]);
    internal_static_google_protobuf_Struct_descriptor = getDescriptor().getMessageTypes().get(0);
    internal_static_google_protobuf_Struct_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Struct_descriptor, new String[] { "Fields" });
    internal_static_google_protobuf_Struct_FieldsEntry_descriptor = internal_static_google_protobuf_Struct_descriptor.getNestedTypes().get(0);
    internal_static_google_protobuf_Struct_FieldsEntry_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Struct_FieldsEntry_descriptor, new String[] { "Key", "Value" });
    internal_static_google_protobuf_Value_descriptor = getDescriptor().getMessageTypes().get(1);
    internal_static_google_protobuf_Value_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_Value_descriptor, new String[] { "NullValue", "NumberValue", "StringValue", "BoolValue", "StructValue", "ListValue", "Kind" });
    internal_static_google_protobuf_ListValue_descriptor = getDescriptor().getMessageTypes().get(2);
    internal_static_google_protobuf_ListValue_fieldAccessorTable = new GeneratedMessageV3.FieldAccessorTable(internal_static_google_protobuf_ListValue_descriptor, new String[] { "Values" });
  }
}
