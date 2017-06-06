package com.cyf.shop.message;

import com.cyf.shop.goods.bean.Product;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/3 9:20
 */
public class DubboConsumerTest {

    public static void main(String[] args){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] customArgs = new String[]{"javaconfig"};
                com.alibaba.dubbo.container.Main.main(customArgs);
            }
        }).start();

        //先等待几秒，等待dubbo启动完成
        try{
            Thread.sleep(10 * 1000L);
        }catch(Exception e){
            e.printStackTrace();
        }

        Product pro = Product.getProduct();
//        DubboConsumerService service = SpringContextsUtil.getBean(DubboConsumerService.class);
    }
}
