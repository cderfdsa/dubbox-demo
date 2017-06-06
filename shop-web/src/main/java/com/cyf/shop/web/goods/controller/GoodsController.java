package com.cyf.shop.web.goods.controller;

import com.cyf.base.common.bean.AjaxResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/21 10:39
 */
@Controller
@RequestMapping("goods/goods")
public class GoodsController {

    @RequestMapping("list_goods")
    public ModelAndView list_goods(){
        ModelAndView mv = new ModelAndView();

        return mv;
    }

    @RequestMapping("edit_goods")
    public ModelAndView editGoods(){
        ModelAndView mv = new ModelAndView();

        return mv;
    }

    @ResponseBody//返回json格式的数据
    @RequestMapping("edit_goods_save/{id}")
    public AjaxResult editGoodsSave(){

        return AjaxResult.success("保存编辑成功");
    }
}
