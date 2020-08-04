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
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.Storage;
import com.lzhpo.stock.entity.StorageDetail;
import com.lzhpo.stock.entity.StorageOperations;
import com.lzhpo.stock.service.IStorageDetailService;
import com.lzhpo.stock.service.IStorageOperationsService;
import com.lzhpo.stock.service.IStorageService;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.entity.Tray;
import com.lzhpo.warehouse.service.IDepotService;
import com.lzhpo.warehouse.service.ITrayService;

/**
 * <p>
 * 入库主表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Controller
@RequestMapping("/stock/storage")
public class StorageController {
	@Autowired
	private IStorageService storageService;

	@Autowired
	private IStorageOperationsService storageOperationsService;

	@Autowired
	private IStorageDetailService storageDetailService;

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

	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		return "stock/storage/listStorage";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:storage:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Storage> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Storage> storagePageData = new PageData<>();
		QueryWrapper<Storage> storageWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		storageWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {//检索项
			String clientCode = (String) map.get("clientCode");
			if (StringUtils.isNotBlank(clientCode)) {
				storageWrapper.like("client_code", clientCode);
			}
			String code = (String) map.get("code");
			if (StringUtils.isNotBlank(code)) {
				storageWrapper.like("code", code);
			}
			String clientId = (String) map.get("clientId");
			if (StringUtils.isNotBlank(clientId)) {
				storageWrapper.eq("client_id", clientId);
			}
			String startTime =(String) map.get("startTime");
			String overTime =(String) map.get("overTime");
			if (StringUtils.isNotBlank(startTime)) {
				storageWrapper.ge("storage_time", startTime);
			}
			if (StringUtils.isNotBlank(overTime)) {
				storageWrapper.le("storage_time", overTime);
			}
		}
		storageWrapper.orderByAsc("status");
		storageWrapper.orderByDesc("code");
		IPage<Storage> storagePage = storageService.page(new Page<>(page, limit), storageWrapper);
		storagePageData.setCount(storagePage.getTotal());
		storagePageData.setData(setUserToStorage(storagePage.getRecords()));
		return storagePageData;
	}

	// 创建者，和修改人
	private List<Storage> setUserToStorage(List<Storage> storages) {
		storages.forEach(r -> {
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

		return storages;
	}

	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<StorageDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request,
			String storageId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<StorageDetail> ContStorageDetailData = new PageData<>();
		QueryWrapper<StorageDetail> StorageDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		StorageDetailWrapper.eq("storage_id", storageId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				StorageDetailWrapper.like("name", keys);
			}
		}
		StorageDetailWrapper.orderByDesc("create_date ");
		IPage<StorageDetail> StorageDetailPage = storageDetailService.page(new Page<>(page, limit),
				StorageDetailWrapper);
		ContStorageDetailData.setCount(StorageDetailPage.getTotal());
		ContStorageDetailData.setData(supplementToDetail(StorageDetailPage.getRecords()));
		return ContStorageDetailData;
	}

	private List<StorageDetail> supplementToDetail(List<StorageDetail> details) {
		details.forEach(r -> {
			if (StringUtils.isNotBlank(r.getItemId())) {// 品项
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setRate("1:" + item.getUnitRate());
				r.setItemName(item.getName());
				r.setNumZ(r.getNumber() / item.getUnitRate() + "." + r.getNumber() % item.getUnitRate());
			}
		});
		return details;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Storage> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap,@RequestParam(value = "continuity", required = false) String continuity) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("continuity", continuity);
		List<Tray> trayList = trayService.selectAll();
		modelMap.put("trayList", trayList);
		return "stock/storage/addStorage";
	}

	@RequiresPermissions("stock:storage:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Storage storage) {
		// if(StringUtils.isBlank(storage.getName())){
		// return ResponseEntity.failure("修改提示信息（不能为空)");
		// }
		// if(storageService.getStorageCount(storage.getName())>0){
		// return ResponseEntity.failure("修改提示信息（不能重复)");
		// }
		ResponseEntity entity = new ResponseEntity();
		String id = storageService.saveStorage(storage).getId();
		boolean flag = SecurityUtils.getSubject().isPermitted("stock:storage:edit");//返回有没有编辑得权限
		entity.setAny("flag", flag);
		entity.setAny("id", id);
		entity.setSuccess(true);
		entity.setMessage("操作成功");
		return entity;
	}

	@RequiresPermissions("stock:storage:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		Storage storage = storageService.getStorageById(id);
		storageService.deleteStorage(storage);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:storage:back")
	@PostMapping("back")
	@ResponseBody
	@SysLog("撤销数据")
	public ResponseEntity back(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("单据ID不能为空");
		}
		storageService.backStorage(id);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("stock:storage:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Storage> storages) {
		if (storages == null || storages.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的单据");
		}
		for (Storage r : storages) {
			storageService.deleteStorage(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Storage storage = storageService.getStorageById(id);
		modelMap.put("storage", storage);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		List<Tray> trayList = trayService.selectByClientId(storage.getClientId());
		modelMap.put("trayList", JSONObject.toJSON(trayList));
		List<Depot> depots = depotService.selectByClientId(storage.getClientId());
		modelMap.put("depots", JSONObject.toJSON(depots));
		List<Clientitem> items = clientitemService.selectByClientIdAll(storage.getClientId());
		modelMap.put("items", JSONObject.toJSON(items));
		return "stock/storage/editStorage";
	}

	@RequiresPermissions("stock:storage:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Storage storage) {
		// if(StringUtils.isBlank(storage.getId())){
		// return ResponseEntity.failure("修改提示信息（不能为空)");
		// }
		// if(StringUtils.isBlank(storage.getName())){
		// return ResponseEntity.failure("单据名称不能为空");
		// }
		// Storage oldStorage = storageService.getStorageById(storage.getId());
		// if(!oldStorage.getName().equals(storage.getName())){
		// if( storageService.getStorageCount(storage.getName())>0){
		// return ResponseEntity.failure("修改提示信息（不能重复)");
		// }
		// }
		try {
			storageService.updateStorage(storage);
		}catch (RuntimeJsonMappingException e) {
			return ResponseEntity.failure(e.getMessage());
		}catch (Exception e) {
			return ResponseEntity.failure("系统异常请联系管理员");
		}
		
		return ResponseEntity.success("操作成功");
	}

	@PostMapping("history")
	@ResponseBody
	@SysLog("历史操作记录数据")
	public List<StorageOperations> history(@RequestParam(value = "storageId", required = false) String storageId) {
		List<StorageOperations> operations = storageOperationsService.selectByStorageId(storageId);
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
