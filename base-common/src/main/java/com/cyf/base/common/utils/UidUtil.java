package com.cyf.base.common.utils;

import java.util.UUID;

public class UidUtil {

	public static String getUniqueId(){
		return UUID.randomUUID().toString();
	}

	public static String getMD5UnqId(){
		return MD5Util.md5(getUniqueId());
	}
}
