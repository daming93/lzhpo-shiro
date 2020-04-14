package com.lzhpo.common.timer;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.entity.ContractMain;
import com.lzhpo.client.entity.ContractMainDetail;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.client.service.IContractMainDetailService;
import com.lzhpo.client.service.IContractMainService;
import com.lzhpo.common.util.ApplicationContextUtil;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.sys.service.IGenerateNoService;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

@Component
public class TimeJob {

	/**
	 * 根据合同每天 00 :05分进行收入流水
	 */
	public void FinanceIncome(){
		//由于定时任务的加载时机在spring bean之前
		IBasicdataService basicdataService = (IBasicdataService) ApplicationContextUtil.getBean("basicdataService");
		IContractMainService contractMainService = (IContractMainService) ApplicationContextUtil.getBean("contractMainService");
		IContractMainDetailService contractMainDetailService = (IContractMainDetailService) ApplicationContextUtil.getBean("contractMainDetailService");
		IIncomeService incomeService = (IIncomeService) ApplicationContextUtil.getBean("incomeService");
		IGenerateNoService generateNoService = (IGenerateNoService) ApplicationContextUtil.getBean("generateNoService");
		
		
		
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
				//找到该合同细则
				ContractMain contractMain = contractMainService.getById(usingContractId);
				//距今多少天
				Date startDay = CommomUtil.localDate2Date(contractMain.getStartTime());
				long betweenDay = DateUtil.between(startDay
						, new Date(), DateUnit.DAY);
				//距今多少个月
				long betweenMonth = DateUtil.betweenMonth(startDay
						, new Date(), true);
				//是不是 日 相同
				boolean dayFlag = DateUtil.dayOfMonth(startDay)== DateUtil.thisDayOfMonth();
				
				//找到合同细则
				List<ContractMainDetail> details = contractMainDetailService.selectDetail(usingContractId);
				
				for (ContractMainDetail contractMainDetail : details) {
					//然后根据不同收费准则收费
					if(JudIfIncome(contractMainDetail.getType(), betweenDay, betweenMonth, dayFlag)){
						//收费
						//这里要登陆管理员的
						Income income = new Income();
						income.setCode(generateNoService.nextCode("SR"));
						income.setBasis(contractMain.getContractCode());
						income.setClientId(contractMain.getClientId());
						income.setOptionId(contractMainDetail.getOptionId());
						income.setMoeny(contractMainDetail.getMoney());
						incomeService.save(income);
						System.out.println("对"+contractMain.getClientId()+"客户收取"+
						contractMainDetail.getMoney()+"费用");
					}
				}
				
			}
		}
	}
	/**
	 * 根据合同的详情的具体收费模式查看今天是不是应该收费
	 * @param type
	 * @return
	 */
	private boolean JudIfIncome(Integer type,long betweenDay,long betweenMonth,boolean dayFlag){
		// 1 日结 2 周 3 月 4 季 5 半年 6 年度
		switch (type) {
		case 2:
			if(betweenDay%7==0){
				return true;
			}else{
				return false;
			}
		case 3:
			if(betweenMonth>0&&dayFlag){
				return true;
			}else{
				return false;
			}
		case 4:
			if(betweenMonth%3==0&&dayFlag){
				return true;
			}else{
				return false;
			}
		case 5:
			if(betweenMonth%6==0&&dayFlag){
				return true;
			}else{
				return false;
			}
		case 6:
			if(betweenMonth%12==0&&dayFlag){
				return true;
			}else{
				return false;
			}
		default:
			return true;
		}
		
	}
}
