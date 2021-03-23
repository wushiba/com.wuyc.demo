package com.yfshop.common.util;

import ch.hsr.geohash.GeoHash;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.distance.DistanceUtils;
import com.spatial4j.core.shape.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * geo工具
 * 基于ch.hsr.geohash和com.spatial4j
 *
 * @author Xulg
 * Created in 2020-03-22 13:51
 */
@SuppressWarnings("WeakerAccess")
public class GeoUtils {

    private static final int MAX_CHARACTER_PRECISION = 12;

    /**
     * 将经度纬度转为geohash base32字符串
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param length    字符长度，最大12
     * @return the geohash base32 string
     */
    public static String toBase32(double latitude, double longitude, int length) {
        return geohash(latitude, longitude, length).toBase32();
    }

    public static GeoHash geohash(double latitude, double longitude, int length) {
        if (length > MAX_CHARACTER_PRECISION) {
            length = MAX_CHARACTER_PRECISION;
        }
        return GeoHash.withCharacterPrecision(latitude, longitude, length);
    }

    /**
     * 获取坐标附近的GeoHash码
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @param length    字符长度，最大12
     * @return the geo hash list
     */
    public static List<String> nearby(double latitude, double longitude, int length) {
        GeoHash geohash = geohash(latitude, longitude, length);
        List<String> list = new ArrayList<>();
        // 当前坐标的GeoHash码
        list.add(geohash.toBase32());
        // 周边8个方位的GeoHash码
        list.addAll(Arrays.stream(geohash.getAdjacent())
                .map(GeoHash::toBase32).collect(Collectors.toList())
        );
        return list;
    }

    /**
     * 球面中，两点间的距离
     *
     * @param longitudeA 经度1
     * @param latitudeA  纬度1
     * @param longitudeB 经度2
     * @param latitudeB  纬度2
     * @return 返回距离，单位km
     */
    public static double distance(double longitudeA, double latitudeA, double longitudeB, double latitudeB) {
        Point pointA = SpatialContext.GEO.makePoint(longitudeA, latitudeA);
        Point pointB = SpatialContext.GEO.makePoint(longitudeB, latitudeB);
        return SpatialContext.GEO.calcDistance(pointB, pointA) * DistanceUtils.DEG_TO_KM;
    }

    /**
     * 半径(米)转GeoHash位数
     *
     * @param radius 半径
     * @return the geo hash length
     */
    @SuppressWarnings("all")
    public static int lengthOfGeoHash(double radius) {
        int result;
        if (radius <= 0) {
            result = MAX_CHARACTER_PRECISION;
        } else if (radius < 1) {
            result = 10;
        } else if (radius < 5) {
            result = 9;
        } else if (radius < 20) {
            result = 8;
        } else if (radius < 77) {
            result = 7;
        } else if (radius < 610) {
            result = 6;
        } else if (radius < 2400) {
            result = 5;
        } else if (radius < 20000) {
            result = 4;
        } else if (radius < 78000) {
            result = 3;
        } else if (radius < 630000) {
            result = 2;
        } else if (radius < 2500000) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(toBase32(39.523423, 40.123121, 12));
        System.out.println(toBase32(39.6, 40.3, 12));
    }
}
