package com.lzhpo.finance.service.impl;

import com.lzhpo.finance.entity.TableDetail;
import com.lzhpo.finance.mapper.TableDetailMapper;
import com.lzhpo.finance.service.ITableDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 自定义收入表明细 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Service
public class TableDetailServiceImpl extends ServiceImpl<TableDetailMapper, TableDetail> implements ITableDetailService {
	@Override
    public long getTableDetailCount(String name) {
        QueryWrapper<TableDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableDetails", allEntries = true)
    public TableDetail saveTableDetail(TableDetail tableDetail) {
        baseMapper.insert(tableDetail);
        /**
	*预留编辑代码 
	*/
        return tableDetail;
    }

    @Override
    public TableDetail getTableDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableDetails", allEntries = true)
    public void updateTableDetail(TableDetail tableDetail) {
        baseMapper.updateById(tableDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TableDetails", allEntries = true)
    public void deleteTableDetail(TableDetail tableDetail) {
        tableDetail.setDelFlag(true);
        baseMapper.updateById(tableDetail);
    }

    @Override
    @Cacheable("TableDetails")
    public List<TableDetail> selectAll() {
        QueryWrapper<TableDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void deleteDetailByTableId(String tableId) {
		QueryWrapper<TableDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("table_id", tableId);
		baseMapper.delete(wrapper);
	}

}
