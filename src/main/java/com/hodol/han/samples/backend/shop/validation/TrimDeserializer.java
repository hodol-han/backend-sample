package com.hodol.han.samples.backend.shop.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import java.io.IOException;

/**
 * A custom Jackson deserializer that trims leading and trailing whitespace from string values. This
 * deserializer checks for the {@link Trim} annotation on a property. If the annotation is present,
 * this deserializer is used to trim the string value. Otherwise, Jackson's standard string
 * deserialization behavior is used. For JSON null values or non-string JSON types, this
 * deserializer will produce a Java null if it is invoked (i.e., for a string type with @Trim).
 */
public class TrimDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

  private static final TrimDeserializer TRIMMING_INSTANCE = new TrimDeserializer();

  /**
   * Default constructor. Jackson uses this to create an initial instance. The {@link
   * #createContextual} method is then called on this instance to obtain the appropriately
   * configured deserializer.
   */
  public TrimDeserializer() {
    // No specific initialization needed here anymore.
  }

  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonToken currentToken = p.currentToken();

    if (currentToken != JsonToken.VALUE_STRING) {
      // If the current token is not a string, return null.
      // This includes JSON null, numbers, booleans, etc.
      return null;
    }

    String text = p.getText();
    // This deserializer now always trims.
    return text.trim();
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
      throws JsonMappingException {
    if (property == null || property.getAnnotation(Trim.class) == null) {
      // No @Trim annotation found on the property, or no property context.
      // Delegate to Jackson's standard string deserializer.
      return StringDeserializer.instance; // No property context, use standard deserializer.
    }

    // @Trim annotation is present, use the shared trimming instance of this deserializer.
    return TRIMMING_INSTANCE;
  }
}
