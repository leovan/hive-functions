# 地理相关函数

## 函数

### `GREAT_CIRCLE_DISTANCE`

计算两个坐标之间的距离。

```sql
CREATE TEMPORARY FUNCTION
  GREAT_CIRCLE_DISTANCE
AS
  'tech.leovan.hive.udf.geo.GreatCircleDistanceUDF';
```

#### 参数

- FROM_LATITUDE（必选）：`DOUBLE` 类型，起始点纬度
- FROM_LONGITUDE（必选）：`DOUBLE` 类型，起始点经度
- TO_LATITUDE（必选）：`DOUBLE` 类型，结束点纬度
- TO_LONGITUDE（必选）：`DOUBLE` 类型，结束点经度
- COORDINATE_SYSTEM（可选）：`STRING` 类型，坐标系
    - `WGS84`：https://zh.wikipedia.org/wiki/世界大地测量系统
    - `GCJ02`：https://zh.wikipedia.org/wiki/中华人民共和国测绘限制
    - `BD09`：https://lbsyun.baidu.com/index.php?title=coordinate
- EARTH_RADIUS_TYPE（可选）：`STRING` 类型，地球半径类型
    - `MEAN`：平均半径，6371.0087714 千米
    - `EQUATORIAL`：赤道半径，6378.137 千米

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

### `GEOHASH_ENCODE`

根据给定的经纬度计算指定精度的 Geohash。

```sql
CREATE TEMPORARY FUNCTION
  GEOHASH_ENCODE
AS
  'tech.leovan.hive.udf.geo.GeohashEncodeUDF';
```

#### 参数

- LATITUDE（必选）：`DOUBLE` 类型，坐标纬度
- LONGITUDE（必选）：`DOUBLE` 类型，坐标经度
- PRECISION（必选）：`INTEGER` 类型，Geohash 精度

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

### `GEOHASH_DECODE`

根据 Geohash 计算中心点纬度、中心点经度、边界纬度最小值、边界纬度最大值、边界经度最小值、边界经度最大值。

```sql
CREATE TEMPORARY FUNCTION
  GEOHASH_DECODE
AS
  'tech.leovan.hive.udf.geo.GeohashDecodeUDF';
```

#### 参数

- GEOHASH（必选）：`STRING` 类型，Geohash

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

### `COORDINATE_SYSTEM_CONVERT`

根据给定的经纬度以及转换前后坐标系计算转换后坐标系的经纬度。

```sql
CREATE TEMPORARY FUNCTION
  COORDINATE_SYSTEM_CONVERT
AS
  'tech.leovan.hive.udf.geo.CoordinateSystemConvertUDF';
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

## 附录

### Geohash 误差

| GeoHash 精度 | 纬度 Bits | 经度 Bits |  纬度误差   |  经度误差   | 距离误差（千米） |
| :----------: | :-------: | :-------: | :---------: | :---------: | :--------------: |
|      1       |     2     |     3     |     ±23     |     ±23     |      ±2500       |
|      2       |     5     |     5     |    ±2.8     |    ±5.6     |       ±630       |
|      3       |     7     |     8     |    ±0.70    |    ±0.70    |       ±78        |
|      4       |    10     |    10     |   ±0.087    |    ±0.18    |       ±20        |
|      5       |    12     |    13     |   ±0.022    |   ±0.022    |       ±2.4       |
|      6       |    15     |    15     |   ±0.0027   |   ±0.0055   |      ±0.61       |
|      7       |    17     |    18     |  ±0.00068   |  ±0.00068   |      ±0.076      |
|      8       |    20     |    20     |  ±0.000085  |  ±0.00017   |     ±0.01911     |
|      9       |    22     |    23     |  ±0.000021  |  ±0.000021  |     ±0.00478     |
|      10      |    25     |    25     | ±0.00000268 | ±0.00000536 |    ±0.0005971    |
|      11      |    27     |    28     | ±0.00000067 | ±0.00000067 |    ±0.0001492    |
|      12      |    30     |    30     | ±0.00000008 | ±0.00000017 |   ±0.00000186    |
