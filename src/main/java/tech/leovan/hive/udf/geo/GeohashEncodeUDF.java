package tech.leovan.hive.udf.geo;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.locationtech.spatial4j.io.GeohashUtils;

@Description(
        name = "GEOHASH_ENCODE",
        value = "_FUNC_(LATITUDE, LONGITUDE, PRECISION) - 根据给定的经纬度 [LATITUDE] 和 [LONGITUDE] 计算指定" + "" +
                "精度 [PRECISION] 的 Geohash"
)
public class GeohashEncodeUDF extends UDF {
    public String evaluate(Double latitude, Double longitude, Integer precision) throws Exception {
        if (precision < 1 || precision > 12) {
            throw new UDFArgumentException("精度 [PRECISION] 应取 [1, 12] 范围内的整数值");
        }

        if (longitude == null ||
                latitude == null ||
                Math.abs(longitude) > 180. ||
                Math.abs(latitude) > 90.) {
            return null;
        }

        try {
            return GeohashUtils.encodeLatLon(latitude, longitude, precision);
        } catch (Exception e) {
            return null;
        }
    }
}
