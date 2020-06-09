package com.lzhpo.warehouse.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.warehouse.entity.MaterialManageDetail;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
public interface IMaterialManageDetailService extends IService<MaterialManageDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialManageDetailCount(String name);
	
	//保存实例 返回该实例
	MaterialManageDetail saveMaterialManageDetail(MaterialManageDetail materialManageDetail);

	//根据实例Id获取实例
	MaterialManageDetail getMaterialManageDetailById(String id);

	//更新单条记录
	void updateMaterialManageDetail(MaterialManageDetail materialManageDetail);
	
	//删除一条记录 通常为软删
	void deleteMaterialManageDetail(MaterialManageDetail materialManageDetail,String code,Integer type);

	//选取所有记录
	List<MaterialManageDetail> selectAll();

	//分页查询数据在父类

	//修改单独列得出库明细
	void updateNumber(MaterialManageDetail detail,String code,Integer type) throws Exception;

}
