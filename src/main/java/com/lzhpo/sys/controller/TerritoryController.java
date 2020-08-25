package com.lzhpo.sys.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;

/**
 * <p>
 * 区域表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
@RestController
@RequestMapping("sys/territory")
public class TerritoryController {
	@Autowired
	private ITerritoryService territoryService;

	// 查询所有省份
	@RequestMapping("selectAllProvince")
	public List<Territory> selectAllprovince() {
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		return territoryService.list(query);
	}

	// 查询所有市
	@RequestMapping("selectCityByProvinceCode")
	public List<Territory> selectAllCity(String id) {

		Territory entity = territoryService.getById(id);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		if(entity==null){
			return null;
		}
		query.eq("level", 2);
		query.eq("parent_code", entity.getCode());
		return territoryService.list(query);
	}

	// 查询所有市
	@RequestMapping("selectAreaByCityCode")
	public List<Territory> selectAllArea(String id) {
		Territory entity = territoryService.getById(id);
		if(entity==null){
			return null;
		}
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 3);
		query.eq("parent_code", entity.getCode());
		return territoryService.list(query);
	}

	// 查询所有市
	@RequestMapping("selectById")
	public Territory selectById(String id) {
		Territory entity = territoryService.getById(id);
		return entity;
	}
}
