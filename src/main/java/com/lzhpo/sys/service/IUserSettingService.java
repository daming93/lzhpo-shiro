package com.lzhpo.sys.service;

import com.lzhpo.sys.entity.UserSetting;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 用户的自定义设置(偏好设置表，目前用于偏好设计的财务附表) 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
public interface IUserSettingService extends IService<UserSetting> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getUserSettingCount(String name);
	
	//保存实例 返回该实例
	UserSetting saveUserSetting(UserSetting userSetting);

	//根据实例Id获取实例
	UserSetting getUserSettingById(String id);

	//更新单条记录
	void updateUserSetting(UserSetting userSetting);
	
	//删除一条记录 通常为软删
	void deleteUserSetting(UserSetting userSetting);

	//选取所有记录
	List<UserSetting> selectAll();

	//分页查询数据在父类


	//给userid查询看可有偏好设定
	UserSetting getUserSettingByUserId(String userId,Integer modular,Integer type);	
}
