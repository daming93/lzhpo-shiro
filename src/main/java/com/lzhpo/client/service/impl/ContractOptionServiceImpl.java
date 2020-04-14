package com.lzhpo.client.service.impl;

import com.lzhpo.client.entity.ContractOption;
import com.lzhpo.client.mapper.ContractOptionMapper;
import com.lzhpo.client.service.IContractOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-25
 */
@Service("contractOptionService")
@Transactional(rollbackFor = Exception.class)
public class ContractOptionServiceImpl extends ServiceImpl<ContractOptionMapper, ContractOption> implements IContractOptionService {
	@Override
    public long getContractOptionCount(String name) {
        QueryWrapper<ContractOption> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractOptions", allEntries = true)
    public ContractOption saveContractOption(ContractOption contractOption) {
        baseMapper.insert(contractOption);
        /**
	*预留编辑代码 
	*/
        return contractOption;
    }

    @Override
    public ContractOption getContractOptionById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractOptions", allEntries = true)
    public void updateContractOption(ContractOption contractOption) {
        baseMapper.updateById(contractOption);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractOptions", allEntries = true)
    public void deleteContractOption(ContractOption contractOption) {
        contractOption.setDelFlag(true);
        baseMapper.updateById(contractOption);
    }

    @Override
    @Cacheable("contractOptions")
    public List<ContractOption> selectAll() {
        QueryWrapper<ContractOption> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }
}
