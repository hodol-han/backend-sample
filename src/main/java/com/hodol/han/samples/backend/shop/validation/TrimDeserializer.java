package com.hodol.han.samples.backend.shop.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;

public class TrimDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

  private final boolean shouldTrim;

  // 기본 생성자는 trim 안 함
  public TrimDeserializer() {
    this(false);
  }

  private TrimDeserializer(boolean shouldTrim) {
    this.shouldTrim = shouldTrim;
  }

  @Override
  public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    String text = p.getValueAsString();
    if (shouldTrim && text != null) {
      return text.trim();
    }
    return text;
  }

  @Override
  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
      throws JsonMappingException {
    if (property != null) {
      // 필드에 @Trim 애노테이션이 붙어 있으면 trim 활성화
      Trim ann = property.getAnnotation(Trim.class);
      if (ann == null) {
        ann = property.getContextAnnotation(Trim.class);
      }
      if (ann != null) {
        return new TrimDeserializer(true);
      }
    }
    // 그 외에는 그대로
    return this;
  }
}
