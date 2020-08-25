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
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.VehicleType;
import com.lzhpo.deliver.service.IVehicleContractMainService;
import com.lzhpo.deliver.service.IVehicleTypeService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-20
 */
@Controller
@RequestMapping("deliver/vehicleType")
public class VehicleTypeController {
	@Autowired
	private IVehicleTypeService vehicleTypeService;

	@Autowired
	private IVehicleContractMainService vehicleContractMainService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "deliver/vehicleType/listVehicleType";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:vehicleType:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<VehicleType> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<VehicleType> vehicleTypePageData = new PageData<>();
		QueryWrapper<VehicleType> vehicleTypeWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		vehicleTypeWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				vehicleTypeWrapper.like("name", keys);
			}
		}
		IPage<VehicleType> vehicleTypePage = vehicleTypeService.page(new Page<>(page, limit), vehicleTypeWrapper);
		vehicleTypePageData.setCount(vehicleTypePage.getTotal());
		vehicleTypePageData.setData(setUserToVehicleType(vehicleTypePage.getRecords()));
		return vehicleTypePageData;
	}

	// 创建者，和修改人
	private List<VehicleType> setUserToVehicleType(List<VehicleType> vehicleTypes) {
		vehicleTypes.forEach(r -> {
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
			if (StringUtils.isNotBlank(r.getContractId())) {
				r.setContractName(vehicleContractMainService.getById(r.getContractId()).getName());
			}
		});

		return vehicleTypes;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<VehicleType> getById(@RequestParam("pkid") String
	// pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		modelMap.put("contractList", vehicleContractMainService.selectAll());
		return "deliver/vehicleType/addVehicleType";
	}

	@RequiresPermissions("deliver:vehicleType:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody VehicleType vehicleType) {
		if (StringUtils.isBlank(vehicleType.getName())) {
			return ResponseEntity.failure("名称（不能为空)");
		}
		if (vehicleTypeService.getVehicleTypeCount(vehicleType.getName()) > 0) {
			return ResponseEntity.failure("名称（不能重复)");
		}
		vehicleTypeService.saveVehicleType(vehicleType);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:vehicleType:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		VehicleType vehicleType = vehicleTypeService.getVehicleTypeById(id);
		vehicleTypeService.deleteVehicleType(vehicleType);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:vehicleType:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<VehicleType> vehicleTypes) {
		if (vehicleTypes == null || vehicleTypes.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的记录");
		}
		for (VehicleType r : vehicleTypes) {
			vehicleTypeService.deleteVehicleType(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		VehicleType vehicleType = vehicleTypeService.getVehicleTypeById(id);
		/**
		 * 自定义代码
		 */
		modelMap.put("vehicleType", vehicleType);
		modelMap.put("contractList", vehicleContractMainService.selectAll());

		return "deliver/vehicleType/editVehicleType";
	}

	@RequiresPermissions("deliver:vehicleType:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody VehicleType vehicleType) {
		if (StringUtils.isBlank(vehicleType.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		if (StringUtils.isBlank(vehicleType.getName())) {
			return ResponseEntity.failure("名称不能为空");
		}
		VehicleType oldVehicleType = vehicleTypeService.getVehicleTypeById(vehicleType.getId());
		if (!oldVehicleType.getName().equals(vehicleType.getName())) {
			if (vehicleTypeService.getVehicleTypeCount(vehicleType.getName()) > 0) {
				return ResponseEntity.failure("名称（不能重复)");
			}
		}
		vehicleTypeService.updateVehicleType(vehicleType);
		return ResponseEntity.success("操作成功");
	}
}
