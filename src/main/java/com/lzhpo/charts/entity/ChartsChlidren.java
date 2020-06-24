package com.lzhpo.charts.entity;

/**
 * <p>
 * 统计报表子表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public class ChartsChlidren  {
	private String name ;
	private Integer value;
	private Integer type;
	private String rate;
	
	private Integer status;
	
	private String statusStr;
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getStatusStr() {
		return statusStr;
	}
	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
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
