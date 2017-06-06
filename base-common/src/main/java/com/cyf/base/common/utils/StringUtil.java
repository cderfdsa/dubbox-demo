package com.cyf.base.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenyf on 2017/3/8.
 */
public class StringUtil {

    public static List<Long> getLongList(String idStr, String separator){
        if(isEmpty(idStr) || isEmpty(separator)) return new ArrayList<>();
        String[] strArr = idStr.split(separator);
        List<Long> idList = new ArrayList<>();
        for(int i=0; i<strArr.length; i++){
            idList.add(Long.valueOf(strArr[i]));
        }
        return idList;
    }

    public static Integer getInteger(String str){
        if(isEmpty(str)){
            return null;
        }else{
            return Integer.valueOf(str.trim());
        }
    }

    public static Long getLong(String str){
        if(isEmpty(str)){
            return null;
        }else{
            return Long.valueOf(str.trim());
        }
    }

    public static Double getDouble(String str){
        if(isEmpty(str)){
            return null;
        }else{
            return Double.valueOf(str.trim());
        }
    }

    public static boolean isString(Object object){
        if(object != null && object instanceof String){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isEmpty(Object str){
        if(str==null || str.toString().trim().length() <= 0){
            return true;
        }
        return false;
    }

    /**
     * 去掉字符串首尾的指定字符
     * @param str
     * @param patt
     * @return
     */
    public static String trim(String str, String patt){
        if(isEmpty(str) || isEmpty(patt)) return str;

        str = str.trim();

        while(str.startsWith(patt)){
            if(str.length()==0){
                return "";
            }else{
                str = str.substring(1, str.length());
            }
        }

        int index = 0;
        while((index = str.lastIndexOf(patt)) == str.length()-1){
            if(str.length()==0){
                return "";
            }else{
                str = str.substring(0, index);
            }
        }
        return str;
    }

    /**
     * 判断source里面是否包含target
     * @param source
     * @param target
     * @param separator
     * @return
     */
    public static boolean isContain(String source, String target, String separator){
        if(isEmpty(source) || isEmpty(target) || isEmpty(separator)){
            return false;
        }
        String[] strArr = source.split(separator);
        List<String> list = Arrays.asList(strArr);
        if(list.contains(target)){//字符串可以用这种方式，Integer、Long等就不可以了
            return true;
        }
        return false;
    }
}
