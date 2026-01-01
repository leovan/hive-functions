---
icon: lucide/earth
---

# 地理相关函数

## 函数

### `COORDINATE_SYSTEM_CONVERT`

根据给定的经纬度以及转换前后坐标系计算转换后坐标系的经纬度。

```sql
CREATE TEMPORARY FUNCTION
  COORDINATE_SYSTEM_CONVERT
AS
  'tech.leovan.hive.udf.geo.UDFCoordinateSystemConvert';
```

#### 参数

- LATITUDE（必选）：`DOUBLE` 类型，坐标纬度
- LONGITUDE（必选）：`DOUBLE` 类型，坐标经度
- FROM（必选）：`STRING` 类型，转换前坐标系
    - `WGS84`：https://zh.wikipedia.org/wiki/世界大地测量系统
    - `GCJ02`：https://zh.wikipedia.org/wiki/中华人民共和国测绘限制
    - `BD09`：https://lbsyun.baidu.com/index.php?title=coordinate
- TO（必选）：`STRING` 类型，转换后坐标系
    - `WGS84`：https://zh.wikipedia.org/wiki/世界大地测量系统
    - `GCJ02`：https://zh.wikipedia.org/wiki/中华人民共和国测绘限制
    - `BD09`：https://lbsyun.baidu.com/index.php?title=coordinate

#### 返回值

转换后坐标系的经纬度：`ARRAY<DOUBLE>` 类型

#### 示例

SQL：

```sql
SELECT
  COORDINATE_SYSTEM_CONVERT(39.909175, 116.397452, 'GCJ02', 'WGS84')
;
```

输出结果为：

```txt
[
  39.90777149066542,
  116.39120837626726
]
```

### `GEOHASH_DECODE`

根据 Geohash 计算中心点纬度、中心点经度、边界纬度最小值、边界纬度最大值、边界经度最小值、边界经度最大值。

```sql
CREATE TEMPORARY FUNCTION
  GEOHASH_DECODE
AS
  'tech.leovan.hive.udf.geo.UDFGeohashDecode';
```

#### 参数

- GEOHASH（必选）：`STRING` 类型，Geohash

#### 返回值

中心点纬度、中心点经度、边界纬度最小值、边界纬度最大值、边界经度最小值、边界经度最大值的数组：`ARRAY<DOUBLE>` 类型

#### 示例

SQL：

```sql
SELECT
  GEOHASH_DECODE('wx4g09n')
;
```

输出结果为：

```txt
[
  39.90852355957031,
  116.39808654785156,
  39.9078369140625,
  39.909210205078125,
  116.39739990234375,
  116.39877319335938
]
```

### `GEOHASH_ENCODE`

根据给定的经纬度计算指定精度的 Geohash。

```sql
CREATE TEMPORARY FUNCTION
  GEOHASH_ENCODE
AS
  'tech.leovan.hive.udf.geo.UDFGeohashEncode';
```

#### 参数

- LATITUDE（必选）：`DOUBLE` 类型，坐标纬度
- LONGITUDE（必选）：`DOUBLE` 类型，坐标经度
- PRECISION（必选）：`INTEGER` 类型，Geohash 精度，取值范围 $\left[1, 12\right]$

#### 返回值

Geohash 编码：`STRING` 类型

#### 示例

SQL：

```sql
SELECT
  GEOHASH_ENCODE(39.909175, 116.397452, 7)
;
```

输出结果为：

```txt
wx4g09n
```

### `GREAT_CIRCLE_DISTANCE`

计算两个坐标之间的距离。

```sql
CREATE TEMPORARY FUNCTION
  GREAT_CIRCLE_DISTANCE
AS
  'tech.leovan.hive.udf.geo.UDFGreatCircleDistance';
```

#### 参数

- FROM_LATITUDE（必选）：`DOUBLE` 类型，起始点纬度
- FROM_LONGITUDE（必选）：`DOUBLE` 类型，起始点经度
- TO_LATITUDE（必选）：`DOUBLE` 类型，结束点纬度
- TO_LONGITUDE（必选）：`DOUBLE` 类型，结束点经度
- COORDINATE_SYSTEM（可选）：`STRING` 类型，坐标系，默认为 `GCJ02`
    - `WGS84`：https://zh.wikipedia.org/wiki/世界大地测量系统
    - `GCJ02`：https://zh.wikipedia.org/wiki/中华人民共和国测绘限制
    - `BD09`：https://lbsyun.baidu.com/index.php?title=coordinate
- EARTH_RADIUS_TYPE（可选）：`STRING` 类型，地球半径类型，默认为 `MEAN`
    - `MEAN`：平均半径，6371008.7714 米
    - `EQUATORIAL`：赤道半径，6378137 米

#### 返回值

距离（单位：米）：`DOUBLE` 类型

#### 示例

SQL：

```sql
SELECT
  GREAT_CIRCLE_DISTANCE(39.909175, 116.397452, 31.239698, 121.499707)
;
```

