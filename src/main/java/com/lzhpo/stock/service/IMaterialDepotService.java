package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.MaterialDepot;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 物料和储位对应表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
public interface IMaterialDepotService extends IService<MaterialDepot> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialDepotCount(String name);
	
	//保存实例 返回该实例
	MaterialDepot saveMaterialDepot(MaterialDepot materialDepot);

	//根据实例Id获取实例
	MaterialDepot getMaterialDepotById(String id);

	//更新单条记录
	void updateMaterialDepot(MaterialDepot materialDepot);
	
	//删除一条记录 通常为软删
	void deleteMaterialDepot(MaterialDepot materialDepot);

	//选取所有记录
	List<MaterialDepot> selectAll();

	//分页查询数据在父类

	//根据materialId 和 DepotId 增减数量 
	/**
	 * 
	 * @param materialId
	 * @param DepotId
	 * @param number
	 * @param math true ＋ 
	 */
	void mathNumberBymaterialIdAndDepotId(String materialId,String depotId,Integer number,boolean math);

	List<MaterialDepot> getListByMaterialAndNumber(String materialId,Integer number,String depot) throws Exception;
	
	MaterialDepot getMaterialDepotByMaterialIdAndDepotCode(String materialId,String depotCode);
}
