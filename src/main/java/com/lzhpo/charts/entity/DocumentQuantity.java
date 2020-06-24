package com.lzhpo.charts.entity;

/**
 * <p>
 *  统计表主数据
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public class DocumentQuantity   {
	private String name ;
	
	private String clientName;
	
	private String clientShortName;
	
	private Integer storageCount;
	
	private Integer takeoutCount;
	
	private Integer returnCout;
	
	private Integer zblCount;
	
	private Integer zlCount;
	
	private Integer allCount;
	
	private String id;
	
	private String time;
	
	private String count;
	

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getZlCount() {
		return zlCount;
	}

	public void setZlCount(Integer zlCount) {
		this.zlCount = zlCount;
	}

	public Integer getAllCount() {
		return allCount;
	}

	public void setAllCount(Integer allCount) {
		this.allCount = allCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientShortName() {
		return clientShortName;
	}

	public void setClientShortName(String clientShortName) {
		this.clientShortName = clientShortName;
	}

	public Integer getStorageCount() {
		return storageCount;
	}

	public void setStorageCount(Integer storageCount) {
		this.storageCount = storageCount;
	}

	public Integer getTakeoutCount() {
		return takeoutCount;
	}

	public void setTakeoutCount(Integer takeoutCount) {
		this.takeoutCount = takeoutCount;
	}

	public Integer getReturnCout() {
		return returnCout;
	}

	public void setReturnCout(Integer returnCout) {
		this.returnCout = returnCout;
	}

	public Integer getZblCount() {
		return zblCount;
	}

	public void setZblCount(Integer zblCount) {
		this.zblCount = zblCount;
	}
	
}
