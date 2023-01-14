package me.leovan.hive.udf.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExtractJsonUDFTest {
    private final ExtractJsonUDF udf = new ExtractJsonUDF();

    @Test
    public void testExtractJsonUDFValue() {
        String jsonStr = "{\"k1\": \"v1\", \"k2\": 3}";

        Assertions.assertEquals("\"v1\"", udf.evaluate(jsonStr, "$.k1"));
        Assertions.assertEquals("3", udf.evaluate(jsonStr, "$.k2"));
    }

    @Test
    public void testExtractJsonUDFArray() {
        String jsonStr = "{\"k\": [\"v\", 3]}";

        Assertions.assertEquals("[\"v\",3]", udf.evaluate(jsonStr, "$.k[*]"));
        Assertions.assertEquals("[\"v\",3]", udf.evaluate(jsonStr, "$.k"));
        Assertions.assertEquals("\"v\"", udf.evaluate(jsonStr, "$.k[0]"));
        Assertions.assertEquals("3", udf.evaluate(jsonStr, "$.k[1]"));
        Assertions.assertNull(udf.evaluate(jsonStr, "$.k[2]"));
    }

    @Test
    public void testExtractJsonUDFMap() {
        String jsonStr = "{\"m\": {\"k1\": \"v1\", \"k2\": 3}}";

        Assertions.assertEquals("{\"k1\":\"v1\",\"k2\":3}", udf.evaluate(jsonStr, "$.m"));
        Assertions.assertEquals("3", udf.evaluate(jsonStr, "$.m.k2"));
    }
}
