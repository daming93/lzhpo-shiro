package com.lzhpo.finance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.entity.ContractOption;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.client.service.IContractOptionService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.service.IIncomeService;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
@Controller
@RequestMapping("/finance/income")
public class IncomeController {

	@Autowired
	private IBasicdataService basicdateService;
	@Autowired
	private IIncomeService incomeService;
	@Autowired
	private IContractOptionService contractOptionService ;
	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "finance/income/listIncome";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("finance:income:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Income> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Income> IncomePageData = new PageData<>();
		QueryWrapper<Income> IncomeWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		IncomeWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				IncomeWrapper.like("name", keys);
			}
		}
		IPage<Income> IncomePage = incomeService.page(new Page<>(page, limit), IncomeWrapper);
		IncomePageData.setCount(IncomePage.getTotal());
		IncomePageData.setData(setUserToIncome(IncomePage.getRecords()));
		return IncomePageData;
	}

	// 创建者，和修改人
	private List<Income> setUserToIncome(List<Income> incomes) {
		incomes.forEach(r -> {
			if (StringUtils.isNotBlank(r.getCreateId())) {
				User u = userService.findUserById(r.getCreateId());
				if (StringUtils.isBlank(u.getNickName())) {
					u.setNickName(u.getLoginName());
				}
				r.setCreateUser(u);
			}
			if (StringUtils.isNotBlank(r.getUpdateId())) {
				User u = userService.findUserById(r.getUpdateId());
				if (StringUtils.isBlank(u.getNickName())) {
					u.setNickName(u.getLoginName());
				} 
				r.setUpdateUser(u);
			}
			if (StringUtils.isNotBlank(r.getAuditMan())) {
				User u = userService.findUserById(r.getAuditMan());
				r.setAuditMan(u.getLoginName());
			}
			if (StringUtils.isNotBlank(r.getClientId())) {
				r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
			}
			if(StringUtils.isNotBlank(r.getOptionId())){
				r.setOptionName(contractOptionService.getById(r.getOptionId()).getName());
			}
			r.setTypeStr("客户收入");//暂时就这一种
		});

		return incomes;
	}

	@RequiresPermissions("finance:income:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("储位ID不能为空");
		}
		Income income = incomeService.getIncomeById(id);
		incomeService.deleteIncome(income);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("finance:income:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Income> incomes) {
		if (incomes == null || incomes.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的储位");
		}
		for (Income r : incomes) {
			incomeService.deleteIncome(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Income income = incomeService.getIncomeById(id);
		modelMap.put("income", income);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", com.alibaba.fastjson.JSONObject.toJSON(basicDatas));
		
		List<ContractOption> options = contractOptionService.selectAll();
		modelMap.put("options", com.alibaba.fastjson.JSONObject.toJSON(options));
		return "finance/income/editIncome";
	}

	@RequiresPermissions("finance:income:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Income income) {
		incomeService.updateIncome(income);
		return ResponseEntity.success("操作成功");
	}
}
