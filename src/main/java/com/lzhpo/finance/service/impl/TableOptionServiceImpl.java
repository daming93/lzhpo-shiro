package com.lzhpo.finance.service.impl;

import com.lzhpo.finance.entity.TableOption;
import com.lzhpo.finance.mapper.TableOptionMapper;
import com.lzhpo.finance.service.ITableOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 自定义收入表选项 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Service
public class TableOptionServiceImpl extends ServiceImpl<TableOptionMapper, TableOption> implements ITableOptionService {
	@Override
    public long getTableOptionCount(String name) {
        QueryWrapper<TableOption> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableOptions", allEntries = true)
    public TableOption saveTableOption(TableOption tableOption) {
        baseMapper.insert(tableOption);
        /**
	*预留编辑代码 
	*/
        return tableOption;
    }

    @Override
    public TableOption getTableOptionById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableOptions", allEntries = true)
    public void updateTableOption(TableOption tableOption) {
        baseMapper.updateById(tableOption);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableOptions", allEntries = true)
    public void deleteTableOption(TableOption tableOption) {
        tableOption.setDelFlag(true);
        baseMapper.updateById(tableOption);
    }

    @Override
    @Cacheable("TableOptions")
    public List<TableOption> selectAll() {
        QueryWrapper<TableOption> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
