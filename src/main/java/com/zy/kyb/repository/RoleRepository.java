package com.zy.kyb.repository;


import com.zy.kyb.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
* @Description:
* @Param: 
* @return: 
* @Author: Mr.yuan zhang
* @Date: 2020/4/30
*/
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
