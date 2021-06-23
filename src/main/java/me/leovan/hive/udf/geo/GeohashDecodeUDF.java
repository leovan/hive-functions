package me.leovan.hive.udf.geo;

import com.google.common.collect.Lists;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.io.GeohashUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Rectangle;

import java.util.Arrays;
import java.util.List;

@Description(
        name = "GEOHASH_DECODE",
        value = "_FUNC_(GEOHASH) - 根据 Geohash 计算 [中心点纬度] [中心点经度] [边界纬度最小值] " +
                "[边界纬度最大值] [边界经度最小值] [边界经度最大值]"
)
public class GeohashDecodeUDF extends UDF {
    public static final SpatialContext ctx = SpatialContext.GEO;

    public List<Double> evaluate(String geohash) {
        List<Double> coordinates = Lists.newArrayList();

        try {
            Point centerPoint = GeohashUtils.decode(geohash, ctx);
            coordinates.add(centerPoint.getLat());
            coordinates.add(centerPoint.getLon());

            Rectangle geohashRect = GeohashUtils.decodeBoundary(geohash, ctx);
            coordinates.add(geohashRect.getMinY());
            coordinates.add(geohashRect.getMaxY());
            coordinates.add(geohashRect.getMinX());
            coordinates.add(geohashRect.getMaxX());
        } catch (Exception e) {
            coordinates = Arrays.asList(null, null, null, null, null, null);
        }

        return coordinates;
    }

}
