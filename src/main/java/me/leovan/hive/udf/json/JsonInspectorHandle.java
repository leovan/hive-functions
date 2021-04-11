package me.leovan.hive.udf.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector.Category;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public interface JsonInspectorHandle {
    Object parseJson(JsonNode jsonNode);
    ObjectInspector getReturnType();

    final class InspectorHandleFactory {
        static public JsonInspectorHandle GenerateInspectorHandle(ObjectInspector inspector)
                throws UDFArgumentException {
            Category category = inspector.getCategory();

            switch (category) {
                case LIST:
                    return new JsonInspectorHandle.ListHandle((ListObjectInspector) inspector);
                case MAP:
                    return new JsonInspectorHandle.MapHandle((MapObjectInspector) inspector);
                case STRUCT:
                    return new JsonInspectorHandle.StructHandle((StructObjectInspector) inspector);
                case PRIMITIVE:
                    return new JsonInspectorHandle.PrimitiveHandle((PrimitiveObjectInspector) inspector);
            }

            return null;
        }

        static public JsonInspectorHandle GenerateInspectorHandleFromTypeInfo(String typeStr)
                throws UDFArgumentException {
            TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeStr);
            ObjectInspector inspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
            return GenerateInspectorHandle(inspector);
        }
    }

    class ListHandle implements JsonInspectorHandle {
        private StandardListObjectInspector listInspector;
        private final JsonInspectorHandle elementHandle;

        public ListHandle(ListObjectInspector inspector) throws UDFArgumentException {
            elementHandle = InspectorHandleFactory.GenerateInspectorHandle(inspector.getListElementObjectInspector());
        }

        @Override
        public Object parseJson(JsonNode jsonNode) {
            if (jsonNode.isNull()) {
                return null;
            }

            List newList = (List) listInspector.create(0);

            Iterator<JsonNode> listNodes = jsonNode.elements();

            while (listNodes.hasNext()) {
                JsonNode elementNode = listNodes.next();
                if (elementNode != null) {
                    Object elementObject = elementHandle.parseJson(elementNode);
                    newList.add(elementObject);
                } else {
                    newList.add(null);
                }
            }

            return newList;
        }

        @Override
        public ObjectInspector getReturnType() {
            listInspector = ObjectInspectorFactory.getStandardListObjectInspector(elementHandle.getReturnType());
            return listInspector;
        }
    }

    class MapHandle implements JsonInspectorHandle {
        private StandardMapObjectInspector mapInspector;
        private final JsonInspectorHandle mapValueHandle;

        public MapHandle(MapObjectInspector inspector) throws UDFArgumentException {
            if (!(inspector.getMapKeyObjectInspector() instanceof StringObjectInspector)) {
                throw new RuntimeException("JSON maps can only have strings as keys");
            }
            mapValueHandle = InspectorHandleFactory.GenerateInspectorHandle(inspector.getMapValueObjectInspector());
        }

        @Override
        public Object parseJson(JsonNode jsonNode) {
            if (jsonNode.isNull()) {
                return null;
            }

            Map<String, Object> newMap = (Map<String, Object>) mapInspector.create();

            Iterator<String> keys = jsonNode.fieldNames();

            while (keys.hasNext()) {
                String key = keys.next();
                JsonNode valueNode = jsonNode.get(key);
                Object value = mapValueHandle.parseJson(valueNode);
                newMap.put(key, value);
            }

            return newMap;
        }

        @Override
        public ObjectInspector getReturnType() {
            mapInspector = ObjectInspectorFactory.getStandardMapObjectInspector(
                    PrimitiveObjectInspectorFactory.javaStringObjectInspector,
                    mapValueHandle.getReturnType());
            return mapInspector;
        }
    }

    class StructHandle implements JsonInspectorHandle {
        private final List<String> fieldNames;
        private final List<JsonInspectorHandle> handles;

        public StructHandle(StructObjectInspector inspector) throws UDFArgumentException {
            fieldNames = new ArrayList<String>();
            handles = new ArrayList<JsonInspectorHandle>();

            List<? extends StructField> refs = inspector.getAllStructFieldRefs();
            for (StructField ref : refs) {
                fieldNames.add(ref.getFieldName());
                JsonInspectorHandle fieldHandle = InspectorHandleFactory.GenerateInspectorHandle(
                        ref.getFieldObjectInspector());
                handles.add(fieldHandle);
            }
        }

        @Override
        public Object parseJson(JsonNode jsonNode) {
            if (jsonNode.isNull()) {
                return null;
            }

            List<Object> valueList = new ArrayList<Object>();

            for (int i = 0; i < fieldNames.size(); ++i) {
                String key = fieldNames.get(i);
                JsonNode valueNode = jsonNode.get(key);
                JsonInspectorHandle valHandle = handles.get(i);
                Object valueObj = valHandle.parseJson(valueNode);
                valueList.add(valueObj);
            }

            return valueList;
        }

        @Override
        public ObjectInspector getReturnType() {
            List<ObjectInspector> structInspectors = new ArrayList<ObjectInspector>();

            for (JsonInspectorHandle handle : handles) {
                structInspectors.add(handle.getReturnType());
            }

            return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, structInspectors);
        }
    }

    class PrimitiveHandle implements JsonInspectorHandle {
        private final PrimitiveCategory category;
        private final DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTimeNoMillis();

        public PrimitiveHandle(PrimitiveObjectInspector inspector) throws UDFArgumentException {
            category = inspector.getPrimitiveCategory();
        }

        @Override
        public Object parseJson(JsonNode jsonNode) {
            if (jsonNode == null || jsonNode.isNull()) {
                return null;
            }

            switch (category) {
                case STRING:
                    if (jsonNode.isTextual())
                        return jsonNode.textValue();
                    else
                        return jsonNode.toString();
                case LONG:
                    return jsonNode.longValue();
                case SHORT:
                    return (short) jsonNode.intValue();
                case BYTE:
                    return (byte) jsonNode.intValue();
                case BINARY:
                    try {
                        return jsonNode.binaryValue();
                    } catch (IOException e) {
                        return jsonNode.toString();
                    }
                case INT:
                    return jsonNode.intValue();
                case FLOAT:
                    return (float) jsonNode.doubleValue();
                case DOUBLE:
                    return jsonNode.doubleValue();
                case BOOLEAN:
                    return jsonNode.booleanValue();
                case TIMESTAMP:
                    long time = isoFormatter.parseMillis(jsonNode.textValue());
                    return new Timestamp(time);
            }

            return null;
        }

        @Override
        public ObjectInspector getReturnType() {
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(category);
        }
    }
}
