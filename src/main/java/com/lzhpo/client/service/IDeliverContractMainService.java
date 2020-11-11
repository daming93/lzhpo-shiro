package com.lzhpo.client.service;

import com.lzhpo.client.entity.DeliverContractMain;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
public interface IDeliverContractMainService extends IService<DeliverContractMain> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDeliverContractMainCount(String name);
	
	//保存实例 返回该实例
	DeliverContractMain saveDeliverContractMain(DeliverContractMain deliverContractMain);

	//根据实例Id获取实例
	DeliverContractMain getDeliverContractMainById(String id);

	//更新单条记录
	void updateDeliverContractMain(DeliverContractMain deliverContractMain);
	
	//删除一条记录 通常为软删
	void deleteDeliverContractMain(DeliverContractMain deliverContractMain);

	//选取所有记录
	List<DeliverContractMain> selectAll();

	//分页查询数据在父类
	//复制合同 改变合同的名字 变成副本 合同编号 主表子表同步复制
	void copyContract(String contractId);


	//得到客户正在使用的合同
	String getUsingContractId(String clientId);
	
	//审核 改变审核状态
	void ChangeAduitStatus(Integer aduitStatus,String id);
}
