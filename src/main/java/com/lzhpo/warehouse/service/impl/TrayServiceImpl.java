package com.lzhpo.warehouse.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.mapper.TrayMapper;
import com.lzhpo.warehouse.service.ITrayService;

/**
 * <p>
 * 储位表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-24
 */
@Service
public class TrayServiceImpl extends ServiceImpl<TrayMapper, Tray> implements ITrayService {
	@Override
	public long getTrayCount(Tray tray) {
		String code = judCode(tray);
		QueryWrapper<Tray> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("code", code);
		if (tray.getId() != null) {
			wrapper.ne("id", tray.getId());
		}
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Trays", allEntries = true)
	public Tray saveTray(Tray tray) {
		tray.setCode(judCode(tray));
		baseMapper.insert(tray);
		/**
		 * 预留编辑代码
		 */
		return tray;
	}

	@Override
	public Tray getTrayById(String id) {

		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Trays", allEntries = true)
	public void updateTray(Tray tray) {
		tray.setCode(judCode(tray));
		baseMapper.updateById(tray);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Trays", allEntries = true)
	public void deleteTray(Tray tray) {

		tray.setDelFlag(true);
		baseMapper.updateById(tray);
	}

	@Override
	@Cacheable("Trays")
	public List<Tray> selectAll() {
		QueryWrapper<Tray> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public String judCode(Tray tray) {
		StringBuffer buf = new StringBuffer();
		boolean flagA = tray.getSubsectionA() != null;
		boolean flagB = flagA && StringUtils.isNotBlank(tray.getSubsectionB());
		boolean flagC = flagB && StringUtils.isNotBlank(tray.getSubsectionC());
		boolean flagD = flagC && StringUtils.isNotBlank(tray.getSubsectionD());
		boolean flagE = flagD && StringUtils.isNotBlank(tray.getSubsectionE());
		boolean flagF = flagE && StringUtils.isNotBlank(tray.getSubsectionF());
		boolean flagG = flagF && StringUtils.isNotBlank(tray.getSubsectionG());
		
		if (flagA) {
			buf.append(tray.getSubsectionA());
		} else {
			return null;
		}
		if (flagB) {
			buf.append(tray.getSubsectionB());
		}
		if (flagC) {
			buf.append(tray.getSubsectionC());
		}
		if (flagD) {
			buf.append(tray.getSubsectionD());
		}
		if (flagE) {
			buf.append(tray.getSubsectionE());
		}
		if (flagF) {
			buf.append(tray.getSubsectionF());
		}
		if (flagG) {
			buf.append(tray.getSubsectionG());
		}
		return buf.toString();
	}

	@Override
	public List<Tray> selectByClientId(String clientId) {
		QueryWrapper<Tray> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
	//	wrapper.like("client_ids", clientId);
		return baseMapper.selectList(wrapper);
	}

}
