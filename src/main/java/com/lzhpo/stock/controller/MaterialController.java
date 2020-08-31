package com.lzhpo.stock.controller;

import java.util.ArrayList;
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
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;

/**
 * <p>
 * 仓储明细表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Controller
@RequestMapping("/stock/material")
public class MaterialController {
	@Autowired
	private IMaterialService materialService;

	@Autowired
	UserService userService;

	@Autowired
	IClientitemService clientitemService;
	
	@Autowired
	private IMaterialOperationsService materialOperationsService;
	
	@Autowired
	private IMaterialDepotService materialDepotService;

	@Autowired
	private IBasicdataService basicdateService;
	
	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		return "stock/material/listMaterial";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("stock:material:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Material> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Material> materialPageData = new PageData<>();
		// 相当于del_flag = 0;
		String itemCode = null;
		String startTime =null;
		String overTime =null;
		String continuity =null;
		String clientId = null;
		String status =null;
		String batch = null;
		Integer mode = 1;//正常模式
		if (!map.isEmpty()) {
			itemCode = (String) map.get("itemCode");
			mode = Integer.valueOf((String)map.get("mode"));
			continuity = (String) map.get("continuity");
			clientId =  (String) map.get("clientId");
			status =  (String) map.get("status");
			batch =  (String) map.get("batch");
		}
		Map<String,Object> mapRes = materialService.selectMaterial(itemCode, startTime, overTime,  (page-1)*limit, limit, mode,continuity,clientId,batch,status);
		materialPageData.setCount((Long) mapRes.get("count"));
		materialPageData.setData(setUserToMaterial((List<Material>) mapRes.get("list"),mode));
		return materialPageData;
	}

	// 创建者，和修改人
	private List<Material> setUserToMaterial(List<Material> materials,Integer mode) {
		materials.forEach(r -> {
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
			if (StringUtils.isNotBlank(r.getItemId())) {// 品项
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setRate(item.getUnitRate() + "");
				r.setItemName(item.getName());
				r.setNumZ(r.getAvailableNum() / item.getUnitRate() + "." + r.getAvailableNum() % item.getUnitRate());
				r.setSystemCode(item.getCode());
				r.setClientName(basicdateService.getById(item.getClientId()).getClientShortName());
			}
			
			if (StringUtils.isNotBlank(r.getType()+"")) {
				r.setTypeStr(CommomUtil.valueToNameInDict(r.getType(), "material_type"));
			}
			if(mode.equals(3)||mode.equals(4)){
				r.setTypeStr("合计");
			}
		});

		return materials;
	}
	
	@GetMapping(value = "listTrunover")
	public String listTrunover() {
		return "stock/material/listTrunover";
	}

	/**
	 * 查询分页数据
	 */
	@SuppressWarnings("unchecked")
	@RequiresPermissions("stock:material:listTrunover")
	@PostMapping("listTrunover")
	@ResponseBody
	public PageData<MaterialOperations> listTrunover(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<MaterialOperations> opertionPageData = new PageData<>();
		QueryWrapper<MaterialOperations> opertionWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		opertionWrapper.eq("del_flag", false);
		String itemCode = null;
		String startTime = null;
		String overTime =null;
		if (!map.isEmpty()) {
			itemCode = (String) map.get("itemCode");
			startTime =(String) map.get("startTime");
			overTime =(String) map.get("overTime");
			
			if (StringUtils.isNotBlank(itemCode)) {
				List<Clientitem> items = clientitemService.selectByItemCode(itemCode);
				List<String> str =  new ArrayList<>();
				for (Clientitem clientitem : items) {
					str.add(clientitem.getId());
				}
				opertionWrapper.in("item_id", str);
			}
			Integer mode = Integer.valueOf((String)map.get("mode"));
			Map<String,Object> mapRes = materialOperationsService.selectOperations(itemCode, startTime, overTime, (page-1)*limit, limit, mode);
			opertionPageData.setCount((Long) mapRes.get("count"));
			if(mode.equals(3)){
				opertionPageData.setData(setUserToMaterialOperations((List<MaterialOperations>) mapRes.get("list"),true));
			}else{
				opertionPageData.setData(setUserToMaterialOperations((List<MaterialOperations>) mapRes.get("list"),false));
			}
		}else{//默认是忽略详情
			Map<String,Object> mapRes = materialOperationsService.selectOperations(itemCode, startTime, overTime,  (page-1)*limit, limit, 2);
			opertionPageData.setCount((Long) mapRes.get("count"));
			opertionPageData.setData(setUserToMaterialOperations((List<MaterialOperations>) mapRes.get("list"),false));
		}
	
	
		return opertionPageData;
	}
	
	// 创建者，和修改人
		private List<MaterialOperations> setUserToMaterialOperations(List<MaterialOperations> materialOperations,boolean batchFlag) {
			materialOperations.forEach(r -> {
//				if (StringUtils.isNotBlank(r.getCreateId())) {
//					User u = userService.findUserById(r.getCreateId());
//					if (StringUtils.isBlank(u.getNickName())) {
//						u.setNickName(u.getLoginName());
//					}
//					r.setCreateUser(u);
//				}
//				if (StringUtils.isNotBlank(r.getUpdateId())) {
//					User u = userService.findUserById(r.getUpdateId());
//					if (StringUtils.isBlank(u.getNickName())) {
//						u.setNickName(u.getLoginName());
//					}
//					r.setUpdateUser(u);
//				}
				if (r.getType()!=null) {
					if(r.getType()==2){
						r.setNumber(0-r.getNumber());
					}
				}
				if (StringUtils.isNotBlank(r.getMaterialId())) {// 品项
					Material material = materialService.getById(r.getMaterialId());
					Clientitem item = clientitemService.getById(material.getItemId());
					r.setRate(item.getUnitRate() + "");
					r.setItemName(item.getName());
					r.setClientName(basicdateService.getById(item.getClientId()).getClientShortName());
					if(r.getNumber() >=0){
						r.setNumZ(r.getNumber() / item.getUnitRate() + "." + r.getNumber() % item.getUnitRate());
					}else{
						Integer tempNum = -r.getNumber();
						r.setNumZ("-"+tempNum / item.getUnitRate() + "." + tempNum% item.getUnitRate());
					}
					if (StringUtils.isNotBlank(material.getType()+"")) {
						r.setTypeStr(CommomUtil.valueToNameInDict(material.getType(), "material_type"));
					}
					r.setSystemCode(item.getCode());
					r.setFromTypeStr(CommomUtil.valueToNameInDict(r.getFromType(), "trunover_type"));
					if(batchFlag){
						r.setBatch(null);
					}else{
						r.setBatch(material.getBatchNumber());
					}
				}
			});

			return materialOperations;
		}
		
	/**
	 * 
	 * @param page
	 * @param limit
	 * @param request
	 * @return
	 */
	@PostMapping("listByClientIdAndBatch")
	@ResponseBody
	public PageData<Material> listByClientIdAndBatch(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit, ServletRequest request, String batch,
			String itemId,Integer materialType) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Material> materialPageData = new PageData<>();
		QueryWrapper<Material> materialWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		materialWrapper.eq("del_flag", false);
		materialWrapper.gt("available_num", 0);
		materialWrapper.eq("batch_number", batch);
		materialWrapper.eq("item_id", itemId);
		materialWrapper.eq("type", materialType);
		materialWrapper.orderByAsc("batch_number");
		IPage<Material> materialPage = materialService.page(new Page<>(page, limit), materialWrapper);
		if (materialPage.getTotal() == 0) {
			// 如果没找到符合批次得 就 放出所有批次得数据
			QueryWrapper<Material> materialWrapperNew = new QueryWrapper<>();
			// 相当于del_flag = 0;
			materialWrapperNew.eq("del_flag", false);
			materialWrapperNew.gt("available_num", 0);
			materialWrapperNew.eq("item_id", itemId);
			materialWrapperNew.eq("type", materialType);
			materialWrapperNew.orderByAsc("batch_number");
			materialPage = materialService.page(new Page<>(page, limit), materialWrapperNew);
		}
		materialPageData.setCount(materialPage.getTotal());
		materialPageData.setData(setItemToMaterial(materialPage.getRecords()));
		return materialPageData;
	}

	// 创建者，和修改人
	private List<Material> setItemToMaterial(List<Material> materials) {
		materials.forEach(r -> {
			if (StringUtils.isNotBlank(r.getItemId())) {
				Clientitem item = clientitemService.getById(r.getItemId());
				r.setItemName(item.getName());
				r.setRate(item.getUnitRate() + "");
				r.setSystemCode(item.getCode());
				r.setNumZ(r.getAvailableNum() / item.getUnitRate() + "." + r.getAvailableNum() % item.getUnitRate());
			}
		});
		return materials;
	}
	
	@PostMapping("history")
	@ResponseBody
	@SysLog("历史操作记录数据")
	public List<MaterialOperations> history(@RequestParam(value = "materialId", required = false) String materialId) {
		List<MaterialOperations> operations = materialOperationsService.selectByMaterialId(materialId);
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
			if(StringUtils.isNotBlank(o.getFromType()+"")){
				String str =o.getFromCode()+"中"+ CommomUtil.valueToNameInDict(o.getFromType(), "trunover_type")+o.getNumber()+"(零数量)";
				o.setTypeStr(str);
			}
		});
		return operations;
	}
	@PostMapping("distribution")
	@ResponseBody
	@SysLog("物料分布记录数据")
	public List<MaterialDepot> distribution(@RequestParam(value = "materialId", required = false) String materialId) {
		QueryWrapper<MaterialDepot> mDepotWrapper = new QueryWrapper<>();
		mDepotWrapper.eq("material_id", materialId);
		mDepotWrapper.ne("number", 0);
		List<MaterialDepot> mDepot = materialDepotService.list(mDepotWrapper);
		mDepot.forEach(o -> {
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
			if(StringUtils.isNotBlank(o.getDepotId()+"")){
				String str = "在"+o.getDepotId()+"中有"+o.getNumber()+"数量(零)";
				o.setTypeStr(str);
			}
		});
		return mDepot;
	}
	
	//以下为根据储位来查询库存
	@GetMapping(value = "listDepotMaterial")
	public String listDepotMaterial(String code, ModelMap modelMap) {
		modelMap.put("code", code);
		return "warehouse/depot/listDepotMaterial";
	}

	/**
	 * 查询分页数据
	 */
	@PostMapping("listDepotMaterial")
	@ResponseBody
	public PageData<Material> listDepotMaterial(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit, ServletRequest request,@RequestParam(value = "depotCode") String depotCode) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Material> materialPageData = new PageData<>();
		// 相当于del_flag = 0;
		Map<String,Object> mapRes = materialService.selectMaterialByDepot((page-1)*limit, limit, depotCode);
		materialPageData.setCount((Long) mapRes.get("count"));
		materialPageData.setData(setUserToMaterial((List<Material>) mapRes.get("list"),1));
		return materialPageData;
	}
	
	//---------------------------以下为调仓------------------------------
	//以下为根据储位来查询库存
	@GetMapping(value = "mangerListDepotMaterial")
	public String mangerListDepotMaterial(String code, ModelMap modelMap) {
		modelMap.put("code", code);
		return "warehouse/depot/mangerListDepotMaterial";
	}

	/**
	 * 查询分页数据
	 */
	@PostMapping("mangerListDepotMaterial")
	@ResponseBody
	public PageData<Material> mangerListDepotMaterial(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit, ServletRequest request ) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Material> materialPageData = new PageData<>();
		String depotCode = null;
		String itemId = null;
		String batch = null;
		if (!map.isEmpty()) {//检索项
			depotCode = (String) map.get("depotCode");
			itemId = (String) map.get("itemId");
			batch = (String) map.get("batch");
		}	
		// 相当于del_flag = 0;
		Map<String,Object> mapRes = materialService.selectMaterialByDepot((page-1)*limit, limit, depotCode,itemId,batch,null);
		materialPageData.setCount((Long) mapRes.get("count"));
		materialPageData.setData(setUserToMaterial((List<Material>) mapRes.get("list"),1));
		return materialPageData;
	}
	
}
