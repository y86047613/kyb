package com.zy.kyb.service;

import com.zy.kyb.entity.*;
import com.zy.kyb.enums.ConstantEnum;
import com.zy.kyb.enums.ExceptionEnum;

import com.zy.kyb.excepiton.BasicException;
import com.zy.kyb.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private AccountRepository accountRepository;
    private RoleRepository roleRepository;
    private AccountRoleRepository accountRoleRepository;
    private PermissionRepository permissionRepository;
    private RolePermissionRepository rolePermissionRepository;

    
    public Account findAccount(String openid){
        if(StringUtils.isBlank(openid)){
            throw new BasicException(ExceptionEnum.PARAM_EXCEPTION.customMessage("openid is null"));
        }
        return accountRepository.findByOpenid(openid);
    }

    /**
     * 注册
     * @param account
     * @return
     */
    @Transactional
    public Account register(Account account){
        account = accountRepository.save(account);
        if (account.getAccountId() == null){
            log.error("register failed, account is [{}]", account);
            throw new BasicException(ExceptionEnum.SERVER_EXCEPTION.customMessage("register failed, account save error"));
        }
        grantDefaultRole(account);
        return account;
    }

    /**
     * 授予默认角色
     * @param account
     */
    @Transactional(rollbackOn = {Exception.class})
    public void grantDefaultRole(Account account){
        Role exampleObj = Role.builder()
                .name(ConstantEnum.DEFAULT_ROLE.getValue())
                .build();
        Role role = roleRepository.findOne(Example.of(exampleObj)).orElse(null);
        if (role == null) {
            log.error("no default role, default role is [{}]", ConstantEnum.DEFAULT_ROLE.getValue());
            throw new BasicException(ExceptionEnum.SERVER_EXCEPTION.customMessage("no default role"));
        }
        AccountRole accountRole = AccountRole.builder()
                .accountId(account.getAccountId())
                .roleId(role.getRoleId())
                .deleted(0)
                .build();
        accountRoleRepository.save(accountRole);
    }

    /**
     * 获取用户权限
     */
    @Transactional
    public List<Permission> acquirePermission(Long accountId){
        AccountRole exampleObj = AccountRole.builder()
                .accountId(accountId)
                .build();
        List<AccountRole> accountRoleList = accountRoleRepository.findAll(Example.of(exampleObj));
        if (CollectionUtils.isEmpty(accountRoleList)) {
            log.error("There is no role assigned to the account, accountId is [{}]", accountId);
            throw new BasicException(ExceptionEnum.SERVER_EXCEPTION.customMessage("There is no role assigned to the account"));
        }
        Set<Long> roleIdList = accountRoleList.stream().map(AccountRole::getRoleId).collect(Collectors.toSet());
        List<RolePermission> rolePermissionList = rolePermissionRepository.findByRoleIdIn(roleIdList);
        if (CollectionUtils.isEmpty(rolePermissionList)) {
            log.error("There is no permission assigned to the role, roleIdList is [{}]", roleIdList);
            throw new BasicException(ExceptionEnum.SERVER_EXCEPTION.customMessage("There is no permission assigned to the role"));
        }
        Set<Long> permissionIdList = rolePermissionList.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        List<Permission> permissionList = permissionRepository.findByPermissionIdIn(permissionIdList);
        if (CollectionUtils.isEmpty(permissionList)) {
            log.error("permission find failed, permissionIdList is [{}]", permissionIdList);
            throw new BasicException(ExceptionEnum.SERVER_EXCEPTION.customMessage("permission find failed"));
        }
        return permissionList;
    }
}
