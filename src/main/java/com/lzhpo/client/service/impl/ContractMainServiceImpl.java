package com.lzhpo.client.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.entity.ContractMain;
import com.lzhpo.client.entity.ContractMainDetail;
import com.lzhpo.client.mapper.ContractMainMapper;
import com.lzhpo.client.service.IContractMainDetailService;
import com.lzhpo.client.service.IContractMainService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
@Service("contractMainService")
@Transactional(rollbackFor = Exception.class)
public class ContractMainServiceImpl extends ServiceImpl<ContractMainMapper, ContractMain> implements IContractMainService {
	
	@Autowired
	private IContractMainDetailService detailService; 
	
	
	@Override
    public long getContractMainCount(String name) {
        QueryWrapper<ContractMain> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false); 
	// 下行编辑条件
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMains", allEntries = true)
    public ContractMain saveContractMain(ContractMain contractMain) {
        baseMapper.insert(contractMain);
        /**
	*预留编辑代码 
	*/
        //插入子表
        Set<ContractMainDetail> detailSet = contractMain.getDetailSet();
        if(detailSet!=null&&detailSet.size()>0){
        	for (ContractMainDetail detail : detailSet) {
        		detail.setContractId(contractMain.getId());
        		detailService.save(detail);
        	}  
        }
        
        return contractMain;
    }

    @Override
    public ContractMain getContractMainById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMains", allEntries = true)
    public void updateContractMain(ContractMain contractMain) {
        baseMapper.updateById(contractMain);
        
        
        /**
	*预留编辑代码
	*/
        //插入子表
        Set<ContractMainDetail> detailSet = contractMain.getDetailSet();
        if(detailSet!=null&&detailSet.size()>0){
        	//删除过去的所有子表
        	detailService.deleteContractMainDetailOb(contractMain.getId());
        	for (ContractMainDetail detail : detailSet) {  
        		detail.setContractId(contractMain.getId());
        		detailService.save(detail);
        	}  
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "contractMains", allEntries = true)
    public void deleteContractMain(ContractMain contractMain) {
        contractMain.setDelFlag(true);
        baseMapper.updateById(contractMain);
    }

    @Override
    @Cacheable("contractMains")
    public List<ContractMain> selectAll() {
        QueryWrapper<ContractMain> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public String getUsingContractId(String clientId) {
		return baseMapper.getUsingContractId(clientId);
	}

	@Override
	public void ChangeAduitStatus(Integer aduitStatus, String id) {
		ContractMain main = new ContractMain();
		main.setId(id);
		main.setIsAudit(aduitStatus);
		baseMapper.updateById(main);
	}
    
    
}
