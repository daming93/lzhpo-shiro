package com.lzhpo.common.timer;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.client.service.IContractMainService;

public class TimeJob {

	
	@Autowired
	private IBasicdataService basicdataService;
	
	@Autowired
	private IContractMainService contractMainService;
	
	
	public void Test(){
		System.out.println("time now"+System.currentTimeMillis());
	}
	/**
	 * 根据合同每天 00 :05分进行收入流水
	 */
	public void FinanceIncome(){
		//找到所有客户的可用合同
		QueryWrapper<Basicdata> basicMainWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		basicMainWrapper.eq("del_flag", false);
		IPage<Basicdata> BasicdataPage = basicdataService.page(new Page<>(1,1000),basicMainWrapper);
		List<Basicdata> records = BasicdataPage.getRecords();
		for (Basicdata basicdata : records) {
			//每个客户，现在找他的合同
			String usingContractId = contractMainService.getUsingContractId(basicdata.getId());
			if (StringUtils.isNotBlank(usingContractId)) {
				//如果现在用的合同不是空的话
			}
		}
	}
}
