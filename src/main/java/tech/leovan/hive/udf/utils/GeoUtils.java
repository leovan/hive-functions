package tech.leovan.hive.udf.utils;

import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 参考：
 * 1. https://gist.github.com/jp1017/71bd0976287ce163c11a7cb963b04dd8
 * 2. https://github.com/wandergis/coordtransform
 */
public class GeoUtils {
    private static final JtsSpatialContext CTX = JtsSpatialContext.GEO;

    /**
     * 扁率
     */
    public static final double EE = 0.00669342162296594323;

    /**
     * 半径（米）
     */
    public static final double EARTH_RADIUS_M = 6378245.0;

    /**
     * PI
     */
    public static final double PI = 3.1415926535897932384626;

    /**
     * X PI
     */
    public static final double X_PI = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 坐标系
     */
    public static final String[] GEO_COORDINATE_SYSTEMS = new String[]{"gcj02", "wgs84", "bd09"};

    /**
     * 地球半径类型
     */
    public static final String[] EARTH_RADIUS_TYPE = new String[]{"mean", "equatorial"};

    /**
     * 地理文本格式
     */
    public static final String[] GEO_STRING_FORMAT = new String[]{"wkt", "geojson"};

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    private static final char[] BASE_32 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    private static final String BASE_32_STRING;

    static {
        // Copied from org.elasticsearch.geometry.utils.Geohash
        BASE_32_STRING = new String(BASE_32);
    }

    /**
     * Description:
     *   根据 GCJ-02 逻辑计算坐标是否位于中国之外
     * @param lat 维度
     * @param lng 经度
     * @return true: 中国之外，false: 中国之内
     */
    public static boolean outOfChinaGCJ02(double lat, double lng) {
        return !(lng > 73.66 && lng < 135.05 && lat > 3.86 && lat < 53.55);
    }

    /**
     * Description:
     *   经度转换
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经度
     */
    public static double transformLongitude(double lat, double lng) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * Description:
     *   转换纬度
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后纬度
     */
    public static double transformLatitude(double lat, double lng) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * Description:
     *   GCJ02 -> WGS84
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] GCJ02ToWGS84(double lat, double lng) {
        if (outOfChinaGCJ02(lat, lng)) {
            return new double[] { lat, lng };
        }

        double dLat = transformLatitude(lat - 35.0, lng - 105.0);
        double dLng = transformLongitude(lat - 35.0, lng - 105.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS_M * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS_M / sqrtMagic * Math.cos(radLat) * PI);
        double magicLat = lat + dLat;
        double magicLng = lng + dLng;

        return new double[] { lat * 2 - magicLat, lng * 2 - magicLng };
    }

    /**
     * Description:
     *   GCJ02 -> BD09
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] GCJ02ToBD09(double lat, double lng) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * X_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * X_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;

        return new double[] { bd_lat, bd_lng };
    }

    /**
     * Description:
     *   WGS84 -> GCJ02
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] WGS84ToGCJ02(double lat, double lng) {
        if (outOfChinaGCJ02(lat, lng)) {
            return new double[] { lat, lng };
        }

        double dLat = transformLatitude(lat - 35.0, lng - 105.0);
        double dLng = transformLongitude(lat - 35.0, lng - 105.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((EARTH_RADIUS_M * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (EARTH_RADIUS_M / sqrtMagic * Math.cos(radLat) * PI);
        double magicLat = lat + dLat;
        double magicLng = lng + dLng;

        return new double[] { magicLat, magicLng };
    }

    /**
     * Description:
     *   WGS84 -> BD09
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] WGS84ToBD09(double lat, double lng) {
        double[] gcj02 = WGS84ToGCJ02(lat, lng);
        return GCJ02ToBD09(gcj02[0], gcj02[1]);
    }

    /**
     * Description:
     *   BD09 -> GCJ02
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] BD09ToGCJ02(double lat, double lng) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * X_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * X_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);

        return new double[] { gg_lat, gg_lng };
    }

    /**
     * Description:
     *   BD09 -> WGS84
     * @param lat 纬度
     * @param lng 经度
     * @return 转换后经纬度
     */
    public static double[] BD09ToWGS84(double lat, double lng) {
        double[] gcj92 = BD09ToGCJ02(lat, lng);
        return GCJ02ToWGS84(gcj92[0], gcj92[1]);
    }

    /**
     * Description:
     *   读取 Shape
     * @param shapeString Shape 文本
     * @param shapeStringFormat Shape 文本格式
     * @return Shape
     */
    public static Shape readShape(String shapeString, String shapeStringFormat) {
        Shape shape = null;

        try {
            if ("wkt".equalsIgnoreCase(shapeStringFormat)) {
                shape = CTX.getFormats().getWktReader().read(shapeString);
            } else if ("geojson".equalsIgnoreCase(shapeStringFormat)) {
                shape = CTX.getFormats().getGeoJsonReader().read(shapeString);
            }
        } catch (Exception ignored) {}

        return shape;
    }

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    public static Collection<? extends CharSequence> getNeighbors(String geohash) {
        return addNeighborsAtLevel(geohash, geohash.length(), new ArrayList(8));
    }

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    public static <E extends Collection<? super String>> void addNeighbors(String geohash, E neighbors) {
        addNeighborsAtLevel(geohash, geohash.length(), neighbors);
    }

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    public static <E extends Collection<? super String>> E addNeighborsAtLevel(String geohash, int level, E neighbors) {
        String south = getNeighbor(geohash, level, 0, -1);
        String north = getNeighbor(geohash, level, 0, 1);
        if (north != null) {
            neighbors.add(getNeighbor(north, level, -1, 0));
            neighbors.add(north);
            neighbors.add(getNeighbor(north, level, 1, 0));
        }

        neighbors.add(getNeighbor(geohash, level, -1, 0));
        neighbors.add(getNeighbor(geohash, level, 1, 0));
        if (south != null) {
            neighbors.add(getNeighbor(south, level, -1, 0));
            neighbors.add(south);
            neighbors.add(getNeighbor(south, level, 1, 0));
        }

        return neighbors;
    }

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    public static String getNeighbor(String geohash, int level, int dx, int dy) {
        int cell = BASE_32_STRING.indexOf(geohash.charAt(level - 1));
        int x0 = cell & 1;
        int y0 = cell & 2;
        int x1 = cell & 4;
        int y1 = cell & 8;
        int x2 = cell & 16;
        int x = x0 + x1 / 2 + x2 / 4;
        int y = y0 / 2 + y1 / 4;
        if (level != 1) {
            int nx = level % 2 == 1 ? x + dx : x + dy;
            int ny = level % 2 == 1 ? y + dy : y + dx;
            if (nx >= 0 && nx <= 7 && ny >= 0 && ny <= 3) {
                String var10000 = geohash.substring(0, level - 1);
                return var10000 + encodeBase32(nx, ny);
            } else {
                String neighbor = getNeighbor(geohash, level - 1, dx, dy);
                return neighbor != null ? neighbor + encodeBase32(nx, ny) : neighbor;
            }
        } else {
            return (dy >= 0 || y != 0) && (dy <= 0 || y != 3) ? Character.toString(encodeBase32(x + dx, y + dy)) : null;
        }
    }

    /**
     * Copied from org.elasticsearch.geometry.utils.Geohash
     */
    private static char encodeBase32(int x, int y) {
        return BASE_32[((x & 1) + (y & 1) * 2 + (x & 2) * 2 + (y & 2) * 4 + (x & 4) * 4) % 32];
    }
}
