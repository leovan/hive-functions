# 设备相关函数

## 函数

### `FORMAT_MAC_ADDRESS`

格式化 MAC 地址

```sql
CREATE TEMPORARY FUNCTION
  FORMAT_MAC_ADDRESS
AS
  'me.leovan.hive.udf.device.FormatMacAddressUDF';
```

#### 参数

- MAC_ADDRESS（必选）：MAC 地址
- ILLEGAL_TO_NULL（可选）：非法 MAC 地址是否转换为 NULL

#### 示例

- 示例 1

```sql
SELECT
  FORMAT_MAC_ADDRESS('001122334455')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

- 示例 2

```sql
SELECT
  FORMAT_MAC_ADDRESS('1122334455')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

- 示例 3

```sql
SELECT
  FORMAT_MAC_ADDRESS('9:11:22:33:44:55')
;
```

输出结果为：

```txt
09:11:22:33:44:55
```

- 示例 4

```sql
SELECT
  FORMAT_MAC_ADDRESS('11:22:33:44:55')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

- 示例 5

```sql
SELECT
  FORMAT_MAC_ADDRESS('11-22-33-44-5A')
;
```

输出结果为：

```txt
00:11:22:33:44:5a
```

- 示例 6

```sql
SELECT
  FORMAT_MAC_ADDRESS(':22:33:44:55')
;
```

输出结果为：

```txt
:22:33:44:55
```

- 示例 7

```sql
SELECT
  FORMAT_MAC_ADDRESS(':22:33:44:55', TRUE)
;
```

输出结果为：

```txt
NULL
```
