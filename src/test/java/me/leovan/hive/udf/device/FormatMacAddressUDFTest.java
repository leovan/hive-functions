package me.leovan.hive.udf.device;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatMacAddressUDFTest {
    private final FormatMacAddressUDF udf = new FormatMacAddressUDF();

    @Test
    public void testFormatMacAddress01() {
        String macAddress = "001122334455";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("00:11:22:33:44:55", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddress02() {
        String macAddress = "01122334455";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("00:11:22:33:44:55", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddress03() {
        String macAddress = "1122334455";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("00:11:22:33:44:55", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddress04() {
        String macAddress = "9:11:22:33:44:55";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("09:11:22:33:44:55", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddress05() {
        String macAddress = "11:22:33:44:55";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("00:11:22:33:44:55", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddress06() {
        String macAddress = "11-22-33-44-5A";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals("00:11:22:33:44:5a", formattedMacAddress);
    }

    @Test
    public void testFormatMacAddressIllegal01() {
        String macAddress = ":22:33:44:55";
        String formattedMacAddress = udf.evaluate(macAddress);
        Assertions.assertEquals(macAddress, formattedMacAddress);

        formattedMacAddress = udf.evaluate(macAddress, true);
        Assertions.assertNull(formattedMacAddress);
    }
}
