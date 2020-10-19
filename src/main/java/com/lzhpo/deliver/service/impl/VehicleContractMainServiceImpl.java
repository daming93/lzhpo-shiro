package com.lzhpo.deliver.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.deliver.entity.VehicleContractMain;
import com.lzhpo.deliver.entity.VehicleContractMainDetail;
import com.lzhpo.deliver.mapper.VehicleContractMainMapper;
import com.lzhpo.deliver.service.IVehicleContractMainDetailService;
import com.lzhpo.deliver.service.IVehicleContractMainService;
import com.lzhpo.sys.service.IGenerateNoService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
@Service
public class VehicleContractMainServiceImpl extends ServiceImpl<VehicleContractMainMapper, VehicleContractMain> implements IVehicleContractMainService {
	
	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private IVehicleContractMainDetailService vehicleContractMainDetailService ;
	@Override
    public long getVehicleContractMainCount(String name) {
        QueryWrapper<VehicleContractMain> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMains", allEntries = true)
    public VehicleContractMain saveVehicleContractMain(VehicleContractMain vehicleContractMain) {
    	vehicleContractMain.setContractCode(generateNoService.nextCode("CLHT"));
        baseMapper.insert(vehicleContractMain);//
        //编辑子表
        Set<VehicleContractMainDetail> details = vehicleContractMain.getDetailSet();
        for (VehicleContractMainDetail detail : details) {
        	detail.setContractId(vehicleContractMain.getId());
        	vehicleContractMainDetailService.save(detail);
		}
        
        return vehicleContractMain;
    }

    @Override
    public VehicleContractMain getVehicleContractMainById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMains", allEntries = true)
    public void updateVehicleContractMain(VehicleContractMain vehicleContractMain) {
        baseMapper.updateById(vehicleContractMain);
        Set<VehicleContractMainDetail> details = vehicleContractMain.getDetailSet();
        //删掉过去的主表
        vehicleContractMainDetailService.deleteAllDetailByMainId(vehicleContractMain.getId());
        for (VehicleContractMainDetail detail : details) {
        	detail.setContractId(vehicleContractMain.getId());
        	vehicleContractMainDetailService.save(detail);
		}
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMains", allEntries = true)
    public void deleteVehicleContractMain(VehicleContractMain vehicleContractMain) {
        vehicleContractMain.setDelFlag(true);
        baseMapper.updateById(vehicleContractMain);
    }

    @Override
    @Cacheable("VehicleContractMains")
    public List<VehicleContractMain> selectAll() {
        QueryWrapper<VehicleContractMain> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void copyContract(String contractId) {
		VehicleContractMain main =  baseMapper.selectById(contractId);
		List<VehicleContractMainDetail> detailList =  vehicleContractMainDetailService.getListByMainId(contractId);
		//先把名字搞出来
		String name = main.getName()+"副本";
		String newName = name;
		int i = 1;
		while (getVehicleContractMainCount(newName)>0){
			i++;
			newName = name+i;
		};
		//名字搞出来,然后改code
		main.setId(null);
		main.setName(name);
		main.setContractCode(generateNoService.nextCode("CLHT"));
		baseMapper.insert(main);
		//处理子表
		for (VehicleContractMainDetail detail : detailList) {
			detail.setId(null);//清空原先的id
			detail.setContractId(main.getId());
        	vehicleContractMainDetailService.save(detail);
		}
	}


}
