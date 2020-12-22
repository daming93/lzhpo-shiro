package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.DirectReturnDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Set;
/**
 * <p>
 * 退货详情表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface IDirectReturnDetailService extends IService<DirectReturnDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDirectReturnDetailCount(String name);
	
	//保存实例 返回该实例
	DirectReturnDetail saveDirectReturnDetail(DirectReturnDetail DirectReturnDetail);

	//根据实例Id获取实例
	DirectReturnDetail getDirectReturnDetailById(String id);

	//更新单条记录
	void updateDirectReturnDetail(DirectReturnDetail DirectReturnDetail);
	
	//删除一条记录 通常为软删
	void deleteDirectReturnDetail(DirectReturnDetail DirectReturnDetail);

	//选取所有记录
	List<DirectReturnDetail> selectAll();

	//分页查询数据在父类

	//根据主表id删除子表 硬删
	void deleteDirectReturnDetailByReturnId(String returnId);
	
	List<DirectReturnDetail> selectDirectReturnDetailByDirectReturnId(String returnId);
	
	Set<String> selectDirectReturnIdsByMaterialId(String materialId);
	
}
