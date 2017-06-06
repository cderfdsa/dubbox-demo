package com.cyf.shop.test.auth;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.base.common.bean.PageParam;
import com.cyf.base.common.utils.JsonUtil;
import com.cyf.shop.auth.bean.Authority;
import com.cyf.shop.business.auth.dao.AuthorityDao;
import com.cyf.shop.business.auth.dao.AuthorityEsDao;
import com.cyf.shop.business.message.dao.MessageConsumeDao;
import com.cyf.shop.business.message.dao.MessageConsumeEsDao;
import com.cyf.shop.message.bean.MessageConsume;
import com.cyf.shop.test.BaseTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/31.
 */
public class EsTest extends BaseTestCase {
    @Autowired
    AuthorityEsDao authorityEsDao;
    @Autowired
    AuthorityDao authorityDao;
    @Autowired
    MessageConsumeDao messageConsumeDao;
    @Autowired
    MessageConsumeEsDao messageConsumeEsDao;

    @Ignore
    @Test
    public void create(){
        List<Authority> authList = authorityDao.listAll("id asc");
        int count = authorityEsDao.insertList(authList);
        System.out.println(count);
    }

    @Ignore
    @Test
    public void listByIdList(){
        List<Long> idList = new ArrayList<>();
        idList.add(141L);
        idList.add(142L);
        idList.add(143L);
        idList.add(144L);
        idList.add(145L);
        idList.add(146L);
        idList.add(147L);
        idList.add(148L);
        idList.add(151L);
        idList.add(152L);
        idList.add(153L);
        idList.add(154L);
        idList.add(155L);

        List<Authority> authList = authorityEsDao.listByIdList(idList);
        System.out.println("idList size = "+idList.size());
        System.out.println("result size = "+authList.size());
        System.out.println(JsonUtil.toStringPretty(authList));
    }

    @Ignore
    @Test
    public void update(){
        Authority auth = authorityEsDao.getById(153L);
        auth.setStatus(2);
        System.out.println(JsonUtil.toStringPretty(auth));
        authorityEsDao.update(auth);
    }

//    @Ignore
    @Test
    public void listByExact(){
        Map<String, Object> filter = new HashMap<>();
        filter.put("parentId", 152);
        filter.put("status", 1);
        List<Authority> authList = authorityEsDao.listByCond(filter);
        System.out.println("result size = "+authList.size());
        System.out.println(JsonUtil.toStringPretty(authList));
    }

    @Ignore
    @Test
    public void listPageByExact(){
        Map<String, Object> filter = new HashMap<>();
        filter.put("parentId", 152);
        filter.put("status", 1);
        PageParam pageParam = PageParam.getInstance(2, 2);
        BaseResponse<List<Authority>> response = authorityEsDao.listByCond(filter, pageParam);
        System.out.println("message = "+response.getMessage());
        System.out.println("total = "+response.getTotalRecord());
        System.out.println("statusCode = "+response.getStatusCode());
        System.out.println("data size = "+response.getData().size());
        System.out.println(JsonUtil.toStringPretty(response.getData()));
    }

    @Ignore
    @Test
    public void listByRange(){
        Map<String, Object> filter = new HashMap<>();
        List<Long> parentIdList = new ArrayList<>();
        parentIdList.add(152L);
        parentIdList.add(162L);

        filter.put("parentIdList", parentIdList);
        filter.put("status", 1);
        List<Authority> authList = authorityEsDao.listByCond(filter);
        System.out.println("data size = "+authList.size());
        System.out.println(JsonUtil.toStringPretty(authList));
    }

    @Ignore
    @Test
    public void listPageByRange(){
        Map<String, Object> filter = new HashMap<>();
        List<Long> parentIdList = new ArrayList<>();
        parentIdList.add(152L);
        parentIdList.add(162L);

        filter.put("parentIdList", parentIdList);
        filter.put("status", 1);
        PageParam pageParam = PageParam.getInstance(2, 3, "id");
        BaseResponse<List<Authority>> response = authorityEsDao.listByCond(filter, pageParam);
        System.out.println("message = "+response.getMessage());
        System.out.println("total = "+response.getTotalRecord());
        System.out.println("statusCode = "+response.getStatusCode());
        System.out.println("data size = "+response.getData().size());
        System.out.println(JsonUtil.toStringPretty(response.getData()));
    }

    @Ignore
    @Test
    public void mapByExact(){
        Map<String, Object> filter = new HashMap<>();
        filter.put("parentId", 152);
        Map<Long, Authority> map = authorityEsDao.mapByCond(filter, "id");
        System.out.println("map size = "+map.size());
        System.out.println(JsonUtil.toStringPretty(map));
    }

    @Ignore
    @Test
    public void mapPageByExact(){
        Map<String, Object> filter = new HashMap<>();
        filter.put("parentId", 152);
        PageParam pageParam = PageParam.getInstance(1, 2, "id desc");
        BaseResponse<Map<Long, Authority>> response = authorityEsDao.mapByCond(filter, "id", pageParam);
        System.out.println("map size = "+response.getData().size());
        System.out.println(JsonUtil.toStringPretty(response.getData()));
    }

    @Test
    public void deleteBy(){
        Map<String, Object> filter = new HashMap<>();
        filter.put("parentId", 152);
        int count = authorityEsDao.deleteBy(filter);
        System.out.println("deleted size = "+count);
    }

    @Ignore
    @Test
    public void importDateIntoEs(){
        boolean isContinue = true;
        PageParam pageParam = PageParam.getInstance(1, 4000);
        while(isContinue){
            BaseResponse<List<MessageConsume>> response = messageConsumeDao.listByCond(new HashMap<>(), pageParam);
            if(response.isSuccess() && response.getData() != null && response.getData().size() > 0){
                int count = messageConsumeEsDao.insertList(response.getData());
                System.out.println("pageCurrent："+response.getPageCurrent());
                System.out.println("插入数量为："+count);
            }
            if(response.isError() || response.getData()==null || response.getData().size() <= 0){
                isContinue = false;
            }else{
                pageParam.setPageCurrent(pageParam.getPageCurrent() + 1);
            }
        }
    }

    @Test
    public void listByCond(){
        BaseResponse<List<MessageConsume>> response;
        Map<String, Object> filter = new HashMap<>();
        filter.put("type", 2);
        PageParam pageParam = PageParam.getInstance(1, 2000);
        pageParam.setScrollId("");//使用scroll方式查询
        do{
            response = messageConsumeEsDao.listByCond(filter, pageParam);
            pageParam.setScrollId(response.getScrollId());
            System.out.println("data size = "+response.getData().size());
//            System.out.println(JsonUtil.toStringPretty(response));
        }while(response.getData().size() >= pageParam.getPageSize());
    }

    @Test
    public void deleteType(){
        System.out.println(messageConsumeEsDao.deleteType());
    }
}
