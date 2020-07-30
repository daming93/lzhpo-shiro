package com.lzhpo.stock.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.MaterialDepot;
/**
 * <p>
 * 仓储明细表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface IMaterialService extends IService<Material> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialCount(String name);
	
	//保存实例 返回该实例
	Material saveMaterial(Material material);

	//根据实例Id获取实例
	Material getMaterialById(String id);

	//更新单条记录
	void updateMaterial(Material material);
	
	//删除一条记录 通常为软删
	void deleteMaterial(Material material);

	//选取所有记录
	List<Material> selectAll();

	//分页查询数据在父类
	
	//物料中 品项id和批次 加入品质 (良品和不良品)联合字段作为识别字段
	
	Material getMaterialByItemId(String itemid,LocalDate batch ,Integer MaterialStatus);
	
	
	List<MaterialDepot> lockMaterial (String materialId,Integer number,Integer wholeNum,Integer scatteredNum,String depotCode)  throws Exception;
	
	
	void unlockMaterial (String materialId,Integer wholeNumber,Integer scatteredNum,Integer rate);
	
	/**
	 * type 1 正常 2忽略批次 3忽略物料状态 continuity on 全部 其他 为非0
	 * @param itemName
	 * @param startTime
	 * @param endTime
	 * @param flag
	 * @return
	 */
	Map<String,Object> selectMaterial(String itemName,String startTime,String endTime,Integer start,Integer limit,Integer type,String continuity,String clientId);
	
	/**
	 * 查询储位上面有多少物资分布
	 * @param start
	 * @param limit
	 * @param depotCode
	 * @return
	 */
	Map<String,Object>  selectMaterialByDepot(Integer start,Integer limit,String depotCode);
	
	Map<String,Object>  selectMaterialByDepot(Integer start,Integer limit,String depotCode,String itemId,String batch,String materialDepotId);
	
	/**
	 * 根据开始时间结束时间 和 客户id去找在物料流水里面 规定时间里面异动盘点使用
	 * @param startTime
	 * @param endTine
	 * @param clientId
	 * @return
	 */
	List<Material> selectListByClientAndTiem(LocalDate startTime,LocalDate endTine,String clientId);
}
