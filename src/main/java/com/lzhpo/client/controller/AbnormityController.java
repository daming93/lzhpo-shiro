package com.lzhpo.client.controller;

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
import com.lzhpo.client.entity.Abnormity;
import com.lzhpo.client.service.IAbnormityService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.sys.entity.Dictionary;

/**
 * <p>
 * 异常表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Controller
@RequestMapping("client/abnormity")
public class AbnormityController {
	@Autowired
	private IAbnormityService abnormityService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "client/abnormity/listAbnormity";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("client:abnormity:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Abnormity> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Abnormity> abnormityPageData = new PageData<>();
		QueryWrapper<Abnormity> abnormityWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		abnormityWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("cause");
			if (StringUtils.isNotBlank(keys)) {
				abnormityWrapper.like("cause", keys);
			}
		}
		IPage<Abnormity> abnormityPage = abnormityService.page(new Page<>(page, limit), abnormityWrapper);
		abnormityPageData.setCount(abnormityPage.getTotal());
		abnormityPageData.setData(setUserToAbnormity(abnormityPage.getRecords()));
		return abnormityPageData;
	}

	// 创建者，和修改人
	private List<Abnormity> setUserToAbnormity(List<Abnormity> abnormitys) {
		abnormitys.forEach(r -> {
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
			if (r.getAbnormityType() != null) {
				r.setAbnormityTypeStr(CommomUtil.valueToNameInDict(r.getAbnormityType(), "abnormity_type"));
			}
		});

		return abnormitys;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Abnormity> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		List<Dictionary> abnormityList = CacheUtils.allDicts.get("abnormity_type");
		modelMap.addAttribute("abnormityList", abnormityList);
		return "client/abnormity/addAbnormity";
	}

	@RequiresPermissions("client:abnormity:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Abnormity abnormity) {
		if (StringUtils.isBlank(abnormity.getCause())) {
			return ResponseEntity.failure("异常原因（不能为空)");
		}
		if (abnormityService.getAbnormityCount(abnormity.getCause()) > 0) {
			return ResponseEntity.failure("异常原因（不能重复)");
		}
		abnormityService.saveAbnormity(abnormity);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:abnormity:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		Abnormity abnormity = abnormityService.getAbnormityById(id);
		abnormityService.deleteAbnormity(abnormity);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:abnormity:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Abnormity> abnormitys) {
		if (abnormitys == null || abnormitys.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (Abnormity r : abnormitys) {
			abnormityService.deleteAbnormity(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Abnormity abnormity = abnormityService.getAbnormityById(id);
		List<Dictionary> abnormityList = CacheUtils.allDicts.get("abnormity_type");
		modelMap.addAttribute("abnormityList", abnormityList);
		Map<String, Object> map = new HashMap();
		modelMap.put("abnormity", abnormity);

		return "client/abnormity/editAbnormity";
	}

	@RequiresPermissions("client:abnormity:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Abnormity abnormity) {
		if (StringUtils.isBlank(abnormity.getId())) {
			return ResponseEntity.failure("ID（不能为空)");
		}
		if (StringUtils.isBlank(abnormity.getCause())) {
			return ResponseEntity.failure("异常原因不能为空");
		}
		Abnormity oldAbnormity = abnormityService.getAbnormityById(abnormity.getId());
		if (!oldAbnormity.getCause().equals(abnormity.getCause())) {
			if (abnormityService.getAbnormityCount(abnormity.getCause()) > 0) {
				return ResponseEntity.failure("异常原因（不能重复)");
			}
		}
		abnormityService.updateAbnormity(abnormity);
		return ResponseEntity.success("操作成功");
	}
	
	@GetMapping("selectAbnorityByType")
	@ResponseBody
	@SysLog("保存编辑数据")
	public List<Abnormity> selectAbnorityByType(@RequestParam(value = "type", required = true)Integer type){
		return abnormityService.selectAbnorityByType(type);
	};
}
