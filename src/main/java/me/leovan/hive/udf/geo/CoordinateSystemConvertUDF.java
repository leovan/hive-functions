package me.leovan.hive.udf.geo;

import me.leovan.hive.utils.GeoUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Description(
        name = "COORDINATE_SYSTEM_CONVERT",
        value = "_FUNC_(LATITUDE, LONGITUDE, FROM, TO) - 根据给定的经纬度 [LATITUDE] 和 [LONGITUDE] 以及转换前后" +
                "坐标系 [FROM] 和 [TO] 计算转换后坐标系的经纬度"
)
public class CoordinateSystemConvertUDF extends UDF {
    public List<Double> evaluate(Double latitude, Double longitude, String from, String to) throws Exception {
        from = from.trim().toLowerCase();
        to = to.trim().toLowerCase();

        if (!(Arrays.asList(GeoUtils.GEO_COORDINATE_SYSTEMS).contains(from) ||
                Arrays.asList(GeoUtils.GEO_COORDINATE_SYSTEMS).contains(to))) {
            throw new UDFArgumentException(
                    String.format("坐标系 [FROM] 和 [TO] 应为 [%s]",
                            String.join(",", GeoUtils.GEO_COORDINATE_SYSTEMS)));
        }

        if (longitude == null ||
                latitude == null ||
                Math.abs(longitude) > 180. ||
                Math.abs(latitude) > 90.) {
            return Arrays.asList(latitude, longitude);
        }

        switch (from) {
            case "gcj02":
                if (to.equals("wgs84")) {
                    return Arrays.stream(GeoUtils.GCJ02ToWGS84(latitude, longitude)).boxed().collect(Collectors.toList());
                } else if (to.equals("bd09")) {
                    return Arrays.stream(GeoUtils.GCJ02ToBD09(latitude, longitude)).boxed().collect(Collectors.toList());
                }
                break;
            case "wgs84":
                if (to.equals("gcj02")) {
                    return Arrays.stream(GeoUtils.WGS84ToGCJ02(latitude, longitude)).boxed().collect(Collectors.toList());
                } else if (to.equals("bd09")) {
                    return Arrays.stream(GeoUtils.WGS84ToBD09(latitude, longitude)).boxed().collect(Collectors.toList());
                }
                break;
            case "bd09":
                if (to.equals("gcj02")) {
                    return Arrays.stream(GeoUtils.BD09ToGCJ02(latitude, longitude)).boxed().collect(Collectors.toList());
                } else if (to.equals("wgs84")) {
                    return Arrays.stream(GeoUtils.BD09ToWGS84(latitude, longitude)).boxed().collect(Collectors.toList());
                }
                break;
        }

        return Arrays.asList(latitude, longitude);
    }
}
