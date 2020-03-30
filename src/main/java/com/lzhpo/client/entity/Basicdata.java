package com.lzhpo.client.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 客户表
 * </p>
 *
 * @author xdm
 * @since 2020-03-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_basicdata")

public class Basicdata  extends DataEntity<Basicdata> implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 公司Id
     */
    @TableField("corporation_id")
    private Long corporationId;

    /**
     * 系统客户编号
     */
    @TableField("system_client_code")
    private String systemClientCode;

    /**
     * 合同编号
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户简称
     */
    @TableField("client_short_name")
    private String clientShortName;

    /**
     * 联系人
     */
    @TableField("relation_name")
    private String relationName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 手机号码
     */
    private String telephone;

    /**
     * 传真号码
     */
    private String faxnumber;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 省id
     */
    @TableField("province_id")
    private Long provinceId;

    /**
     * 市id
     */
    @TableField("city_id")
    private Long cityId;

    /**
     * 区id
     */
    @TableField("counties_id")
    private Long countiesId;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 客户级别(0:A级客户; 1:B级客户; 2:C级客户)
     */
    @TableField("client_level")
    private Integer clientLevel;

    /**
     * 拆零状态
     */
    @TableField("tear_open_status")
    private Integer tearOpenStatus;

    /**
     * 客户类型 0合同客户 1 意向客户
     */
    @TableField("client_type_select")
    private Integer clientTypeSelect;

    /**
     * 客户类型
     */
    @TableField("client_type")
    private String clientType;

    /**
     * 结算类型
     */
    @TableField("count_type")
    private Integer countType;

    /**
     * 客户状态
     */
    @TableField("client_status")
    private Integer clientStatus;

    /**
     * 合同状态
     */
    @TableField("use_type")
    private Integer useType;

    /**
     * 合同开始时间
     */
    @TableField("contract_start_time")
    private LocalDate contractStartTime;

    /**
     * 合同结束时间
     */
    @TableField("contract_over_time")
    private LocalDate contractOverTime;

    /**
     * 费用系数
     */
    @TableField("cost_coefficient")
    private Integer costCoefficient;

    /**
     * 默认配送区域
     */
    private Long defaultProvince;

    /**
     * 窝边系统对接需要得对应客户id
     */
    private String interfaceNum;

    /**
     * 联系人的职位
     */
    private String contactsPosition;

    /**
     * 引进日期
     */
    private LocalDate introduction;

	public Long getCorporationId() {
		return corporationId;
	}

	public void setCorporationId(Long corporationId) {
		this.corporationId = corporationId;
	}

	public String getSystemClientCode() {
		return systemClientCode;
	}

	public void setSystemClientCode(String systemClientCode) {
		this.systemClientCode = systemClientCode;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
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

	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getFaxnumber() {
		return faxnumber;
	}

	public void setFaxnumber(String faxnumber) {
		this.faxnumber = faxnumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(Long provinceId) {
		this.provinceId = provinceId;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getCountiesId() {
		return countiesId;
	}

	public void setCountiesId(Long countiesId) {
		this.countiesId = countiesId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getClientLevel() {
		return clientLevel;
	}

	public void setClientLevel(Integer clientLevel) {
		this.clientLevel = clientLevel;
	}

	public Integer getTearOpenStatus() {
		return tearOpenStatus;
	}

	public void setTearOpenStatus(Integer tearOpenStatus) {
		this.tearOpenStatus = tearOpenStatus;
	}

	public Integer getClientTypeSelect() {
		return clientTypeSelect;
	}

	public void setClientTypeSelect(Integer clientTypeSelect) {
		this.clientTypeSelect = clientTypeSelect;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public Integer getCountType() {
		return countType;
	}

	public void setCountType(Integer countType) {
		this.countType = countType;
	}

	public Integer getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(Integer clientStatus) {
		this.clientStatus = clientStatus;
	}

	public Integer getUseType() {
		return useType;
	}

	public void setUseType(Integer useType) {
		this.useType = useType;
	}

	public LocalDate getContractStartTime() {
		return contractStartTime;
	}

	public void setContractStartTime(LocalDate contractStartTime) {
		this.contractStartTime = contractStartTime;
	}

	public LocalDate getContractOverTime() {
		return contractOverTime;
	}

	public void setContractOverTime(LocalDate contractOverTime) {
		this.contractOverTime = contractOverTime;
	}

	public Integer getCostCoefficient() {
		return costCoefficient;
	}

	public void setCostCoefficient(Integer costCoefficient) {
		this.costCoefficient = costCoefficient;
	}

	public Long getDefaultProvince() {
		return defaultProvince;
	}

	public void setDefaultProvince(Long defaultProvince) {
		this.defaultProvince = defaultProvince;
	}

	public String getInterfaceNum() {
		return interfaceNum;
	}

	public void setInterfaceNum(String interfaceNum) {
		this.interfaceNum = interfaceNum;
	}

	public String getContactsPosition() {
		return contactsPosition;
	}

	public void setContactsPosition(String contactsPosition) {
		this.contactsPosition = contactsPosition;
	}

	public LocalDate getIntroduction() {
		return introduction;
	}

	public void setIntroduction(LocalDate introduction) {
		this.introduction = introduction;
	}

}
