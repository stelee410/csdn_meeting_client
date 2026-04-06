package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis-Plus BaseMapper for t_user
 */
@Mapper
public interface UserBaseMapper extends BaseMapper<UserPO> {

    @Select("SELECT * FROM t_user WHERE user_id = #{userId}")
    UserPO selectByUserId(@Param("userId") String userId);

    @Select("SELECT * FROM t_user WHERE mobile = #{mobile}")
    UserPO selectByMobile(@Param("mobile") String mobile);

    @Select("SELECT * FROM t_user WHERE csdn_bind_id = #{csdnBindId}")
    UserPO selectByCsdnBindId(@Param("csdnBindId") String csdnBindId);

    @Select("SELECT * FROM t_user WHERE email = #{email}")
    UserPO selectByEmail(@Param("email") String email);

    /**
     * 查询非注销状态的用户（status != 2表示未注销）
     * 用于注册时检查手机号是否可用
     */
    @Select("SELECT * FROM t_user WHERE mobile = #{mobile} AND status != 2")
    UserPO selectActiveByMobile(@Param("mobile") String mobile);
}
