package com.lzhpo.stock.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.stock.entity.LineTakeout;

/**
 * <p>
 * 线路发单（无关库存的发单) 表单形式和库存发单接近，但是没有子表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2021-01-25
 */
public interface LineTakeoutMapper extends BaseMapper<LineTakeout> {
	List<LineTakeout> selectAllByDispatchIds(@Param("takeoutIds") Set<String> takeoutIds);
}
