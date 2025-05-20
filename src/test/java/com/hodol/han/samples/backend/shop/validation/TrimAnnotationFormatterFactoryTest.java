package com.hodol.han.samples.backend.shop.validation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

class TrimAnnotationFormatterFactoryTest {
  private TrimAnnotationFormatterFactory factory;
  private Trim annotation;
  private Parser<String> parser;
  private Printer<String> printer;

  private Locale locale;

  @BeforeEach
  void setUp() {
    factory = new TrimAnnotationFormatterFactory();
    // Obtain a dummy Trim instance via annotation on class
    annotation = this.getClass().getAnnotation(Trim.class);
    parser = factory.getParser(annotation, String.class);
    printer = factory.getPrinter(annotation, String.class);

    locale = Locale.getDefault();
  }

  @Test
  void parseShouldTrimText() throws Exception {
    assertEquals("abc", parser.parse("  abc  ", locale));
    assertEquals("", parser.parse("", locale));
  }

  @Test
  void printShouldReturnTextUnchanged() {
    assertEquals(" abc ", printer.print(" abc ", locale));
    assertEquals("", printer.print("", locale));
  }

  @Test
  void getFieldTypesShouldContainString() {
    assertTrue(factory.getFieldTypes().contains(String.class));
  }
}
