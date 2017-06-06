package com.cyf.shop.business.goods.biz;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.goods.bean.Goods;
import com.cyf.shop.business.goods.dao.GoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenyf on 2017/3/11.
 */
@Transactional
@Component
public class GoodsBiz {
    @Autowired
    GoodsDao goodsDao;

    public List<Goods> listAllGoods(){
        return goodsDao.listAll();
    }

    public Goods getGoodsById(Long goodsId){
        return goodsDao.getById(goodsId);
    }

    public List<Goods> listGoodsByIdList(List<Long> goodsIdList){
        return goodsDao.listByIdList(goodsIdList);
    }

    public Long addGoods(Goods goods){
        if(goods.getStatus()==null) goods.setStatus(1);
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());

        if(goodsDao.insert(goods) > 0){
            return goods.getId();
        }else{
            return null;
        }
    }

    public List<Long> batchAddGoods(List<Goods> goodsList){
        for(Goods goods : goodsList){
            if(goods.getStatus()==null) goods.setStatus(1);
            goods.setCreateTime(new Date());
            goods.setUpdateTime(new Date());
        }

        List<Long> goodsIdList = new ArrayList<>();
        if(goodsDao.insertList(goodsList) > 0){
            for(Goods goods : goodsList){
                goodsIdList.add(goods.getId());
            }
            return goodsIdList;
        }else{
            return null;
        }
    }

    public BaseResponse<String> updateGoods(Goods goods){
        goods.setUpdateTime(new Date());

        if(goodsDao.update(goods) > 0){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("更新记录失败！");
        }
    }

    public BaseResponse<String> batchUpdateGoods(List<Goods> goodsList){
        for(Goods goods : goodsList){
            goods.setUpdateTime(new Date());
        }
        if(goodsDao.updateList(goodsList) > 0){
            return BaseResponse.success();
        }else{
            return BaseResponse.fail("批量更新记录失败！");
        }
    }
}
