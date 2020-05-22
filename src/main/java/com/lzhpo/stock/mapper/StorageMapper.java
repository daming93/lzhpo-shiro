package com.lzhpo.stock.mapper;

import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.Storage;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 入库主表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface StorageMapper extends BaseMapper<Storage> {
	MathStockNumber selectMathStorageNumberByStorageId(@Param("storageId") String storageId);
}
