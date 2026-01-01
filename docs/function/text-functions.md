---
icon: lucide/file-type-corner
---

# 文本函数

## 函数

### `BLANK_TO_NULL`

将空字符串转换为 NULL。

```sql
CREATE TEMPORARY FUNCTION
  BLANK_TO_NULL
AS
  'tech.leovan.hive.udf.text.UDFBlankToNull';
```

#### 参数

- TEXT（必选）：待转换文本
- TRIM（可选）：`BOOLEAN` 类型，是否去除字符串两端空白字符，默认为 `TRUE`

#### 返回值

转换后的字符串：`STRING` 类型

#### 示例

SQL：

```sql
SELECT
  BLANK_TO_NULL('')
;
```

输出结果为：

```text
null
```

SQL：

```sql
SELECT
  BLANK_TO_NULL('\t')
;
```

输出结果为：

```text
null
```

SQL：

```sql
SELECT
  BLANK_TO_NULL(' not null ')
;
```

输出结果为：

```text
 not null 
```

SQL：

```sql
SELECT
  BLANK_TO_NULL('\t', FALSE)
;
```

输出结果为：

```text
\t
```
