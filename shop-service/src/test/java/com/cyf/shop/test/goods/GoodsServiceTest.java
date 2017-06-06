package com.cyf.shop.test.goods;

import com.cyf.base.common.utils.JsonUtil;
import com.cyf.shop.goods.bean.Goods;
import com.cyf.shop.goods.service.GoodsService;
import com.cyf.shop.test.BaseTestCase;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenyf on 2017/3/15.
 */
public class GoodsServiceTest extends BaseTestCase {
    @Autowired
    private GoodsService goodsService;//可能IDE会报错，不用理会

    @Test
    @Ignore
    public void addGoods(){
        for(int i=0; i<5; i++){
            Goods goods = new Goods();
            goods.setName("i"+i);
            goods.setStatus(1);
            goods.setPrice(23.5);
            goods.setCreateTime(new Date());
            goods.setUpdateTime(new Date());
            goodsService.addGoods(goods);
        }
    }

    @Test
    @Ignore
    public void batchAddGoods(){
        List<Goods> goodList = new ArrayList<>();
        for(int i=0; i<5; i++){
            Goods goods = new Goods();
            goods.setName("i+"+(i+1));
            goods.setStatus(1);
            goods.setPrice(26.5);
            goods.setCreateTime(new Date());
            goods.setUpdateTime(new Date());
            goodList.add(goods);
        }
        goodsService.batchAddGoods(goodList);
    }

    @Test
    @Ignore
    public void updateGoods(){
        Goods goods = goodsService.getGoodsById(1l);
        goods.setName("张三");
        goodsService.updateGoods(goods);
    }

    @Test
    @Ignore
    public void batchUpdateGoods(){
        Goods goods1 = goodsService.getGoodsById(2l);
        Goods goods2 = goodsService.getGoodsById(3l);

        goods1.setName("李四");
        goods2.setName("王五");
        List<Goods> goodList = new ArrayList<>();
        goodList.add(goods1);
        goodList.add(goods2);
        goodsService.batchUpdateGoods(goodList);
    }

    @Test
    @Ignore
    public void getGoodsById(){
        Goods goods = goodsService.getGoodsById(1l);
        System.out.println("");
        System.out.println(JsonUtil.toStringPretty(goods));
        System.out.println("");
    }

    @Test
    @Ignore
    public void listAllGoods(){
        List<Goods> allGoods = goodsService.listAllGoods();
        System.out.println("");
        System.out.println(JsonUtil.toStringPretty(allGoods));
        System.out.println("");
    }
}
