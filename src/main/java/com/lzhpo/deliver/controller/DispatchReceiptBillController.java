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
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
import com.lzhpo.deliver.entity.DispatchReceiptBill;
import com.lzhpo.deliver.service.IDispatchReceiptBillService;

@Controller
@RequestMapping("deliver/dispatchReceiptBill")
public class DispatchReceiptBillController {
	@Autowired
	private IDispatchReceiptBillService dispatchReceiptBillService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "deliver/dispatchReceiptBill/listDispatchReceiptBill";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:dispatchReceiptBill:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<DispatchReceiptBill> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DispatchReceiptBill> dispatchReceiptBillPageData = new PageData<>();
		QueryWrapper<DispatchReceiptBill> dispatchReceiptBillWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		dispatchReceiptBillWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				dispatchReceiptBillWrapper.like("name", keys);
			}
		}
		IPage<DispatchReceiptBill> dispatchReceiptBillPage = dispatchReceiptBillService.page(new Page<>(page, limit),
				dispatchReceiptBillWrapper);
		dispatchReceiptBillPageData.setCount(dispatchReceiptBillPage.getTotal());
		dispatchReceiptBillPageData.setData(setUserToDispatchReceiptBill(dispatchReceiptBillPage.getRecords()));
		return dispatchReceiptBillPageData;
	}

	// 创建者，和修改人
	private List<DispatchReceiptBill> setUserToDispatchReceiptBill(List<DispatchReceiptBill> dispatchReceiptBills) {
		dispatchReceiptBills.forEach(r -> {
		});

		return dispatchReceiptBills;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<DispatchReceiptBill> getById(@RequestParam("pkid")
	// String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		/**
		 * 自定义传入add页面的数据
		 */
		return "deliver/dispatchReceiptBill/addDispatchReceiptBill";
	}

	@RequiresPermissions("deliver:dispatchReceiptBill:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody DispatchReceiptBill dispatchReceiptBill) {
		dispatchReceiptBillService.saveDispatchReceiptBill(dispatchReceiptBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:dispatchReceiptBill:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		DispatchReceiptBill dispatchReceiptBill = dispatchReceiptBillService.getDispatchReceiptBillById(id);
		dispatchReceiptBillService.deleteDispatchReceiptBill(dispatchReceiptBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:dispatchReceiptBill:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<DispatchReceiptBill> dispatchReceiptBills) {
		if (dispatchReceiptBills == null || dispatchReceiptBills.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (DispatchReceiptBill r : dispatchReceiptBills) {
			dispatchReceiptBillService.deleteDispatchReceiptBill(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		DispatchReceiptBill dispatchReceiptBill = dispatchReceiptBillService.getDispatchReceiptBillById(id);
		/**
		 * 自定义代码
		 */
		Map<String, Object> map = new HashMap();
		modelMap.put("dispatchReceiptBill", dispatchReceiptBill);

		return "deliver/dispatchReceiptBill/editDispatchReceiptBill";
	}

	@RequiresPermissions("deliver:dispatchReceiptBill:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody DispatchReceiptBill dispatchReceiptBill) {
		if (StringUtils.isBlank(dispatchReceiptBill.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		dispatchReceiptBillService.updateDispatchReceiptBill(dispatchReceiptBill);
		return ResponseEntity.success("操作成功");
	}
}
