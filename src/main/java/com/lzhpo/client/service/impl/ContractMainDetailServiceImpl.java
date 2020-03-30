package com.lzhpo.client.service.impl;

import com.lzhpo.client.entity.ContractMainDetail;
import com.lzhpo.client.mapper.ContractMainDetailMapper;
import com.lzhpo.client.service.IContractMainDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 合同收费项 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ContractMainDetailServiceImpl extends ServiceImpl<ContractMainDetailMapper, ContractMainDetail> implements IContractMainDetailService {
	@Override
    public long getContractMainDetailCount(String name) {
        QueryWrapper<ContractMainDetail> wrapper = new QueryWrapper<>();
	wrapper.eq("del_flag",false); 
	// 下行编辑条件
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMainDetails", allEntries = true)
    public ContractMainDetail saveContractMainDetail(ContractMainDetail contractMainDetail) {
        baseMapper.insert(contractMainDetail);
        /**
	*预留编辑代码 
	*/
        return contractMainDetail;
    }

    @Override
    public ContractMainDetail getContractMainDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMainDetails", allEntries = true)
    public void updateContractMainDetail(ContractMainDetail contractMainDetail) {
        baseMapper.updateById(contractMainDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMainDetails", allEntries = true)
    public void deleteContractMainDetail(ContractMainDetail contractMainDetail) {
        contractMainDetail.setDelFlag(true);
        baseMapper.updateById(contractMainDetail);
    }

    @Override
    @Cacheable("contractMainDetails")
    public List<ContractMainDetail> selectAll() {
        QueryWrapper<ContractMainDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }
}
