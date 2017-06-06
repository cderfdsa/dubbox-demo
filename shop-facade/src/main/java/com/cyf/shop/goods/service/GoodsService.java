package com.cyf.shop.goods.service;

import com.cyf.base.common.bean.BaseResponse;
import com.cyf.shop.goods.bean.Goods;

import java.util.List;

/**
 * Created by chenyf on 2017/3/12.
 */
public interface GoodsService {

    public List<Goods> listAllGoods();

    public Goods getGoodsById(Long goodsId);

    public BaseResponse<Goods> addGoods(Goods goods);

    public BaseResponse<List<Goods>> batchAddGoods(List<Goods> goodsList);

    public BaseResponse<Goods> updateGoods(Goods goods);

    public BaseResponse<List<Goods>> batchUpdateGoods(List<Goods> goodsList);
}
