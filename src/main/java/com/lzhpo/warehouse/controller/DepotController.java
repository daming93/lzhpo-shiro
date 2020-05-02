package com.lzhpo.warehouse.controller;

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
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.service.IDepotService;

/**
 * <p>
 * 储位表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
@Controller
@RequestMapping("/warehouse/depot")
public class DepotController {

	@Autowired
	private IBasicdataService basicdateService;
	@Autowired
	private IDepotService depotService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "warehouse/depot/listDepot";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("warehouse:depot:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Depot> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Depot> depotPageData = new PageData<>();
		QueryWrapper<Depot> depotWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		depotWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				depotWrapper.like("name", keys);
			}
		}
		IPage<Depot> depotPage = depotService.page(new Page<>(page, limit), depotWrapper);
		depotPageData.setCount(depotPage.getTotal());
		depotPageData.setData(setUserToDepot(depotPage.getRecords()));
		return depotPageData;
	}

	// 创建者，和修改人
	private List<Depot> setUserToDepot(List<Depot> depots) {
		depots.forEach(r -> {
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

		return depots;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Depot> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		/**
		 * 自定义传入add页面的数据
		 */
		modelMap.put("basicDatas", com.alibaba.fastjson.JSONObject.toJSON(basicDatas));
		return "warehouse/depot/addDepot";
	}

	@RequiresPermissions("warehouse:depot:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Depot depot) {
		// if(StringUtils.isBlank(depot.getName())){
		// return ResponseEntity.failure("修改提示信息（不能为空)");
		// }
		if (depotService.getDepotCount(depot) > 0) {
			return ResponseEntity.failure("不能重复");
		}
		depotService.saveDepot(depot);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("warehouse:depot:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("储位ID不能为空");
		}
		Depot depot = depotService.getDepotById(id);
		depotService.deleteDepot(depot);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("warehouse:depot:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Depot> depots) {
		if (depots == null || depots.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的储位");
		}
		for (Depot r : depots) {
			depotService.deleteDepot(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Depot depot = depotService.getDepotById(id);
		/**
		 * 自定义代码
		 */
		Map<String, Object> map = new HashMap();
		modelMap.put("depot", depot);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", com.alibaba.fastjson.JSONObject.toJSON(basicDatas));
		return "warehouse/depot/editDepot";
	}

	@RequiresPermissions("warehouse:depot:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Depot depot) {
		if (depotService.getDepotCount(depot) > 0) {
			return ResponseEntity.failure("编号不能重复");
		}
		depotService.updateDepot(depot);
		return ResponseEntity.success("操作成功");
	}
}
