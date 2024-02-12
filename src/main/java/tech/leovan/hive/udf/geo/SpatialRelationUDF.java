package tech.leovan.hive.udf.geo;

import tech.leovan.hive.udf.utils.GeoUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.locationtech.spatial4j.shape.Shape;

import java.util.Arrays;

@Description(
        name = "SPATIAL_RELATION",
        value = "_FUNC_(GEO_STRING_1, GEO_STRING_2, GEO_STRING_FORMAT) - 计算两个地理区域的空间关系"
)
public class SpatialRelationUDF extends UDF {
    public String evaluate(
            String geoString1,
            String geoString2,
            String geoStringFormat) throws Exception {
        if (geoString1 == null ||
                geoString2 == null ||
                geoStringFormat == null) {
            return null;
        }

        if (!Arrays.asList(GeoUtils.GEO_STRING_FORMAT).contains(
                geoStringFormat.toLowerCase())) {
            throw new UDFArgumentException("格式 [GEO_STR_FORMAT] 应为 " +
                    Arrays.toString(GeoUtils.GEO_STRING_FORMAT) + " 其中一种");
        }

        Shape shape1 = GeoUtils.readShape(geoString1, geoStringFormat);
        Shape shape2 = GeoUtils.readShape(geoString2, geoStringFormat);
        if (shape1 == null || shape2 == null) {
            return null;
        }

        return shape1.relate(shape2).name();
    }

    public String evaluate(String geoWkt1, String geoWkt2) throws Exception {
        return this.evaluate(geoWkt1, geoWkt2, "wkt");
    }
}
