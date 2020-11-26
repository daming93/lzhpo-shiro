package com.lzhpo.stock.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
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
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.customer.service.IAbnormityService;
import com.lzhpo.customer.service.IAbnormityTypeService;
import com.lzhpo.stock.entity.ReceiptBill;
import com.lzhpo.stock.service.IReceiptBillService;
import com.lzhpo.stock.service.ITakeoutService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-11-23
 */
@Controller
@RequestMapping("stock/receiptBill")
public class ReceiptBillController {
	@Autowired
	private IReceiptBillService receiptBillService;

	@Autowired
	private ITakeoutService takeoutService;

	@Autowired
	private IAbnormityTypeService abnormityTypeService ;
	
	@Autowired
	private IAbnormityService abnormityService;
	
	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "stock/receiptBill/listReceiptBill";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:receiptBill:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<ReceiptBill> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<ReceiptBill> receiptBillPageData = new PageData<>();
		QueryWrapper<ReceiptBill> receiptBillWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		receiptBillWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				receiptBillWrapper.like("name", keys);
			}
		}
		//这边看权限给什么状态值得内容
		//调度审核撤回权限
		// 待确认 1
		Integer receipt_status_no = CacheUtils.keyDict.get("receipt_status_no").getValue();
		// 已确认 3
		Integer receipt_status_yes = CacheUtils.keyDict.get("receipt_status_yes").getValue();	
		// 待审核 2
		Integer receipt_status_audit = CacheUtils.keyDict.get("receipt_status_audit").getValue();	
		// 待录入路单 0
		Integer receipt_status_entry = CacheUtils.keyDict.get("receipt_status_entry").getValue();	
		List<Integer> statusList = new ArrayList<Integer>();
		Subject currentUser = SecurityUtils.getSubject();
		boolean fristEdit = currentUser.isPermitted("stock:receiptBill:fristEdit");//对应 1
		boolean fristBack = currentUser.isPermitted("stock:receiptBill:fristBack");//对应  2
		boolean finalEdit = currentUser.isPermitted("stock:receiptBill:finalEdit");//对应  2
		boolean finalBack = currentUser.isPermitted("stock:receiptBill:finalBack");//对应  3
		statusList.add(receipt_status_entry);//0 谁都看得见
		if(fristEdit) statusList.add(receipt_status_no);
		if(fristBack) statusList.add(receipt_status_audit);
		if(finalEdit) statusList.add(receipt_status_audit);
		if(finalBack) statusList.add(receipt_status_yes);
		receiptBillWrapper.in("receipt_status", statusList);
		IPage<ReceiptBill> receiptBillPage = receiptBillService.page(new Page<>(page, limit), receiptBillWrapper);
		receiptBillPageData.setCount(receiptBillPage.getTotal());
		receiptBillPageData.setData(setUserToReceiptBill(receiptBillPage.getRecords()));
		return receiptBillPageData;
	}

	// 创建者，和修改人
	private List<ReceiptBill> setUserToReceiptBill(List<ReceiptBill> receiptBills) {
		receiptBills.forEach(r -> {
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
			if (StringUtils.isNotBlank(r.getRefId())) {
				r.setTakeout(takeoutService.getTakeoutById(r.getRefId()));
			}
			if (StringUtils.isNotBlank(r.getAbnormityId())) {
				r.setAbnormityName(abnormityService.getById(r.getAbnormityId()).getCause());
			}
			if (StringUtils.isNotBlank(r.getAbnormityTypeId())) {
				r.setAbnormityTypeName(abnormityTypeService.getById(r.getAbnormityTypeId()).getName());
			}
			if(r.getReceiptStatus()!=null){
				r.setReceiptStatusName(CommomUtil.valueToNameInDict(r.getReceiptStatus(), "receipt_status"));
			}
		});

		return receiptBills;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<ReceiptBill> getById(@RequestParam("pkid") String
	// pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		/**
		 * 自定义传入add页面的数据
		 */
		return "stock/receiptBill/addReceiptBill";
	}

	@RequiresPermissions("stock:receiptBill:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody ReceiptBill receiptBill) {
		receiptBillService.saveReceiptBill(receiptBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:receiptBill:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		ReceiptBill receiptBill = receiptBillService.getReceiptBillById(id);
		receiptBillService.deleteReceiptBill(receiptBill);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:receiptBill:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<ReceiptBill> receiptBills) {
		if (receiptBills == null || receiptBills.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (ReceiptBill r : receiptBills) {
			receiptBillService.deleteReceiptBill(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id,String type, ModelMap modelMap) {
		ReceiptBill r = receiptBillService.getReceiptBillById(id);
		if (StringUtils.isNotBlank(r.getRefId())) {
			r.setTakeout(takeoutService.getTakeoutById(r.getRefId()));
		}
		if (StringUtils.isNotBlank(r.getAbnormityId())) {
			r.setAbnormityName(abnormityService.getById(r.getAbnormityId()).getCause());
		}
		if (StringUtils.isNotBlank(r.getAbnormityTypeId())) {
			r.setAbnormityTypeName(abnormityTypeService.getById(r.getAbnormityTypeId()).getName());
		}
		if(r.getReceiptStatus()!=null){
			r.setReceiptStatusName(CommomUtil.valueToNameInDict(r.getReceiptStatus(), "receipt_status"));
		}
		Map<String, Object> map = new HashMap();
		modelMap.put("receiptBill", r);
		modelMap.put("type", type);
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		return "stock/receiptBill/editReceiptBill";
	}

	@RequiresPermissions(value={"stock:receiptBill:fristEdit","stock:receiptBill:finalEdit"},logical=Logical.OR)
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody ReceiptBill receiptBill) {
		if (StringUtils.isBlank(receiptBill.getId())) {
			return ResponseEntity.failure("关键信息（不能为空)");
		}
		receiptBillService.updateReceiptBill(receiptBill);
		return ResponseEntity.success("操作成功");
	}
	
	@RequiresPermissions(value={"stock:receiptBill:fristBack","stock:receiptBill:finalBack"},logical=Logical.OR)
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤回回單")
	public ResponseEntity back(String id,Integer status) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("关键信息（不能为空)");
		}
		receiptBillService.changeStatusById(id, status);
		return ResponseEntity.success("操作成功");
	}
}
