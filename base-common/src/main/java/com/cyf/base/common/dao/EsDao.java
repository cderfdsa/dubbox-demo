package com.cyf.base.common.dao;

import com.cyf.base.common.bean.BaseEntity;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.component.EsClient;
import com.cyf.base.common.config.PublicConfig;
import com.cyf.base.common.exception.BizException;
import com.cyf.base.common.utils.*;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DaoSupport;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * elasticsearch 的基础Dao
 * Created by chenyf on 2017/5/21.
 */
public abstract class EsDao<T extends BaseEntity<PK>, PK extends Serializable> extends DaoSupport{
    protected LoggerWrapper logger = LoggerWrapper.getLoggerWrapper(this.getClass());
    public static final String RANGE_QUERY_START_SUFFIX = "Start";
    public static final String RANGE_QUERY_END_SUFFIX = "End";
    public static final String QUERY_LIST_SUFFIX = "List";
    public static final String QUERY_LIKE_SUFFIX = "Like";
    public final static String DEFAULT_ID_COLUMN_NAME = "id";
    public final static int MAX_PAGE_QUERY_NUM = 100000;//通过 from + size 分页时，es默认允许的最大记录数

    /**
     * elasticsearch的index名称
     */
    protected String index;

    /**
     * elasticsearch的type名称
     */
    protected String type;

    @Autowired
    private EsClient esClient;
    //当前class
    private Class<T> currClass;

    //检查Dao属性
    protected void checkDaoConfig() throws IllegalArgumentException{
        Assert.notNull(this.esClient, "Property 'esClient' are required");
    }
    /**
     * 初始化Dao
     * @throws Exception
     */
    protected void initDao() throws Exception {
        this.setIndex();
        this.setType();
        this.currClass =(Class) ClassUtil.getSuperClassGenericType(this.getClass(),0);
    }

    /**
     * 获得当前类定义的T的Class
     * @return
     */
    protected Class<T> getCurrClass(){
        return currClass;
    }

    /**
     * 获取elasticsearch的客户端对象
     * @return
     */
    protected TransportClient getTransportClient(){
        return this.esClient.getTransportClient();
    }

    /**
     * 获取es索引名称
     * @return
     */
    protected String getIndex(){
        return this.index;
    }

    /**
     * 获取es的类型
     * @return
     */
    protected String getType(){
        return this.type;
    }

    /**
     * 设置es的索引名称，可类比为数据库的数据库名称
     * @return
     */
    protected abstract void setIndex();

    /**
     * 设置es的类型，可类比为数据库的表名
     * @return
     */
    protected abstract void setType();

    /**
     * 插入数据
     *
     * @param  entity
     * @return int 插入的记录数
     */
    public int insert(T entity) {
        int resultCount = 0;
        IndexResponse indexResponse = getTransportClient()
                .prepareIndex(getIndex(), getType(), String.valueOf(entity.getId()))
                .setSource(JsonUtil.toStringWithNull(entity), XContentType.JSON)
                .execute()
                .actionGet();
        //如果插入es失败，则打印到日志文件中
        if(indexResponse.getResult().compareTo(DocWriteResponse.Result.CREATED) != 0){
            logger.error("insert into elasticsearch fail: " + JsonUtil.toStringPretty(entity));
        }
        return resultCount;
    }

