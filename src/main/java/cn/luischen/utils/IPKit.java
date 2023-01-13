package cn.luischen.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * ip工具类
 * Created by BlueT on 2017/3/9.
 */
public class IPKit {
    /**
     * 注意：
     * 因为多级反向代理涉及到多台服务器，每一台服务器都会添加一个X-Forwarded-For的值，所以X-Forwarded-For的值会不止一个。
     * @param request 请求
     * @return IP Address
     */
    public static String getIpAddrByRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
//    /**
//     * 获取当前网络ip
//     * 因为多级反向代理涉及到多台服务器，每一台服务器都会添加一个X-Forwarded-For的值，所以X-Forwarded-For的值会不止一个。
//     * @param request
//     * @return
//     */
//    public String getIpAddr(HttpServletRequest request) {
//        String ipAddress = request.getHeader("x-forwarded-for");
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
//            ipAddress = request.getRemoteAddr();
//            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
//                //根据网卡取本机配置的IP
//                InetAddress inet = null;
//                try {
//                    inet = InetAddress.getLocalHost();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
//                ipAddress = inet.getHostAddress();
//            }
//        }
//        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
//        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
//            if (ipAddress.indexOf(",") > 0) {
//                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
//            }
//        }
//        return ipAddress;
//    }

    /**
     * @return 本机IPSocketException
     * @throws SocketException
     */
    public static String getRealIp() throws SocketException {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip = null;
        boolean finded = false;// 是否找到外网IP
        while (netInterfaces.hasMoreElements() && !finded) {
            NetworkInterface ni = netInterfaces.nextElement();
            Enumeration<InetAddress> address = ni.getInetAddresses();
            while (address.hasMoreElements()) {
                ip = address.nextElement();
                if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 外网IP
                    netip = ip.getHostAddress();
                    finded = true;
                    break;
                } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && !ip.getHostAddress().contains(":")) {// 内网IP
                    localip = ip.getHostAddress();
                }
            }
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }
}
