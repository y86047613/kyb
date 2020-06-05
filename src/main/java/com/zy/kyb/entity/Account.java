package com.zy.kyb.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@Entity
@Table(name = "account")
@SQLDelete(sql = "Update account set deleted = 1 where account_id = ?")
@Where(clause = "deleted = 0")
public class Account implements Serializable {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long accountId;

	/**
	 * 微信的openid
	 */
	private String openid;

	/**
	 * 微信的session_key
	 */
	@Column(name = "session_key")
	private String sessionKey;

	/**
	 * 微信昵称
	 */
	@Column(name = "nick_name")
	private String nickName;

	/**
	 * 性别 0：未知、1：男、2：女
	 */
	private Integer gender;

	/**
	 * 头像url
	 */
	@Column(name = "avatar_url")
	private String avatarUrl;

	/**
	 * 国家
	 */
	private String country;

	/**
	 * 省份
	 */
	private String province;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 删除状态，0：未删除，1：已删除
	 */
	private Integer deleted;

	@Column(name = "create_time")
	private Date createTime;

	@Column(name = "last_update_time")
	private Date lastUpdateTime;

	@PrePersist
	public void prePersist() {
		if (this.createTime == null) createTime = new Date();
		if (this.lastUpdateTime == null) lastUpdateTime = new Date();
	}

	@PreUpdate
	public void preUpdate() { this.lastUpdateTime = new Date(); }

	@PreRemove
	public void preRemove() { this.lastUpdateTime = new Date(); }

}
