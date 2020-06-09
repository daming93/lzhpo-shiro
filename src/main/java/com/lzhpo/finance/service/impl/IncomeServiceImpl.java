package com.lzhpo.finance.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.client.entity.ContractMain;
import com.lzhpo.client.service.IContractMainService;
import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.mapper.IncomeMapper;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.stock.entity.Storage;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.sys.service.IGenerateNoService;
/**
 * <p>
 * 财务收入 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-14
 */
@Service("incomeService")
public class IncomeServiceImpl extends ServiceImpl<IncomeMapper, Income> implements IIncomeService {
	@Autowired
	private IContractMainService contractMainService ;
	@Autowired
	private IGenerateNoService generateNoService;
	
	@Override
    public long getIncomeCount(String name) {
        QueryWrapper<Income> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Incomes", allEntries = true)
    public Income saveIncome(Income income) {
        baseMapper.insert(income);
        /**
	*预留编辑代码 
	*/
        return income;
    }

    @Override
    public Income getIncomeById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Incomes", allEntries = true)
    public void updateIncome(Income income) {
        baseMapper.updateById(income);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Incomes", allEntries = true)
    public void deleteIncome(Income income) {
        //income.setDelFlag(true);
        baseMapper.updateById(income);
    }

    @Override
    @Cacheable("Incomes")
    public List<Income> selectAll() {
        QueryWrapper<Income> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void takeoutIncomeMath(Takeout takeout)throws Exception {
		//出库装卸费
		String opId = "4c089061ca5243fd97f02213015d44e7";
		//找到客户对应在使用的合同
		String usingContractId = contractMainService.getUsingContractId(takeout.getClientId());
		if (StringUtils.isNotBlank(usingContractId)) {
			ContractMain contractMain = contractMainService.getById(usingContractId);
			BigDecimal money = new BigDecimal(0.0);
			BigDecimal handingTakeoutMoney = contractMain.getHandingTakeoutMoney();
			//有合同看他装卸类型是怎算的
			switch (contractMain.getHandingType()) {
			case 1:// 按件
				money = handingTakeoutMoney.multiply(new BigDecimal(takeout.getTotal()));
				break;
			case 2:// 按体积
				money = handingTakeoutMoney.multiply(takeout.getVolume());
				break;
			case 3:// 按重量
				money = handingTakeoutMoney.multiply(takeout.getWeight());
				break;	
			default:
				break;
			}
			Income income = new Income();
			income.setCode(generateNoService.nextCode("SR"));
			income.setBasis(contractMain.getContractCode());
			income.setClientId(contractMain.getClientId());
			income.setOptionId(opId);
			income.setMoeny(money);
			save(income);
		}else{
			throw new RuntimeJsonMappingException("该客户暂无正在使用的合同");
		}
	}
	@Override
	public Storage storageIncomeMath(Storage storage) throws Exception {

		//入库装卸费
		String opId = "4c089061ca5243fd97f02213015d44e6";
		//没有的加
		//找到客户对应在使用的合同
		String usingContractId = contractMainService.getUsingContractId(storage.getClientId());
		if (StringUtils.isNotBlank(usingContractId)) {
			ContractMain contractMain = contractMainService.getById(usingContractId);
			BigDecimal money = new BigDecimal(0.0);
			BigDecimal handingStorageMoney = contractMain.getHandingStorageMoney();
			//有合同看他装卸类型是怎算的
			switch (contractMain.getHandingType()) {
			case 1:// 按件
				money = handingStorageMoney.multiply(new BigDecimal(storage.getTotal()));
				break;
			case 2:// 按体积
				money = handingStorageMoney.multiply(storage.getVolume());
				break;
			case 3:// 按重量
				money = handingStorageMoney.multiply(storage.getWeight());
				break;	
			default:
				break;
			}
			
			if (StringUtils.isNotBlank(storage.getIncomeId())) {//有的更新 就更新钱
				Income income = getById(storage.getIncomeId());
				income.setMoeny(money);
				updateById(income);
			}else{
				Income income = new Income();
				income.setCode(generateNoService.nextCode("SR"));
				income.setBasis(contractMain.getContractCode());
				income.setClientId(contractMain.getClientId());
				income.setOptionId(opId);
				income.setMoeny(money);
				save(income);
				storage.setIncomeId(income.getId());
			}
		}else{
			throw new RuntimeJsonMappingException("该客户暂无正在使用的合同");
		}
		return storage;
	}
}
