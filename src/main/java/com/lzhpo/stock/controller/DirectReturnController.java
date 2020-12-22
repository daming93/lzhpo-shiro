package com.lzhpo.stock.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
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
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.admin.entity.User;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.DirectReturn;
import com.lzhpo.stock.entity.DirectReturnDetail;
import com.lzhpo.stock.entity.DirectReturnOperations;
import com.lzhpo.stock.entity.HandleAbnormity;
import com.lzhpo.stock.service.IDirectReturnDetailService;
import com.lzhpo.stock.service.IDirectReturnOperationsService;
import com.lzhpo.stock.service.IDirectReturnService;
import com.lzhpo.stock.service.IHandleAbnormityService;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.service.IDepotService;
import com.lzhpo.warehouse.service.ITrayService;

/**
 * <p>
 * 退货表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Controller
@RequestMapping("/stock/directReturn")
public class DirectReturnController {
	@Autowired
	private IDirectReturnService directReturnService;

	@Autowired
	private IDirectReturnDetailService directReturnDetailService;
	
	@Autowired
	private IDirectReturnOperationsService directReturnOperationsService;
	
	@Autowired
	private IClientitemService clientitemService;
	
	@Autowired
	UserService userService;

	@Autowired
	private IBasicdataService basicdateService;

	@Autowired
	private ITrayService trayService;

	@Autowired
	private IDepotService depotService;

	@Autowired
	private ITakeoutService takeoutService;

	
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IHandleAbnormityService handleAbnormityService;
	@GetMapping(value = "list")
	public String list() {
		return "stock/directReturn/listDirectReturn";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:directReturn:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<DirectReturn> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DirectReturn> directReturnPageData = new PageData<>();
		QueryWrapper<DirectReturn> directReturnWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		directReturnWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				directReturnWrapper.like("name", keys);
			}
		}
		IPage<DirectReturn> directReturnPage = directReturnService.page(new Page<>(page, limit), directReturnWrapper);
		directReturnPageData.setCount(directReturnPage.getTotal());
		directReturnPageData.setData(setUserToDirectReturn(directReturnPage.getRecords()));
		return directReturnPageData;
	}

	// 创建者，和修改人
	private List<DirectReturn> setUserToDirectReturn(List<DirectReturn> directReturns) {
		directReturns.forEach(r -> {
//			if (StringUtils.isNotBlank(r.getCreateId())) {
//				User u = userService.findUserById(r.getCreateId());
//				if (StringUtils.isBlank(u.getNickName())) {
//					u.setNickName(u.getLoginName());
//				}
//				r.setCreateUser(u);
//			}
//			if (StringUtils.isNotBlank(r.getUpdateId())) {
//				User u = userService.findUserById(r.getUpdateId());
//				if (StringUtils.isBlank(u.getNickName())) {
//					u.setNickName(u.getLoginName());
//				}
//				r.setUpdateUser(u);
//			}
			if (StringUtils.isNotBlank(r.getClientId())) {
				r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
			}
			if (r.getStatus() != null) {
				r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
			}
		});

		return directReturns;
	}
	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<DirectReturnDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String returnId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DirectReturnDetail> contretrunDetailData = new PageData<>();
		QueryWrapper<DirectReturnDetail> retrunDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		retrunDetailWrapper.eq("direct_return_id", returnId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				retrunDetailWrapper.like("name", keys);
			}
		}
		retrunDetailWrapper.orderByDesc("create_date ");
		IPage<DirectReturnDetail> returnDetailPage = directReturnDetailService.page(new Page<>(page, limit),
				retrunDetailWrapper);
		contretrunDetailData.setCount(returnDetailPage.getTotal());
		contretrunDetailData.setData(supplementToDetail(returnDetailPage.getRecords()));
		return contretrunDetailData;
	}

	private List<DirectReturnDetail> supplementToDetail(List<DirectReturnDetail> details) {
		details.forEach(r -> {
			if (StringUtils.isNotBlank(r.getItemId())) {// 品项
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setRate(item.getUnitRate()+"");
				r.setItemName(item.getName());
				r.setNumZ(r.getNumber() / item.getUnitRate() + "." + r.getNumber() % item.getUnitRate());
			}
		});
		return details;
	}

	@GetMapping("add")
	public String add(ModelMap modelMap,String takeoutId,String type,String handAbnormityId) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("takeout", takeoutService.getById(takeoutId));
		modelMap.put("type", type);
		modelMap.put("handAbnormityId", handAbnormityId);
		List<Tray> trayList = trayService.selectAll();
		List<Depot> depots = depotService.selectByClientId(takeoutService.getById(takeoutId).getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		modelMap.put("trayList", trayList);
		
		List<Address> addressList = addressService.selectAll();
		modelMap.put("addressList", addressList);
		
		return "stock/directReturn/addDirectReturn";
	}

	@RequiresPermissions("stock:directReturn:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody DirectReturn directReturn) {
		ResponseEntity entity = new ResponseEntity();
		boolean flag = SecurityUtils.getSubject().isPermitted("stock:directReturn:edit");//返回有没有编辑得权限
		entity.setAny("flag", flag);
		//这里先检测下由来的异常单据状态
		Integer handle_abnormity_status_wait = CacheUtils.keyDict.get("handle_abnormity_status_wait").getValue();
		HandleAbnormity handleAbnormity = handleAbnormityService.getById(directReturn.getHandleAbnormityId());
		if(handleAbnormity!=null&&handleAbnormity.getStatus()!=null&&handleAbnormity.getStatus().equals(handle_abnormity_status_wait)){
			//只有带选择的单据才可以进行编辑
		}else{
			entity.setSuccess(false);
			entity.setMessage("该单据已经操作过");
			return entity;
		}
		String id = directReturnService.saveDirectReturn(directReturn).getId();
		Integer handle_abnormity_status_return = CacheUtils.keyDict.get("handle_abnormity_status_return").getValue();
		handleAbnormity.setStatus(handle_abnormity_status_return);
		handleAbnormityService.updateById(handleAbnormity);
		entity.setAny("id", id);
		entity.setSuccess(true);
		entity.setMessage("操作成功");
		return entity;
	}

	@RequiresPermissions("stock:directReturn:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		DirectReturn directReturn = directReturnService.getDirectReturnById(id);
		directReturnService.deleteDirectReturn(directReturn);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:directReturn:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<DirectReturn> directReturns) {
		if (directReturns == null || directReturns.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (DirectReturn r : directReturns) {
			directReturnService.deleteDirectReturn(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		DirectReturn directReturn = directReturnService.getDirectReturnById(id);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Address> addressList = addressService.selectAll();
		modelMap.put("addressList", addressList);
		List<Depot> depots = depotService.selectByClientId(directReturn.getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		List<Clientitem> items = clientitemService.selectByClientIdAll(directReturn.getClientId());
		modelMap.put("items", JSONObject.toJSON(items));
		modelMap.put("directReturn", directReturn);
		return "stock/directReturn/editDirectReturn";
	}

	@RequiresPermissions("stock:directReturn:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody DirectReturn directReturn) {
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		if(!modify_status_await.equals(directReturnService.getById(directReturn.getId()).getStatus())){
			return ResponseEntity.failure("该单据不在可编辑状态无法编辑");
		}
		try {
			directReturnService.updateDirectReturn(directReturn);
		}catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("系统异常请联系管理员");
		}
		
		return ResponseEntity.success("操作成功");
	}
	
	@RequiresPermissions("stock:directReturn:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		if(!modify_status_revocation.equals(directReturnService.getById(id).getStatus())){
			return ResponseEntity.failure("该单据不在可撤销状态无法撤销");
		}
		directReturnService.backDirectReturn(id);
		return ResponseEntity.success("操作成功");
	}

	
	@PostMapping("history")
	@ResponseBody
	@SysLog("历史操作记录数据")
	public List<DirectReturnOperations> history(@RequestParam(value = "returnId", required = false) String returnId) {
		List<DirectReturnOperations> operations = directReturnOperationsService.selectByReturnId(returnId);
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
	


}
