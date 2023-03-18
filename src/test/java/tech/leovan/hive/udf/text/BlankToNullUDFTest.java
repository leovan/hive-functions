package tech.leovan.hive.udf.text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BlankToNullUDFTest {
    private final BlankToNullUDF udf = new BlankToNullUDF();

    @Test
    public void testBlankToNullUDF() {
        Assertions.assertNull(udf.evaluate(""));
        Assertions.assertNull(udf.evaluate("\t"));
        Assertions.assertNull(udf.evaluate("\n"));
        Assertions.assertNull(udf.evaluate(" "));
        Assertions.assertEquals(" not null", udf.evaluate(" not null"));
        Assertions.assertEquals("not null ",udf.evaluate("not null "));
        Assertions.assertEquals(" not null ",udf.evaluate(" not null "));
    }

    @Test
    public void testBlankToNullUDFTrim() {
        Assertions.assertEquals("\t", udf.evaluate("\t", false));
    }
}
