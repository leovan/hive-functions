package tech.leovan.hive.udf.geo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

public class UDFPolygonAreaTest {
    private final UDFPolygonArea udf = new UDFPolygonArea();

    @Test
    public void testPolygonAreaUDFPolygon() throws Exception {
        String polygonStr = new String(
                Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(
                        "geo/polygon-area-polygon.txt").toURI())));

        Double area = udf.evaluate(polygonStr);
        Assertions.assertEquals(29114823.5, area, 1e0);
    }

    @Test
    public void testPolygonAreaUDFMultipolygon() throws Exception {
        String polygonStr = new String(
                Files.readAllBytes(Paths.get(ClassLoader.getSystemResource(
                        "geo/polygon-area-multipolygon.txt").toURI())));

        Double area = udf.evaluate(polygonStr);
        Assertions.assertEquals(13079153376.1, area, 1e0);
    }
}
