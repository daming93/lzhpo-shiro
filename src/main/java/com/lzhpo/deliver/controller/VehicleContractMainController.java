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
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.VehicleContractMain;
import com.lzhpo.deliver.entity.VehicleContractMainDetail;
import com.lzhpo.deliver.service.IVehicleContractMainDetailService;
import com.lzhpo.deliver.service.IVehicleContractMainService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
@Controller
@RequestMapping("deliver/vehicleContractMain")
public class VehicleContractMainController {
	@Autowired
	private IVehicleContractMainService vehicleContractMainService;

	@Autowired
	private IVehicleContractMainDetailService  vehicleContractMainDetailService;
	
	@Autowired
	UserService userService;

	@Autowired
	private ITerritoryService territoryService;
	

	@GetMapping(value = "list")
	public String list() {
		return "deliver/vehicleContractMain/listVehicleContractMain";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:vehicleContractMain:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<VehicleContractMain> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<VehicleContractMain> vehicleContractMainPageData = new PageData<>();
		QueryWrapper<VehicleContractMain> vehicleContractMainWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		vehicleContractMainWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				vehicleContractMainWrapper.like("name", keys);
			}
		}
		IPage<VehicleContractMain> vehicleContractMainPage = vehicleContractMainService.page(new Page<>(page, limit),
				vehicleContractMainWrapper);
		vehicleContractMainPageData.setCount(vehicleContractMainPage.getTotal());
		vehicleContractMainPageData.setData(setUserToVehicleContractMain(vehicleContractMainPage.getRecords()));
		return vehicleContractMainPageData;
	}

	// 创建者，和修改人
	private List<VehicleContractMain> setUserToVehicleContractMain(List<VehicleContractMain> vehicleContractMains) {
		vehicleContractMains.forEach(r -> {
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
		});

		return vehicleContractMains;
	}
	
	private List<VehicleContractMainDetail> setNosqlOptionDetail(List<VehicleContractMainDetail> details){
		details.forEach(r ->{
			if (StringUtils.isNotBlank(r.getProvinceId())) {
				r.setProvinceName(territoryService.getById(r.getProvinceId()).getName());
			}
			if (StringUtils.isNotBlank(r.getCityId())) {
				r.setCityName(territoryService.getById(r.getCityId()).getName());
			}
			if (StringUtils.isNotBlank(r.getAreaId())) {
				r.setAreaName(territoryService.getById(r.getAreaId()).getName());
			}
		});
		return details;
	}
	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<VehicleContractMainDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "9999") Integer limit, ServletRequest request,
			String contractId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<VehicleContractMainDetail> ContVehicleContractMainDetailData = new PageData<>();
		QueryWrapper<VehicleContractMainDetail> VehicleContractMainDetailWrapper = new QueryWrapper<>();
		VehicleContractMainDetailWrapper.eq("contract_id", contractId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				VehicleContractMainDetailWrapper.like("name", keys);
			}
		}
		VehicleContractMainDetailWrapper.orderByDesc("create_date ");
		IPage<VehicleContractMainDetail> VehicleContractMainDetailPage = vehicleContractMainDetailService.page(new Page<>(page, limit),
				VehicleContractMainDetailWrapper);
		ContVehicleContractMainDetailData.setCount(VehicleContractMainDetailPage.getTotal());
		ContVehicleContractMainDetailData.setData(setNosqlOptionDetail(VehicleContractMainDetailPage.getRecords()));
		return ContVehicleContractMainDetailData;
	}
	
	
	
	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<VehicleContractMain> getById(@RequestParam("pkid")
	// String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		return "deliver/vehicleContractMain/addVehicleContractMain";
	}

	@RequiresPermissions("deliver:vehicleContractMain:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody VehicleContractMain vehicleContractMain) {
		if (StringUtils.isBlank(vehicleContractMain.getName())) {
			return ResponseEntity.failure("合同名称（不能为空)");
		}
		if (vehicleContractMainService.getVehicleContractMainCount(vehicleContractMain.getName()) > 0) {
			return ResponseEntity.failure("合同名称（不能重复)");
		}
		vehicleContractMainService.saveVehicleContractMain(vehicleContractMain);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:vehicleContractMain:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		VehicleContractMain vehicleContractMain = vehicleContractMainService.getVehicleContractMainById(id);
		vehicleContractMainService.deleteVehicleContractMain(vehicleContractMain);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:vehicleContractMain:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<VehicleContractMain> vehicleContractMains) {
		if (vehicleContractMains == null || vehicleContractMains.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (VehicleContractMain r : vehicleContractMains) {
			vehicleContractMainService.deleteVehicleContractMain(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		VehicleContractMain vehicleContractMain = vehicleContractMainService.getVehicleContractMainById(id);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		Map<String, Object> map = new HashMap();
		modelMap.put("vehicleContractMain", vehicleContractMain);

		return "deliver/vehicleContractMain/editVehicleContractMain";
	}

	@RequiresPermissions("deliver:vehicleContractMain:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody VehicleContractMain vehicleContractMain) {
		if (StringUtils.isBlank(vehicleContractMain.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		if (StringUtils.isBlank(vehicleContractMain.getName())) {
			return ResponseEntity.failure("名称不能为空");
		}
		VehicleContractMain oldVehicleContractMain = vehicleContractMainService
				.getVehicleContractMainById(vehicleContractMain.getId());
		if (!oldVehicleContractMain.getName().equals(vehicleContractMain.getName())) {
			if (vehicleContractMainService.getVehicleContractMainCount(vehicleContractMain.getName()) > 0) {
				return ResponseEntity.failure("合同名称（不能重复)");
			}
		}
		vehicleContractMainService.updateVehicleContractMain(vehicleContractMain);
		return ResponseEntity.success("操作成功");
	}
}
