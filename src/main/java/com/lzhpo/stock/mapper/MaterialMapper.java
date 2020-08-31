package com.lzhpo.stock.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.stock.entity.Material;

/**
 * <p>
 * 仓储明细表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface MaterialMapper extends BaseMapper<Material> {
	List<Material> selectMaterial(@Param("itemName") String itemName, @Param("startTime")String startTime, @Param("overTime")String overTime,
			@Param("start")Integer start,@Param("limit")Integer limit, @Param("type")Integer type,@Param("continuity")String continuity,@Param("clientId")String clientId,
			@Param("batch")String batch,@Param("status")String status);

	Long selectMaterialCount(@Param("itemName") String itemName, @Param("startTime")String startTime, @Param("overTime")String overTime,
			@Param("start")Integer start,@Param("limit")Integer limit, @Param("type")Integer type,@Param("continuity")String continuity,@Param("clientId")String clientId,
			@Param("batch")String batch,@Param("status")String status);
	
	
	List<Material> selectMaterialByDepot(@Param("start")Integer start,@Param("limit")Integer limit, @Param("depotCode")String depotCode,  @Param("itemId")String itemId, @Param("batch")String batch, @Param("depotMaterilId")String depotMaterilId);
	
	Long selectMaterialByDepotCount(@Param("depotCode")String depotCode, @Param("itemId")String itemId, @Param("batch")String batch);
	
	List<Material> selectListByClientAndTiem(@Param("startTime")LocalDate startTime, @Param("endTime")LocalDate endTime, @Param("clientId")String clientId);
}
