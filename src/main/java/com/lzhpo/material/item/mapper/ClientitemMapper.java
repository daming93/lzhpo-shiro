package com.lzhpo.material.item.mapper;

import com.lzhpo.material.item.entity.Clientitem;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 城坤品项表 Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-04-16
 */
public interface ClientitemMapper extends BaseMapper<Clientitem> {
	public List<Clientitem> selectByClientId(@Param("clientId") String clientId);

}
