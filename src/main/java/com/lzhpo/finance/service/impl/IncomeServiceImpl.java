package com.lzhpo.finance.service.impl;

import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.mapper.IncomeMapper;
import com.lzhpo.finance.service.IIncomeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
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


}
