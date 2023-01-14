# JSON 相关函数

## 函数

### `TO_JSON`

将 Hive 结构（例如：`LIST`，`MAP`，`NAMED_STRUCT` 等）转换成为 JSON。

```sql
CREATE TEMPORARY FUNCTION
  TO_JSON
AS
  'me.leovan.hive.udf.json.ToJsonUDF';
```

#### 参数

- STRUCT（必选）：待转换的 Hive 结构
- CONVERT_TO_CAMEL_CASE（可选）：`BOOLEAN` 类型，是否将 JSON 的 KEY 转换为 Camel Case 样式

#### 示例

##### 示例 1

```sql
SELECT
  TO_JSON(
    NAMED_STRUCT(
      "k1", 0,
      "k2", "a string",
      "k3", ARRAY(4, 5, 6),
      "k4", MAP("a", 2.3, "b", 5.6)
    )
  )
;
```

输出结果为：

```json
{
  "k1": 0,
  "k2": "a string",
  "k3": [4, 5, 6],
  "k4": {
    "a": 2.3,
    "b": 5.6
  }
}
```

### `EXTRACT_JSON`

根据 JSON PATH 提取 JSON 中对应的值。

```sql
CREATE TEMPORARY FUNCTION
  EXTRACT_JSON
AS
  'me.leovan.hive.udf.json.ExtractJsonUDF';
```

#### 参数
- JSON（必选）：待提取的 JSON
- JSON_PATH（必选）：JSON Path 表达式

#### 示例

##### 示例 1

```sql
SELECT
  EXTRACT_JSON(
    '{"k1": "v1", "k2": 3}',
    '$.k1'
  )
;
```

输出结果为：

```
"v1"
```

##### 示例 2

```sql
SELECT
  EXTRACT_JSON(
    '{"k1": "v1", "k2": 3}',
    '$.k2'
  )
;
```

输出结果为：

```
3
```

##### 示例 3

```sql
SELECT
  EXTRACT_JSON(
    '{"k": ["v", 3]}',
    '$.k[*]'
  )
;
```

输出结果为：

```
["v",3]
```

##### 示例 4

```sql
SELECT
  EXTRACT_JSON(
    '{"k": ["v", 3]}',
    '$.k[0]'
  )
;
```

输出结果为：

```
"v"
```

##### 示例 5

```sql
SELECT
  EXTRACT_JSON(
    '{"m": {"k1": "v1", "k2": 3}}',
    '$.m'
  )
;
```

输出结果为：

```
{"k1":"v1","k2":3}
```

##### 示例 6

```sql
SELECT
  EXTRACT_JSON(
    '{"m": {"k1": "v1", "k2": 3}}',
    '$.m.k2'
  )
;
```

输出结果为：

```
3
```
