package com.cyf.base.common.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cyf.base.common.bean.BaseEntity;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.utils.ClassUtil;
import com.cyf.base.common.utils.LoggerWrapper;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基于Mybatis的基础DAO
 *
 * @author chenyf
 * @date 2017-03-02
 */
public class MyBatisDao<T extends BaseEntity<PK>, PK extends Serializable> extends SqlSessionDaoSupport {
    protected final LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(this.getClass());
    protected final static String SORT_COLUMNS = "sortColumns";

    private final static String DEFAULT_ID_COLUMN_NAME = "id";
    private final static String NAMESPACE_SEPARATOR = ".";

    private final static String INSERT_SQL = "insert";
    private final static String INSERT_LIST_SQL = "insertList";
    private final static String UPDATE_SQL = "update";
    private final static String UPDATE_IF_NOT_NULL_SQL = "updateIfNotNull";
    private final static String UPDATE_LIST_SQL = "updateList";
    private final static String DELETE_BY_SQL = "deleteBy";
    private final static String LIST_ALL_SQL = "listAll";
    private final static String COUNT_BY_COND_SQL = "countByCond";
    private final static String LIST_BY_COND_SQL = "listByCond";
    private final static String GET_BY_ID_SQL = "getById";
    private final static String LIST_BY_ID_LIST_SQL = "listByIdList";
    private final static String DELETE_BY_ID_SQL = "deleteById";
    private final static String DELETE_BY_ID_LIST_SQL = "deleteByIdList";
    private final static String MAP_BY_ID_COND_SQL = "mapByIdCond";

    private String myBatisMapperNamespace;

    /**
     * 因为mybatis-spring在1.2.0之后在SqlSessionDaoSupport取消了自动注入，所以此处需要自己注入
     * @param sqlSessionFactory
     */
    @Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    //初始化bean属性
    protected void initDao() throws Exception {
        this.myBatisMapperNamespace = (ClassUtil.getSuperClassGenericType(this.getClass(), 0)).getName();
    }

    /**
     * 插入数据
     *
     * @param  entity
     * @return int 插入的记录数
     */
    public int insert(T entity) {
        prepareObjectForSaveOrUpdate(entity);
        int result = this.getSqlSession().insert(getQueryName(INSERT_SQL), entity);
        if (result > 0) {
            this.afterInsertOrUpdate(entity);
        }
        return result;
    }

    /**
     * 批量插入数据
     * @param list
     * @return
     */
    public int insertList(List<T> list) {
        int result =  this.getSqlSession().insert(getQueryName(INSERT_LIST_SQL), list);
        if (result > 0) {
            this.afterBatchInsert(list);
        }
        return result;
    }

    /**
     * 根据自定义sql插入数据
     *
     * @param entity
     * @return int 更新记录数
     */
    public int insert(String sqlId, T entity) {
        prepareObjectForSaveOrUpdate(entity);
        int result =  this.getSqlSession().insert(fillQueryName(sqlId), entity);
        if (result > 0) {
            this.afterInsertOrUpdate(entity);
        }
        return result;
    }

    /**
     * 根据多个条件删除记录
     * @param paramMap
     */
    public int deleteBy(Map<String, Object> paramMap) {
        return this.getSqlSession().delete(getQueryName(DELETE_BY_SQL), paramMap);
    }

    /**
     * 根据自定义sql删除记录
     * @param param
     */
    public int deleteBy(String sqlId, Object param) {
        return this.getSqlSession().delete(this.fillQueryName(sqlId), param);
    }

    /**
     * 更新数据
     * @param entity
     * @return int 更新记录数
     */
    public int update(T entity) {
        prepareObjectForSaveOrUpdate(entity);
        int result =  this.getSqlSession().update(getQueryName(UPDATE_SQL), entity);
        if(result > 0){
            this.afterInsertOrUpdate(entity);
        }
        return result;
    }

    /**
     * 按值更新，字段值不为null或""的才更新
     * @param entity
     * @return int 更新记录数
     */
    public int updateIfNotNull(T entity) {
        prepareObjectForSaveOrUpdate(entity);
        int result =  this.getSqlSession().update(getQueryName(UPDATE_IF_NOT_NULL_SQL), entity);
        if(result > 0){
            this.afterInsertOrUpdate(entity);
        }
        return result;
    }

