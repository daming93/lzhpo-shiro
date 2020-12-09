package com.lzhpo.stock.controller;

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
import com.lzhpo.customer.service.IAbnormityTypeService;
import com.lzhpo.deliver.entity.WayBill;
import com.lzhpo.deliver.service.IWayBillService;
import com.lzhpo.stock.entity.HandleAbnormity;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.IHandleAbnormityService;
import com.lzhpo.stock.service.ITakeoutService;

/**
 * <p>
 * 异常处理表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-12-02
 */
@Controller
@RequestMapping("stock/handleAbnormity")
public class HandleAbnormityController {
	@Autowired
	private IHandleAbnormityService handleAbnormityService;

	@Autowired
	UserService userService;

	@Autowired
	private IWayBillService wayBillService;

	@Autowired
	private ITakeoutService takeoutService;

	@Autowired
	private IAbnormityTypeService abnormityTypeService;

	@GetMapping(value = "list")
	public String list() {
		return "stock/handleAbnormity/listHandleAbnormity";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:handleAbnormity:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<HandleAbnormity> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<HandleAbnormity> handleAbnormityPageData = new PageData<>();
		QueryWrapper<HandleAbnormity> handleAbnormityWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		handleAbnormityWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				handleAbnormityWrapper.like("name", keys);
			}
		}
		IPage<HandleAbnormity> handleAbnormityPage = handleAbnormityService.page(new Page<>(page, limit),
				handleAbnormityWrapper);
		handleAbnormityPageData.setCount(handleAbnormityPage.getTotal());
		handleAbnormityPageData.setData(setUserToHandleAbnormity(handleAbnormityPage.getRecords()));
		return handleAbnormityPageData;
	}

	// 创建者，和修改人
	private List<HandleAbnormity> setUserToHandleAbnormity(List<HandleAbnormity> handleAbnormitys) {
		handleAbnormitys.forEach(r -> {
			if(StringUtils.isNotBlank(r.getTakeoutId())){
				r.setTakeout(takeoutService.getTakeoutById(r.getTakeoutId()));
			}
			if(StringUtils.isNotBlank(r.getWayBillId())){
				r.setWayBill(wayBillService.getById(r.getWayBillId()));
			}
		});

		return handleAbnormitys;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<HandleAbnormity> getById(@RequestParam("pkid") String
	// pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap, String takeoutId, String wayBillId) {
		// 传过来要有两个信息，一个是出库单号 一个是路单号
		// id也同步传过来,用id去找数据
		Takeout takeout = takeoutService.getById(takeoutId);
		WayBill wayBill = wayBillService.getById(wayBillId);
		modelMap.put("takeout", takeout);
		modelMap.put("wayBill", wayBill);
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		return "stock/handleAbnormity/addHandleAbnormity";
	}

	@RequiresPermissions("stock:handleAbnormity:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody HandleAbnormity handleAbnormity) {
		handleAbnormityService.saveHandleAbnormity(handleAbnormity);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:handleAbnormity:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		HandleAbnormity handleAbnormity = handleAbnormityService.getHandleAbnormityById(id);
		handleAbnormityService.deleteHandleAbnormity(handleAbnormity);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:handleAbnormity:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<HandleAbnormity> handleAbnormitys) {
		if (handleAbnormitys == null || handleAbnormitys.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (HandleAbnormity r : handleAbnormitys) {
			handleAbnormityService.deleteHandleAbnormity(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		HandleAbnormity handleAbnormity = handleAbnormityService.getHandleAbnormityById(id);
		Takeout takeout = takeoutService.getById(handleAbnormity.getTakeoutId());
		WayBill wayBill = wayBillService.getById(handleAbnormity.getWayBillId());
		modelMap.put("takeout", takeout);
		modelMap.put("wayBill", wayBill);
		modelMap.put("abnormitytypeList", abnormityTypeService.selectAll());
		Map<String, Object> map = new HashMap();
		modelMap.put("handleAbnormity", handleAbnormity);

		return "stock/handleAbnormity/editHandleAbnormity";
	}

	@RequiresPermissions("stock:handleAbnormity:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody HandleAbnormity handleAbnormity) {
		if (StringUtils.isBlank(handleAbnormity.getId())) {
			return ResponseEntity.failure("必要信息（不能为空)");
		}
		handleAbnormityService.updateHandleAbnormity(handleAbnormity);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:handleAbnormity:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("退回审核数据")
	public ResponseEntity back(String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("必要信息（不能为空)");
		}
		handleAbnormityService.backAudit(id);
		return ResponseEntity.success("操作成功");
	}
}
