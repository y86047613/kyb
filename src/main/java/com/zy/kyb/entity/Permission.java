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
@Table(name = "permission")
@SQLDelete(sql = "Update permission set deleted = 1 where permission_id = ?")
@Where(clause = "deleted = 0")
public class Permission implements Serializable {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name = "permission_id")
	private Long permissionId;

	/**
	 * 资源权限，如：user:list,user:create
	 */
	private String permission;

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
