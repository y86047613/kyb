package com.zy.kyb.repository;

import com.zy.kyb.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
* @Description:
* @Param: 
* @return: 
* @Author: Mr.yuan zhang
* @Date: 2020/4/30
*/
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleIdIn(Set<Long> roleIdList);
}
