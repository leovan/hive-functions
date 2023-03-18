# Hive 函数

## 简介

本仓库是一系列 Hive 函数的集合，用于提高开发人员的生产力。

## 函数

| 序号 | 分类 | 函数                        | 说明                                                         |
| :--- | :--- | :-------------------------- | :----------------------------------------------------------- |
| 1    | 网络 | `FORMAT_MAC_ADDRESS`        | 格式化 MAC 地址                                              |
| 2    | 网络 | `FORMAT_IP_ADDRESS`         | 格式化 IP 地址                                               |
| 3    | 地理 | `GREAT_CIRCLE_DISTANCE`     | 计算两个坐标之间的距离                                       |
| 4    | 地理 | `GEOHASH_ENCODE`            | 根据给定的经纬度计算指定精度的 Geohash                       |
| 5    | 地理 | `GEOHASH_DECODE`            | 根据 Geohash 计算中心点纬度、中心点经度、边界纬度最小值、边界纬度最大值、边界经度最小值、边界经度最大值 |
| 6    | 地理 | `COORDINATE_SYSTEM_CONVERT` | 根据给定的经纬度以及转换前后坐标系计算转换后坐标系的经纬度   |
| 7    | JSON | `TO_JSON`                   | 将 Hive 结构（例如：`LIST`，`MAP`，`NAMED_STRUCT` 等）转换成为 JSON |
| 8    | JSON | `EXTRACT_JSON`              | 根据 JSON PATH 提取 JSON 中对应的值                          |
| 9    | 文本 | `BLANK_TO_NULL`             | 将空字符串转换为 NULL                                        |

## 许可

The MIT License (MIT)

Copyright (c) 2020-2023 Leo Van
