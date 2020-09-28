package com.lzhpo.finance.service.impl;

import com.lzhpo.finance.entity.UserTableDetail;
import com.lzhpo.finance.mapper.UserTableDetailMapper;
import com.lzhpo.finance.service.IUserTableDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 自定义收入表明细--用户使用（在不同表格中不同体现) 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Service
public class UserTableDetailServiceImpl extends ServiceImpl<UserTableDetailMapper, UserTableDetail> implements IUserTableDetailService {
	@Override
    public long getUserTableDetailCount(String name) {
        QueryWrapper<UserTableDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTableDetails", allEntries = true)
    public UserTableDetail saveUserTableDetail(UserTableDetail userTableDetail) {
        baseMapper.insert(userTableDetail);
        /**
	*预留编辑代码 
	*/
        return userTableDetail;
    }

    @Override
    public UserTableDetail getUserTableDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTableDetails", allEntries = true)
    public void updateUserTableDetail(UserTableDetail userTableDetail) {
        baseMapper.updateById(userTableDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTableDetails", allEntries = true)
    public void deleteUserTableDetail(UserTableDetail userTableDetail) {
        userTableDetail.setDelFlag(true);
        baseMapper.updateById(userTableDetail);
    }

    @Override
    @Cacheable("UserTableDetails")
    public List<UserTableDetail> selectAll() {
        QueryWrapper<UserTableDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
