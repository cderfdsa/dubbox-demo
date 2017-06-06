package com.cyf.shop.business.goods.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.goods.bean.Goods;
import com.cyf.shop.goods.service.GoodsService;
import com.cyf.shop.business.goods.biz.GoodsBiz;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyf on 2017/3/11.
 */
@Service
@Path("goods")
public class GoodsServiceImpl implements GoodsService{
    @Autowired
    GoodsBiz goodsBiz;

    @Path("listAllGoods")
    public List<Goods> listAllGoods(){
        List<Goods> goodsList = goodsBiz.listAllGoods();
        return goodsList;
    }

    @Path("getGoodsById")
    public Goods getGoodsById(Long goodsId){
        return goodsBiz.getGoodsById(goodsId);
    }

    @Path("addGoods")
    public BaseResponse<Goods> addGoods(Goods goods){
        Long goodsId = goodsBiz.addGoods(goods);
        return BaseResponse.success(goodsBiz.getGoodsById(goodsId));
    }

    @Path("batchAddGoods")
    public BaseResponse<List<Goods>> batchAddGoods(List<Goods> goodsList){
        List<Long> goodsIdList = goodsBiz.batchAddGoods(goodsList);
        goodsList = goodsBiz.listGoodsByIdList(goodsIdList);
        return BaseResponse.success(goodsList);
    }

    @Path("updateGoods")
    public BaseResponse<Goods> updateGoods(Goods goods){
        BaseResponse<String> resp = goodsBiz.updateGoods(goods);
        if(resp.isSuccess()){
            return BaseResponse.success("更新记录失败");
        }else{
            return BaseResponse.fail("更新记录失败");
        }
    }

    @Path("batchUpdateGoods")
    public BaseResponse<List<Goods>> batchUpdateGoods(List<Goods> goodsList){
        BaseResponse<String> resp = goodsBiz.batchUpdateGoods(goodsList);

        if(resp.isError()){
            return BaseResponse.fail(resp.getMessage());
        }else{
            List<Long> goodsIdList = new ArrayList<>();
            for(Goods goods : goodsList){
                goodsIdList.add(goods.getId());
            }
            goodsList = goodsBiz.listGoodsByIdList(goodsIdList);
            return BaseResponse.success(goodsList);
        }
    }
}
