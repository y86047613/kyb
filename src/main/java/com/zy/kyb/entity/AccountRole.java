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
@Table(name = "account_role")
@SQLDelete(sql = "Update account_role set deleted = 1 where account_role_id = ?")
@Where(clause = "deleted = 0")
public class AccountRole implements Serializable {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "account_role_id")
	private Long accountRoleId;

	@Column(name = "account_id")
	private Long accountId;

	@Column(name = "role_id")
	private Long roleId;

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
