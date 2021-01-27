package com.lzhpo.deliver.controller;

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
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.config.MySysUser;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.Driver;
import com.lzhpo.deliver.entity.Vehicle;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.deliver.service.IDriverService;
import com.lzhpo.deliver.service.IVehicleService;
import com.lzhpo.finance.service.ITableService;
import com.lzhpo.finance.service.IUserTableService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;
import com.lzhpo.sys.service.IUserSettingService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
@Controller
@RequestMapping("deliver/dispatch")
public class DispatchController {
	@Autowired
	private IDispatchService dispatchService;

	@Autowired
	private IDriverService driverService;
	@Autowired
	UserService userService;

	@Autowired
	private IVehicleService vehicleService;


	@Autowired
	private IUserSettingService userSettingService; 
 	
	@Autowired
    private IUserTableService userTableService;
	
	@Autowired
    private ITableService tableService;
	@Autowired
	private ITerritoryService territoryService;
	@GetMapping(value = "list")
	public String list(ModelMap modelMap){
    	// 自定义附表
		Integer user_setting_table = CacheUtils.keyDict.get("user_setting_table").getValue();
		//快速发单模块
		Integer modular_dispatch = CacheUtils.keyDict.get("modular_dispatch").getValue();
		
		modelMap.put("tableList", tableService.selectListByModular(modular_dispatch));
		String userId = MySysUser.id(); 
		modelMap.put("modular", userSettingService.getUserSettingByUserId(userId, modular_dispatch, user_setting_table));
		return "deliver/dispatch/listDispatch";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:dispatch:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Dispatch> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Dispatch> dispatchPageData = new PageData<>();
		QueryWrapper<Dispatch> dispatchWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		dispatchWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				dispatchWrapper.like("name", keys);
			}
			String wayBillId = (String) map.get("wayBillId");
			if (StringUtils.isNotBlank(wayBillId)) {
				dispatchWrapper.like("way_bill_id", wayBillId);
			}
		}
		//排序
		dispatchWrapper.orderByDesc("code");
		IPage<Dispatch> dispatchPage = dispatchService.page(new Page<>(page, limit), dispatchWrapper);
		dispatchPageData.setCount(dispatchPage.getTotal());
		dispatchPageData.setData(setUserToDispatch(dispatchPage.getRecords()));
		return dispatchPageData;
	}

	// 创建者，和修改人
	private List<Dispatch> setUserToDispatch(List<Dispatch> dispatchs) {
		dispatchs.forEach(r -> {
			if(StringUtils.isNotBlank(r.getVehicleId())){
				r.setVehicleCode(vehicleService.getById(r.getVehicleId()).getLicencePlate());
			}
			if(StringUtils.isNotBlank(r.getDriverId())){
				r.setDriverName(driverService.getById(r.getDriverId()).getDriverName());
			}
			if (r.getStatus() != null) {
				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
			}
			if (r.getDispatchStatus() != null) {
				r.setDispactStatusStr(CommomUtil.valueToNameInDict(r.getDispatchStatus(), "scheduling_status"));
			}
			 r.setUserTable(userTableService.getUserTableByuserTableId(r.getId()));
		});

		return dispatchs;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Dispatch> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		List<Vehicle> vehicleList = vehicleService.selectAll();
		modelMap.put("vehicleList", vehicleList);
		List<Driver> driverList = driverService.selectAll();
		modelMap.put("driverList", driverList);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		return "deliver/dispatch/addDispatch";
	}

	@RequiresPermissions("deliver:dispatch:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Dispatch dispatch) {
		dispatchService.saveDispatch(dispatch);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:dispatch:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		Dispatch dispatch = dispatchService.getDispatchById(id);
		dispatchService.deleteDispatch(dispatch);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:dispatch:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Dispatch> dispatchs) {
		if (dispatchs == null || dispatchs.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (Dispatch r : dispatchs) {
			dispatchService.deleteDispatch(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Dispatch dispatch = dispatchService.getDispatchById(id);
		List<Vehicle> vehicleList = vehicleService.selectAll();
		modelMap.put("vehicleList", vehicleList);
		List<Driver> driverList = driverService.selectAll();
		modelMap.put("driverList", driverList);
		modelMap.put("dispatch", dispatch);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		return "deliver/dispatch/editDispatch";
	}

	@RequiresPermissions("deliver:dispatch:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Dispatch dispatch) {
		if (StringUtils.isBlank(dispatch.getId())) {
			return ResponseEntity.failure("数据Id（不能为空)");
		}
		try {
			dispatchService.updateDispatch(dispatch);
		} catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		}
		return ResponseEntity.success("操作成功");
	}
	
	@PostMapping("editJustRate")
	@ResponseBody
	@SysLog("保存编辑数据仅百分比数据")
	public ResponseEntity editJustRate(@RequestBody Dispatch dispatch) {
		if (StringUtils.isBlank(dispatch.getId())) {
			return ResponseEntity.failure("数据Id（不能为空)");
		}
		dispatchService.updateById(dispatch);
		return ResponseEntity.success("操作成功");
	}
	
	@RequiresPermissions("deliver:dispatch:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		dispatchService.backDispatch(id);
		return ResponseEntity.success("操作成功");
	}
}
