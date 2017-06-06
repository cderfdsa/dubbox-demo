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
 * 角色-用户关联表
 */
public class RoleUser extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//角色-用户关联id(主键)
	private Long id;
	//角色id(外键)
	private Long fkRoleId;
	//用户id(外键)
	private Long fkUserId;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public RoleUser(){}

	public RoleUser(Long id){
		this.id = id;
	}

	/**
	*角色-用户关联id(主键)
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*角色-用户关联id(主键)
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*角色id(外键)
	*/
	public void setFkRoleId(Long value) {
		this.fkRoleId = value;
	}
	
	/**
	*角色id(外键)
	*/
	public Long getFkRoleId() {
		return this.fkRoleId;
	}
	/**
	*用户id(外键)
	*/
	public void setFkUserId(Long value) {
		this.fkUserId = value;
	}
	
	/**
	*用户id(外键)
	*/
	public Long getFkUserId() {
		return this.fkUserId;
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
			.append("FkRoleId",getFkRoleId())
			.append("FkUserId",getFkUserId())
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
		if(obj instanceof RoleUser == false) return false;
		if(this == obj) return true;
		RoleUser other = (RoleUser)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("角色-用户关联id(主键)不能为空.");
		}
		if (null == this.fkRoleId) {
			throw new RuntimeException("角色id(外键)不能为空.");
		}
		if (null == this.fkUserId) {
			throw new RuntimeException("用户id(外键)不能为空.");
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
			message = "角色-用户关联id(主键)不能为空.";
		}
		if (null == this.fkRoleId) {
			message = "角色id(外键)不能为空.";
		}
		if (null == this.fkUserId) {
			message = "用户id(外键)不能为空.";
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

