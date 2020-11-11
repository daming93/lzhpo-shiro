package com.lzhpo.client.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.client.entity.DeliverContractMainDetail;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
public interface IDeliverContractMainDetailService extends IService<DeliverContractMainDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDeliverContractMainDetailCount(String name);
	
	//保存实例 返回该实例
	DeliverContractMainDetail saveDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail);

	//根据实例Id获取实例
	DeliverContractMainDetail getDeliverContractMainDetailById(String id);

	//更新单条记录
	void updateDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail);
	
	//删除一条记录 通常为软删
	void deleteDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail);

	//选取所有记录
	List<DeliverContractMainDetail> selectAll();

	//分页查询数据在父类


	public void deleteAllDetailByMainId(String mainId);
	
	//给主表id找子表
	List<DeliverContractMainDetail> getListByMainId(String mainId);
	
	
	public DeliverContractMainDetail selectDetailMoneyByInfo(String mainId, String proviceId, String cityId, String areaId,Object range,Integer type);
	
	/**
	 * 根据合同去看该车能不能送该区域
	 * @param mainId 合同主体id
	 * @param proviceId
	 * @param cityId
	 * @param areaId
	 * @return
	 */
	public DeliverContractMainDetail selectDetailMoneyByInfoNoRange(String mainId, String proviceId, String cityId,
			String areaId);
}
