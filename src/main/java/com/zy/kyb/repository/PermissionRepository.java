package com.zy.kyb.repository;


import com.zy.kyb.entity.Permission;
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
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    List<Permission> findByPermissionIdIn(Set<Long> permissionIdList);
}
