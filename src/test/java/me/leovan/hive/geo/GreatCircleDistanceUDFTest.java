package me.leovan.hive.geo;

import me.leovan.hive.udf.geo.GreatCircleDistanceUDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GreatCircleDistanceUDFTest {
    private final GreatCircleDistanceUDF udf = new GreatCircleDistanceUDF();

    @Test
    public void testGreatCircleDistanceUDF01() throws Exception {
        Assertions.assertEquals(1068., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707), 1e0);
        Assertions.assertEquals(1068., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707, "gcj02", "mean"), 1e0);
        Assertions.assertEquals(1069., udf.evaluate(39.909175, 116.397452, 31.239698, 121.499707, "gcj02", "equatorial"), 1e0);
    }
}
