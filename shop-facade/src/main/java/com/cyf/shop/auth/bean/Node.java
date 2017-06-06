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
 * 节点表
 */
public class Node extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//节点id(主键)
	private Long id;
	//节点类型(1=菜单 2=元素,ajax等)
	private Integer type;
	//节点名称
	private String name;
	//节点访问的url
	private String url;
	//节点图标
	private String icon;
	//是否独占(0=否 1=是,不允许分配给多个权限)
	private Integer exclusive;
	//节点class属性
	private String classAttr;
	//父节点id(0=顶级节点)
	private Long parentId;
	//所有祖先节点的id(逗号分割)
	private String ancestorsId;
	//节点层级
	private Integer level;
	//排序(越大越优先)
	private Integer sort;
	//状态(1=正常,2=禁用)
	private Integer status;
	//创建时间
	private java.util.Date createTime;
	//更新时间
	private java.util.Date updateTime;
	//columns END

	public Node(){}

	public Node(Long id){
		this.id = id;
	}

	/**
	*节点id(主键)
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*节点id(主键)
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*节点类型(1=菜单 2=元素,ajax等)
	*/
	public void setType(Integer value) {
		this.type = value;
	}
	
	/**
	*节点类型(1=菜单 2=元素,ajax等)
	*/
	public Integer getType() {
		return this.type;
	}
	/**
	*节点名称
	*/
	public void setName(String value) {
		this.name = value;
	}
	
	/**
	*节点名称
	*/
	public String getName() {
		return this.name;
	}
	/**
	*节点访问的url
	*/
	public void setUrl(String value) {
		this.url = value;
	}
	
	/**
	*节点访问的url
	*/
	public String getUrl() {
		return this.url;
	}
	/**
	*节点图标
	*/
	public void setIcon(String value) {
		this.icon = value;
	}
	
	/**
	*节点图标
	*/
	public String getIcon() {
		return this.icon;
	}
	/**
	*是否独占(0=否 1=是,不允许分配给多个权限)
	*/
	public void setExclusive(Integer value) {
		this.exclusive = value;
	}
	
	/**
	*是否独占(0=否 1=是,不允许分配给多个权限)
	*/
	public Integer getExclusive() {
		return this.exclusive;
	}
	/**
	*节点class属性
	*/
	public void setClassAttr(String value) {
		this.classAttr = value;
	}
	
	/**
	*节点class属性
	*/
	public String getClassAttr() {
		return this.classAttr;
	}
	/**
	*父节点id(0=顶级节点)
	*/
	public void setParentId(Long value) {
		this.parentId = value;
	}
	
	/**
	*父节点id(0=顶级节点)
	*/
	public Long getParentId() {
		return this.parentId;
	}
	/**
	*所有祖先节点的id(逗号分割)
	*/
	public void setAncestorsId(String value) {
		this.ancestorsId = value;
	}
	
	/**
	*所有祖先节点的id(逗号分割)
	*/
	public String getAncestorsId() {
		return this.ancestorsId;
	}
	/**
	*节点层级
	*/
	public void setLevel(Integer value) {
		this.level = value;
	}
	
	/**
	*节点层级
	*/
	public Integer getLevel() {
		return this.level;
	}
	/**
	*排序(越大越优先)
	*/
	public void setSort(Integer value) {
		this.sort = value;
	}
	
	/**
	*排序(越大越优先)
	*/
	public Integer getSort() {
		return this.sort;
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
			.append("Type",getType())
			.append("Name",getName())
			.append("Url",getUrl())
			.append("Icon",getIcon())
			.append("Exclusive",getExclusive())
			.append("ClassAttr",getClassAttr())
			.append("ParentId",getParentId())
			.append("AncestorsId",getAncestorsId())
			.append("Level",getLevel())
			.append("Sort",getSort())
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
		if(obj instanceof Node == false) return false;
		if(this == obj) return true;
		Node other = (Node)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("节点id(主键)不能为空.");
		}
		if (null == this.type) {
			throw new RuntimeException("节点类型(1=菜单 2=元素,ajax等)不能为空.");
		}
		if (StringUtils.isBlank(this.name)) {
			throw new RuntimeException("节点名称不能为空.");
		}
		if(null != this.name && this.name.length() > 80){
				throw new RuntimeException("节点名称不能超过80个字.");
		}
		if(null != this.url && this.url.length() > 150){
				throw new RuntimeException("节点访问的url不能超过150个字.");
		}
		if(null != this.icon && this.icon.length() > 60){
				throw new RuntimeException("节点图标不能超过60个字.");
		}
		if (null == this.exclusive) {
			throw new RuntimeException("是否独占(0=否 1=是,不允许分配给多个权限)不能为空.");
		}
		if(null != this.classAttr && this.classAttr.length() > 100){
				throw new RuntimeException("节点class属性不能超过100个字.");
		}
		if (null == this.parentId) {
			throw new RuntimeException("父节点id(0=顶级节点)不能为空.");
		}
		if (StringUtils.isBlank(this.ancestorsId)) {
			throw new RuntimeException("所有祖先节点的id(逗号分割)不能为空.");
		}
		if(null != this.ancestorsId && this.ancestorsId.length() > 80){
				throw new RuntimeException("所有祖先节点的id(逗号分割)不能超过80个字.");
		}
		if (null == this.level) {
			throw new RuntimeException("节点层级不能为空.");
		}
		if (null == this.sort) {
			throw new RuntimeException("排序(越大越优先)不能为空.");
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
			message = "节点id(主键)不能为空.";
		}
		if (null == this.type) {
			message = "节点类型(1=菜单 2=元素,ajax等)不能为空.";
		}
		if (StringUtils.isBlank(this.name)) {
			message = "节点名称不能为空.";
		}
		if(null != this.name && this.name.length() > 80){
			message = "节点名称不能超过80个字.";
		}
		if(null != this.url && this.url.length() > 150){
			message = "节点访问的url不能超过150个字.";
		}
		if(null != this.icon && this.icon.length() > 60){
			message = "节点图标不能超过60个字.";
		}
		if (null == this.exclusive) {
			message = "是否独占(0=否 1=是,不允许分配给多个权限)不能为空.";
		}
		if(null != this.classAttr && this.classAttr.length() > 100){
			message = "节点class属性不能超过100个字.";
		}
		if (null == this.parentId) {
			message = "父节点id(0=顶级节点)不能为空.";
		}
		if (StringUtils.isBlank(this.ancestorsId)) {
			message = "所有祖先节点的id(逗号分割)不能为空.";
		}
		if(null != this.ancestorsId && this.ancestorsId.length() > 80){
			message = "所有祖先节点的id(逗号分割)不能超过80个字.";
		}
		if (null == this.level) {
			message = "节点层级不能为空.";
		}
		if (null == this.sort) {
			message = "排序(越大越优先)不能为空.";
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

