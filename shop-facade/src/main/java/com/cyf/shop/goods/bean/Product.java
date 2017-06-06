package com.cyf.shop.goods.bean;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Title：
 * @Description：
 * @Author： chenyf
 * @Version： V1.0
 * @Date： 2017/3/2 13:48
 */
public class Product implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long productId;

    private String name;

    private int quantity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productId == null) ? 0 : productId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Product other = (Product) obj;
        if (productId == null) {
            if (other.productId != null)
                return false;
        } else if (!productId.equals(other.productId))
            return false;
        return true;
    }

    private static AtomicLong al = new AtomicLong(1);

    public static Product getProduct(){
        Product pro = new Product();
        pro.setProductId(al.getAndIncrement());
        pro.setQuantity(getRandomInt(115));
        pro.setName(getRandomString());
        return pro;
    }
    private static int getRandomInt(int max){
        return (new Random()).nextInt(max);
    }
    private static String getRandomString(){
        String[] arr = {"小米5","苹果7","魅族6","华为7","OPPO","VIVO"};
        int index = (new Random()).nextInt(5);
        return arr[index];
    }

    @Override
    public String toString() {
        return "[productId=" + productId + ", name=" + name + ", quantity=" + quantity + "]";
    }
}
