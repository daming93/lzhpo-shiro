package com.lzhpo.finance.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.finance.entity.UserTable;
import com.lzhpo.finance.entity.UserTableDetail;
import com.lzhpo.finance.mapper.UserTableMapper;
import com.lzhpo.finance.service.IUserTableDetailService;
import com.lzhpo.finance.service.IUserTableService;
import com.lzhpo.sys.service.IGenerateNoService;
/**
 * <p>
 * 自定义收入表--用户使用（在不同表格中不同体现) 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Service
public class UserTableServiceImpl extends ServiceImpl<UserTableMapper, UserTable> implements IUserTableService {
	@Autowired
	private IGenerateNoService generateNoService;
	
	@Autowired
	private IUserTableDetailService userTableDetailService ;
	
	@Override
    public long getUserTableCount(String name) {
        QueryWrapper<UserTable> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTables", allEntries = true)
    public UserTable saveUserTable(UserTable userTable) {
    	userTable.setCode(generateNoService.nextCode("YHFB"));
    	userTable.setModularName(CommomUtil.valueToNameInDict(userTable.getModular(), "modular"));
        baseMapper.insert(userTable);
        Set<UserTableDetail> detailSet = userTable.getDetailSet();
        for (UserTableDetail userTableDetail : detailSet) {
        	userTableDetail.setId(null);//清空前台传来id
        	userTableDetail.setTableId(userTable.getId());
        	userTableDetailService.save(userTableDetail);
		}
        return userTable;
    }

    @Override
    public UserTable getUserTableById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTables", allEntries = true)
    public void updateUserTable(UserTable userTable) {
        baseMapper.updateById(userTable);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "UserTables", allEntries = true)
    public void deleteUserTable(UserTable userTable) {
        userTable.setDelFlag(true);
        baseMapper.updateById(userTable);
    }

    @Override
    @Cacheable("UserTables")
    public List<UserTable> selectAll() {
        QueryWrapper<UserTable> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public UserTable getUserTableByuserTableId(String userTableId) {
		QueryWrapper<UserTable> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        wrapper.eq("table_id", userTableId);
        return baseMapper.selectList(wrapper)==null||baseMapper.selectList(wrapper).size()==0?new UserTable():baseMapper.selectList(wrapper).get(0);
	}


}
