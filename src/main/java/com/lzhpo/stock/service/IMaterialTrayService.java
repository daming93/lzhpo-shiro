package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.MaterialTray;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 物料和托盘对应表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
public interface IMaterialTrayService extends IService<MaterialTray> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialTrayCount(String name);
	
	//保存实例 返回该实例
	MaterialTray saveMaterialTray(MaterialTray materialTray);

	//根据实例Id获取实例
	MaterialTray getMaterialTrayById(String id);

	//更新单条记录
	void updateMaterialTray(MaterialTray materialTray);
	
	//删除一条记录 通常为软删
	void deleteMaterialTray(MaterialTray materialTray);

	//选取所有记录
	List<MaterialTray> selectAll();

	//分页查询数据在父类

	/**
	 * 
	 * @param materialId
	 * @param TrayId
	 * @param number
	 * @param math true ＋ 
	 */
	void mathNumberBymaterialIdAndTrayId(String materialId,String trayId,Integer number,boolean math);
}
