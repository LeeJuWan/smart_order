package andbook.example.smartorder;

import java.net.InetAddress;

public class GetIP   {
    private static final String ip = "zwsdkd.cafe24.com";
    private static String token = "";
    private static String serialNumber="";

    public static String getIp() {
        return ip;
    }

    public static void setToken(String new_Token){
        token = new_Token;
    }
    public static String getToken(){
        return token;
    }

    public static void setSerialNumber(String serial){ serialNumber = serial; }
    public static String getSerialNumber(){
        return serialNumber;
    }
}
