package com.cyf.base.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/4.
 */
public class NumberUtil {
    private final static String NUMERIC_REGEX = "^(-|\\+)?\\d+(\\.\\d+)?$";//验证数字的正则表达式

    /**
     * 判断一个对象是否为数字
     * @param obj
     * @return
     */
    public static boolean isNumeric(Object obj){
        if(obj == null) return false;
        Matcher isNum = Pattern.compile(NUMERIC_REGEX).matcher(obj.toString());
        return isNum.matches();
    }
}
