package com.lzhpo.warehouse.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.warehouse.entity.Depot;
/**
 * <p>
 * 储位表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
public interface IDepotService extends IService<Depot> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDepotCount(Depot depot);
	
	//保存实例 返回该实例
	Depot saveDepot(Depot depot);

	//根据实例Id获取实例
	Depot getDepotById(String id);

	//更新单条记录
	void updateDepot(Depot depot);
	
	//删除一条记录 通常为软删
	void deleteDepot(Depot depot);

	//选取所有记录
	List<Depot> selectAll();

	//分页查询数据在父类


	//验证code是否符合标准
	String judCode(Depot depot);
	
	List<Depot> selectByClientId(String clientId);
	
	//上传xls文件 
	String upload(MultipartFile file,List<Basicdata> basicDatas);
}
