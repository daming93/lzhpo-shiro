package com.lzhpo.client.service;

import com.lzhpo.client.entity.ContractMainDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 * 合同收费项 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-27
 */
public interface IContractMainDetailService extends IService<ContractMainDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getContractMainDetailCount(String name);
	
	//保存实例 返回该实例
	ContractMainDetail saveContractMainDetail(ContractMainDetail contractMainDetail);

	//根据实例Id获取实例
	ContractMainDetail getContractMainDetailById(String id);

	//更新单条记录
	void updateContractMainDetail(ContractMainDetail contractMainDetail);
	
	//删除一条记录 通常为软删
	void deleteContractMainDetail(ContractMainDetail contractMainDetail);

	//选取所有记录
	List<ContractMainDetail> selectAll();

	//硬删 根据主表id
	void deleteContractMainDetailOb(String mainId);
}	
