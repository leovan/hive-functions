package tech.leovan.hive.udf.network;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.List;
import java.util.regex.Pattern;

@Description(
        name = "FORMAT_MAC_ADDRESS",
        value = "_FUNC_(MAC_ADDRESS, ILLEGAL_TO_NULL) - 格式化 MAC 地址"
)
public class UDFFormatMacAddress extends UDF {
    protected static final Pattern PATTERN_MAC_ADDRESS_1 = Pattern.compile("[0-9A-Fa-f]{1,12}");
    protected static final Pattern PATTERN_MAC_ADDRESS_2 = Pattern.compile(
            "([0-9A-Fa-f]{1,2}[:-]){0,5}[0-9A-Fa-f]{1,2}");

    public String evaluate(String macAddress) {
        return evaluate(macAddress, false);
    }

    public String evaluate(String macAddress, Boolean illegalToNULL) {
        String cleanedMacAddress = macAddress.trim().toLowerCase();
        String formattedMacAddress = cleanedMacAddress;

        if (PATTERN_MAC_ADDRESS_1.matcher(cleanedMacAddress).matches()) {
            cleanedMacAddress = Strings.padStart(cleanedMacAddress, 12, '0');

            List<String> formattedMacAddressParts = Lists.newArrayList();
            StringBuilder formattedMacAddressPart = new StringBuilder();

            for (int i = 0; i < cleanedMacAddress.length(); i++) {
                formattedMacAddressPart.append(cleanedMacAddress.charAt(i));

                if (i % 2 == 1) {
                    formattedMacAddressParts.add(formattedMacAddressPart.toString());
                    formattedMacAddressPart = new StringBuilder();
                }
            }

            formattedMacAddress = Joiner.on(":").join(formattedMacAddressParts);
        } else if (PATTERN_MAC_ADDRESS_2.matcher(cleanedMacAddress).matches()) {
            String[] macAddressParts = cleanedMacAddress.split("[:-]");
            List<String> formattedMacAddressParts = Lists.newArrayList();

            for (String macAddressPart : macAddressParts) {
                formattedMacAddressParts.add(Strings.padStart(macAddressPart, 2, '0'));
            }

            for (int i = 0; i < 6 - macAddressParts.length; i++) {
                formattedMacAddressParts.add(0, "00");
            }

            formattedMacAddress = Joiner.on(":").join(formattedMacAddressParts);
        } else {
            if (illegalToNULL) {
                formattedMacAddress = null;
            }
        }

        return formattedMacAddress;
    }
}
