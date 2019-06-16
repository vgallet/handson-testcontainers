package org.springframework.samples.petclinic;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class UtilsTest {

    /**
     * retrieve first ipv4 {@link InetAddress} as {@link String} for first {@link NetworkInterface} matching pattern as name
     * @param interfaceNamePattern
     * @return
     * @throws SocketException
     */
    public static String getDockerInterfaceIp(Pattern interfaceNamePattern) {
        // return null if exception
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                // filter NetworkInterface matching pattern
                .filter(i -> interfaceNamePattern.matcher(i.getName()).matches()).findFirst()
                // get Inet4Address
                .map(NetworkInterface::getInetAddresses)
                .map(inetAddressEnumeration ->
                    // filter InetAddress instanceof Inet4Address and not loopbackAddress
                    Collections.list(inetAddressEnumeration).stream()
                        .filter(Inet4Address.class::isInstance)
                        .filter(not(InetAddress::isLoopbackAddress))
                        // take first Ipv4
                        .findFirst()
                        .orElse(null)
                )
                .map(InetAddress::getHostAddress)
                .orElse(null);
        } catch (SocketException e) {
            return null;
        }
    }

    // negate java.util.function.Predicate polyfills of java 11 java.util.function.Predicate#not
    private static <T> Predicate<T> not(Predicate<T> t) {
        return t.negate();
    }
}
