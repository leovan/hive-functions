package tech.leovan.hive.udf.geo;

import org.elasticsearch.geometry.utils.Geohash;
import tech.leovan.hive.udf.utils.GeoUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.io.GeohashUtils;
import org.locationtech.spatial4j.shape.Shape;

import java.util.*;

@Description(
        name = "POLYGON_GEOHASH",
        value = "_FUNC_(POLYGON_STRING, PRECISION, POLYGON_STRING_FORMAT) - 计算覆盖多边形的 Geohash 列表"
)
public class PolygonGeohashUDF extends UDF {
    private static final JtsSpatialContext CTX = JtsSpatialContext.GEO;

    public List<String> evaluate(
            String polygonString,
            Integer precision,
            String polygonStringFormat) throws Exception {
        if (polygonString == null || precision == null || polygonStringFormat == null) {
            return null;
        }

        if (precision < 1 || precision > 12 ) {
            throw new UDFArgumentException("精度 [PRECISION] 应取 [1, 12] 范围内的整数");
        }

        if (!Arrays.asList(GeoUtils.GEO_STRING_FORMAT).contains(polygonStringFormat.toLowerCase())) {
            throw new UDFArgumentException("格式 [POLYGON_STRING_FORMAT] 应为 " + Arrays.toString(GeoUtils.GEO_STRING_FORMAT) + " 其中一种");
        }

        Shape shape = GeoUtils.readShape(polygonString, polygonStringFormat);
        if (shape == null) {
            return null;
        }

        Geometry polygon = CTX.getShapeFactory().getGeometryFrom(shape);
        if (!(polygon instanceof Polygon || polygon instanceof MultiPolygon)) {
            return null;
        }

        Point centroid = polygon.getCentroid();

        Set<String> geohashes = new HashSet<>();
        Queue<String> testingGeohashes = new LinkedList<>();
        testingGeohashes.add(GeohashUtils.encodeLatLon(centroid.getY(), centroid.getX(), precision));

        while (!testingGeohashes.isEmpty()) {
            String geohash = testingGeohashes.poll();
            Geometry geohashGeometry = CTX.getShapeFactory().getGeometryFrom(
                    GeohashUtils.decodeBoundary(geohash, CTX));

            if (polygon.contains(geohashGeometry) || polygon.intersects(geohashGeometry)) {
                geohashes.add(geohash);

                List<String> geohashNeighbors = new ArrayList<>();
                Geohash.addNeighbors(geohash, geohashNeighbors);

                for (String geohashNeighbor : geohashNeighbors) {
                    if (!geohashes.contains(geohashNeighbor) && !testingGeohashes.contains(geohashNeighbor)) {
                        testingGeohashes.add(geohashNeighbor);
                    }
                }
            }
        }

        return new ArrayList<>(geohashes);
    }

    public List<String> evaluate(
            String polygonStr,
            Integer precision) throws Exception {
        return this.evaluate(polygonStr, precision, "wkt");
    }
}
