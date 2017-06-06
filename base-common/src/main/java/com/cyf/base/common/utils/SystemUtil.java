package com.cyf.base.common.utils;

import java.net.InetAddress;

/**
 * Created by chenyf on 2017/3/14.
 */
public class SystemUtil {

    public static String getLocalIp(){
        try{
            return InetAddress.getLocalHost().getHostAddress();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
}
