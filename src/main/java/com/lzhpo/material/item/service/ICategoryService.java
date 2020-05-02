package com.lzhpo.material.item.service;

import com.lzhpo.material.item.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 物料分类表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
public interface ICategoryService extends IService<Category> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getCategoryCount(String name);
	
	//保存实例 返回该实例
	Category saveCategory(Category category);

	//根据实例Id获取实例
	Category getCategoryById(String id);

	//更新单条记录
	void updateCategory(Category category);
	
	//删除一条记录 通常为软删
	void deleteCategory(Category category);

	//选取所有记录
	List<Category> selectAll();

	//分页查询数据在父类


}
