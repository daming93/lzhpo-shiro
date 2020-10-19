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
import com.lzhpo.finance.entity.Table;
import com.lzhpo.finance.entity.TableDetail;
import com.lzhpo.finance.mapper.TableMapper;
import com.lzhpo.finance.service.ITableDetailService;
import com.lzhpo.finance.service.ITableService;
import com.lzhpo.sys.service.IGenerateNoService;

/**
 * <p>
 * 自定义收入表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements ITableService {
	@Autowired
	private IGenerateNoService generateNoService;

	@Autowired
	private ITableDetailService tableDetailService;
	@Override
	public long getTableCount(String name) {
		QueryWrapper<Table> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("name", name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Tables", allEntries = true)
	public Table saveTable(Table table) {
		table.setCode(generateNoService.nextCode("CWBD"));
		table.setModularName(CommomUtil.valueToNameInDict(table.getModular(), "modular"));
		table.setIsAudit(0);//未审核
		baseMapper.insert(table);
		Set<TableDetail> detailSet = table.getDetailSets();
		for (TableDetail tableDetail : detailSet) {
			tableDetail.setTableId(table.getId());
			tableDetailService.save(tableDetail);
		}
		return table;
	}

	@Override
	public Table getTableById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Tables", allEntries = true)
	public void updateTable(Table table) {
		table.setModularName(CommomUtil.valueToNameInDict(table.getModular(), "modular"));
		baseMapper.updateById(table);
		//删除过去得选项
		tableDetailService.deleteDetailByTableId(table.getId());
		Set<TableDetail> detailSet = table.getDetailSets();
		for (TableDetail tableDetail : detailSet) {
			tableDetail.setTableId(table.getId());
			tableDetailService.save(tableDetail);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Tables", allEntries = true)
	public void deleteTable(Table table) {
		table.setDelFlag(true);
		baseMapper.updateById(table);
	}

	@Override
	@Cacheable("Tables")
	public List<Table> selectAll() {
		QueryWrapper<Table> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		//要审核过得
		wrapper.eq("is_audit", 1);
		return baseMapper.selectList(wrapper);
	}


	@Override
	public void ChangeAduitStatus(Integer status, String id) {
		Table table = new Table();
		table.setId(id);
		table.setIsAudit(status);
		baseMapper.updateById(table);
	}

	@Override
	public List<Table> selectListByModular(Integer modular) {
		QueryWrapper<Table> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		//要审核过得
		wrapper.eq("is_audit", 1);
		wrapper.eq("modular", modular);
		return baseMapper.selectList(wrapper);
	};
}
