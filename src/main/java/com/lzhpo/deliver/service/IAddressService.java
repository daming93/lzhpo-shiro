package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
/**
 * <p>
 * 送货地址表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
public interface IAddressService extends IService<Address> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getAddressCount(String name);
	
	//保存实例 返回该实例
	Address saveAddress(Address address);

	//根据实例Id获取实例
	Address getAddressById(String id);

	//更新单条记录
	void updateAddress(Address address);
	
	//删除一条记录 通常为软删
	void deleteAddress(Address address);

	//选取所有记录
	List<Address> selectAll();

	//分页查询数据在父类

	
	//上传
	String upload(MultipartFile file);

}
