package tech.leovan.hive.udf.text;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.regex.Pattern;

@Description(
        name = "BLANK_TO_NULL",
        value = "_FUNC_(TEXT, TRIM) - 将空字符串转换为 NULL"
)
public class BlankToNullUDF extends UDF {

    public String evaluate(String text, Boolean trim) {
        if (text == null) {
            return null;
        }

        String checkText = text;

        if (trim) {
            checkText = checkText.trim();
        }

        if ("".equals(checkText)) {
            return null;
        } else {
            return text;
        }
    }

    public String evaluate(String text) {
        return evaluate(text, true);
    }
}
