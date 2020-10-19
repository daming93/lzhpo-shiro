package com.lzhpo.finance.controller;

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
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.finance.entity.UserTable;
import com.lzhpo.finance.entity.UserTableDetail;
import com.lzhpo.finance.service.IUserTableDetailService;
import com.lzhpo.finance.service.IUserTableService;

/**
 * <p>
 * 自定义收入表--用户使用（在不同表格中不同体现) 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Controller
@RequestMapping("finance/userTable")
public class UserTableController {
	@Autowired
	private IUserTableService userTableService;

	@Autowired
	private IUserTableDetailService userTableDetailService;
	
	@Autowired
	UserService userService;

	
	@GetMapping(value = "list")
	public String list() {
		return "finance/userTable/listUserTable";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("finance:userTable:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<UserTable> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<UserTable> userTablePageData = new PageData<>();
		QueryWrapper<UserTable> userTableWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		userTableWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				userTableWrapper.like("name", keys);
			}
		}
		IPage<UserTable> userTablePage = userTableService.page(new Page<>(page, limit), userTableWrapper);
		userTablePageData.setCount(userTablePage.getTotal());
		userTablePageData.setData(setUserToUserTable(userTablePage.getRecords()));
		return userTablePageData;
	}

	// 创建者，和修改人
	private List<UserTable> setUserToUserTable(List<UserTable> userTables) {
		userTables.forEach(r -> {
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

		return userTables;
	}
	// 创建者，和修改人
	private List<UserTableDetail> setUserToUserTableDetail(List<UserTableDetail> userTables) {
		userTables.forEach(r -> {
			if(r.getMath()!=null)
			r.setMathStr(CommomUtil.valueToNameInDict(r.getMath(), "income_type"));
		});

		return userTables;
	}
	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<UserTableDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "1000") Integer limit, ServletRequest request,
			String tableId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<UserTableDetail> ContUserTableDetailData = new PageData<>();
		QueryWrapper<UserTableDetail> UserTableDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		UserTableDetailWrapper.eq("table_id", tableId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				UserTableDetailWrapper.like("name", keys);
			}
		}
		UserTableDetailWrapper.orderByDesc("create_date ");
		IPage<UserTableDetail> UserTableDetailPage = userTableDetailService.page(new Page<>(page, limit),
				UserTableDetailWrapper);
		ContUserTableDetailData.setCount(UserTableDetailPage.getTotal());
		ContUserTableDetailData.setData(setUserToUserTableDetail(UserTableDetailPage.getRecords()));
		return ContUserTableDetailData;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<UserTable> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap, String tableId, String userTableId, Integer modular,String code) {
		modelMap.put("tableId", tableId);
		modelMap.put("userTableId", userTableId);
		modelMap.put("modular", modular);
		modelMap.put("modularName", CommomUtil.valueToNameInDict(modular, "modular"));
		modelMap.put("name", code+"得附表");
		
		return "finance/userTable/addUserTable";
	}

	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody UserTable userTable) {
		if (StringUtils.isBlank(userTable.getName())) {
			return ResponseEntity.failure("关键id（不能为空)");
		}
		if (userTableService.getUserTableCount(userTable.getName()) > 0) {
			return ResponseEntity.failure("名称（不能重复)");
		}
		userTableService.saveUserTable(userTable);
		return ResponseEntity.success("操作成功");
	}

	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		UserTable userTable = userTableService.getUserTableById(id);
		userTableService.deleteUserTable(userTable);
		return ResponseEntity.success("操作成功");
	}

	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<UserTable> userTables) {
		if (userTables == null || userTables.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (UserTable r : userTables) {
			userTableService.deleteUserTable(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		UserTable userTable = userTableService.getUserTableById(id);
		/**
		 * 自定义代码
		 */
		Map<String, Object> map = new HashMap();
		modelMap.put("userTable", userTable);

		return "finance/userTable/editUserTable";
	}

	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody UserTable userTable) {
		if (StringUtils.isBlank(userTable.getId())) {
			return ResponseEntity.failure("关键id（不能为空)");
		}
		if (StringUtils.isBlank(userTable.getName())) {
			return ResponseEntity.failure("角色名称不能为空");
		}
		UserTable oldUserTable = userTableService.getUserTableById(userTable.getId());
		if (!oldUserTable.getName().equals(userTable.getName())) {
			if (userTableService.getUserTableCount(userTable.getName()) > 0) {
				return ResponseEntity.failure("名称（不能重复)");
			}
		}
		userTableService.updateUserTable(userTable);
		return ResponseEntity.success("操作成功");
	}
	
	@PostMapping("editDetail")
	@ResponseBody
	@SysLog("保存编辑明细出库数据")
	public ResponseEntity editDetail(@RequestBody UserTableDetail detail) {
		if (StringUtils.isBlank(detail.getId())) {
			return ResponseEntity.failure("id（不能为空)");
		}
		//只能改带确认状态得出库单
		// 待确认
		String tableId = userTableDetailService.getById(detail.getId()).getTableId();
		if(userTableService.getById(tableId).getIsAudit()!=null&&userTableService.getById(tableId).getIsAudit()==1){
			return ResponseEntity.failure("该单据已审核无法修改");
		}
		try {
			userTableDetailService.updateById(detail);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("操作失败");
		}
		return ResponseEntity.success("操作成功");
	}
	
	@RequiresPermissions("finance:userTable:audit")
	@PostMapping("audit")
	@ResponseBody
	@SysLog("审核数据")
	public ResponseEntity audit(@RequestParam(value = "id", required = false) String id,Integer status) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		userTableService.ChangeAduitStatus(status, id);
		return ResponseEntity.success("操作成功");
	}
}
