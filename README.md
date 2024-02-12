# Hive 函数 <img src="https://raw.githubusercontent.com/leovan/hive-functions/main/docs/images/hive-functions-icon.png" align="right" alt="logo" height="100" style="border: none; float: right; height: 100px;">

![License](https://img.shields.io/github/license/leovan/hive-functions.svg)
![Issues](https://img.shields.io/github/issues/leovan/hive-functions.svg)
![Build](https://img.shields.io/travis/com/leovan/hive-functions.svg)

---

## 简介

本仓库是一系列 Hive 函数的集合，用于提高开发人员的生产力。

## 函数

| 序号 | 分类     | 函数                         | 说明                                                         |
| :--- | :------- | :--------------------------- | :----------------------------------------------------------- |
| 1    | 日期时间 | `DATETIME_INTERVAL_RELATION` | 计算两个日期时间区间的关系                                   |
| 2    | 地理     | `COORDINATE_SYSTEM_CONVERT`  | 根据给定的经纬度以及转换前后坐标系计算转换后坐标系的经纬度   |
| 3    | 地理     | `GEOHASH_DECODE`             | 根据 Geohash 计算中心点纬度、中心点经度、边界纬度最小值、边界纬度最大值、边界经度最小值、边界经度最大值 |
| 4    | 地理     | `GEOHASH_ENCODE`             | 根据给定的经纬度计算指定精度的 Geohash                       |
| 5    | 地理     | `GREAT_CIRCLE_DISTANCE`      | 计算两个坐标之间的距离                                       |
| 6    | 地理     | `POLYGON_AREA`               | 计算多边形面积                                               |
| 7    | 地理     | `POLYGON_GEOHASH`            | 计算覆盖多边形的 Geohash 列表                                |
| 8    | 地理     | `SPATIAL_RELATION`           | 计算两个地理区域的空间关系                                   |
| 9    | JSON     | `EXTRACT_JSON`               | 根据 JSON PATH 提取 JSON 中对应的值                          |
| 10   | JSON     | `TO_JSON`                    | 将 Hive 结构（例如：`LIST`，`MAP`，`NAMED_STRUCT` 等）转换成为 JSON |
| 11   | 网络     | `FORMAT_IP_ADDRESS`          | 格式化 IP 地址                                               |
| 12   | 网络     | `FORMAT_MAC_ADDRESS`         | 格式化 MAC 地址                                              |
| 13   | 文本     | `BLANK_TO_NULL`              | 将空字符串转换为 NULL                                        |

## 许可

The MIT License (MIT)

版权所有 &copy; 2021-2024，<a href="https://leovan.me" target="_blank">范叶亮 | Leo Van</a>
