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
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.SaleReturn;
import com.lzhpo.stock.entity.SaleReturnDetail;
import com.lzhpo.stock.entity.SaleReturnOperations;
import com.lzhpo.stock.service.ISaleReturnDetailService;
import com.lzhpo.stock.service.ISaleReturnOperationsService;
import com.lzhpo.stock.service.ISaleReturnService;
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
@RequestMapping("/stock/saleReturn")
public class SaleReturnController {
	@Autowired
	private ISaleReturnService saleReturnService;

	@Autowired
	private ISaleReturnDetailService saleReturnDetailService;
	
	@Autowired
	private ISaleReturnOperationsService saleReturnOperationsService;
	
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

	
	@GetMapping(value = "list")
	public String list() {
		return "stock/saleReturn/listSaleReturn";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:saleReturn:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<SaleReturn> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<SaleReturn> saleReturnPageData = new PageData<>();
		QueryWrapper<SaleReturn> saleReturnWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		saleReturnWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				saleReturnWrapper.like("name", keys);
			}
		}
		IPage<SaleReturn> saleReturnPage = saleReturnService.page(new Page<>(page, limit), saleReturnWrapper);
		saleReturnPageData.setCount(saleReturnPage.getTotal());
		saleReturnPageData.setData(setUserToSaleReturn(saleReturnPage.getRecords()));
		return saleReturnPageData;
	}

	// 创建者，和修改人
	private List<SaleReturn> setUserToSaleReturn(List<SaleReturn> saleReturns) {
		saleReturns.forEach(r -> {
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

		return saleReturns;
	}
	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<SaleReturnDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String returnId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<SaleReturnDetail> contretrunDetailData = new PageData<>();
		QueryWrapper<SaleReturnDetail> retrunDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		retrunDetailWrapper.eq("sales_return_id", returnId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				retrunDetailWrapper.like("name", keys);
			}
		}
		retrunDetailWrapper.orderByDesc("create_date ");
		IPage<SaleReturnDetail> returnDetailPage = saleReturnDetailService.page(new Page<>(page, limit),
				retrunDetailWrapper);
		contretrunDetailData.setCount(returnDetailPage.getTotal());
		contretrunDetailData.setData(supplementToDetail(returnDetailPage.getRecords()));
		return contretrunDetailData;
	}

	private List<SaleReturnDetail> supplementToDetail(List<SaleReturnDetail> details) {
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
	public String add(ModelMap modelMap,@RequestParam(value = "continuity", required = false) String continuity) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("continuity", continuity);
		List<Tray> trayList = trayService.selectAll();
		modelMap.put("trayList", trayList);
		return "stock/saleReturn/addSaleReturn";
	}

	@RequiresPermissions("stock:saleReturn:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody SaleReturn saleReturn) {
		String id = saleReturnService.saveSaleReturn(saleReturn).getId();
		ResponseEntity entity = new ResponseEntity();
		boolean flag = SecurityUtils.getSubject().isPermitted("stock:saleReturn:edit");//返回有没有编辑得权限
		entity.setAny("flag", flag);
		entity.setAny("id", id);
		entity.setSuccess(true);
		entity.setMessage("操作成功");
		return entity;
	}

	@RequiresPermissions("stock:saleReturn:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		SaleReturn saleReturn = saleReturnService.getSaleReturnById(id);
		saleReturnService.deleteSaleReturn(saleReturn);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:saleReturn:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<SaleReturn> saleReturns) {
		if (saleReturns == null || saleReturns.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (SaleReturn r : saleReturns) {
			saleReturnService.deleteSaleReturn(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		SaleReturn saleReturn = saleReturnService.getSaleReturnById(id);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Tray> trayList = trayService.selectByClientId(saleReturn.getClientId());
		modelMap.put("trayList", JSONObject.toJSON(trayList));
		List<Depot> depots = depotService.selectByClientId(saleReturn.getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		List<Clientitem> items = clientitemService.selectByClientIdAll(saleReturn.getClientId());
		modelMap.put("items", JSONObject.toJSON(items));
		modelMap.put("saleReturn", saleReturn);
		return "stock/saleReturn/editSaleReturn";
	}

	@RequiresPermissions("stock:saleReturn:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody SaleReturn saleReturn) {
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		if(!modify_status_await.equals(saleReturnService.getById(saleReturn.getId()).getStatus())){
			return ResponseEntity.failure("该单据不在可编辑状态无法编辑");
		}
		try {
			saleReturnService.updateSaleReturn(saleReturn);
		}catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.failure("系统异常请联系管理员");
		}
		
		return ResponseEntity.success("操作成功");
	}
	
	@RequiresPermissions("stock:saleReturn:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		if(!modify_status_revocation.equals(saleReturnService.getById(id).getStatus())){
			return ResponseEntity.failure("该单据不在可撤销状态无法撤销");
		}
		saleReturnService.backSaleReturn(id);
		return ResponseEntity.success("操作成功");
	}

	
	@PostMapping("history")
	@ResponseBody
	@SysLog("历史操作记录数据")
	public List<SaleReturnOperations> history(@RequestParam(value = "returnId", required = false) String returnId) {
		List<SaleReturnOperations> operations = saleReturnOperationsService.selectByReturnId(returnId);
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
