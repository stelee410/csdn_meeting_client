package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.VerificationCodePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus BaseMapper for t_verification_code
 */
@Mapper
public interface VerificationCodeBaseMapper extends BaseMapper<VerificationCodePO> {

    @Select("SELECT * FROM t_verification_code WHERE target = #{target} AND type = #{type} AND scene = #{scene} " +
            "ORDER BY create_time DESC LIMIT 1")
    VerificationCodePO selectLatestByTarget(@Param("target") String target,
                                            @Param("type") Integer type,
                                            @Param("scene") Integer scene);

    @Select("SELECT * FROM t_verification_code WHERE target = #{target} AND type = #{type} AND scene = #{scene} " +
            "AND used = 0 AND expire_time > NOW() ORDER BY create_time DESC LIMIT 1")
    VerificationCodePO selectLatestValidCode(@Param("target") String target,
                                             @Param("type") Integer type,
                                             @Param("scene") Integer scene);

    @Select("SELECT COUNT(*) FROM t_verification_code WHERE target = #{target} AND type = #{type} AND scene = #{scene} " +
            "AND DATE(create_time) = CURDATE()")
    int countTodayByTarget(@Param("target") String target,
                           @Param("type") Integer type,
                           @Param("scene") Integer scene);

    @Select("DELETE FROM t_verification_code WHERE expire_time < #{before}")
    void deleteExpiredCodes(@Param("before") LocalDateTime before);
}
