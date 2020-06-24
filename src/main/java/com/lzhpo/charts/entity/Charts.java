package com.lzhpo.charts.entity;

import java.util.List;

/**
 * <p>
 *  统计表主数据
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public class Charts   {
	private String name ;
	private Integer value;
	private Integer type;
	
	private List<ChartsChlidren> charts;
	
	private List<String> nameList;
	
	
	
	public List<String> getNameList() {
		return nameList;
	}
	public void setNameList(List<String> nameList) {
		this.nameList = nameList;
	}
	public List<ChartsChlidren> getCharts() {
		return charts;
	}
	public void setCharts(List<ChartsChlidren> charts) {
		this.charts = charts;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	
	
}
