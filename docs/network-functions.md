# 设备相关函数

## 函数

### `FORMAT_MAC_ADDRESS`

格式化 MAC 地址

```sql
CREATE TEMPORARY FUNCTION
  FORMAT_MAC_ADDRESS
AS
  'tech.leovan.hive.udf.network.FormatMacAddressUDF';
```

#### 参数

- MAC_ADDRESS（必选）：MAC 地址
- ILLEGAL_TO_NULL（可选）：非法 MAC 地址是否转换为 NULL

#### 返回值

格式化的 MAC 地址：`STRING` 类型

#### 示例

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS('001122334455')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS('1122334455')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS('9:11:22:33:44:55')
;
```

输出结果为：

```txt
09:11:22:33:44:55
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS('11:22:33:44:55')
;
```

输出结果为：

```txt
00:11:22:33:44:55
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS('11-22-33-44-5A')
;
```

输出结果为：

```txt
00:11:22:33:44:5a
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS(':22:33:44:55')
;
```

输出结果为：

```txt
:22:33:44:55
```

SQL：

```sql
SELECT
  FORMAT_MAC_ADDRESS(':22:33:44:55', TRUE)
;
```

输出结果为：

```txt
NULL
```

### `FORMAT_IP_ADDRESS`

格式化 IP 地址

```sql
CREATE TEMPORARY FUNCTION
  FORMAT_IP_ADDRESS
AS
  'tech.leovan.hive.udf.network.FormatIPAddressUDF';
```

#### 参数

- IP_ADDRESS（必选）：IP 地址
- ILLEGAL_TO_NULL（可选）：非法 IP 地址是否转换为 NULL

#### 返回值

格式化的 IP 地址：`STRING` 类型

#### 示例

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('255.255.255.255')
;
```

输出结果为：

```txt
255.255.255.255
```

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('::255.255.255.255')
;
```

输出结果为：

```txt
255.255.255.255
```

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('::ffff:255.255.255.255')
;
```

输出结果为：

```txt
255.255.255.255
```

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('::ffff:0:255.255.255.255')
;
```

输出结果为：

```txt
255.255.255.255
```

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('1:2:3:4:5:6:7:8')
;
```

输出结果为：

```txt
1:2:3:4:5:6:7:8
```

SQL：

```sql
SELECT
  FORMAT_IP_ADDRESS('2001:db8:3:4::192.0.2.33')
;
```

输出结果为：

```txt
2001:db8:3:4::192.0.2.33
```
