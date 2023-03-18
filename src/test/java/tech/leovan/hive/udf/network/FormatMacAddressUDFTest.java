package tech.leovan.hive.udf.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatMacAddressUDFTest {
    private final FormatMacAddressUDF udf = new FormatMacAddressUDF();

    @Test
    public void testFormatMacAddressUDF() {
        Assertions.assertEquals("00:11:22:33:44:55", udf.evaluate("001122334455"));
        Assertions.assertEquals("00:11:22:33:44:55", udf.evaluate("01122334455"));
        Assertions.assertEquals("00:11:22:33:44:55", udf.evaluate("1122334455"));
        Assertions.assertEquals("09:11:22:33:44:55", udf.evaluate("9:11:22:33:44:55"));
        Assertions.assertEquals("00:11:22:33:44:55", udf.evaluate("11:22:33:44:55"));
        Assertions.assertEquals("00:11:22:33:44:5a", udf.evaluate("11-22-33-44-5A"));
    }

    @Test
    public void testFormatMacAddressUDFIllegal() {
        String macAddress = ":22:33:44:55";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals(macAddress, formattedMacAddress);

        formattedMacAddress = udf.evaluate(macAddress, true);
        Assertions.assertNull(formattedMacAddress);
    }
}
