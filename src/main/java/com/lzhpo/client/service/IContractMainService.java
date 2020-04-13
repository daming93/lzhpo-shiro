package com.lzhpo.client.service;

import com.lzhpo.client.entity.ContractMain;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
public interface IContractMainService extends IService<ContractMain> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getContractMainCount(String name);
	
	//保存实例 返回该实例
	ContractMain saveContractMain(ContractMain contractMain);

	//根据实例Id获取实例
	ContractMain getContractMainById(String id);

	//更新单条记录
	void updateContractMain(ContractMain contractMain);
	
	//删除一条记录 通常为软删
	void deleteContractMain(ContractMain contractMain);

	//选取所有记录
	List<ContractMain> selectAll();

	//分页查询数据在父类
	String getUsingContractId(String clientId);
	
	//审核 改变审核状态
	void ChangeAduitStatus(Integer aduitStatus,String id);
}	
