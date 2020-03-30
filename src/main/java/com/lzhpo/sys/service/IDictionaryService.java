package com.lzhpo.sys.service;

import com.lzhpo.sys.entity.Dictionary;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 * 字典表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
public interface IDictionaryService extends IService<Dictionary> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDictionaryCount(String name);
	
	//保存实例 返回该实例
	Dictionary saveDictionary(Dictionary dictionary);

	//根据实例Id获取实例
	Dictionary getDictionaryById(String id);

	//更新单条记录
	void updateDictionary(Dictionary dictionary);
	
	//删除一条记录 通常为软删
	void deleteDictionary(Dictionary dictionary);

	//选取所有记录
	List<Dictionary> selectAll();

	//分页查询数据在父类
	
}	
