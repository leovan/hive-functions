package tech.leovan.hive.udf.geo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UDFSpatialRelationTest {
    private final UDFSpatialRelation udf = new UDFSpatialRelation();

    @Test
    public void testSpatialRelation() throws Exception {
        String point01 = "POINT (116.4791 40.0088)";
        String point02 = "POINT (117.4791 41.0088)";
        String polygon = "POLYGON ((" +
                "116.478817 40.007749, " +
                "116.481185 40.009136, " +
                "116.479925 40.010188, " +
                "116.477621 40.008850, " +
                "116.478817 40.007749" +
                "))";

        Assertions.assertEquals("WITHIN", udf.evaluate(point01, polygon));
        Assertions.assertEquals("DISJOINT", udf.evaluate(point02, polygon));
    }
}
