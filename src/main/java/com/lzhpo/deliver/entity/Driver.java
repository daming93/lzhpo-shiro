package com.lzhpo.deliver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 驾驶员信息表
 * </p>
 *
 * @author xdm
 * @since 2020-08-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_driver")
public class Driver extends DataEntity<Driver> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 驾驶员姓名
     */
    @TableField("driver_name")
    private String driverName;

    /**
     * 微信登录账号
     */
    @TableField("wx_login_account")
    private String wxLoginAccount;

    /**
     * 微信登录密码
     */
    @TableField("wx_login_pwd")
    private String wxLoginPwd;

    /**
     * 联系电话
     */
    private Long phone;

    /**
     * 紧急联系电话
     */
    @TableField("urgency_phone")
    private Long urgencyPhone;

    /**
     * 驾照号码
     */
    @TableField("licencse_number")
    private String licencseNumber;

    /**
     * 驾照类型(0:c照; 1:b照; 2:a照)
     */
    @TableField("licencse_type")
    private Integer licencseType;

    /**
     * 驾照有效期
     */
    @TableField("licencse_time")
    private LocalDate licencseTime;

    /**
     * 身份证
     */
    @TableField("identity_number")
    private String identityNumber;

    /**
     * 是否有从业资格证(0:有; 1:无)
     */
    @TableField("is_exist_cert_num")
    private Integer isExistCertNum;

    /**
     * 司机分配状态(0:未分配; 1:已分配)
     */
    @TableField("driver_line_status")
    private Integer driverLineStatus;

    /**
     * 司机状态(0:正常; 1:条件不符)
     */
    @TableField("driver_status")
    private Integer driverStatus;
    
    @TableField(exist=false)
    private String licencseTypeStr;

    @TableField(exist=false)
    private String isExistCertNumStr;
    
	public String getDriverName() {
		return driverName;
	}

	public String getLicencseTypeStr() {
		return licencseTypeStr;
	}

	public void setLicencseTypeStr(String licencseTypeStr) {
		this.licencseTypeStr = licencseTypeStr;
	}

	public String getIsExistCertNumStr() {
		return isExistCertNumStr;
	}

	public void setIsExistCertNumStr(String isExistCertNumStr) {
		this.isExistCertNumStr = isExistCertNumStr;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getWxLoginAccount() {
		return wxLoginAccount;
	}

	public void setWxLoginAccount(String wxLoginAccount) {
		this.wxLoginAccount = wxLoginAccount;
	}

	public String getWxLoginPwd() {
		return wxLoginPwd;
	}

	public void setWxLoginPwd(String wxLoginPwd) {
		this.wxLoginPwd = wxLoginPwd;
	}

	public Long getPhone() {
		return phone;
	}

	public void setPhone(Long phone) {
		this.phone = phone;
	}

	public Long getUrgencyPhone() {
		return urgencyPhone;
	}

	public void setUrgencyPhone(Long urgencyPhone) {
		this.urgencyPhone = urgencyPhone;
	}

	public String getLicencseNumber() {
		return licencseNumber;
	}

	public void setLicencseNumber(String licencseNumber) {
		this.licencseNumber = licencseNumber;
	}

	public Integer getLicencseType() {
		return licencseType;
	}

	public void setLicencseType(Integer licencseType) {
		this.licencseType = licencseType;
	}

	public LocalDate getLicencseTime() {
		return licencseTime;
	}

	public void setLicencseTime(LocalDate licencseTime) {
		this.licencseTime = licencseTime;
	}

	public String getIdentityNumber() {
		return identityNumber;
	}

	public void setIdentityNumber(String identityNumber) {
		this.identityNumber = identityNumber;
	}

	public Integer getIsExistCertNum() {
		return isExistCertNum;
	}

	public void setIsExistCertNum(Integer isExistCertNum) {
		this.isExistCertNum = isExistCertNum;
	}

	public Integer getDriverLineStatus() {
		return driverLineStatus;
	}

	public void setDriverLineStatus(Integer driverLineStatus) {
		this.driverLineStatus = driverLineStatus;
	}

	public Integer getDriverStatus() {
		return driverStatus;
	}

	public void setDriverStatus(Integer driverStatus) {
		this.driverStatus = driverStatus;
	}
}
