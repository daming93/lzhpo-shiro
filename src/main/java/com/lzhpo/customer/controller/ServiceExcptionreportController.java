package com.lzhpo.customer.controller;

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
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.customer.entity.ServiceExcptionreport;
import com.lzhpo.customer.service.IAbnormityTypeService;
import com.lzhpo.customer.service.ICustomerServiceResponsibleDepartmentService;
import com.lzhpo.customer.service.IServiceExcptionreportService;
import com.lzhpo.deliver.service.IAddressService;

/**
 * <p>
 * 异常日报 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Controller
@RequestMapping("customer/serviceExcptionreport")
public class ServiceExcptionreportController {
	@Autowired
	private IServiceExcptionreportService serviceExcptionreportService;

	@Autowired
	private IAbnormityTypeService abnormityTypeService;

	@Autowired
	private IAddressService addressService;

	@Autowired
	private IBasicdataService basicdataService ;
	
	@Autowired
	private ICustomerServiceResponsibleDepartmentService customerServiceResponsibleDepartmentService;
	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		modelMap.put("addressList", addressService.selectAll());
		modelMap.put("basicList", basicdataService.selectAll());
		modelMap.put("resDeptList", customerServiceResponsibleDepartmentService.selectAll());
		return "customer/serviceExcptionreport/listServiceExcptionreport";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("customer:serviceExcptionreport:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<ServiceExcptionreport> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<ServiceExcptionreport> serviceExcptionreportPageData = new PageData<>();
		QueryWrapper<ServiceExcptionreport> serviceExcptionreportWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		serviceExcptionreportWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String clientId = (String) map.get("clientId");
			String addressId = (String) map.get("addressId");
			String abnormityTypeId = (String) map.get("abnormityTypeId");
			String responsibilityDept = (String) map.get("responsibilityDept");
			String timeStart = (String) map.get("timeStart");
			String timeEnd = (String) map.get("timeEnd");
			if (StringUtils.isNotBlank(clientId)) {
				serviceExcptionreportWrapper.eq("client_id", clientId);
			}
			if (StringUtils.isNotBlank(addressId)) {
				serviceExcptionreportWrapper.eq("address_id", addressId);
			}
			if (StringUtils.isNotBlank(abnormityTypeId)) {
				serviceExcptionreportWrapper.eq("abnormity_type_id", abnormityTypeId);
			}
			if (StringUtils.isNotBlank(responsibilityDept)) {
				serviceExcptionreportWrapper.eq("responsibility_dept", responsibilityDept);
			}
			if (StringUtils.isNotBlank(timeStart)) {
				serviceExcptionreportWrapper.gt("time_start", timeStart);
			}
			if (StringUtils.isNotBlank(timeEnd)) {
				serviceExcptionreportWrapper.lt("time_start", timeEnd);
			}
		}
		serviceExcptionreportWrapper.orderByDesc("create_date");
		IPage<ServiceExcptionreport> serviceExcptionreportPage = serviceExcptionreportService
				.page(new Page<>(page, limit), serviceExcptionreportWrapper);
		serviceExcptionreportPageData.setCount(serviceExcptionreportPage.getTotal());
		serviceExcptionreportPageData.setData(setUserToServiceExcptionreport(serviceExcptionreportPage.getRecords()));
		return serviceExcptionreportPageData;
	}

	// 创建者，和修改人
	private List<ServiceExcptionreport> setUserToServiceExcptionreport(
			List<ServiceExcptionreport> serviceExcptionreports) {
		serviceExcptionreports.forEach(r -> {
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

		return serviceExcptionreports;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<ServiceExcptionreport> getById(@RequestParam("pkid")
	// String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		// 异常类型也要了
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		modelMap.put("addressList", addressService.selectAll());
		modelMap.put("basicList", basicdataService.selectAll());
		modelMap.put("resDeptList", customerServiceResponsibleDepartmentService.selectAll());
		return "customer/serviceExcptionreport/addServiceExcptionreport";
	}

	@RequiresPermissions("customer:serviceExcptionreport:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody ServiceExcptionreport serviceExcptionreport) {
		serviceExcptionreportService.saveServiceExcptionreport(serviceExcptionreport);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("customer:serviceExcptionreport:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		ServiceExcptionreport serviceExcptionreport = serviceExcptionreportService.getServiceExcptionreportById(id);
		serviceExcptionreportService.deleteServiceExcptionreport(serviceExcptionreport);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("customer:serviceExcptionreport:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<ServiceExcptionreport> serviceExcptionreports) {
		if (serviceExcptionreports == null || serviceExcptionreports.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (ServiceExcptionreport r : serviceExcptionreports) {
			serviceExcptionreportService.deleteServiceExcptionreport(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		ServiceExcptionreport serviceExcptionreport = serviceExcptionreportService.getServiceExcptionreportById(id);
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		modelMap.put("addressList", addressService.selectAll());
		Map<String, Object> map = new HashMap();
		modelMap.put("serviceExcptionreport", serviceExcptionreport);
		modelMap.put("basicList", basicdataService.selectAll());
		modelMap.put("resDeptList", customerServiceResponsibleDepartmentService.selectAll());
		return "customer/serviceExcptionreport/editServiceExcptionreport";
	}

	@RequiresPermissions("customer:serviceExcptionreport:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody ServiceExcptionreport serviceExcptionreport) {
		serviceExcptionreportService.updateServiceExcptionreport(serviceExcptionreport);
		return ResponseEntity.success("操作成功");
	}
}
