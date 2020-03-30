package com.lzhpo.client.service;

import com.lzhpo.client.entity.ContractOption;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-25
 */
public interface IContractOptionService extends IService<ContractOption> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getContractOptionCount(String name);
	
	//保存实例 返回该实例
	ContractOption saveContractOption(ContractOption contractOption);

	//根据实例Id获取实例
	ContractOption getContractOptionById(String id);

	//更新单条记录
	void updateContractOption(ContractOption contractOption);
	
	//删除一条记录 通常为软删
	void deleteContractOption(ContractOption contractOption);

	//选取所有记录
	List<ContractOption> selectAll();

	//分页查询数据在父类
	
}	
