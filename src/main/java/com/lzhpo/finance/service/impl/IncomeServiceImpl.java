package com.lzhpo.finance.service.impl;

import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.mapper.IncomeMapper;
import com.lzhpo.finance.service.IIncomeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 财务收入 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-13
 */
@Service
public class IncomeServiceImpl extends ServiceImpl<IncomeMapper, Income> implements IIncomeService {

}
