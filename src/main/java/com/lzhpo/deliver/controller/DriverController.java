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
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Driver;
import com.lzhpo.deliver.service.IDriverService;

/**
 * <p>
 * 驾驶员信息表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-14
 */
@Controller
@RequestMapping("deliver/driver")
public class DriverController {
	@Autowired
	private IDriverService driverService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "deliver/driver/listDriver";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:driver:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Driver> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Driver> driverPageData = new PageData<>();
		QueryWrapper<Driver> driverWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		driverWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				driverWrapper.like("driver_name", keys);
			}
		}
		IPage<Driver> driverPage = driverService.page(new Page<>(page, limit), driverWrapper);
		driverPageData.setCount(driverPage.getTotal());
		driverPageData.setData(setUserToDriver(driverPage.getRecords()));
		return driverPageData;
	}

	// 创建者，和修改人
	private List<Driver> setUserToDriver(List<Driver> drivers) {
		drivers.forEach(r -> {
			if (StringUtils.isNotBlank(r.getIsExistCertNum() + "")) {
				r.setIsExistCertNumStr(CommomUtil.valueToNameInDict(r.getIsExistCertNum(), "is_exist_cert_num"));
			}
			if (StringUtils.isNotBlank(r.getLicencseType() + "")) {
				r.setLicencseTypeStr(CommomUtil.valueToNameInDict(r.getLicencseType(), "licencse_type"));
			}
		});

		return drivers;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Driver> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		modelMap.addAttribute("licencseType",  CacheUtils.allDicts.get("licencse_type"));
		//is_exist_cert_num
		modelMap.addAttribute("certNum",  CacheUtils.allDicts.get("is_exist_cert_num"));
		return "deliver/driver/addDriver";
	}

	@RequiresPermissions("deliver:driver:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Driver driver) {
		driverService.saveDriver(driver);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:driver:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		Driver driver = driverService.getDriverById(id);
		driverService.deleteDriver(driver);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:driver:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Driver> drivers) {
		if (drivers == null || drivers.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (Driver r : drivers) {
			driverService.deleteDriver(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Driver driver = driverService.getDriverById(id);
		modelMap.addAttribute("licencseType",  CacheUtils.allDicts.get("licencse_type"));
		//is_exist_cert_num
		modelMap.addAttribute("certNum",  CacheUtils.allDicts.get("is_exist_cert_num"));
		Map<String, Object> map = new HashMap();
		modelMap.put("driver", driver);

		return "deliver/driver/editDriver";
	}

	@RequiresPermissions("deliver:driver:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Driver driver) {
		if (StringUtils.isBlank(driver.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		driverService.updateDriver(driver);
		return ResponseEntity.success("操作成功");
	}
}
