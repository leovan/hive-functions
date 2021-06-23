package me.leovan.hive.udf.geo;

import me.leovan.hive.utils.GeoUtils;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.locationtech.spatial4j.distance.DistanceUtils;

import java.util.Arrays;

@Description(
        name = "GREAT_CIRCLE_DISTANCE",
        value = "_FUNC_(FROM_LATITUDE, FROM_LONGITUDE, TO_LATITUDE, TO_LONGITUDE, COORDINATE_SYSTEM, EARTH_RADIUS_TYPE) - " +
                "计算两个坐标之间的距离"
)
public class GreatCircleDistanceUDF extends UDF {
    public Double evaluate(
            Double fromLatitude,
            Double fromLongitude,
            Double toLatitude,
            Double toLongitude) throws Exception {
        return evaluate(fromLatitude, fromLongitude, toLatitude, toLongitude, "gcj02", "mean");
    }
    public Double evaluate(
            Double fromLatitude,
            Double fromLongitude,
            Double toLatitude,
            Double toLongitude,
            String coordinateSystem) throws Exception {
        return evaluate(fromLatitude, fromLongitude, toLatitude, toLongitude, coordinateSystem, "mean");
    }

    public Double evaluate(
            Double fromLatitude,
            Double fromLongitude,
            Double toLatitude,
            Double toLongitude,
            String coordinateSystem,
            String earthRadiusType) throws Exception {
        coordinateSystem = coordinateSystem.toLowerCase();
        earthRadiusType = earthRadiusType.toLowerCase();

        if (!(Arrays.asList(GeoUtils.GEO_COORDINATE_SYSTEMS).contains(coordinateSystem))) {
            throw new UDFArgumentException(
                    String.format("坐标系 [COORDINATE_SYSTEM] 应为 [%s]",
                            String.join(",", GeoUtils.GEO_COORDINATE_SYSTEMS)));
        }

        if (!(Arrays.asList(GeoUtils.EARTH_RADIUS_TYPE).contains(earthRadiusType))) {
            throw new UDFArgumentException(
                    String.format("地球半径类型 [EARTH_RADIUS_TYPE] 应为 [%s]",
                            String.join(",", GeoUtils.EARTH_RADIUS_TYPE)));
        }

        double[] fromLatLngWGS84 = new double[]{fromLatitude, fromLongitude};
        double[] toLatLngWGS84 = new double[]{toLatitude, toLongitude};

        switch (coordinateSystem) {
            case "gcj02":
                fromLatLngWGS84 = GeoUtils.GCJ02ToWGS84(fromLatitude, fromLongitude);
                toLatLngWGS84 = GeoUtils.GCJ02ToWGS84(toLatitude, toLongitude);
                break;
            case "bd09":
                fromLatLngWGS84 = GeoUtils.BD09ToWGS84(fromLatitude, fromLongitude);
                toLatLngWGS84 = GeoUtils.BD09ToWGS84(toLatitude, toLongitude);
                break;
        }

        double fromLatitudeRadians = DistanceUtils.toRadians(fromLatLngWGS84[0]);
        double fromLongitudeRadians = DistanceUtils.toRadians(fromLatLngWGS84[1]);
        double toLatitudeRadians = DistanceUtils.toRadians(toLatLngWGS84[0]);
        double toLongitudeRadians = DistanceUtils.toRadians(toLatLngWGS84[1]);

        double distRadians = DistanceUtils.distLawOfCosinesRAD(
                fromLatitudeRadians, fromLongitudeRadians,
                toLatitudeRadians, toLongitudeRadians);

        double earthRadius = DistanceUtils.EARTH_MEAN_RADIUS_KM;

        switch (earthRadiusType) {
            case "mean":
                earthRadius = DistanceUtils.EARTH_MEAN_RADIUS_KM;
                break;
            case "equatorial":
                earthRadius = DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM;
                break;
        }

        return DistanceUtils.radians2Dist(distRadians, earthRadius);
    }
}
