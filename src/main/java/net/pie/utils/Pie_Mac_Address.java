package net.pie.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Usage : Pie_Mac_Address mac = new Pie_Mac_Address();
 */

public class Pie_Mac_Address {
    private List<Integer> mac_addresses = new ArrayList<>();

    public void getAddresses() {
        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
            String mac = null;
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
            NetworkInterface ni = null;
            byte[] hardwareAddress = null;
            String[] hexadecimalFormat = null;

            while (networkInterfaces.hasMoreElements()) {
                ni = networkInterfaces.nextElement();
                hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    hexadecimalFormat = new String[hardwareAddress.length];
                    for (int i = 0; i < hardwareAddress.length; i++)
                        hexadecimalFormat[i] = String.format("%02X", hardwareAddress[i]);
                    mac = String.join("-", hexadecimalFormat);
                    getMac_addresses().add(mac.hashCode());
                }
            }
        } catch (SocketException ignored) { }
    }

    public List<Integer> getMac_addresses() {
        return mac_addresses;
    }

    public void setMac_addresses(List<Integer> mac_addresses) {
        this.mac_addresses = mac_addresses;
    }
}
