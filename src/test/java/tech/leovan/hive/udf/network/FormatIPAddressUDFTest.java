package tech.leovan.hive.udf.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatIPAddressUDFTest {
    private final FormatIPAddressUDF udf = new FormatIPAddressUDF();

    @Test
    public void testFormatIPAddressUDF() {
        Assertions.assertEquals("255.255.255.255", udf.evaluate("255.255.255.255"));
        Assertions.assertEquals("255.255.255.255", udf.evaluate("::255.255.255.255"));
        Assertions.assertEquals("255.255.255.255", udf.evaluate("::ffff:255.255.255.255"));
        Assertions.assertEquals("255.255.255.255", udf.evaluate("::ffff:0:255.255.255.255"));
        Assertions.assertEquals("1:2:3:4:5:6:7:8", udf.evaluate("1:2:3:4:5:6:7:8"));
        Assertions.assertEquals("2001:db8:3:4::192.0.2.33", udf.evaluate("2001:db8:3:4::192.0.2.33"));
    }
}
