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
import com.lzhpo.client.entity.DeliverContractMain;
import com.lzhpo.client.entity.DeliverContractMainDetail;
import com.lzhpo.client.mapper.DeliverContractMainMapper;
import com.lzhpo.client.service.IDeliverContractMainDetailService;
import com.lzhpo.client.service.IDeliverContractMainService;
import com.lzhpo.sys.service.IGenerateNoService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
@Service
public class DeliverContractMainServiceImpl extends ServiceImpl<DeliverContractMainMapper, DeliverContractMain> implements IDeliverContractMainService {
	@Autowired
	private IDeliverContractMainDetailService deliverContractMainDetailService ;
	@Autowired
	private IGenerateNoService generateNoService;
	
	@Override
    public long getDeliverContractMainCount(String name) {
        QueryWrapper<DeliverContractMain> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMains", allEntries = true)
    public DeliverContractMain saveDeliverContractMain(DeliverContractMain deliverContractMain) {
    	deliverContractMain.setContractCode(generateNoService.nextCode("KHPS"));
        baseMapper.insert(deliverContractMain);
        
        Set<DeliverContractMainDetail> details = deliverContractMain.getDetailSet();
        for (DeliverContractMainDetail detail : details) {
        	detail.setContractId(deliverContractMain.getId());
        	deliverContractMainDetailService.save(detail);
		}
        return deliverContractMain;
    }

    @Override
    public DeliverContractMain getDeliverContractMainById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMains", allEntries = true)
    public void updateDeliverContractMain(DeliverContractMain deliverContractMain) {
        baseMapper.updateById(deliverContractMain);
        Set<DeliverContractMainDetail> details = deliverContractMain.getDetailSet();
        deliverContractMainDetailService.deleteAllDetailByMainId(deliverContractMain.getId());
        for (DeliverContractMainDetail detail : details) {
        	detail.setContractId(deliverContractMain.getId());
        	deliverContractMainDetailService.save(detail);
		}
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMains", allEntries = true)
    public void deleteDeliverContractMain(DeliverContractMain deliverContractMain) {
        deliverContractMain.setDelFlag(true);
        baseMapper.updateById(deliverContractMain);
    }

    @Override
    @Cacheable("DeliverContractMains")
    public List<DeliverContractMain> selectAll() {
        QueryWrapper<DeliverContractMain> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void copyContract(String contractId) {
		DeliverContractMain main =  baseMapper.selectById(contractId);
		List<DeliverContractMainDetail> detailList =  deliverContractMainDetailService.getListByMainId(contractId);
		//先把名字搞出来
		String name = main.getName()+"副本";
		String newName = name;
		int i = 1;
		while (getDeliverContractMainCount(newName)>0){
			i++;
			newName = name+i;
		};
		//名字搞出来,然后改code
		main.setId(null);
		main.setName(name);//拷贝的时候状态是有问题的,要考虑
		main.setIsAudit(0);//设置成未审核的状态
		main.setContractCode(generateNoService.nextCode("KHPS"));
		baseMapper.insert(main);
		//处理子表
		for (DeliverContractMainDetail detail : detailList) {
			detail.setId(null);//清空原先的id
			detail.setContractId(main.getId());
        	deliverContractMainDetailService.save(detail);
		}
	}
	@Override
	public String getUsingContractId(String clientId) {
		return baseMapper.getUsingContractId(clientId);
	}

	@Override
	public void ChangeAduitStatus(Integer aduitStatus, String id) {
		DeliverContractMain main = new DeliverContractMain();
		main.setId(id);
		main.setIsAudit(aduitStatus);
		baseMapper.updateById(main);
	}

}
