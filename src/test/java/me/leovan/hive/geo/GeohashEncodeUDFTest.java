package me.leovan.hive.geo;

import me.leovan.hive.udf.geo.GeohashEncodeUDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeohashEncodeUDFTest {
    private final GeohashEncodeUDF udf = new GeohashEncodeUDF();

    @Test
    public void testGeohashEncodeUDF01() throws Exception {
        Assertions.assertNull(udf.evaluate(null, 1., 10));
        Assertions.assertNull(udf.evaluate(1., null, 10));
        Assertions.assertNull(udf.evaluate(1., 210., 10));
        Assertions.assertNull(udf.evaluate(91., 1., 10));

        Assertions.assertEquals("wx4g09n", udf.evaluate(39.909175, 116.397452, 7));
    }

    @Test
    public void testGeohashEncodeUDFException01() {
        Assertions.assertThrows(UDFArgumentException.class, () -> udf.evaluate(1., 1., 13));
    }
}
