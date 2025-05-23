package com.hodol.han.samples.backend.shop;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ShopApplicationTests {

  private static final Logger logger = LoggerFactory.getLogger(ShopApplicationTests.class);

  @Test
  void contextLoads() {
    logger.info("Starting contextLoads test...");
  }
}
