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
 * 角色表
 */
public class Role extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//角色id(主键)
	private Long id;
	//角色名称
	private String name;
	//角色类型(1=普通角色 2=超级管理员(只有一个))
	private Integer type;
	//备注
	private String remark;
	//状态(1=正常,2=禁用)
	private Integer status;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public Role(){}

	public Role(Long id){
		this.id = id;
	}

	/**
	*角色id(主键)
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*角色id(主键)
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*角色名称
	*/
	public void setName(String value) {
		this.name = value;
	}
	
	/**
	*角色名称
	*/
	public String getName() {
		return this.name;
	}
	/**
	*角色类型(1=普通角色 2=超级管理员(只有一个))
	*/
	public void setType(Integer value) {
		this.type = value;
	}
	
	/**
	*角色类型(1=普通角色 2=超级管理员(只有一个))
	*/
	public Integer getType() {
		return this.type;
	}
	/**
	*备注
	*/
	public void setRemark(String value) {
		this.remark = value;
	}
	
	/**
	*备注
	*/
	public String getRemark() {
		return this.remark;
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
			.append("Type",getType())
			.append("Remark",getRemark())
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
		if(obj instanceof Role == false) return false;
		if(this == obj) return true;
		Role other = (Role)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("角色id(主键)不能为空.");
		}
		if(null != this.name && this.name.length() > 80){
				throw new RuntimeException("角色名称不能超过80个字.");
		}
		if (null == this.type) {
			throw new RuntimeException("角色类型(1=普通角色 2=超级管理员(只有一个))不能为空.");
		}
		if(null != this.remark && this.remark.length() > 300){
				throw new RuntimeException("备注不能超过300个字.");
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
			message = "角色id(主键)不能为空.";
		}
		if(null != this.name && this.name.length() > 80){
			message = "角色名称不能超过80个字.";
		}
		if (null == this.type) {
			message = "角色类型(1=普通角色 2=超级管理员(只有一个))不能为空.";
		}
		if(null != this.remark && this.remark.length() > 300){
			message = "备注不能超过300个字.";
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

