package com.lzhpo.charts.entity;

import java.time.LocalDate;

/**
 * <p>
 *  统计表主数据
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public class ClientCountWVQ   {
	private LocalDate date;
	private String clientName;
	private String returnVolume;
	private String returnWeight;
	private String returnNumber;
	private String returnTotal;
	private String takeoutVolume;
	private String takeoutWeight;
	private String takeoutNumber;
	private String takeoutTotal;
	private String storageVolume;
	private String storageWeight;
	private String storageNumber;
	private String storageTotal;
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getReturnVolume() {
		return returnVolume;
	}
	public void setReturnVolume(String returnVolume) {
		this.returnVolume = returnVolume;
	}
	public String getReturnWeight() {
		return returnWeight;
	}
	public void setReturnWeight(String returnWeight) {
		this.returnWeight = returnWeight;
	}
	public String getReturnNumber() {
		return returnNumber;
	}
	public void setReturnNumber(String returnNumber) {
		this.returnNumber = returnNumber;
	}
	public String getReturnTotal() {
		return returnTotal;
	}
	public void setReturnTotal(String returnTotal) {
		this.returnTotal = returnTotal;
	}
	public String getTakeoutVolume() {
		return takeoutVolume;
	}
	public void setTakeoutVolume(String takeoutVolume) {
		this.takeoutVolume = takeoutVolume;
	}
	public String getTakeoutWeight() {
		return takeoutWeight;
	}
	public void setTakeoutWeight(String takeoutWeight) {
		this.takeoutWeight = takeoutWeight;
	}
	public String getTakeoutNumber() {
		return takeoutNumber;
	}
	public void setTakeoutNumber(String takeoutNumber) {
		this.takeoutNumber = takeoutNumber;
	}
	public String getTakeoutTotal() {
		return takeoutTotal;
	}
	public void setTakeoutTotal(String takeoutTotal) {
		this.takeoutTotal = takeoutTotal;
	}
	public String getStorageVolume() {
		return storageVolume;
	}
	public void setStorageVolume(String storageVolume) {
		this.storageVolume = storageVolume;
	}
	public String getStorageWeight() {
		return storageWeight;
	}
	public void setStorageWeight(String storageWeight) {
		this.storageWeight = storageWeight;
	}
	public String getStorageNumber() {
		return storageNumber;
	}
	public void setStorageNumber(String storageNumber) {
		this.storageNumber = storageNumber;
	}
	public String getStorageTotal() {
		return storageTotal;
	}
	public void setStorageTotal(String storageTotal) {
		this.storageTotal = storageTotal;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	
	
	
	
}
