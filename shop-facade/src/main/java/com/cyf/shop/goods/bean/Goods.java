/*
 * Powered By [cyf.com]
 */

package com.cyf.shop.goods.bean;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import com.cyf.base.common.bean.BaseEntity;

/**
 * 
 */
public class Goods extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//主键id
	private Long id;
	//商品名称
	private String name;
	//商品价格
	private Double price;
	//状态 1=有效 2=无效
	private Integer status;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public Goods(){}

	public Goods(Long id){
		this.id = id;
	}

	/**
	*主键id
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*主键id
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*商品名称
	*/
	public void setName(String value) {
		this.name = value;
	}
	
	/**
	*商品名称
	*/
	public String getName() {
		return this.name;
	}
	/**
	*商品价格
	*/
	public void setPrice(Double value) {
		this.price = value;
	}
	
	/**
	*商品价格
	*/
	public Double getPrice() {
		return this.price;
	}
	/**
	*状态 1=有效 2=无效
	*/
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	/**
	*状态 1=有效 2=无效
	*/
	public Integer getStatus() {
		return this.status;
	}
	/**
	*创建时间
	*/
	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}
	
	/**
	*创建时间
	*/
	public java.util.Date getCreateTime() {
		return this.createTime;
	}
	/**
	*更新时间
	*/
	public void setUpdateTime(java.util.Date value) {
		this.updateTime = value;
	}
	
	/**
	*更新时间
	*/
	public java.util.Date getUpdateTime() {
		return this.updateTime;
	}

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("Price",getPrice())
			.append("Status",getStatus())
			.append("CreateTime",getCreateTime())
			.append("UpdateTime",getUpdateTime())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Goods == false) return false;
		if(this == obj) return true;
		Goods other = (Goods)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("主键id不能为空.");
		}
		if(null != this.name && this.name.length() > 300){
				throw new RuntimeException("商品名称不能超过300个字.");
		}
	}

	/**
	* 验证数据是否合法,合法时返回空字符串，不合法时返回错误信息
	*/
	public String getValidateMessage(boolean isIgnorePk){
		String message = "";
		if (isIgnorePk == false && null == this.id) {
			message = "主键id不能为空.";
		}
		if(null != this.name && this.name.length() > 300){
			message = "商品名称不能超过300个字.";
		}
		return message;
	}
}