    /**
     * 批量插入数据
     * @param entityList
     * @return
     */
    public int insertList(List<T> entityList){
        BulkRequestBuilder bulkRequest = getTransportClient().prepareBulk();
        for(T entity : entityList){
            IndexRequestBuilder idxBuilder = getTransportClient()
                    .prepareIndex(getIndex(), getType(), String.valueOf(entity.getId()))
                    .setSource(JsonUtil.toStringWithNull(entity), XContentType.JSON);
            bulkRequest.add(idxBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return calcBulkExecuteSuccessCount(bulkResponse, entityList.size());
    }

    /**
     * 根据查询条件删除
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param filter
     */
    public int deleteBy(Map<String, Object> filter) {
        SearchResponse response = executeCondQuery(filter, PageParam.getInstance(0, 0), DEFAULT_ID_COLUMN_NAME);
        List<PK> entityIdList = getIdList(response);
        if(entityIdList != null && entityIdList.size() > 0){
            return deleteByIdList(entityIdList);
        }else{
            return 0;
        }
    }

    /**
     * 更新数据，将会更新所有字段
     * @param entity
     * @return int 更新记录数
     */
    public int update(T entity){
        int resultCount = 0;
        UpdateResponse updateResponse = getTransportClient()
                .prepareUpdate(getIndex(), getType(), String.valueOf(entity.getId()))
                .setDoc(JsonUtil.toStringWithNull(entity), XContentType.JSON)
                .execute()
                .actionGet();

        if(updateResponse.getResult().compareTo(DocWriteResponse.Result.UPDATED) != 0 &&
                updateResponse.getResult().compareTo(DocWriteResponse.Result.NOOP) != 0){
            resultCount --;
            logger.error("update into elasticsearch fail: " + JsonUtil.toStringPretty(entity) + ",Result is "+
                    JsonUtil.toStringPretty(updateResponse.getResult()) + ",failures are " +
                    JsonUtil.toStringPretty(updateResponse.getShardInfo().getFailures()));
        }
        return resultCount;
    }

    /**
     * 按值更新，字段值不为null或""的才更新
     * @param entity
     * @return int 更新记录数
     */
    public int updateIfNotNull(T entity){
        int resultCount = 0;
        UpdateResponse updateResponse = getTransportClient()
                .prepareUpdate(getIndex(), getType(), String.valueOf(entity.getId()))
                .setDoc(JsonUtil.toString(entity), XContentType.JSON)
                .execute()
                .actionGet();

        if(updateResponse.getResult().compareTo(DocWriteResponse.Result.UPDATED) != 0 &&
                updateResponse.getResult().compareTo(DocWriteResponse.Result.NOOP) != 0){
            resultCount --;
            logger.error("update into elasticsearch fail: " + JsonUtil.toStringPretty(entity) + ",Result is "+
                    JsonUtil.toStringPretty(updateResponse.getResult()) + ",failures are " +
                    JsonUtil.toStringPretty(updateResponse.getShardInfo().getFailures()));
        }
        return resultCount;
    }

    /**
     * 批量更新对象
     * @param entityList
     * @return int 更新记录数
     */
    public int updateList(List<T> entityList){
        BulkRequestBuilder bulkRequest = getTransportClient().prepareBulk();
        for(T entity : entityList){
            UpdateRequestBuilder updBuilder = getTransportClient()
                    .prepareUpdate(getIndex(), getType(), String.valueOf(entity.getId()))
                    .setDoc(JsonUtil.toStringWithNull(entity), XContentType.JSON);
            bulkRequest.add(updBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return calcBulkExecuteSuccessCount(bulkResponse, entityList.size());
    }

    /**
     * 获取符合条件的单个对象
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param filter
     * @return
     */
    public T getOne(Map<String, Object> filter) {
        SearchResponse response = executeCondQuery(filter, PageParam.getInstance(1,1));
        if(response.getHits().getTotalHits() > 0){
            SearchHit[] hits = response.getHits().getHits();
            return JsonUtil.toBean(hits[0].getSourceAsString(), this.getCurrClass());
        }
        return null;
    }

    /**
     * 获取所有记录并返回List
     * @see #listAll(String)
     * @return
     */
    public List<T> listAll() {
        return listAll(null);
    }

    /**
     * 获取所有记录并返回List，并指定排序字段
     * @description
     *      注意：
     *          1、此方法最多只能取出 Integer.MAX_VALUE 条数据
     *          2、一般来说，放到搜索引擎里面的数据都是比较大量的数据，想一次性拿出全部数据，显然不靠谱，因为这会受限于网络传输速度、jvm内存
     *              等因素影响，所以，本方法只适用于哪些数据量比较小的情况，比如：几万以内的数据
     * @param sortColumns 排序条件
     * @return
     */
    public List<T> listAll(String sortColumns) {
        //构造查询对象
        SearchRequestBuilder searchBuilder = getTransportClient()
                .prepareSearch(getIndex())
                .setTypes(getType())
                .setQuery(QueryBuilders.matchAllQuery())
                .setSize(Integer.MAX_VALUE);

        //添加排序字段
        addSort(searchBuilder, sortColumns);

        //查询、然后取得返回值并转化为List
        SearchResponse response = searchBuilder.execute().actionGet();
        return getEntityList(response);
    }

    /**
     * 多条件组合and查询并返回List(不分页、不可排序)
     * @see #listByCond(Map, String)
     * @param filter
     * @return
     */
    public List<T> listByCond(Map<String, Object> filter) {
        return listByCond(filter, "");
    }

    /**
     * 精确查询并返回List(不分页、可排序)
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param filter
     * @param sortColumns
     * @return
     */
    public List<T> listByCond(Map<String, Object> filter, String sortColumns) {
        PageParam pageParam = PageParam.getInstance(0, 0, sortColumns);
        SearchResponse searchResponse = executeCondQuery(filter, pageParam);
        return getEntityList(searchResponse);
    }

    /**
     * 多条件组合and查询并返回List(可分页、可排序)
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param filter
     * @param pageParam
     * @return
     */
    public BaseResponse<List<T>> listByCond(Map<String, Object> filter, PageParam pageParam){
        SearchResponse searchResponse = executeCondQuery(filter, pageParam);
        List<T> entityList = getEntityList(searchResponse);
        BaseResponse<List<T>> response = BaseResponse.success(entityList, pageParam, (int) searchResponse.getHits().getTotalHits());
        response.setScrollId(searchResponse.getScrollId());
        return response;
    }

    /**
     * 多条件组合and查询并返回MAP（不分页）
     * @see #mapByCond(Map, String, PageParam)
     * @param paramMap
     * @param property
     * @param <K>
     * @return
     */
    public <K> Map<K, T> mapByCond(Map<String, Object> paramMap, String property){
        return getMapResponse(executeCondQuery(paramMap, PageParam.getInstance(0, 0)), property, false);
    }

    /**
     * 多条件组合and查询并返回MAP（分页）
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param paramMap
     * @param property
     * @param <K>
     * @return
     */
    public <K> BaseResponse<Map<K, T>> mapByCond(Map<String, Object> paramMap, String property, PageParam pageParam){
        SearchResponse searchResponse = executeCondQuery(paramMap, pageParam);
        Map<K, T> data = getMapResponse(searchResponse, property, ! StringUtil.isEmpty(pageParam.getSortColumns()));
        BaseResponse<Map<K, T>> response = BaseResponse.success(data, (int) searchResponse.getHits().getTotalHits());
        response.setScrollId(searchResponse.getScrollId());
        return response;
    }

    /**------------------------------------------------- 根据Id作相关操作的便捷方法 START ---------------------------------------------------*/
    /**
     * 根据主键删除记录
     *
     * @param id
     */
    public int deleteById(PK id) {
        DeleteResponse response = getTransportClient().prepareDelete(getIndex(), getType(), String.valueOf(id))
                .execute()
                .actionGet();
        if(response.getResult().compareTo(DocWriteResponse.Result.DELETED) != 0){
            return 0;
        }else{
            return 1;
        }
    }

    /**
     * 根据多个主键id删除记录
     * @param idList
     * @return
     */
    public int deleteByIdList(List<PK> idList) {
        BulkRequestBuilder bulkRequest = getTransportClient().prepareBulk();
        for(PK id : idList){
            DeleteRequestBuilder deleteRequestBuilder = getTransportClient().prepareDelete(getIndex(), getType(), String.valueOf(id));
            bulkRequest.add(deleteRequestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        return calcBulkExecuteSuccessCount(bulkResponse, idList.size());
    }

    /**
     * 根据主键获取记录
     * @param id
     * @return
     */
    public T getById(PK id) {
        GetResponse response = getTransportClient()
                .prepareGet(getIndex(), getType(), String.valueOf(id))
                .get();
        if(response.isExists()){
            return JsonUtil.toBean(response.getSourceAsString(), this.getCurrClass());
        }else{
            return null;
        }
    }

    /**
     * 根据多个主键获取记录
     * @param idList List<Long>
     * @return
     */
    public List<T> listByIdList(List<PK> idList){
        List<T> entityList = new ArrayList<>();
        String[] idArray = new String[idList.size()];
        int i = 0;
        for(PK id :  idList){
            idArray[i] = String.valueOf(id);
            i++;
        }

        MultiGetResponse response = getTransportClient()
                .prepareMultiGet()
                .add(getIndex(), getType(), idArray)
                .get();
        Iterator<MultiGetItemResponse> iterator = response.iterator();
        while(iterator.hasNext()){
            MultiGetItemResponse itemResponse = iterator.next();
            if(! itemResponse.isFailed()){
                T entity = JsonUtil.toBean(itemResponse.getResponse().getSourceAsString(), this.getCurrClass());
                entityList.add(entity);
            }
        }
        return entityList;
    }

    /**
     * 多条件组合and查询并返回以id为key的MAP（不分页）
     * key:value = 主键:实体对象 的键值对，其中key默认是字段名为id的值
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param paramMap
     * @return
     */
    public Map<PK, T> mapByIdCond(Map<String, Object> paramMap){
        return getMapResponse(executeCondQuery(paramMap, PageParam.getInstance(0, 0)), DEFAULT_ID_COLUMN_NAME, false);
    }

    /**
     * 多条件组合and查询并返回以id为key的MAP（分页）
     * key:value = 主键:实体对象 的键值对，其中key默认是字段名为id的值
     * @see #executeCondQuery(Map, PageParam, String...)
     * @param paramMap
     * @return
     */
    public Map<PK, T> mapByIdCond(Map<String, Object> paramMap, PageParam pageParam){
        return getMapResponse(executeCondQuery(paramMap, pageParam), DEFAULT_ID_COLUMN_NAME, ! StringUtil.isEmpty(pageParam.getSortColumns()));
    }

    /**
     * 删除整个index
     * @return
     */
    public boolean deleteIndex(){
        DeleteResponse response = getTransportClient().prepareDelete()
                .setIndex(getIndex())
                .execute()
                .actionGet();
        if(response.getResult().compareTo(DocWriteResponse.Result.DELETED) != 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除某个index下的某个type
     * @return
     */
    public boolean deleteType(){
        DeleteResponse response = getTransportClient().prepareDelete()
                .setIndex(getIndex())
                .setType(getType())
                .execute()
                .actionGet();
        if(response.getResult().compareTo(DocWriteResponse.Result.DELETED) != 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 添加排序字段
     * @param searchBuilder
     * @param sortColumns
     */
    protected void addSort(SearchRequestBuilder searchBuilder, String sortColumns){
        if(! StringUtil.isEmpty(sortColumns)){
            String[] sortColumnArray = sortColumns.split(PublicConfig.COMMA_SEPARATOR);
            for(int i=0; i<sortColumnArray.length; i++){
                String[] sortColumn = sortColumnArray[i].split(" ");
                if(sortColumn.length > 1){
                    searchBuilder.addSort(sortColumn[0], SortOrder.fromString(sortColumn[1]));
                }else{
                    searchBuilder.addSort(sortColumn[0], SortOrder.ASC);
                }
            }
        }
    }

    /**
     * 计算批量操作成功的个数
     * @param bulkResponse
     * @param totalCount
     * @return
     */
    protected int calcBulkExecuteSuccessCount(BulkResponse bulkResponse, int totalCount){
        if(bulkResponse.hasFailures()){
            logger.error(bulkResponse.buildFailureMessage());//如果es操作失败，则打印到日志文件中
            Iterator<BulkItemResponse> iterator = bulkResponse.iterator();
            while(iterator.hasNext()){
                BulkItemResponse item = iterator.next();
                if(item.isFailed()){
                    totalCount --;
                }
            }
        }
        return totalCount;
    }

    /**
     * 从查询结果中转换成List<T>返回
     * @param searchResponse
     * @return
     */
    protected List<T> getEntityList(SearchResponse searchResponse){
        List<T> entityList = new ArrayList<>();
        if(searchResponse.getHits().getTotalHits() > 0){
            SearchHit[] hits = searchResponse.getHits().getHits();
            for(int i=0; i<hits.length; i++){
                entityList.add(JsonUtil.toBean(hits[i].getSourceAsString(), this.getCurrClass()));
            }
        }
        return entityList;
    }

    /**
     * 从查询结果中转换成List<PK>返回
     * @param searchResponse
     * @return
     */
    protected List<PK> getIdList(SearchResponse searchResponse){
        List<PK> idList = null;
        if(searchResponse.getHits().getTotalHits() > 0){
            idList = new ArrayList<>();
            SearchHit[] hits = searchResponse.getHits().getHits();
            for(int i=0; i<hits.length; i++){
                idList.add((PK) hits[i].getId());
            }
        }
        return idList;
    }

    /**
     * 取得Map返回方式的返回值
     * @param response
     * @param property
     * @param <K>
     * @return
     */
    protected <K> Map<K, T> getMapResponse(SearchResponse response, String property, boolean isSort){
        Map<K, T> returnMap;
        if(isSort){
            returnMap = new LinkedHashMap<>();//保持排序顺序
        }else{
            returnMap = new HashMap<>();
        }

        if(response.getHits().getTotalHits() > 0){
            SearchHit[] hits = response.getHits().getHits();
            for(int i=0; i<hits.length; i++){
                K key = (K) hits[i].getSourceAsMap().get(property);
                returnMap.put(key, JsonUtil.toBean(hits[i].getSourceAsString(), this.getCurrClass()));
            }
        }
        return returnMap;
    }

    /**
     * 是否范围查询：只有数字类型，或者字符串形式的日期，才是范围查询的（等同于sql中的 in > < 查询）
     * @param value
     * @return
     */
    private boolean isRangeQuery(String key, Object value){
        if(key.contains(RANGE_QUERY_START_SUFFIX) || key.contains(RANGE_QUERY_END_SUFFIX)){
            if(value instanceof Number){
                return true;
            }else if(TimeUtil.isDateTime(value)){
                return true;
            }
        }
        return false;
    }

    /**
     * 取得范围查询对象 RangeQueryBuilder
     * @param key
     * @param value
     * @param filter
     * @param alreadyUsedKey
     * @return
     */
    private RangeQueryBuilder getRangeQueryBuilder(String key, Object value, Map<String, Object> filter, Map<String, String> alreadyUsedKey){
        String startKey = "";
        String endKey = "";
        String fieldName = key;//默认等于key
        if(key.contains(RANGE_QUERY_START_SUFFIX)){
            fieldName = getFieldName(key, RANGE_QUERY_START_SUFFIX);
            startKey = key;
            endKey = fieldName + RANGE_QUERY_END_SUFFIX;
        }else if(key.contains(RANGE_QUERY_END_SUFFIX)){
            fieldName = getFieldName(key, RANGE_QUERY_END_SUFFIX);
            startKey = fieldName + RANGE_QUERY_START_SUFFIX;
            endKey = key;
        }

        RangeQueryBuilder queryBuilder = QueryBuilders.rangeQuery(fieldName);

        //如果是日期，还需要设置日期的格式
        String strValue = String.valueOf(value);//因为只有数字、字符串类型的日期格式的才会成为范围查询，所以可以直接转为字符串
        if(TimeUtil.isDateTime(strValue)){
            if(TimeUtil.isDateOnly(strValue)){
                queryBuilder.format(TimeUtil.DATE_FORMAT);
            }else{
                queryBuilder.format(TimeUtil.DATE_TIME_FORMAT);
            }
        }

        if(filter.containsKey(startKey) && ! StringUtil.isEmpty(filter.get(startKey))){
            queryBuilder.from(filter.get(startKey), true);//设置开始值
            alreadyUsedKey.put(startKey, "");
        }
        if(filter.containsKey(endKey) && ! StringUtil.isEmpty(filter.get(endKey))){
            queryBuilder.to(filter.get(endKey));//设置结束值
            alreadyUsedKey.put(endKey, "");
        }
        return queryBuilder;
    }

    /**
     * @title 多条件组合and查询并返回List(可分页、可排序)
     * @Description
     *   1、Map中key是以 Start、End 为后缀，并且value是数字或字符串时，这个字段才会进行范围查询，如下面两个字段：
     *      如price字段：
     *          key:value = "priceStart":20
     *          key:value = "priceEnd":25.06
     *
     *      如createTime字段：
     *          key:value = "createTimeStart":"2017-02-03 01:02:20"
     *          key:value = "createTimeEnd":"2017-05-02 22:20:19"
     *
     *   2、当key不是以Start、End 为后缀，并且value是数字时，会作精确匹配查询，如 key:value = "status":2
     *   3、当key不是以Start、End 为后缀，并且value是字符串时
     *       3.1、如果key是以Like最为后缀，则会作全文检索查询，如 key:value = "titleLike":"elasticsearch is good"
     *       3.1、如果key没有后缀，则会作精确匹配查询，如 key:value = "product":"elasticsearch"
     *   4、如果value是一个Collection，则会作精确匹配查询，等同于sql中的 IN 查询
     *   5、Map中key是""或null的，将会被忽略
     *   6、Map中value是""或null的，将会被忽略
     *
     * @param filter 查询条件
     * @param pageParam 分页参数，如果 sortColumns 属性为空，则不排序，如果 pageCurrent 或 pageSize <= 0 则不分页
     * @param fetchFields 指定只获取某些字段，如果不指定，则默认取全部字段
     * @return
     */
    private SearchResponse executeCondQuery(Map<String, Object> filter, PageParam pageParam, String... fetchFields){
        //校验参数
        if(pageParam.isScroll()){
            if(pageParam.getTimeValue() <= 0){
                throw new BizException("timeValue需大于0");
            }else if(pageParam.getTimeUnit()==null){
                throw new BizException("timeUnit不能为空");
            }
        }

        //是否属于scroll查询的非首次查询，如果是，则查询之后直接返回
        boolean isScrollQueryContinue = pageParam.isScroll() && StringUtil.isEmpty(pageParam.getScrollId())==false;
        if(isScrollQueryContinue){
            SearchResponse searchResponse = getTransportClient().prepareSearchScroll(pageParam.getScrollId())
                    .setScroll(new TimeValue(pageParam.getTimeValue(), pageParam.getTimeUnit()))//刷新有效时间
                    .execute()
                    .actionGet();

            //根据scrollId清空scroll context
            if(searchResponse.getHits().getHits().length < pageParam.getPageSize()){//如果数据已经取完了，那就清空scroll
                clearScroll(getTransportClient(), pageParam.getScrollId(), searchResponse.getScrollId());
            }
            return searchResponse;
        }

        //构建查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Map<String, String> alreadyUsedKey = new HashMap<>();//已经使用过的key
        if(filter != null){
            for(Map.Entry<String, Object> entry : filter.entrySet()){
                Object value = entry.getValue();
                if(StringUtil.isEmpty(entry.getKey())){
                    continue;
                }else if(StringUtil.isEmpty(value)){
                    continue;
                }else if(alreadyUsedKey.containsKey(entry.getKey())){
                    continue;
                }

                if(isRangeQuery(entry.getKey(), value)){
                    queryBuilder.filter(getRangeQueryBuilder(entry.getKey(), value, filter, alreadyUsedKey));
                }else if(value instanceof Number){
                    queryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), value));//精确匹配
                }else if(value instanceof String){
                    if(StringUtil.isEmpty(value)){
                        continue;//elasticsearch 不允许 null、"" 的查询，所以直接忽略
                    }else if(entry.getKey().contains(QUERY_LIKE_SUFFIX)){
                        queryBuilder.filter(QueryBuilders.matchQuery(getFieldName(entry.getKey(), QUERY_LIKE_SUFFIX), value));//全文搜索
                    }else{
                        queryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), value));//精确匹配
                    }
                }else if(value instanceof Collection){
                    String fieldName = entry.getKey();
                    if(fieldName.contains(QUERY_LIST_SUFFIX)){
                        fieldName = getFieldName(fieldName, QUERY_LIST_SUFFIX);
                    }
                    queryBuilder.filter(QueryBuilders.termsQuery(fieldName, ((Collection)value).toArray()));//相当于sql的 in 查询
                }
            }
        }

        //构造查询请求对象
        SearchRequestBuilder searchBuilder  = getTransportClient().prepareSearch()
                .setIndices(getIndex())
                .setTypes(getType())
                .setSize(pageParam.getPageSize());
        if(pageParam.isScroll()){//使用scroll分页查询的首次查询
            searchBuilder.setScroll(new TimeValue(pageParam.getTimeValue(), pageParam.getTimeUnit()));
        }else if(pageParam.getPageCurrent() > 0 && pageParam.getPageSize() > 0){//如果不用scroll，则使用传统分页，为查询对象添加分页起始位置
            int offset = (pageParam.getPageCurrent() - 1) * pageParam.getPageSize();
            if(! pageParam.isScroll() && offset + pageParam.getPageSize() > MAX_PAGE_QUERY_NUM){
                throw new BizException("当前查询方式不能查询超过"+MAX_PAGE_QUERY_NUM+"条");
            }
            searchBuilder.setFrom(offset);
        }

        //如果有指定只获取某些字段，就设置只返回某些字段
        if(fetchFields != null && fetchFields.length > 0){
            searchBuilder.setFetchSource(fetchFields, null);
        }

        //为查询对象设置查询条件
        searchBuilder.setQuery(queryBuilder);

        //为查询对象添加排序字段
        addSort(searchBuilder, pageParam.getSortColumns());

        return searchBuilder.execute().actionGet();
    }

    private String getFieldName(String key, String suffix){
        return key.substring(0, key.length()-suffix.length());
    }

    /**
     * 清除滚动ID
     * @param client
     * @param scrollIds
     * @return
     */
    public boolean clearScroll(TransportClient client, String... scrollIds){
        ClearScrollRequestBuilder clearScrollRequestBuilder = client.prepareClearScroll();
        clearScrollRequestBuilder.setScrollIds(Arrays.asList(scrollIds));
        ClearScrollResponse response = clearScrollRequestBuilder.get();
        return response.isSucceeded();
    }
}
