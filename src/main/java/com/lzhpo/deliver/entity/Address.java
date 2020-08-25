package com.lzhpo.deliver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 送货地址表
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_address")
public class Address extends DataEntity<Address> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 送达方名称
     */
    @TableField("address_name")
    private String addressName;

    /**
     * 联系人
     */
    @TableField("contact_name")
    private String contactName;

    /**
     * 归属系统
     */
    @TableField("from_system")
    private String fromSystem;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 域别(0:市内; 1:外埠)
     */
    @TableField("area_type")
    private Integer areaType;

    /**
     * 送达方类型(0:超市; 1:经销商)
     */
    @TableField("address_type")
    private Integer addressType;

    /**
     * 省id
     */
    @TableField("province_id")
    private String provinceId;

    /**
     * 市id
     */
    @TableField("city_id")
    private String cityId;

    /**
     * 区id
     */
    @TableField("counties_id")
    private String countiesId;

    /**
     * 地域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 详细地址
     */
    private String descritpion;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 门店等级(0:A; 1:B; 2:C)
     */
    @TableField("area_level")
    private Integer areaLevel;

    @TableField(exist=false)
    private String provinceName;
    
    @TableField(exist=false)
    private String cityName;
    
    @TableField(exist=false)
    private String countiesName;
    
    @TableField(exist=false)
    private  String addressTypeStr;
    /**
     * 公里数
     */
    private Integer distance;

    @TableField(exist=false)
    private String areaLevelStr;

    
    
	public String getAddressTypeStr() {
		return addressTypeStr;
	}

	public void setAddressTypeStr(String addressTypeStr) {
		this.addressTypeStr = addressTypeStr;
	}

	public String getAreaLevelStr() {
		return areaLevelStr;
	}

	public void setAreaLevelStr(String areaLevelStr) {
		this.areaLevelStr = areaLevelStr;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCountiesName() {
		return countiesName;
	}

	public void setCountiesName(String countiesName) {
		this.countiesName = countiesName;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getFromSystem() {
		return fromSystem;
	}

	public void setFromSystem(String fromSystem) {
		this.fromSystem = fromSystem;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getAreaType() {
		return areaType;
	}

	public void setAreaType(Integer areaType) {
		this.areaType = areaType;
	}

	public Integer getAddressType() {
		return addressType;
	}

	public void setAddressType(Integer addressType) {
		this.addressType = addressType;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountiesId() {
		return countiesId;
	}

	public void setCountiesId(String countiesId) {
		this.countiesId = countiesId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getDescritpion() {
		return descritpion;
	}

	public void setDescritpion(String descritpion) {
		this.descritpion = descritpion;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Integer getAreaLevel() {
		return areaLevel;
	}

	public void setAreaLevel(Integer areaLevel) {
		this.areaLevel = areaLevel;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

  


}
