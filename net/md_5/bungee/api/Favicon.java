package net.md_5.bungee.api;

import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.NonNull;

public class Favicon {
  private Favicon(@NonNull String encoded) {
    if (encoded == null)
      throw new NullPointerException("encoded is marked non-null but is null"); 
    this.encoded = encoded;
  }
  
  private static final TypeAdapter<Favicon> FAVICON_TYPE_ADAPTER = new TypeAdapter<Favicon>() {
      public void write(JsonWriter out, Favicon value) throws IOException {
        if (value == null) {
          out.nullValue();
        } else {
          out.value(value.getEncoded());
        } 
      }
      
      public Favicon read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        if (peek == JsonToken.NULL) {
          in.nextNull();
          return null;
        } 
        String enc = in.nextString();
        return (enc == null) ? null : Favicon.create(enc);
      }
    };
  
  @NonNull
  private final String encoded;
  
  public static TypeAdapter<Favicon> getFaviconTypeAdapter() {
    return FAVICON_TYPE_ADAPTER;
  }
  
  @NonNull
  public String getEncoded() {
    return this.encoded;
  }
  
  public static Favicon create(BufferedImage image) {
    byte[] imageBytes;
    Preconditions.checkArgument((image != null), "image is null");
    if (image.getWidth() != 64 || image.getHeight() != 64)
      throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels"); 
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      ImageIO.write(image, "PNG", stream);
      imageBytes = stream.toByteArray();
    } catch (IOException e) {
      throw new AssertionError(e);
    } 
    String encoded = "data:image/png;base64," + BaseEncoding.base64().encode(imageBytes);
    if (encoded.length() > 32767)
      throw new IllegalArgumentException("Favicon file too large for server to process"); 
    return new Favicon(encoded);
  }
  
  @Deprecated
  public static Favicon create(String encodedString) {
    return new Favicon(encodedString);
  }
}
