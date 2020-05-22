package com.lzhpo.stock.mapper;

import com.lzhpo.stock.entity.MaterialOperations;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 仓储明细表-操作表可以相当于流水表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface MaterialOperationsMapper extends BaseMapper<MaterialOperations> {
	
	List<MaterialOperations> selectOperations(@Param("itemName") String itemName, @Param("startTime")String startTime, @Param("overTime")String overTime,@Param("start")Integer start,@Param("limit")Integer limit, @Param("flag")Integer flag);

	Long selectOperationsCount(@Param("itemName") String itemName, @Param("startTime")String startTime, @Param("overTime")String overTime,@Param("start")Integer start,@Param("limit")Integer limit, @Param("flag")Integer flag);
}
