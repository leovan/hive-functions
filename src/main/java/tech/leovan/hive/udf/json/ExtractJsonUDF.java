package tech.leovan.hive.udf.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.EnumSet;
import java.util.Set;

@Description(
        name = "EXTRACT_JSON",
        value = "_FUNC_(JSON, JSON_PATH) - 根据 JSON PATH 提取 JSON 中对应的值"
)
public class ExtractJsonUDF extends UDF {
    static {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new GsonJsonProvider();
            private final MappingProvider mappingProvider = new GsonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }
        });
    }

    public String evaluate(String jsonStr, String jsonPath) {
        if (jsonPath == null || "".equals(jsonPath.trim())) {
            return null;
        }

        String extractedStr = null;

        try {
            extractedStr = JsonPath.parse(jsonStr).read(jsonPath).toString();
        } catch (Exception e) {
            return null;
        }

        return extractedStr;
    }
}
