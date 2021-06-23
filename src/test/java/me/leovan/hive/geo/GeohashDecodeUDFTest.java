package me.leovan.hive.geo;

import me.leovan.hive.udf.geo.GeohashDecodeUDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GeohashDecodeUDFTest {
    private final GeohashDecodeUDF udf = new GeohashDecodeUDF();

    @Test
    public void testGeohashDecodeUDF01() {
        List<Double> coordinates = udf.evaluate("wx4g09n");
        Assertions.assertEquals(39.908, coordinates.get(0), 1e-3);
        Assertions.assertEquals(116.398, coordinates.get(1), 1e-3);
        Assertions.assertEquals(39.908, coordinates.get(2), 1e-3);
        Assertions.assertEquals(39.909, coordinates.get(3), 1e-3);
        Assertions.assertEquals(116.397, coordinates.get(4), 1e-3);
        Assertions.assertEquals(116.399, coordinates.get(5), 1e-3);
    }
}
