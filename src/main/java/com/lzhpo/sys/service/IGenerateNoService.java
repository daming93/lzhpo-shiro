package com.lzhpo.sys.service;

import com.lzhpo.sys.entity.GenerateNo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 * 唯一编号 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
public interface IGenerateNoService extends IService<GenerateNo> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getGenerateNoCount(String prefix,String time);
	
	//保存实例 返回该实例
	GenerateNo saveGenerateNo(GenerateNo generateNo);

	//根据实例Id获取实例
	GenerateNo getGenerateNoById(String id);

	//更新单条记录
	void updateGenerateNo(GenerateNo generateNo);
	
	//删除一条记录 通常为软删
	void deleteGenerateNo(GenerateNo generateNo);

	//选取所有记录
	List<GenerateNo> selectAll();

	//分页查询数据在父类
	GenerateNo selectGenerate(String prefix,String time);
	
	
	String nextCode(String prefix);
}	
