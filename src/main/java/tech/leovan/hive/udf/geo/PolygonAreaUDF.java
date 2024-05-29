package tech.leovan.hive.udf.geo;

import tech.leovan.hive.udf.utils.GeoUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.shape.Shape;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

@Description(
        name = "POLYGON_AREA",
        value = "_FUNC_(POLYGON_STRING, POLYGON_STRING_FORMAT) - 计算多边形面积（单位：平方米）"
)
public class PolygonAreaUDF extends UDF {
    public static final JtsSpatialContext CTX = JtsSpatialContext.GEO;
    public static CoordinateReferenceSystem SOURCE_CRS;
    public static CoordinateReferenceSystem TARGET_CRS;

    static {
        try {
            SOURCE_CRS = CRS.decode("CRS:84");  // WGS 84 经纬度
            TARGET_CRS = CRS.decode("EPSG:3857");  // Web 墨卡托投影
        } catch (Exception ignored) {}
    }

    public Double evaluate(String polygonString, String polygonStringFormat) {
        if (polygonString == null) {
            return null;
        }

        Shape shape = GeoUtils.readShape(polygonString, polygonStringFormat);
        if (shape == null) {
            return null;
        }

        Geometry geometry = CTX.getShapeFactory().getGeometryFrom(shape);
        if (!(geometry instanceof Polygon || geometry instanceof MultiPolygon)) {
            return null;
        }

        try {
            MathTransform transform = CRS.findMathTransform(SOURCE_CRS, TARGET_CRS, false);
            Geometry geometryMercator = JTS.transform(geometry, transform);

            return geometryMercator.getArea();
        } catch (Exception e) {
            return null;
        }
    }

    public Double evaluate(String polygonWkt) {
        return this.evaluate(polygonWkt, "wkt");
    }
}

