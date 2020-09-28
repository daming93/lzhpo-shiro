package com.lzhpo.deliver.controller;

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
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.config.MySysUser;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.ExpressBill;
import com.lzhpo.deliver.service.IExpressBillService;
import com.lzhpo.finance.service.ITableService;
import com.lzhpo.finance.service.IUserTableService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;
import com.lzhpo.sys.service.IUserSettingService;

/**
 * <p>
 * 配送零单配送 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-12
 */
@Controller
@RequestMapping("deliver/expressBill")
public class ExpressBillController {
	@Autowired
	private IExpressBillService expressBillService;

	@Autowired
	private ITerritoryService territoryService;

 	@Autowired
    private ITableService tableService;
 	
 	@Autowired
	private IUserSettingService userSettingService; 
 	
	@Autowired
    private IUserTableService userTableService;
	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		// 自定义附表
		Integer user_setting_table = CacheUtils.keyDict.get("user_setting_table").getValue();
		//快速发单模块
		Integer modular_express_bill = CacheUtils.keyDict.get("modular_express_bill").getValue();
		
		modelMap.put("tableList", tableService.selectListByModular(modular_express_bill));
		String userId = MySysUser.id();
		modelMap.put("modular", userSettingService.getUserSettingByUserId(userId, modular_express_bill, user_setting_table));
		return "deliver/expressBill/listExpressBill";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:expressBill:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<ExpressBill> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<ExpressBill> expressBillPageData = new PageData<>();
		QueryWrapper<ExpressBill> expressBillWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		expressBillWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("code");
			if (StringUtils.isNotBlank(keys)) {
				expressBillWrapper.like("code", keys);
			}
		}
		expressBillWrapper.orderByAsc("status");
		expressBillWrapper.orderByDesc("code");
		IPage<ExpressBill> expressBillPage = expressBillService.page(new Page<>(page, limit), expressBillWrapper);
		expressBillPageData.setCount(expressBillPage.getTotal());
		expressBillPageData.setData(setUserToExpressBill(expressBillPage.getRecords()));
		return expressBillPageData;
	}

	// 创建者，和修改人
	private List<ExpressBill> setUserToExpressBill(List<ExpressBill> expressBills) {
		expressBills.forEach(r -> {
			// 待编辑
			if (StringUtils.isNotBlank(r.getSendProvinceId())) {
				r.setProvinceName(territoryService.getById(r.getSendProvinceId()).getName());
			}
			if (StringUtils.isNotBlank(r.getSendCityId())) {
				r.setCityName(territoryService.getById(r.getSendCityId()).getName());
			}
			if (StringUtils.isNotBlank(r.getSendAreaId())) {
				r.setCountiesName(territoryService.getById(r.getSendAreaId()).getName());
			}
			if (StringUtils.isNotBlank(r.getReceiveProvinceId())) {
				r.setReceiveProvinceName(territoryService.getById(r.getReceiveProvinceId()).getName());
			}
			if (StringUtils.isNotBlank(r.getReceiveCityId())) {
				r.setReceiveCityName(territoryService.getById(r.getReceiveCityId()).getName());
			}
			if (StringUtils.isNotBlank(r.getReceiveAreaId())) {
				r.setReceiveCountiesName(territoryService.getById(r.getReceiveAreaId()).getName());
			}
			if (r.getStatus() != null) {
				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
			}
			r.setSendDetail(r.getProvinceName()+r.getCityName()+r.getCountiesName());
			r.setReceiveDetail(r.getReceiveProvinceName()+r.getReceiveCityName()+r.getReceiveCountiesName());
			r.setUserTable(userTableService.getUserTableByuserTableId(r.getId()));
		});

		return expressBills;
	}
	private ExpressBill setExpToExpressBill (ExpressBill r){
		if (StringUtils.isNotBlank(r.getSendProvinceId())) {
			r.setProvinceName(territoryService.getById(r.getSendProvinceId()).getName());
		}
		if (StringUtils.isNotBlank(r.getSendCityId())) {
			r.setCityName(territoryService.getById(r.getSendCityId()).getName());
		}
		if (StringUtils.isNotBlank(r.getSendAreaId())) {
			r.setCountiesName(territoryService.getById(r.getSendAreaId()).getName());
		}
		if (StringUtils.isNotBlank(r.getReceiveProvinceId())) {
			r.setReceiveProvinceName(territoryService.getById(r.getReceiveProvinceId()).getName());
		}
		if (StringUtils.isNotBlank(r.getReceiveCityId())) {
			r.setReceiveCityName(territoryService.getById(r.getReceiveCityId()).getName());
		}
		if (StringUtils.isNotBlank(r.getReceiveAreaId())) {
			r.setReceiveCountiesName(territoryService.getById(r.getReceiveAreaId()).getName());
		}
		if (r.getStatus() != null) {
			r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
		}
		r.setSendDetail(r.getProvinceName()+r.getCityName()+r.getCountiesName());
		r.setReceiveDetail(r.getReceiveProvinceName()+r.getReceiveCityName()+r.getReceiveCountiesName());
		return r;
	}
	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<ExpressBill> getById(@RequestParam("pkid") String
	// pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		return "deliver/expressBill/addExpressBill";
	}

	@RequiresPermissions("deliver:expressBill:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody ExpressBill expressBill) {
		expressBillService.saveExpressBill(expressBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:expressBill:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		ExpressBill expressBill = expressBillService.getExpressBillById(id);
		expressBillService.deleteExpressBill(expressBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:expressBill:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<ExpressBill> expressBills) {
		if (expressBills == null || expressBills.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (ExpressBill r : expressBills) {
			expressBillService.deleteExpressBill(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		ExpressBill expressBill = expressBillService.getExpressBillById(id);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		Map<String, Object> map = new HashMap();
		modelMap.put("expressBill", expressBill);

		return "deliver/expressBill/editExpressBill";
	}

	@RequiresPermissions("deliver:expressBill:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody ExpressBill expressBill) {
		if (StringUtils.isBlank(expressBill.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		expressBillService.updateExpressBill(expressBill);
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("getSendPeopelInfoBySendPhone")
	@ResponseBody
	public ResponseEntity getSendPeopelInfoBySendPhone(String sendPhone) {
		ExpressBill expressBill = expressBillService.getSendPeopelInfoBySendPhone(sendPhone);
		if (expressBill != null) {
			return ResponseEntity.success("操作成功").setAny("data", setExpToExpressBill(expressBill));
		} else {
			return ResponseEntity.failure("无");
		}
	}
	

	@GetMapping("getReceivePeopelInfoBySendPhone")
	@ResponseBody
	public ResponseEntity getReceivePeopelInfoByReceivePhone(String receivePhone) {
		ExpressBill expressBill = expressBillService.getReceivePeopelInfoByReceivePhone(receivePhone);
		if (expressBill != null) {
			return ResponseEntity.success("操作成功").setAny("data", setExpToExpressBill(expressBill));
		} else {
			return ResponseEntity.failure("无");
		}
	}
	
	
	@RequiresPermissions("deliver:expressBill:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		expressBillService.backExpressBill(id);
		return ResponseEntity.success("操作成功");
	}

}
