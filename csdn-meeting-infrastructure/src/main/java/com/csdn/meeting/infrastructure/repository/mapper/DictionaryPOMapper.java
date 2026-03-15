package com.csdn.meeting.infrastructure.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csdn.meeting.infrastructure.po.DictionaryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DictionaryPOMapper extends BaseMapper<DictionaryPO> {

    @Select("SELECT * FROM t_dictionary WHERE dict_type = #{dictType} AND is_active = 1 ORDER BY sort_order ASC")
    List<DictionaryPO> selectActiveByType(@Param("dictType") String dictType);
}
