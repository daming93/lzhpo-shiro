package com.lzhpo.stock.entity;

import java.io.Serializable;
import java.math.BigDecimal;
/**
 * <p>
 * 入库明细表
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public class MathStockNumber  implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal  weightSum;
    
    private BigDecimal  volumeSum;
    
    private Integer numZ;
    
    private Integer scatteredNum;
    
    private Integer number;
    
    private Integer tray;
    
    private String storageId;
    
    private String name;

    
	public Integer getScatteredNum() {
		return scatteredNum;
	}

	public void setScatteredNum(Integer scatteredNum) {
		this.scatteredNum = scatteredNum;
	}

	public BigDecimal getWeightSum() {
		return weightSum;
	}

	public void setWeightSum(BigDecimal weightSum) {
		this.weightSum = weightSum;
	}

	public BigDecimal getVolumeSum() {
		return volumeSum;
	}

	public void setVolumeSum(BigDecimal volumeSum) {
		this.volumeSum = volumeSum;
	}

	public Integer getNumZ() {
		return numZ;
	}

	public void setNumZ(Integer numZ) {
		this.numZ = numZ;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getTray() {
		return tray;
	}

	public void setTray(Integer tray) {
		this.tray = tray;
	}

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    
    

}
