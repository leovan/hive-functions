# JSON 函数

## `FROM_JSON`

根据给定的 JSON 和返回类型返回相应的 Hive 结构。

```sql
CREATE TEMPORARY FUNCTION FROM_JSON AS 'me.leovan.hive.udf.json.FromJsonUDF';
```

### 参数

- JSON（必选）：`STRING` 类型，待解析的 JSON 字符串
- 返回类型（必选）：需要返回的 Hive 结构类型

### 示例

#### 示例 1

```sql
SELECT FROM_JSON(
    '{ "k1": [1, 2], "k2": [3, 4, 5], "k3": [6] }',
    'MAP<STRING, ARRAY<INT>>'
);
```

## `TO_JSON`

将 Hive 结构（例如：`LIST`，`MAP`，`NAMED_STRUCT` 等）转换成为 JSON。

```sql
CREATE TEMPORARY FUNCTION FROM_JSON AS 'me.leovan.hive.udf.json.ToJsonUDF';
```

### 参数

- Hive 结构（必选）：待转换的 Hive 结构
- 是否转换为 Camel Case（可选）：`BOOLEAN` 类型，是够将 JSON 的 KEY 转换为 Camel Case 样式

### 示例

#### 示例 1

```sql
SELECT TO_JSON(
    ARRAY(1, 2, 3)
);
```

输出结果为：

```json
[1, 2, 3]
```

#### 示例 2

```sql
SELECT TO_JSON(
    NAMED_STRUCT(
        "k1", 0,
        "k2", "a string",
        "k3", ARRAY(4, 5, 6),
        "k4", MAP("a", 2.3, "b", 5.6)
    )
);
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