输出结果为：

```txt
1067.9805366585604
```

SQL：

```sql
SELECT
  GREAT_CIRCLE_DISTANCE(
      39.909175, 116.397452, 31.239698, 121.499707,
      'gcj02', 'mean')
;
```

输出结果为：

```txt
1067.9805366585604
```

SQL：

```sql
SELECT
  GREAT_CIRCLE_DISTANCE(
      39.909175, 116.397452, 31.239698, 121.499707,
      'gcj02', 'equatorial')
;
```

输出结果为：

```txt
1069.1754509458906
```

### `POLYGON_AREA`

计算多边形面积。

```sql
CREATE TEMPORARY FUNCTION
  POLYGON_AREA
AS
  'tech.leovan.hive.udf.geo.UDFPolygonArea';
```

#### 参数

- POLYGON_STRING（必选）：`STRING` 类型，多边形文本，仅支持 Polygon 和 MultiPolygon
- POLYGON_STRING_FORMAT（可选）：`STRING` 类型，多边形文本格式，仅支持 `WKT`、`GEOJSON`，默认为 `WKT`

#### 返回值

面积（单位：平方米）：`DOUBLE` 类型

#### 示例

SQL：

```sql
SELECT
  POLYGON_AREA(
    "POLYGON ((..., ..., ...)",
    "WKT"
  )
;
```

输出结果为：

```txt
1.0
```

### `POLYGON_GEOHASH`

计算覆盖多边形的 Geohash 列表。

```sql
CREATE TEMPORARY FUNCTION
  POLYGON_GEOHASH
AS
  'tech.leovan.hive.udf.geo.UDFPolygonGeohash';
```

#### 参数

- POLYGON_STRING（必选）：`STRING` 类型，多边形文本，仅支持 Polygon 和 MultiPolygon
- PRECISION（必选）：`INTEGER` 类型，Geohash 精度，取值范围 $\left[1, 12\right]$
- POLYGON_STRING_FORMAT（可选）：`STRING` 类型，多边形文本格式，仅支持 `WKT`、`GEOJSON`，默认为 `WKT`

#### 返回值

Geohash 列表：`ARRAY<STRING>` 类型

#### 示例

SQL：

```sql
SELECT
  POLYGON_AREA(
    "POLYGON ((..., ..., ...)",
    8,
    "WKT"
  )
;
```

输出结果为：

```txt
[
  ...,
  ...,
  ...
]
```

### `SPATIAL_RELATION`

计算两个地理区域的空间关系。

```sql
CREATE TEMPORARY FUNCTION
  SPATIAL_RELATION
AS
  'tech.leovan.hive.udf.geo.UDFSpatialRelation';
```

#### 参数

- GEO_STRING_1（必选）：`STRING` 类型，地理区域 1 文本
- GEO_STRING_2（必选）：`INTEGER` 类型，地理区域 2 文本
- GEO_STRING_FORMAT（可选）：`STRING` 类型，地理区域文本格式，仅支持 `WKT`、`GEOJSON`，默认为 `WKT`

#### 返回值

关系名称：`STRING` 类型

可能的返回值如下：

| 关系名称   | 关系描述 |
| ---------- | -------- |
| WITHIN     | 在其中   |
| CONTAINS   | 包含     |
| DISJOINT   | 不相交   |
| INTERSECTS | 相交     |

#### 示例

SQL：

```sql
SELECT
  SPATIAL_RELATION(
    "POINT (..., ...",
    "POLYGON ((..., ..., ...)",
    "WKT"
  )
;
```

输出结果为：

```txt
WITHIN
```

## 附录

### Geohash 误差

| GeoHash 精度 | 纬度 Bits | 经度 Bits | 纬度误差    | 经度误差    | 距离误差（千米） |
| ------------ | --------- | --------- | ----------- | ----------- | ---------------- |
| 1            | 2         | 3         | ±23         | ±23         | ±2500            |
| 2            | 5         | 5         | ±2.8        | ±5.6        | ±630             |
| 3            | 7         | 8         | ±0.70       | ±0.70       | ±78              |
| 4            | 10        | 10        | ±0.087      | ±0.18       | ±20              |
| 5            | 12        | 13        | ±0.022      | ±0.022      | ±2.4             |
| 6            | 15        | 15        | ±0.0027     | ±0.0055     | ±0.61            |
| 7            | 17        | 18        | ±0.00068    | ±0.00068    | ±0.076           |
| 8            | 20        | 20        | ±0.000085   | ±0.00017    | ±0.01911         |
| 9            | 22        | 23        | ±0.000021   | ±0.000021   | ±0.00478         |
| 10           | 25        | 25        | ±0.00000268 | ±0.00000536 | ±0.0005971       |
| 11           | 27        | 28        | ±0.00000067 | ±0.00000067 | ±0.0001492       |
| 12           | 30        | 30        | ±0.00000008 | ±0.00000017 | ±0.00000186      |