    /**
     * 批量更新对象
     * @param entityList
     * @return int 更新记录数
     */
    public int updateList(List<T> entityList) {
        for(T entity : entityList){
            prepareObjectForSaveOrUpdate(entity);
        }
        int result =  this.getSqlSession().update(getQueryName(UPDATE_LIST_SQL), entityList);
        if(result > 0){
            this.afterBatchUpdate(entityList);
        }
        return result;
    }

    /**
     * 按自定义的sql更新数据
     *
     * @param sqlId
     * @param paramMap
     */
    public int update(String sqlId, Object paramMap) {
        return this.getSqlSession().update(this.fillQueryName(sqlId), paramMap);
    }

    /**
     * 取得只可能有一条记录的数据，如：根据 unique key 获取
     * 注意：请自行确保查询条件只会查到一条记录，否则会报错
     * @param param
     * @return
     */
    public T getOne(Object param) {
        T entity = this.getSqlSession().selectOne(getQueryName(LIST_BY_COND_SQL), param);
        if (entity == null) return null;
        return entity;
    }

    /**
     * 根据自定义语句，获取符合条件的单个对象
     * @param sqlId
     * @param param
     * @return
     */
    public T getOne(String sqlId, Object param) {
        T entity = this.getSqlSession().selectOne(fillQueryName(sqlId), param);
        if (entity == null) return null;
        return entity;
    }

    /**
     * 多条件and查询并返回List(不分页、不排序)
     * @param paramMap
     * @return
     */
    public List<T> listByCond(Map<String, Object> paramMap) {
        return listByCond(paramMap, "");
    }

