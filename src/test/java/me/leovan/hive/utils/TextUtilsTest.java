package me.leovan.hive.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextUtilsTest {
    @Test
    public void testCamelCaseToSnakeCase01() {
        String camelCase = "camelCase";
        String snakeCase = "camel_case";
        Assertions.assertEquals(snakeCase, TextUtils.camelCaseToSnakeCase(camelCase));
    }

    @Test
    public void testSnakeCaseToCamelCase01() {
        String snakeCase = "snake_case";
        String camelCase = "snakeCase";
        Assertions.assertEquals(camelCase, TextUtils.snakeCaseToCamelCase(snakeCase));
    }

    @Test
    public void testSnakeCaseToCamelCase02() {
        String snakeCase = "snake__case";
        String camelCase = "snakeCase";
        Assertions.assertEquals(camelCase, TextUtils.snakeCaseToCamelCase(snakeCase));
    }
}
