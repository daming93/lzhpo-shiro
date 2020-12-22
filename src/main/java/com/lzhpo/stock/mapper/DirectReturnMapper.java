package com.lzhpo.stock.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.stock.entity.DirectReturn;
import com.lzhpo.stock.entity.MathStockNumber;

/**
 * <p>
 * 退货表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface DirectReturnMapper extends BaseMapper<DirectReturn> {
	MathStockNumber selectMathReturnNumberByReturnId(@Param("returnId") String returnId);
}
