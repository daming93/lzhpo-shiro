package com.lzhpo.stock.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.Material;
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
	
	//物料中 品项id和批次联合字段作为识别字段
	
	Material getMaterialByItemId(String itemid,LocalDate batch );
	
	
	@Transactional(rollbackFor=Exception.class)
	void lockMaterial (String materialId,Integer number)  throws Exception;
	
	
	void unlockMaterial (String materialId,Integer number);
	
	
}
