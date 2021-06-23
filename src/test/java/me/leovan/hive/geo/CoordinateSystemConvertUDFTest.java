package me.leovan.hive.geo;

import me.leovan.hive.udf.geo.CoordinateSystemConvertUDF;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CoordinateSystemConvertUDFTest {
    private final CoordinateSystemConvertUDF udf = new CoordinateSystemConvertUDF();

    @Test
    public void testCoordinateSystemConvertUDF01() throws Exception {
        List<Double> toLatLng = udf.evaluate(39.909175, 116.397452, "GCJ02", "WGS84");

        Assertions.assertEquals(39.907771, toLatLng.get(0), 1e-6);
        Assertions.assertEquals(116.391208, toLatLng.get(1), 1e-6);
    }

    @Test
    public void testCoordinateSystemConvertUDF02() throws Exception {
        List<Double> toLatLng = udf.evaluate(39.909175, 116.397452, "GCJ02", "BD09");

        Assertions.assertEquals(39.915518, toLatLng.get(0), 1e-6);
        Assertions.assertEquals(116.403825, toLatLng.get(1), 1e-6);
    }
}
