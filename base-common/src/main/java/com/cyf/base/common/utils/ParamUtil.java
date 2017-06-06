package com.cyf.base.common.utils;

import java.util.*;

/**
 * Created by Administrator on 2017/5/6.
 */
public class ParamUtil {
    /**
     * 是否允许的查询字段
     * @param filter
     * @param validParamString 参数类型是用英文输入法下，逗号分隔的字符串，如： apple,orange,pear
     * @return
     */
    public static boolean isValidQueryParam(Map<String ,Object> filter, String validParamString){
        if(validParamString == null){
            return false;
        }else if(filter == null || filter.isEmpty()){//应该允许不带参数查询，即使用默认查询
            return true;
        }

        Set<String> keys = filter.keySet();
        String validParamsArray[] = validParamString.split(",");
        Set<String> validParamsSet = new HashSet<>(Arrays.asList(validParamsArray));
        return validParamsSet.containsAll(keys);
    }

    /**
     * 判断是否时允许的排序字段,各参数的值应该形如:
     *      sortColumns: pk_org_id
     * @param sortColumns
     * @param validSortColumns
     * @return
     */
    public static boolean isValidOrderBy(String sortColumns, String validSortColumns){
        if(sortColumns == null || sortColumns.trim().length() <= 0) return false;
        if(validSortColumns == null || validSortColumns.trim().length() <= 0) return false;
        return isValidOrderBy(sortColumns.split(","), validSortColumns.split(","));
    }

    /**
     * 判断orderByArray是否全部都是允许的排序字段
     * @param sortColumnArray
     * @param validSortColumnArray
     * @return
     */
    public static boolean isValidOrderBy(String[] sortColumnArray, String[] validSortColumnArray) {
        if(validSortColumnArray==null || validSortColumnArray.length <= 0){
            return false;
        }else if(sortColumnArray==null || sortColumnArray.length <= 0){//应该允许不带排序字段，或者使用默认排序字段
            return true;
        }

        boolean result = true;
        List<String> orderByList = new ArrayList<>();
        String[] oneOrderBySplit;
        String orderByDirect;
        int fullLength = sortColumnArray.length;

        //判断排序顺序是否属于：desc, asc
        for (int i = 0; i < fullLength; i++) {
            oneOrderBySplit = sortColumnArray[i].trim().split(" +");
            if ( oneOrderBySplit.length > 1 ) {
                orderByDirect = oneOrderBySplit[1].trim().toLowerCase();
                if ( !(orderByDirect.equals("desc") || orderByDirect.equals("asc")) ) {
                    result = false;
                    break;
                }
            }
            if(result) orderByList.add(oneOrderBySplit[0].trim());
        }

        //判断排序字段是否在允许的字段范围内
        List<String> validOrderByList = Arrays.asList(validSortColumnArray);
        if ( !validOrderByList.containsAll(orderByList) ) {
            result = false;
        }

        return result;
    }

    /**
     * 检查必传参数是否都有传，并且都不为null
     * @param filter
     * @param requireParamString
     * @return 如果检查通过则返回空对象 null，如果检查不通过，则返回不为空的字符串提示信息
     */
    public static String checkRequireParam(Map<String ,Object> filter, String requireParamString){
        if(filter == null || filter.isEmpty()) return "参数为空";
        String requireParamsArray[] = requireParamString.split(",");
        for(int i=0; i<requireParamsArray.length; i++){
            if(! filter.containsKey(requireParamsArray[i])){
                return requireParamsArray[i] + " 必传参数不存在";
            }else if(filter.get(requireParamsArray[i]) == null){
                return requireParamsArray[i] + " 必传参数为null";
            }
        }
        return null;
    }
}
