package com.zy.kyb.repository;


import com.zy.kyb.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;


/**
* @Description:
* @Param: 
* @return: 
* @Author: Mr.yuan zhang
* @Date: 2020/4/30
*/
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByOpenid(String openid);
}
