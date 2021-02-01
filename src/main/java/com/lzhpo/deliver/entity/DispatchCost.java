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
 * 配送计划支出表 司机运费
 * </p>
 *
 * @author xdm
 * @since 2021-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_dispatch_cost")
public class DispatchCost extends DataEntity<DispatchCost> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配送计划id
     */
    @TableField("dispatch_id")
    private String dispatchId;
    /**
     * 点费单价
     */
    @TableField("point_price")
    private BigDecimal pointPrice;

    /**
     * 最小计算点数
     */
    @TableField("min_point")
    private Integer minPoint;

    /**
     * 难点费用
     */
    @TableField("difficult_point_price")
    private BigDecimal difficultPointPrice;

    /**
     * 难点数
     */
    @TableField("difficult_point_num")
    private Integer difficultPointNum;

    /**
     * 点数
     */
    @TableField("point_num")
    private Integer pointNum;

    /**
     * 费用
     */
    private BigDecimal moeny;

    
    
	public String getDispatchId() {
		return dispatchId;
	}

	public void setDispatchId(String dispatchId) {
		this.dispatchId = dispatchId;
	}

	public BigDecimal getPointPrice() {
		return pointPrice;
	}

	public void setPointPrice(BigDecimal pointPrice) {
		this.pointPrice = pointPrice;
	}

	public Integer getMinPoint() {
		return minPoint;
	}

	public void setMinPoint(Integer minPoint) {
		this.minPoint = minPoint;
	}

	public BigDecimal getDifficultPointPrice() {
		return difficultPointPrice;
	}

	public void setDifficultPointPrice(BigDecimal difficultPointPrice) {
		this.difficultPointPrice = difficultPointPrice;
	}

	public Integer getDifficultPointNum() {
		return difficultPointNum;
	}

	public void setDifficultPointNum(Integer difficultPointNum) {
		this.difficultPointNum = difficultPointNum;
	}

	public Integer getPointNum() {
		return pointNum;
	}

	public void setPointNum(Integer pointNum) {
		this.pointNum = pointNum;
	}

	public BigDecimal getMoeny() {
		return moeny;
	}

	public void setMoeny(BigDecimal moeny) {
		this.moeny = moeny;
	}

}
