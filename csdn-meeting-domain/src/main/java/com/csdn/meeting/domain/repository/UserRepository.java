package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.User;
import com.csdn.meeting.domain.valueobject.UserStatus;
import com.csdn.meeting.domain.valueobject.UserType;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 根据ID查询
     */
    Optional<User> findById(Long id);

    /**
     * 根据业务用户ID查询
     */
    Optional<User> findByUserId(String userId);

    /**
     * 根据手机号查询
     */
    Optional<User> findByMobile(String mobile);

    /**
     * 根据手机号查询非注销状态的用户
     * 用于注册时检查手机号是否已被占用（排除已注销用户）
     *
     * @param mobile 手机号
     * @return 非注销状态的用户
     */
    Optional<User> findActiveByMobile(String mobile);

    /**
     * 根据CSDN绑定ID查询
     */
    Optional<User> findByCsdnBindId(String csdnBindId);

    /**
     * 根据邮箱查询
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据用户类型分页查询
     */
    PageResult<User> findByUserType(UserType userType, int page, int size);

    /**
     * 根据状态查询
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 分页查询所有用户
     */
    PageResult<User> findAll(int page, int size);

    /**
     * 根据用户类型和状态分页查询
     */
    PageResult<User> findByUserTypeAndStatus(UserType userType, UserStatus status, int page, int size);

    /**
     * 删除用户
     */
    void delete(User user);

    /**
     * 根据ID删除
     */
    void deleteById(Long id);
}
