package com.lzhpo.material.item.service;

import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.material.item.entity.Clientitem;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
/**
 * <p>
 * 城坤品项表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-16
 */
public interface IClientitemService extends IService<Clientitem> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getClientitemCount(String code,String clientId);
	
	//保存实例 返回该实例
	Clientitem saveClientitem(Clientitem clientitem);

	//根据实例Id获取实例
	Clientitem getClientitemById(String id);

	//更新单条记录
	void updateClientitem(Clientitem clientitem);
	
	//删除一条记录 通常为软删
	void deleteClientitem(Clientitem clientitem);

	//选取所有记录
	List<Clientitem> selectAll();

	//分页查询数据在父类

	List<Clientitem> selectByClientId(String clientId);
	//上传xls文件 
	String upload(MultipartFile file,List<Basicdata> basicDatas);
	
	List<Clientitem> selectByItemCode(String itemCode);
	
	public List<Clientitem> selectByClientIdAll(String clientId);
}
