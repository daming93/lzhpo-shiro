package com.lzhpo.deliver.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 配送零单配送
 * </p>
 *
 * @author xdm
 * @since 2020-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_express_bill")
public class ExpressBill extends DataEntity<ExpressBill> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单号
     */
    private String code;

    /**
     * 发件人姓名
     */
    @TableField("send_name")
    private String sendName;

    /**
     * 寄件人号码
     */
    @TableField("send_phone")
    private String sendPhone;

    /**
     * 寄件省
     */
    @TableField("send_province_id")
    private String sendProvinceId;

    /**
     * 寄件市
     */
    @TableField("send_city_id")
    private String sendCityId;

    /**
     * 寄件区
     */
    @TableField("send_area_id")
    private String sendAreaId;
    
    private Integer number;
    
    private BigDecimal volume;
    
    private BigDecimal weight;
    
    
    /**
     * 寄件人详细地址
     */
    @TableField("send_detail_area")
    private String sendDetailArea;

    /**
     * 收件人姓名
     */
    @TableField("receive_name")
    private String receiveName;

    /**
     * 收件人号码
     */
    @TableField("receive_phone")
    private String receivePhone;

    /**
     * 收件省
     */
    @TableField("receive_province_id")
    private String receiveProvinceId;

    /**
     * 收件市
     */
    @TableField("receive_city_id")
    private String receiveCityId;

    /**
     * 收件区
     */
    @TableField("receive_area_id")
    private String receiveAreaId;
    
    /**
     * 收件人详细地址
     */
    @TableField("receive_detail_area")
    private String receiveDetailArea;

    /**
     * 预留 模式
     */
    private Integer type;

    /**
     * 预留（额外费用单)
     */
    @TableField("ex_bill_id")
    private String exBillId;
    
    /**
     * 状态
     */
    private Integer status;

    /**
     * 收费
     */
    private BigDecimal moeny;
    
    @TableField(exist=false)
    private String sendDetail ;
    
    @TableField(exist=false)
    private String receiveDetail ;
    
    @TableField(exist=false)
    private String provinceName;
    
    @TableField(exist=false)
    private String cityName;
    
    @TableField(exist=false)
    private String countiesName;
    
    @TableField(exist=false)
    private String receiveProvinceName;
    
    @TableField(exist=false)
    private String receiveCityName;
    
    @TableField(exist=false)
    private String receiveCountiesName;
    
    @TableField(exist=false)
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

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}



	public String getReceiveProvinceName() {
		return receiveProvinceName;
	}

	public void setReceiveProvinceName(String receiveProvinceName) {
		this.receiveProvinceName = receiveProvinceName;
	}

	public String getReceiveCityName() {
		return receiveCityName;
	}

	public void setReceiveCityName(String receiveCityName) {
		this.receiveCityName = receiveCityName;
	}

	public String getReceiveCountiesName() {
		return receiveCountiesName;
	}

	public void setReceiveCountiesName(String receiveCountiesName) {
		this.receiveCountiesName = receiveCountiesName;
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

	public String getSendDetailArea() {
		return sendDetailArea;
	}

	public void setSendDetailArea(String sendDetailArea) {
		this.sendDetailArea = sendDetailArea;
	}

	public String getReceiveDetailArea() {
		return receiveDetailArea;
	}

	public void setReceiveDetailArea(String receiveDetailArea) {
		this.receiveDetailArea = receiveDetailArea;
	}

	public String getSendDetail() {
		return sendDetail;
	}

	public void setSendDetail(String sendDetail) {
		this.sendDetail = sendDetail;
	}

	public String getReceiveDetail() {
		return receiveDetail;
	}

	public void setReceiveDetail(String receiveDetail) {
		this.receiveDetail = receiveDetail;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public String getSendPhone() {
		return sendPhone;
	}

	public void setSendPhone(String sendPhone) {
		this.sendPhone = sendPhone;
	}

	public String getSendProvinceId() {
		return sendProvinceId;
	}

	public void setSendProvinceId(String sendProvinceId) {
		this.sendProvinceId = sendProvinceId;
	}

	public String getSendCityId() {
		return sendCityId;
	}

	public void setSendCityId(String sendCityId) {
		this.sendCityId = sendCityId;
	}

	public String getSendAreaId() {
		return sendAreaId;
	}

	public void setSendAreaId(String sendAreaId) {
		this.sendAreaId = sendAreaId;
	}

	public String getReceiveName() {
		return receiveName;
	}

	public void setReceiveName(String receiveName) {
		this.receiveName = receiveName;
	}

	public String getReceivePhone() {
		return receivePhone;
	}

	public void setReceivePhone(String receivePhone) {
		this.receivePhone = receivePhone;
	}

	public String getReceiveProvinceId() {
		return receiveProvinceId;
	}

	public void setReceiveProvinceId(String receiveProvinceId) {
		this.receiveProvinceId = receiveProvinceId;
	}

	public String getReceiveCityId() {
		return receiveCityId;
	}

	public void setReceiveCityId(String receiveCityId) {
		this.receiveCityId = receiveCityId;
	}

	public String getReceiveAreaId() {
		return receiveAreaId;
	}

	public void setReceiveAreaId(String receiveAreaId) {
		this.receiveAreaId = receiveAreaId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getExBillId() {
		return exBillId;
	}

	public void setExBillId(String exBillId) {
		this.exBillId = exBillId;
	}

	public BigDecimal getMoeny() {
		return moeny;
	}

	public void setMoeny(BigDecimal moeny) {
		this.moeny = moeny;
	}

    


}
