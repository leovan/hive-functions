package me.leovan.hive.udf.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;

import java.io.IOException;

@Description(
        name = "FROM_JSON",
        value = "_FUNC_(JSON, RETURN_TYPE) - 根据给定的 [JSON] 和 [返回类型] 返回相应的 Hive 结构"
)
public class FromJsonUDF extends GenericUDF {
    private StringObjectInspector jsonInspector;
    private JsonInspectorHandle inspectorHandle;

    @Override
    public ObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
        if (args.length != 2) {
            throw new UDFArgumentException("FROM_JSON 需要 2 个必选参数：[JSON]，[返回类型]");
        }

        if (args[0].getCategory() != Category.PRIMITIVE ||
                ((PrimitiveObjectInspector) args[0]).getPrimitiveCategory() != PrimitiveCategory.STRING) {
            throw new UDFArgumentException("FROM_JSON 参数 [JSON] 需要为字符串");
        }

        jsonInspector = (StringObjectInspector) args[0];

        if (args[1].getCategory() != Category.PRIMITIVE &&
                ((PrimitiveObjectInspector) args[1]).getPrimitiveCategory() != PrimitiveCategory.STRING) {
            if (!(args[1] instanceof ConstantObjectInspector)) {
                throw new UDFArgumentException("FROM_JSON 参数 [返回类型] 需要为一个常量");
            }

            ConstantObjectInspector typeInspector = (ConstantObjectInspector) args[1];
            String typeStr = typeInspector.getWritableConstantValue().toString();
            inspectorHandle = JsonInspectorHandle.InspectorHandleFactory.GenerateInspectorHandleFromTypeInfo(typeStr);
        } else {
            inspectorHandle = JsonInspectorHandle.InspectorHandleFactory.GenerateInspectorHandle(args[1]);
        }

        assert inspectorHandle != null;
        return inspectorHandle.getReturnType();
    }

    @Override
    public Object evaluate(DeferredObject[] args) throws HiveException {
        try {
            String jsonStr = jsonInspector.getPrimitiveJavaObject(args[0].get());

            if (jsonStr == null) {
                return null;
            }

            ObjectMapper jacksonParser = new ObjectMapper();
            JsonNode jsonNode = jacksonParser.readTree(jsonStr);

            return inspectorHandle.parseJson(jsonNode);
        } catch (IOException e) {
            throw new HiveException(e);
        }
    }

    @Override
    public String getDisplayString(String[] args) {
        return String.format("FROM_JSON(%s, %s)", args[0], args[1]);
    }
}
