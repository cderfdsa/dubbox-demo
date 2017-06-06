/*
 * Powered By [cyf.com]
 */

package com.cyf.shop.auth.bean;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import com.cyf.base.common.bean.BaseEntity;

/**
 * 权限表
 */
public class Authority extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//节点分组id(主键)
	private Long id;
	//分组名称
	private String name;
	//父分组id(0=顶级分组)
	private Long parentId;
	//分组层级
	private Integer level;
	//权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)
	private Integer checkLevel;
	//状态(1=正常,2=禁用)
	private Integer status;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public Authority(){}

	public Authority(Long id){
		this.id = id;
	}

	/**
	*节点分组id(主键)
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*节点分组id(主键)
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*分组名称
	*/
	public void setName(String value) {
		this.name = value;
	}
	
	/**
	*分组名称
	*/
	public String getName() {
		return this.name;
	}
	/**
	*父分组id(0=顶级分组)
	*/
	public void setParentId(Long value) {
		this.parentId = value;
	}
	
	/**
	*父分组id(0=顶级分组)
	*/
	public Long getParentId() {
		return this.parentId;
	}
	/**
	*分组层级
	*/
	public void setLevel(Integer value) {
		this.level = value;
	}
	
	/**
	*分组层级
	*/
	public Integer getLevel() {
		return this.level;
	}
	/**
	*权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)
	*/
	public void setCheckLevel(Integer value) {
		this.checkLevel = value;
	}
	
	/**
	*权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)
	*/
	public Integer getCheckLevel() {
		return this.checkLevel;
	}
	/**
	*状态(1=正常,2=禁用)
	*/
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	/**
	*状态(1=正常,2=禁用)
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
			.append("ParentId",getParentId())
			.append("Level",getLevel())
			.append("CheckLevel",getCheckLevel())
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
		if(obj instanceof Authority == false) return false;
		if(this == obj) return true;
		Authority other = (Authority)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("节点分组id(主键)不能为空.");
		}
		if (StringUtils.isBlank(this.name)) {
			throw new RuntimeException("分组名称不能为空.");
		}
		if(null != this.name && this.name.length() > 80){
				throw new RuntimeException("分组名称不能超过80个字.");
		}
		if (null == this.parentId) {
			throw new RuntimeException("父分组id(0=顶级分组)不能为空.");
		}
		if (null == this.level) {
			throw new RuntimeException("分组层级不能为空.");
		}
		if (null == this.checkLevel) {
			throw new RuntimeException("权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)不能为空.");
		}
		if (null == this.status) {
			throw new RuntimeException("状态(1=正常,2=禁用)不能为空.");
		}
		if (null == this.createTime) {
			throw new RuntimeException("创建时间不能为空.");
		}
		if (null == this.updateTime) {
			throw new RuntimeException("更新时间不能为空.");
		}
	}

	/**
	* 验证数据是否合法,合法时返回空字符串，不合法时返回错误信息
	*/
	public String getValidateMessage(boolean isIgnorePk){
		String message = "";
		if (isIgnorePk == false && null == this.id) {
			message = "节点分组id(主键)不能为空.";
		}
		if (StringUtils.isBlank(this.name)) {
			message = "分组名称不能为空.";
		}
		if(null != this.name && this.name.length() > 80){
			message = "分组名称不能超过80个字.";
		}
		if (null == this.parentId) {
			message = "父分组id(0=顶级分组)不能为空.";
		}
		if (null == this.level) {
			message = "分组层级不能为空.";
		}
		if (null == this.checkLevel) {
			message = "权限检查等级(1=游客可访问 2=登陆可访问 3=授权可访问)不能为空.";
		}
		if (null == this.status) {
			message = "状态(1=正常,2=禁用)不能为空.";
		}
		if (null == this.createTime) {
			message = "创建时间不能为空.";
		}
		if (null == this.updateTime) {
			message = "更新时间不能为空.";
		}
		return message;
	}
}

