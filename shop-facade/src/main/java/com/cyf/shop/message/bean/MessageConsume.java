/*
 * Powered By [cyf.com]
 */

package com.cyf.shop.message.bean;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.StringUtils;
import com.cyf.base.common.bean.BaseEntity;

/**
 * 消息消费
 */
public class MessageConsume extends BaseEntity<Long>{

	private static final long serialVersionUID = 1L;
	
	//可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	//columns START
	//主键
	private Long id;
	//消息名称
	private String name;
	//消息类型(1=queue 2=topic)
	private Integer type;
	//消息内容
	private String payload;
	//消费者名称
	private String consumer;
	//消息描述
	private String messageDesc;
	//创建时间
	private java.util.Date createTime;
	//是否定时任务消息(0=否 1=是)
	private Integer isTimer;
	//columns END

	public MessageConsume(){}

	public MessageConsume(Long id){
		this.id = id;
	}

	/**
	*主键
	*/
	public void setId(Long value) {
		this.id = value;
	}
	
	/**
	*主键
	*/
	public Long getId() {
		return this.id;
	}
	/**
	*消息名称
	*/
	public void setName(String value) {
		this.name = value;
	}
	
	/**
	*消息名称
	*/
	public String getName() {
		return this.name;
	}
	/**
	*消息类型(1=queue 2=topic)
	*/
	public void setType(Integer value) {
		this.type = value;
	}
	
	/**
	*消息类型(1=queue 2=topic)
	*/
	public Integer getType() {
		return this.type;
	}
	/**
	*消息内容
	*/
	public void setPayload(String value) {
		this.payload = value;
	}
	
	/**
	*消息内容
	*/
	public String getPayload() {
		return this.payload;
	}
	/**
	*消费者名称
	*/
	public void setConsumer(String value) {
		this.consumer = value;
	}
	
	/**
	*消费者名称
	*/
	public String getConsumer() {
		return this.consumer;
	}
	/**
	*消息描述
	*/
	public void setMessageDesc(String value) {
		this.messageDesc = value;
	}
	
	/**
	*消息描述
	*/
	public String getMessageDesc() {
		return this.messageDesc;
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
	*是否定时任务消息(0=否 1=是)
	*/
	public void setIsTimer(Integer value) {
		this.isTimer = value;
	}
	
	/**
	*是否定时任务消息(0=否 1=是)
	*/
	public Integer getIsTimer() {
		return this.isTimer;
	}

	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
			.append("Id",getId())
			.append("Name",getName())
			.append("Type",getType())
			.append("Payload",getPayload())
			.append("Consumer",getConsumer())
			.append("MessageDesc",getMessageDesc())
			.append("CreateTime",getCreateTime())
			.append("IsTimer",getIsTimer())
			.toString();
	}
	
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getId())
			.toHashCode();
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof MessageConsume == false) return false;
		if(this == obj) return true;
		MessageConsume other = (MessageConsume)obj;
		return new EqualsBuilder()
			.append(getId(),other.getId())
			.isEquals();
	}

	/**
	* 验证数据是否合法,不合法时会直接抛出错误！
	*/
	public void validate(){
		if (null == this.id) {
			throw new RuntimeException("主键不能为空.");
		}
		if(null != this.name && this.name.length() > 100){
				throw new RuntimeException("消息名称不能超过100个字.");
		}
		if (null == this.type) {
			throw new RuntimeException("消息类型(1=queue 2=topic)不能为空.");
		}
		if(null != this.consumer && this.consumer.length() > 80){
				throw new RuntimeException("消费者名称不能超过80个字.");
		}
		if(null != this.messageDesc && this.messageDesc.length() > 300){
				throw new RuntimeException("消息描述不能超过300个字.");
		}
	}

	/**
	* 验证数据是否合法,合法时返回空字符串，不合法时返回错误信息
	*/
	public String getValidateMessage(boolean isIgnorePk){
		String message = "";
		if (isIgnorePk == false && null == this.id) {
			message = "主键不能为空.";
		}
		if(null != this.name && this.name.length() > 100){
			message = "消息名称不能超过100个字.";
		}
		if (null == this.type) {
			message = "消息类型(1=queue 2=topic)不能为空.";
		}
		if(null != this.consumer && this.consumer.length() > 80){
			message = "消费者名称不能超过80个字.";
		}
		if(null != this.messageDesc && this.messageDesc.length() > 300){
			message = "消息描述不能超过300个字.";
		}
		return message;
	}
}

