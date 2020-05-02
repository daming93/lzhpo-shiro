package com.lzhpo.material.item.service;

import com.lzhpo.material.item.entity.Brand;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 物料品牌表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
public interface IBrandService extends IService<Brand> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getBrandCount(String name);
	
	//保存实例 返回该实例
	Brand saveBrand(Brand brand);

	//根据实例Id获取实例
	Brand getBrandById(String id);

	//更新单条记录
	void updateBrand(Brand brand);
	
	//删除一条记录 通常为软删
	void deleteBrand(Brand brand);

	//选取所有记录
	List<Brand> selectAll();

	//分页查询数据在父类


}
