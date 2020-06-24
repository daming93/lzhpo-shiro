package com.lzhpo.material.item.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.mapper.ClientitemMapper;
import com.lzhpo.material.item.service.IClientitemService;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

/**
 * <p>
 * 城坤品项表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-16
 */
@Service
public class ClientitemServiceImpl extends ServiceImpl<ClientitemMapper, Clientitem> implements IClientitemService {
	@Override
	public long getClientitemCount(String code, String clientId) {
		QueryWrapper<Clientitem> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("code", code);
		wrapper.eq("client_id", clientId);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Clientitems", allEntries = true)
	public Clientitem saveClientitem(Clientitem clientitem) {
		// 算下体积
		clientitem.setItemVolume(NumberUtil.div(
				NumberUtil.mul(clientitem.getItemLength(), clientitem.getItemWidth(), clientitem.getItemHeight()),
				1000000));
		baseMapper.insert(clientitem);
		/**
		 * 预留编辑代码
		 */
		return clientitem;
	}

	@Override
	public Clientitem getClientitemById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Clientitems", allEntries = true)
	public void updateClientitem(Clientitem clientitem) {
		clientitem.setItemVolume(NumberUtil.div(
				NumberUtil.mul(clientitem.getItemLength(), clientitem.getItemWidth(), clientitem.getItemHeight()),
				1000000));
		baseMapper.updateById(clientitem);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Clientitems", allEntries = true)
	public void deleteClientitem(Clientitem clientitem) {
		clientitem.setDelFlag(true);
		baseMapper.updateById(clientitem);
	}

	@Override
	@Cacheable("Clientitems")
	public List<Clientitem> selectAll() {
		QueryWrapper<Clientitem> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public String upload(MultipartFile file, List<Basicdata> basicDatas) {
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer buffer = new StringBuffer();
		for (Basicdata basicdata : basicDatas) {
			map.put(basicdata.getClientShortName(), basicdata.getId());
		}
		try {
			ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
			reader
			.addHeaderAlias("客户", "clientId")
			.addHeaderAlias("编号", "code")
			.addHeaderAlias("名称", "name")
			.addHeaderAlias("分类", "category")
			.addHeaderAlias("品牌", "brand")
			.addHeaderAlias("长度(cm)", "itemLength")
			.addHeaderAlias("宽度(cm)", "itemWidth")
			.addHeaderAlias("高度(cm)", "itemHeight")
			.addHeaderAlias("体积", "itemVolume")
			.addHeaderAlias("单位（整）", "unitWhole")
			.addHeaderAlias("单位（零）", "unitScattered")
			.addHeaderAlias("件/托", "tray")
			.addHeaderAlias("换算率", "unitRate")
			.addHeaderAlias("保质天数", "day");
			List<Clientitem> items = reader.readAll(Clientitem.class);
			int i = 1;
			for (Clientitem clientitem : items) {
				if(map.containsKey(clientitem.getClientId())){
					clientitem.setClientId(map.get(clientitem.getClientId()));
					if (getClientitemCount(clientitem.getCode(), clientitem.getClientId()) > 0) {
						buffer.append("第"+i+"条同一客户下重复品项<br>");
					}else{
						saveClientitem(clientitem);
					}
				}else{//else就是没有找到改客户简称
					buffer.append("第"+i+"条无匹配客户<br>");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			buffer.append("文件格式有误(使用导出模板,另存为xls[excel2003-2007]格式)");
			return buffer.toString();
		}
		return buffer.toString();
	}

	@Override
	public List<Clientitem> selectByClientId(String clientId) {
		List<Clientitem> list = baseMapper.selectByClientId(clientId);
		return list;
	}

	@Override
	public List<Clientitem> selectByItemCode(String itemCode) {
		QueryWrapper<Clientitem> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.like("code", itemCode);
		List<Clientitem> list = baseMapper.selectList(wrapper);
		return list;
	}

}
