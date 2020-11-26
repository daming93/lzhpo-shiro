package com.lzhpo.customer.service;

import com.lzhpo.customer.entity.AbnormityType;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 异常类型 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
public interface IAbnormityTypeService extends IService<AbnormityType> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getAbnormityTypeCount(String name);
	
	//保存实例 返回该实例
	AbnormityType saveAbnormityType(AbnormityType abnormityType);

	//根据实例Id获取实例
	AbnormityType getAbnormityTypeById(String id);

	//更新单条记录
	void updateAbnormityType(AbnormityType abnormityType);
	
	//删除一条记录 通常为软删
	void deleteAbnormityType(AbnormityType abnormityType);

	//选取所有记录
	List<AbnormityType> selectAll();

	//分页查询数据在父类


}
