package com.lzhpo.sys.service.impl;

import com.lzhpo.sys.entity.UserSetting;
import com.lzhpo.sys.mapper.UserSettingMapper;
import com.lzhpo.sys.service.IUserSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 用户的自定义设置(偏好设置表，目前用于偏好设计的财务附表) 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingMapper, UserSetting> implements IUserSettingService {
	@Override
    public long getUserSettingCount(String name) {
        QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserSettings", allEntries = true)
    public UserSetting saveUserSetting(UserSetting userSetting) {
    	//也是先找有没有改设定，有更新没有就添加
    	UserSetting entity = getUserSettingByUserId(userSetting.getUserId(), userSetting.getModular(), userSetting.getType());
    	if(entity.getId()==null){
    		baseMapper.insert(userSetting);
    	}else{
    		userSetting.setId(entity.getId());
    		baseMapper.updateById(userSetting);
    	}
        return userSetting;
    }

    @Override
    public UserSetting getUserSettingById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserSettings", allEntries = true)
    public void updateUserSetting(UserSetting userSetting) {
        baseMapper.updateById(userSetting);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserSettings", allEntries = true)
    public void deleteUserSetting(UserSetting userSetting) {
        userSetting.setDelFlag(true);
        baseMapper.updateById(userSetting);
    }

    @Override
    @Cacheable("UserSettings")
    public List<UserSetting> selectAll() {
        QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public UserSetting getUserSettingByUserId(String userId,Integer modular,Integer type) {
		QueryWrapper<UserSetting> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("user_id", userId);
        wrapper.eq("modular",modular);
        wrapper.eq("type", type);
        
        return baseMapper.selectList(wrapper)==null||baseMapper.selectList(wrapper).size()==0?new UserSetting():baseMapper.selectList(wrapper).get(0);
	}


}
