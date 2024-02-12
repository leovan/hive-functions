package tech.leovan.hive.udf.geo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PolygonAreaUDFTest {
    private final PolygonAreaUDF udf = new PolygonAreaUDF();

    @Test
    public void testPolygonAreaUDFPolygon() throws Exception {
        String polygonStr = new String(
                Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(
                        "geo/polygon-area-polygon.txt").getPath())));

        Double area = udf.evaluate(polygonStr);
        Assertions.assertEquals(29114823.5, area, 1e0);
    }

    @Test
    public void testPolygonAreaUDFMultipolygon() throws Exception {
        String polygonStr = new String(
                Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(
                        "geo/polygon-area-multipolygon.txt").getPath())));

        Double area = udf.evaluate(polygonStr);
        Assertions.assertEquals(13079153376.1, area, 1e0);
    }
}
