package tech.leovan.hive.udf.geo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UDFGreatCircleDistanceTest {
    private final UDFGreatCircleDistance udf = new UDFGreatCircleDistance();

    @Test
    public void testGreatCircleDistanceUDF() throws Exception {
        Assertions.assertEquals(1068000., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707), 1e3);
        Assertions.assertEquals(1068000., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707, "gcj02", "mean"), 1e3);
        Assertions.assertEquals(1069000., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707, "gcj02", "equatorial"), 1e3);
    }
}
