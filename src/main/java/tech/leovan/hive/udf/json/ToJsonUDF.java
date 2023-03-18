package tech.leovan.hive.udf.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import tech.leovan.hive.udf.utils.TextUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ConstantObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BinaryObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.BooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.FloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.ShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.TimestampObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.WritableConstantBooleanObjectInspector;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Description(
        name = "TO_JSON",
        value = "_FUNC_(STRUCT, CONVERT_TO_CAMEL_CASE) - 将 Hive 结构（例如：`LIST`，`MAP`，`NAMED_STRUCT` 等）转换成为 JSON"
)
public class ToJsonUDF extends GenericUDF {
    private InspectorHandle structHandle;
    private Boolean convertToCamelCase = Boolean.FALSE;
    private JsonFactory jsonFactory;

    private interface InspectorHandle {
        void generateJson(JsonGenerator gen, Object obj) throws IOException;
    }

    private class MapInspectorHandle implements InspectorHandle {
        private MapObjectInspector mapInspector;
        private StringObjectInspector keyInspector;
        private InspectorHandle valueHandle;

        public MapInspectorHandle(MapObjectInspector inspector) throws UDFArgumentException {
            mapInspector = inspector;

            try {
                keyInspector = (StringObjectInspector) inspector.getMapKeyObjectInspector();
            } catch (ClassCastException castException) {
                throw new UDFArgumentException("仅 MAP 的 KEY 为字符串时才可以转换为 JSON");
            }

            valueHandle = GenerateInspectorHandle(inspector.getMapValueObjectInspector());
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                gen.writeStartObject();
                Map map = mapInspector.getMap(obj);

                for (Map.Entry entry : (Iterable<Map.Entry>) map.entrySet()) {
                    String keyJson = keyInspector.getPrimitiveJavaObject(entry.getKey());

                    if (convertToCamelCase) {
                        gen.writeFieldName(TextUtils.snakeCaseToCamelCase(keyJson));
                    } else {
                        gen.writeFieldName(keyJson);
                    }

                    valueHandle.generateJson(gen, entry.getValue());
                }

                gen.writeEndObject();
            }
        }
    }

    private class StructInspectorHandle implements InspectorHandle {
        private StructObjectInspector structInspector;
        private List<String> fieldNames;
        private List<InspectorHandle> fieldInspectorHandles;

        public StructInspectorHandle(StructObjectInspector inspector) throws UDFArgumentException {
            structInspector = inspector;
            List<? extends StructField> fieldList = inspector.getAllStructFieldRefs();
            this.fieldNames = new ArrayList<String>();
            this.fieldInspectorHandles = new ArrayList<InspectorHandle>();

            for (StructField sf : fieldList) {
                fieldNames.add(sf.getFieldName());
                fieldInspectorHandles.add(GenerateInspectorHandle(sf.getFieldObjectInspector()));
            }
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws JsonGenerationException, IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                gen.writeStartObject();
                List structObjs = structInspector.getStructFieldsDataAsList(obj);

                for (int i = 0; i < fieldNames.size(); ++i) {
                    String fieldName = fieldNames.get(i);

                    if (convertToCamelCase) {
                        gen.writeFieldName(TextUtils.snakeCaseToCamelCase(fieldName));
                    } else {
                        gen.writeFieldName(fieldName);
                    }

                    fieldInspectorHandles.get(i).generateJson(gen, structObjs.get(i));
                }

                gen.writeEndObject();
            }
        }
    }

    private class ListInspectorHandle implements InspectorHandle {
        private ListObjectInspector listInspector;
        private InspectorHandle valueHandle;

        public ListInspectorHandle(ListObjectInspector inspector) throws UDFArgumentException {
            listInspector = inspector;
            valueHandle = GenerateInspectorHandle(listInspector.getListElementObjectInspector());
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                gen.writeStartArray();
                List list = listInspector.getList(obj);

                for (Object listObj : list) {
                    valueHandle.generateJson(gen, listObj);
                }

                gen.writeEndArray();
            }
        }
    }

    private class StringInspectorHandle implements InspectorHandle {
        private StringObjectInspector stringInspector;

        public StringInspectorHandle(StringObjectInspector inspector) {
            stringInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                String str = stringInspector.getPrimitiveJavaObject(obj);
                gen.writeString(str);
            }
        }
    }

    private class IntInspectorHandle implements InspectorHandle {
        private IntObjectInspector intInspector;

        public IntInspectorHandle(IntObjectInspector inspector) {
            intInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null)
                gen.writeNull();
            else {
                int num = intInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class DoubleInspectorHandle implements InspectorHandle {
        private DoubleObjectInspector doubleInspector;

        public DoubleInspectorHandle(DoubleObjectInspector inspector) {
            doubleInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                double num = doubleInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class LongInspectorHandle implements InspectorHandle {
        private LongObjectInspector longInspector;

        public LongInspectorHandle(LongObjectInspector inspector) {
            longInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                long num = longInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class ShortInspectorHandle implements InspectorHandle {
        private ShortObjectInspector shortInspector;

        public ShortInspectorHandle(ShortObjectInspector inspector) {
            shortInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                short num = shortInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class ByteInspectorHandle implements InspectorHandle {
        private ByteObjectInspector byteInspector;

        public ByteInspectorHandle(ByteObjectInspector inspector) {
            byteInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                byte num = byteInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class FloatInspectorHandle implements InspectorHandle {
        private FloatObjectInspector floatInspector;

        public FloatInspectorHandle(FloatObjectInspector inspector) {
            floatInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                float num = floatInspector.get(obj);
                gen.writeNumber(num);
            }
        }
    }

    private class BooleanInspectorHandle implements InspectorHandle {
        private BooleanObjectInspector booleanInspector;

        public BooleanInspectorHandle(BooleanObjectInspector inspector) {
            booleanInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                boolean tf = booleanInspector.get(obj);
                gen.writeBoolean(tf);
            }
        }
    }

    private class BinaryInspectorHandle implements InspectorHandle {
        private BinaryObjectInspector binaryInspector;

        public BinaryInspectorHandle(BinaryObjectInspector inspector) {
            binaryInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                byte[] bytes = binaryInspector.getPrimitiveJavaObject(obj);
                gen.writeBinary(bytes);
            }
        }
    }

    private class TimestampInspectorHandle implements InspectorHandle {
        private TimestampObjectInspector timestampInspector;
        private DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTimeNoMillis();

        public TimestampInspectorHandle(TimestampObjectInspector inspector) {
            timestampInspector = inspector;
        }

        @Override
        public void generateJson(JsonGenerator gen, Object obj) throws IOException {
            if (obj == null) {
                gen.writeNull();
            } else {
                Timestamp timestamp = timestampInspector.getPrimitiveJavaObject(obj);
                String timeStr = isoFormatter.print(timestamp.getTime());
                gen.writeString(timeStr);
            }
        }
    }

    private InspectorHandle GenerateInspectorHandle(ObjectInspector inspector) throws UDFArgumentException {
        Category category = inspector.getCategory();
        if (category == Category.MAP) {
            return new MapInspectorHandle((MapObjectInspector) inspector);
        } else if (category == Category.LIST) {
            return new ListInspectorHandle((ListObjectInspector) inspector);
        } else if (category == Category.STRUCT) {
            return new StructInspectorHandle((StructObjectInspector) inspector);
        } else if (category == Category.PRIMITIVE) {
            PrimitiveObjectInspector primitiveInspector = (PrimitiveObjectInspector) inspector;
            PrimitiveCategory primitiveCategory = primitiveInspector.getPrimitiveCategory();
            if (primitiveCategory == PrimitiveCategory.STRING) {
                return new StringInspectorHandle((StringObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.INT) {
                return new IntInspectorHandle((IntObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.LONG) {
                return new LongInspectorHandle((LongObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.SHORT) {
                return new ShortInspectorHandle((ShortObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.BOOLEAN) {
                return new BooleanInspectorHandle((BooleanObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.FLOAT) {
                return new FloatInspectorHandle((FloatObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.DOUBLE) {
                return new DoubleInspectorHandle((DoubleObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.BYTE) {
                return new ByteInspectorHandle((ByteObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.BINARY) {
                return new BinaryInspectorHandle((BinaryObjectInspector) primitiveInspector);
            } else if (primitiveCategory == PrimitiveCategory.TIMESTAMP) {
                return new TimestampInspectorHandle((TimestampObjectInspector) primitiveInspector);
            }
        }

        throw new UDFArgumentException(String.format("无法处理的类型：%s", inspector));
    }

    @Override
    public ObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {
        if (args.length != 1 && args.length != 2) {
            throw new UDFArgumentException(
                    "TO_JSON 需要 1 个必选参数：[STRUCT]，1 个可选参数：[是否转换为 Camel Case]");
        }

        ObjectInspector structInspector = args[0];
        structHandle = GenerateInspectorHandle(structInspector);

        if (args.length == 2) {
            ObjectInspector flagInspector = args[1];

            if (flagInspector.getCategory() != Category.PRIMITIVE
                    || ((PrimitiveObjectInspector) flagInspector).getPrimitiveCategory() != PrimitiveCategory.BOOLEAN
                    || !(flagInspector instanceof ConstantObjectInspector)) {
                throw new UDFArgumentException("TO_JSON 参数 [是否转换为 Camel Case] 需要为布尔型常量");
            }

            WritableConstantBooleanObjectInspector constInspector =
                    (WritableConstantBooleanObjectInspector) flagInspector;
            convertToCamelCase = constInspector.getWritableConstantValue().get();
        }

        jsonFactory = new JsonFactory();

        return PrimitiveObjectInspectorFactory.javaStringObjectInspector;
    }

    @Override
    public Object evaluate(DeferredObject[] args) throws HiveException {
        try {
            StringWriter writer = new StringWriter();
            JsonGenerator gen = jsonFactory.createGenerator(writer);
            structHandle.generateJson(gen, args[0].get());
            gen.close();
            writer.close();
            return writer.toString();
        } catch (IOException e) {
            throw new HiveException(e);
        }
    }

    @Override
    public String getDisplayString(String[] args) {
        if (args.length == 1) {
            return String.format("TO_JSON(%s)".format(args[0]));
        } else {
            return String.format("TO_JSON(%s, %s)".format(args[0], args[1]));
        }
    }
}
