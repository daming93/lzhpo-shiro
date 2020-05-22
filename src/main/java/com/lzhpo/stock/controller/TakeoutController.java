package com.lzhpo.stock.controller;

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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.TakeoutOperations;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.entity.TakeoutDetail;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.ITakeoutDetailService;
import com.lzhpo.stock.service.ITakeoutOperationsService;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.service.IDepotService;
import com.lzhpo.warehouse.service.ITrayService;

/**
 * <p>
 * 出库表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
@Controller
@RequestMapping("/stock/takeout")
public class TakeoutController {
	@Autowired
	private ITakeoutService takeoutService;

	@Autowired
	private ITakeoutOperationsService takeoutOperationsService;

	@Autowired
	private ITakeoutDetailService takeoutDetailService;

	@Autowired
	UserService userService;

	@Autowired
	private IBasicdataService basicdateService;

	@Autowired
	private ITrayService trayService;

	@Autowired
	private IDepotService depotService;
	
	@Autowired
	private IClientitemService clientitemService;

	@Autowired
	private IMaterialService materialSrivice;
	
	@GetMapping(value = "list")
	public String list() {
		return "stock/takeout/listTakeout";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:takeout:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Takeout> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Takeout> takeoutPageData = new PageData<>();
		QueryWrapper<Takeout> takeoutWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		takeoutWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				takeoutWrapper.like("name", keys);
			}
		}
		IPage<Takeout> takeoutPage = takeoutService.page(new Page<>(page, limit), takeoutWrapper);
		takeoutPageData.setCount(takeoutPage.getTotal());
		takeoutPageData.setData(setUserToTakeout(takeoutPage.getRecords()));
		return takeoutPageData;
	}

	// 创建者，和修改人
	private List<Takeout> setUserToTakeout(List<Takeout> takeouts) {
		takeouts.forEach(r -> {
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
			if (StringUtils.isNotBlank(r.getClientId())) {
				r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
			}
			if (r.getStatus() != null) {
				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
			}
		});

		return takeouts;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Takeout> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Tray> trayList = trayService.selectAll();
		modelMap.put("trayList", trayList);
		return "stock/takeout/addTakeout";
	}

	@RequiresPermissions("stock:takeout:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Takeout takeout) {
		takeoutService.saveTakeout(takeout);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:takeout:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		Takeout takeout = takeoutService.getTakeoutById(id);
		takeoutService.deleteTakeout(takeout);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:takeout:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Takeout> takeouts) {
		if (takeouts == null || takeouts.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (Takeout r : takeouts) {
			takeoutService.deleteTakeout(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Takeout takeout = takeoutService.getTakeoutById(id);
		modelMap.put("takeout", takeout);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Tray> trayList = trayService.selectByClientId(takeout.getClientId());
		modelMap.put("trayList", JSONObject.toJSON(trayList));
		List<Depot> depots = depotService.selectByClientId(takeout.getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		List<Clientitem> items = clientitemService.selectByClientId(takeout.getClientId());
		modelMap.put("items", JSONObject.toJSON(items));
		return "stock/takeout/editTakeout";
	}

	@RequiresPermissions("stock:takeout:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Takeout takeout) {
		if (StringUtils.isBlank(takeout.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		takeoutService.updateTakeout(takeout);
		return ResponseEntity.success("操作成功");
	}

	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<TakeoutDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String takeoutId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<TakeoutDetail> ConttakeoutDetailData = new PageData<>();
		QueryWrapper<TakeoutDetail> takeoutDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		takeoutDetailWrapper.eq("takeout_id", takeoutId);
		takeoutDetailWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				takeoutDetailWrapper.like("name", keys);
			}
		}
		takeoutDetailWrapper.orderByDesc("create_date ");
		IPage<TakeoutDetail> takeoutDetailPage = takeoutDetailService.page(new Page<>(page, limit),
				takeoutDetailWrapper);
		ConttakeoutDetailData.setCount(takeoutDetailPage.getTotal());
		ConttakeoutDetailData.setData(supplementToDetail(takeoutDetailPage.getRecords()));
		return ConttakeoutDetailData;
	}

	private List<TakeoutDetail> supplementToDetail(List<TakeoutDetail> details) {
		details.forEach(r -> {
			if (StringUtils.isNotBlank(r.getItemId())) {// 品项
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setRate(item.getUnitRate()+"");
				r.setItemName(item.getName());
				r.setNumZ(r.getNumber() / item.getUnitRate() + "." + r.getNumber() % item.getUnitRate());
			}
			if(StringUtils.isNotBlank(r.getMaterial())){
				Material material = materialSrivice.getById(r.getMaterial());
				r.setMaxNumber(material.getAvailableNum());
			}
		});
		return details;
	}
	
	@RequiresPermissions("stock:takeout:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		takeoutService.backTakeout(id);
		return ResponseEntity.success("操作成功");
	}

	@PostMapping("history")
	@ResponseBody
	@SysLog("历史操作记录数据")
	public List<TakeoutOperations> history(@RequestParam(value = "takeoutId", required = false) String takeoutId) {
		List<TakeoutOperations> operations = takeoutOperationsService.selectByTakeoutId(takeoutId);
		operations.forEach(o -> {
			if (StringUtils.isNotBlank(o.getCreateId())) {
				User u = userService.findUserById(o.getCreateId());
				if (StringUtils.isBlank(u.getNickName())) {
					u.setNickName(u.getLoginName());
				}
				o.setCreateUser(u);
			}
			if (StringUtils.isNotBlank(o.getUpdateId())) {
				User u = userService.findUserById(o.getUpdateId());
				if (StringUtils.isBlank(u.getNickName())) {
					u.setNickName(u.getLoginName());
				}
				o.setUpdateUser(u);
			}
			if (StringUtils.isNotBlank(o.getType() + "")) {
				o.setTypeStr(CommomUtil.valueToNameInDict(o.getType(), "stock_type"));
			}
		});
		return operations;
	}
	@PostMapping("editDetail")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity editDetail(@RequestBody TakeoutDetail detail) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		try {
			takeoutDetailService.updateNumber(detail,takeoutService.getById(detail.getTakeoutId()).getCode());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("操作失败");
		}
		return ResponseEntity.success("操作成功");
	}
	@PostMapping("deleteDetail")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity deleteDetail(@RequestBody TakeoutDetail detail) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		takeoutDetailService.deleteTakeoutDetail(detail,takeoutService.getById(detail.getTakeoutId()).getCode());
		return ResponseEntity.success("操作成功");
	}
}