    /**
     * 多条件and查询并返回List(不分页、排序)
     * @param paramMap
     * @param sortColumns
     * @return
     */
    public List<T> listByCond(Map<String, Object> paramMap, String sortColumns) {
        if(isNotEmpty(sortColumns)){
            if(paramMap == null) paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(sortColumns));
        }
        return this.getSqlSession().selectList(getQueryName(LIST_BY_COND_SQL), paramMap);
    }

    /**
     * 多条件and查询并返回List(可分页、可排序)
     * @param paramMap
     * @param pageParam
     * @return
     */
    public BaseResponse<List<T>> listByCond(Map<String, Object> paramMap, PageParam pageParam){
        Integer totalRecord = countByCond(paramMap);
        List<T> dataList = null;
        if(totalRecord > 0){
            if(isNotEmpty(pageParam.getSortColumns())){
                if(paramMap == null) paramMap = new HashMap<>(1);
                paramMap.put(SORT_COLUMNS, filterSortColumns(pageParam.getSortColumns()));
            }
            dataList = this.getSqlSession().selectList(getQueryName(LIST_BY_COND_SQL), paramMap,
                    new RowBounds(getOffset(pageParam), pageParam.getPageSize()));
        }
        return BaseResponse.success(dataList, pageParam, totalRecord);
    }

    /**
     * 获取所有记录并返回List
     * @return
     */
    public List<T> listAll() {
        return listAll("");
    }


    /**
     * 获取所有记录并返回List，并指定排序字段
     * @param sortColumns 排序条件
     * @return
     */
    public List<T> listAll(String sortColumns) {
        Map<String, String> paramMap = null;
        if(isNotEmpty(sortColumns)){
            paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(sortColumns));
        }
        return this.getSqlSession().selectList(getQueryName(LIST_ALL_SQL), paramMap);
    }

    /**
     * 根据自定义语句，取得符合条件的总记录数
     * @param paramMap
     * @return
     */
    public int countBy(String sqlId, Map<String, Object> paramMap) {
        Integer counts = this.getSqlSession().selectOne(fillQueryName(sqlId), paramMap);
        if (counts == null) return 0;
        return counts;
    }

    /**
     * 根据自定义语句，取得符合条件的记录并返回List，不限制查询参数的格式
     * @param sqlId
     * @param value
     * @return
     */
    public <T> List<T> listBy(String sqlId, Object value) {
        if(value != null && value instanceof Map){
            Map<String, Object> paramMap = (Map) value;
            if(paramMap.containsKey(SORT_COLUMNS)){
                paramMap.put(SORT_COLUMNS, filterSortColumns(String.valueOf(paramMap.get(SORT_COLUMNS))));
                value = paramMap;
            }
        }
        return this.getSqlSession().selectList(fillQueryName(sqlId), value);
    }

    /**
     * 根据自定义语句，取得符合条件的记录并返回List(不分页、可排序)
     * @param paramMap
     * @param sortColumns
     * @return
     */
    public <T> List<T> listBy(String sqlId, Map<String, Object> paramMap, String sortColumns){
        if(isNotEmpty(sortColumns)){
            if(paramMap == null) paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(sortColumns));
        }
        return this.getSqlSession().selectList(fillQueryName(sqlId), paramMap);
    }

    /**
     * 根据自定义语句，取得符合条件的记录并返回List(可分页、可排序)
     * @param sqlId
     * @param paramMap
     * @param pageParam
     * @return
     */
    public <T> BaseResponse<List<T>> listBy(String sqlId, Map<String, Object> paramMap, PageParam pageParam) {
        Integer totalRecord = countBy(sqlId, paramMap);
        List<T> dataList = null;
        if(totalRecord > 0){
            if(isNotEmpty(pageParam.getSortColumns())){
                if (paramMap == null) paramMap = new HashMap<>(1);
                paramMap.put(SORT_COLUMNS, filterSortColumns(pageParam.getSortColumns()));
            }
            dataList = this.getSqlSession().selectList(fillQueryName(sqlId), paramMap,
                    new RowBounds(getOffset(pageParam), pageParam.getPageSize()));
        }
        return BaseResponse.success(dataList, pageParam, totalRecord);
    }

    /**
     * 多条件and查询并返回MAP（不分页）
     * @param paramMap
     * @param property
     * @param <K>
     * @return
     */
    public <K> Map<K, T> mapByCond(Map<String, Object> paramMap, String property){
        return this.getSqlSession().selectMap(getQueryName(LIST_BY_COND_SQL), paramMap, property);
    }

    /**
     * 多条件and查询并返回MAP（分页）
     * @param paramMap
     * @param property
     * @param <K>
     * @return
     */
    public <K> Map<K, T> mapByCond(Map<String, Object> paramMap, String property, PageParam pageParam){
        if(isNotEmpty(pageParam.getSortColumns())){
            if (paramMap == null) paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(pageParam.getSortColumns()));
        }
        return this.getSqlSession().selectMap(getQueryName(LIST_BY_COND_SQL), paramMap, property,
                new RowBounds(getOffset(pageParam), pageParam.getPageSize()));
    }

    /**
     * 自定义语句查询并返回MAP（不分页）
     * key:value = 某个值为字符串的字段:实体对象 的键值对，其中key是property参数指定的字段名的值
     * @param paramMap
     * @return
     */
    public <K, T> Map<K, T> mapBy(String sqlId, Map<String, Object> paramMap, String property){
        return this.getSqlSession().selectMap(fillQueryName(sqlId), paramMap, property);
    }

    /**
     * 自定义语句查询并返回MAP（分页）
     * 注意：会在数据库进行排序，但返回到程序中的Map是无序的
     * @param paramMap
     * @return Map key:value = 某个值为字符串的字段:实体对象 的键值对，其中key是property参数指定的字段名的值
     */
    public <K, T> Map<K, T> mapBy(String sqlId, Map<String, Object> paramMap, String property, PageParam pageParam){
        if(isNotEmpty(pageParam.getSortColumns())){
            if (paramMap == null) paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(pageParam.getSortColumns()));
        }
        return this.getSqlSession().selectMap(fillQueryName(sqlId), paramMap, property,
                new RowBounds(getOffset(pageParam), pageParam.getPageSize()));
    }


    /**------------------------------------------------- 根据Id作相关操作的便捷方法 START ---------------------------------------------------*/
    /**
     * 根据主键删除记录
     * 当单一主键时传主键对象即可,当为组合组件时传MAP
     *
     * @param id
     */
    public int deleteById(PK id) {
        int result =  this.getSqlSession().delete(getQueryName(DELETE_BY_ID_SQL), id);
        if (result > 0) {
            this.afterDel(id);
        }
        return result;
    }

    /**
     * 根据多个主键id删除记录
     * @param idList
     * @return
     */
    public int deleteByIdList(List<PK> idList) {
        int result =  this.getSqlSession().delete(getQueryName(DELETE_BY_ID_LIST_SQL), idList);
        if (result > 0) {
            this.afterDel(idList);
        }
        return result;
    }

    /**
     * 根据主键获取记录
     * @param id
     * @return
     */
    public T getById(PK id) {
        T object = this.getSqlSession().selectOne(getQueryName(GET_BY_ID_SQL), id);
        return object;
    }

    /**
     * 根据多个主键获取记录
     * @param idList List<Long>
     * @return
     */
    public List<T> listByIdList(List<PK> idList){
        return this.getSqlSession().selectList(getQueryName(LIST_BY_ID_LIST_SQL), idList);
    }

    /**
     * 多条件and查询并返回以id为key的MAP（不分页）
     * key:value = 主键:实体对象 的键值对，其中key默认是字段名为id的值
     * @param paramMap
     * @return
     */
    public Map<PK, T> mapByIdCond(Map<String, Object> paramMap){
        return this.getSqlSession().selectMap(getQueryName(MAP_BY_ID_COND_SQL), paramMap, DEFAULT_ID_COLUMN_NAME);
    }
    /**
     * 多条件and查询并返回以id为key的MAP（分页）
     *
     * key:value = 主键:实体对象 的键值对，其中key默认是字段名为id的值
     * @param paramMap
     * @return
     */
    public Map<PK, T> mapByIdCond(Map<String, Object> paramMap, PageParam pageParam){
        if(isNotEmpty(pageParam.getSortColumns())){
            if (paramMap == null) paramMap = new HashMap<>(1);
            paramMap.put(SORT_COLUMNS, filterSortColumns(pageParam.getSortColumns()));
        }
        return this.getSqlSession().selectMap(getQueryName(MAP_BY_ID_COND_SQL), paramMap, DEFAULT_ID_COLUMN_NAME,
                new RowBounds(getOffset(pageParam), pageParam.getPageSize()));
    }
    /**------------------------------------------------- 根据Id作相关操作的便捷方法 END ---------------------------------------------------*/


    public String getQueryName(String sqlId) {
        return getMyBatisMapperNamespace() + NAMESPACE_SEPARATOR + sqlId;
    }

    private String fillQueryName(String sqlId){
        if(isNotEmpty(sqlId)){
            if(sqlId.indexOf(NAMESPACE_SEPARATOR) >= 0){//已经指定了命名空间，则直接返回
                return sqlId;
            }
        }
        return getQueryName(sqlId);
    }

    public void flush() {
        //ignore
    }


    /**
     * 用于子类覆盖,在insert,update之前调用
     *
     * @param o
     */
    protected void prepareObjectForSaveOrUpdate(T o) {

    }

    /**
     * 用于子类覆盖，在insert,upate之后调用
     *
     * @param o
     */
    protected void afterInsertOrUpdate(T o) {

    }

    /**
     * 用于子类覆盖，在批量插入之后调用
     *
     * @param list
     */
    protected void afterBatchInsert(List<T> list) {

    }

    /**
     * 用于子类覆盖，在批量更新之后调用
     *
     * @param list
     */
    protected void afterBatchUpdate(List<T> list) {

    }

    /**
     * 用于子类覆盖，根据主键删除后调用
     *
     * @param primaryKey
     */
    protected void afterDel(Object primaryKey) {

    }

    /**
     * 校验sortColumn里面是否包含了：""、'' 这两种符号，避免sql注入
     * @param sortColumns
     * @return
     */
    public String filterSortColumns(String sortColumns){
        if(isNotEmpty(sortColumns)){
            if(sortColumns.indexOf("\"") >= 0 || sortColumns.indexOf("'") >= 0){
                throw new IllegalArgumentException("illegal sortColumns value");
            }
            return sortColumns;
        }
        return sortColumns;
    }

    /**
     * 取得命名空间
     * @return
     */
    private String getMyBatisMapperNamespace() {
        return this.myBatisMapperNamespace;
    }

    /**
     * 多条件and查询，获取总记录数，私有方法
     * @param paramMap
     * @return
     */
    private int countByCond(Map<String, Object> paramMap) {
        Integer counts = this.getSqlSession().selectOne(getQueryName(COUNT_BY_COND_SQL), paramMap);
        if (counts == null) return 0;
        return counts;
    }

    private int getOffset(PageParam pageParam){
        return (pageParam.getPageCurrent() - 1) * pageParam.getPageSize();
    }

    /**
     * 判断字符串是否不为空
     * @param str
     * @return
     */
    private boolean isNotEmpty(String str){
        if(str != null && str.trim().length() > 0){
            return true;
        }
        return false;
    }
}
