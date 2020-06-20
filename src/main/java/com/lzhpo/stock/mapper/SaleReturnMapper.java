package com.lzhpo.stock.mapper;

import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.SaleReturn;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 退货表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface SaleReturnMapper extends BaseMapper<SaleReturn> {
	MathStockNumber selectMathReturnNumberByReturnId(@Param("returnId") String returnId);
}
