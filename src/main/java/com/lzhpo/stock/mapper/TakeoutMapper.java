package com.lzhpo.stock.mapper;

import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.Takeout;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 出库表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
public interface TakeoutMapper extends BaseMapper<Takeout> {
	MathStockNumber selectMathTakeoutNumberByTakeoutId(@Param("takeoutId") String TakeoutId);
}
