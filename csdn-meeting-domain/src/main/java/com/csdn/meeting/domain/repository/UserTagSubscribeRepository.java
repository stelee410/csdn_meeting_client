package com.csdn.meeting.domain.repository;

import com.csdn.meeting.domain.entity.UserTagSubscribe;

import java.util.List;
import java.util.Optional;

/**
 * 用户标签订阅仓储接口
 * @author 13786
 */
public interface UserTagSubscribeRepository {

    /**
     * 根据ID查询订阅
     */
    Optional<UserTagSubscribe> findById(Long id);

    /**
     * 根据用户ID和标签ID查询订阅
     */
    Optional<UserTagSubscribe> findByUserIdAndTagId(Long userId, Long tagId);

    /**
     * 查询用户的所有订阅
     */
    List<UserTagSubscribe> findByUserId(Long userId);

    /**
     * 查询用户的所有订阅（分页）
     */
    List<UserTagSubscribe> findByUserId(Long userId, int page, int size);

    /**
     * 查询标签的所有订阅用户ID
     */
    List<Long> findUserIdsByTagId(Long tagId);

    /**
     * 查询多个标签的所有订阅用户ID（去重）
     */
    List<Long> findUserIdsByTagIds(List<Long> tagIds);

    /**
     * 保存订阅
     */
    UserTagSubscribe save(UserTagSubscribe subscribe);

    /**
     * 取消订阅（软删除）
     */
    void unsubscribe(Long userId, Long tagId);

    /**
     * 检查用户是否订阅了指定标签
     */
    boolean exists(Long userId, Long tagId);

    /**
     * 批量检查用户订阅的标签ID（给定 tagIds 中用户已订阅的 ID 集合，避免循环内查库）
     */
    java.util.Set<Long> findSubscribedTagIdsByUserIdAndTagIds(Long userId, List<Long> tagIds);

    /**
     * 统计标签的订阅数
     */
    long countByTagId(Long tagId);

    /**
     * 统计用户的订阅标签数
     */
    long countByUserId(Long userId);
}
