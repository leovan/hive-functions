package tech.leovan.hive.udf.network;

import com.dynatrace.openkit.core.util.InetAddressValidator;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;

@Description(
        name = "FORMAT_IP_ADDRESS",
        value = "_FUNC_(IP_ADDRESS, ILLEGAL_TO_NULL) - 格式化 IP 地址"
)
public class FormatIPAddressUDF extends UDF {
    private static final String IPV4_MAPPED_IPV6_ADDRESS_PREFIX = "::(?:ffff(?::0{1,4})?:)?";

    public String evaluate(String networkAddress, Boolean illegalToNULL) {
        if (networkAddress == null) {
            return null;
        }

        String formattedNetworkAddress = networkAddress.trim().toLowerCase();

        if (!InetAddressValidator.isValidIP(formattedNetworkAddress)) {
            if (illegalToNULL) {
                return null;
            } else {
                return networkAddress;
            }
        }

        if (InetAddressValidator.isIPv6Address(formattedNetworkAddress)) {
            String tmpNetworkAddress = formattedNetworkAddress.replaceAll(IPV4_MAPPED_IPV6_ADDRESS_PREFIX, "");

            if (InetAddressValidator.isIPv4Address(tmpNetworkAddress)) {
                formattedNetworkAddress = tmpNetworkAddress;
            }
        }

        return formattedNetworkAddress;
    }

    public String evaluate(String networkAddress) {
        return evaluate(networkAddress, false);
    }
}
