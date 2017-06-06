/*
 * Powered By [o2omedical.com]
 */

package com.cyf.base.user.bean;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import com.cyf.base.common.bean.BaseEntity;

/**
 * 用户表
 */
public class User extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//id(主键)
	private Long id;
	//手机
	private String phone;
	//邮箱
	private String email;
	//用户编码
	private String usercode;
	//密码
	private String password;
	//姓名
	private String username;
	//性别(1=男 2=女 3=保密)
	private Integer gender;
	//状态(1=正常 2=禁用)
	private Integer status;
	//是否删除(0=否 1=是)
	private Integer deleted;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public User(){
	}

	public User(
		Long id
	){
		this.id = id;
	}

	/**
	*id(主键)
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*id(主键)
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*手机
	*/
	public void setPhone(String value) {
		this.phone = value;
	}
	
	/**
	*手机
	*/
	public String getPhone() {
		return this.phone;
	}
	/**
	*邮箱
	*/
	public void setEmail(String value) {
		this.email = value;
	}
	
	/**
	*邮箱
	*/
	public String getEmail() {
		return this.email;
	}
	/**
	*用户编码
	*/
	public void setUsercode(String value) {
		this.usercode = value;
	}
	
	/**
	*用户编码
	*/
	public String getUsercode() {
		return this.usercode;
	}
	/**
	*密码
	*/
	public void setPassword(String value) {
		this.password = value;
	}
	
	/**
	*密码
	*/
	public String getPassword() {
		return this.password;
	}
	/**
	*姓名
	*/
	public void setUsername(String value) {
		this.username = value;
	}
	
	/**
	*姓名
	*/
	public String getUsername() {
		return this.username;
	}
	/**
	*性别(1=男 2=女 3=保密)
	*/
	public void setGender(Integer value) {
		this.gender = value;
	}
	
	/**
	*性别(1=男 2=女 3=保密)
	*/
	public Integer getGender() {
		return this.gender;
	}
	/**
	*状态(1=正常 2=禁用)
	*/
	public void setStatus(Integer value) {
		this.status = value;
	}
	
	/**
	*状态(1=正常 2=禁用)
	*/
	public Integer getStatus() {
		return this.status;
	}
	/**
	*是否删除(0=否 1=是)
	*/
	public void setDeleted(Integer value) {
		this.deleted = value;
	}
	
	/**
	*是否删除(0=否 1=是)
	*/
	public Integer getDeleted() {
		return this.deleted;
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
			.append("Phone",getPhone())
			.append("Email",getEmail())
			.append("Usercode",getUsercode())
			.append("Password",getPassword())
			.append("Username",getUsername())
			.append("Gender",getGender())
			.append("Status",getStatus())
			.append("Deleted",getDeleted())
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
		if(obj instanceof User == false) return false;
		if(this == obj) return true;
		User other = (User)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
					if (null == this.id) {
						throw new RuntimeException("id(主键)不能为空.");
					}

					if (StringUtils.isBlank(this.phone)) {
						throw new RuntimeException("手机不能为空.");
					}
					if(null != this.phone && this.phone.length() > 30){
						throw new RuntimeException("手机不能超过30个字.");
					}

					if (StringUtils.isBlank(this.email)) {
						throw new RuntimeException("邮箱不能为空.");
					}
					if(null != this.email && this.email.length() > 100){
						throw new RuntimeException("邮箱不能超过100个字.");
					}

					if (StringUtils.isBlank(this.usercode)) {
						throw new RuntimeException("用户编码不能为空.");
					}
					if(null != this.usercode && this.usercode.length() > 100){
						throw new RuntimeException("用户编码不能超过100个字.");
					}

					if (StringUtils.isBlank(this.password)) {
						throw new RuntimeException("密码不能为空.");
					}
					if(null != this.password && this.password.length() > 100){
						throw new RuntimeException("密码不能超过100个字.");
					}

					if(null != this.username && this.username.length() > 80){
						throw new RuntimeException("姓名不能超过80个字.");
					}

					if (null == this.gender) {
						throw new RuntimeException("性别(1=男 2=女 3=保密)不能为空.");
					}

					if (null == this.status) {
						throw new RuntimeException("状态(1=正常 2=禁用)不能为空.");
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
						message = "id(主键)不能为空.";
					}
					if (StringUtils.isBlank(this.phone)) {
						message = "手机不能为空.";
					}
					if(null != this.phone && this.phone.length() > 30){
						message = "手机不能超过30个字.";
					}
					if (StringUtils.isBlank(this.email)) {
						message = "邮箱不能为空.";
					}
					if(null != this.email && this.email.length() > 100){
						message = "邮箱不能超过100个字.";
					}
					if (StringUtils.isBlank(this.usercode)) {
						message = "用户编码不能为空.";
					}
					if(null != this.usercode && this.usercode.length() > 100){
						message = "用户编码不能超过100个字.";
					}
					if (StringUtils.isBlank(this.password)) {
						message = "密码不能为空.";
					}
					if(null != this.password && this.password.length() > 100){
						message = "密码不能超过100个字.";
					}
					if(null != this.username && this.username.length() > 80){
						message = "姓名不能超过80个字.";
					}
					if (null == this.gender) {
						message = "性别(1=男 2=女 3=保密)不能为空.";
					}
					if (null == this.status) {
						message = "状态(1=正常 2=禁用)不能为空.";
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

