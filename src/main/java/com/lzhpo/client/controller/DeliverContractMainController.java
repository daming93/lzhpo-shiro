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
import com.lzhpo.client.entity.DeliverContractMain;
import com.lzhpo.client.entity.DeliverContractMainDetail;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.client.service.IDeliverContractMainDetailService;
import com.lzhpo.client.service.IDeliverContractMainService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.stock.service.ILineTakeoutService;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
@Controller
@RequestMapping("client/deliverContractMain")
public class DeliverContractMainController {
	@Autowired
	private IDeliverContractMainService deliverContractMainService;

	@Autowired
	private IDeliverContractMainDetailService deliverContractMainDetailService;

	@Autowired
	UserService userService;

	@Autowired
	private IBasicdataService basicdataService;

	@Autowired
	private ITerritoryService territoryService;

	@Autowired
	private ITakeoutService takeoutService ;
	@Autowired
	private ILineTakeoutService lineTakeoutService ;
	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		modelMap.put("clientList", basicdataService.selectAll());
		return "client/deliverContractMain/listDeliverContractMain";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("client:deliverContractMain:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<DeliverContractMain> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DeliverContractMain> deliverContractMainPageData = new PageData<>();
		QueryWrapper<DeliverContractMain> deliverContractMainWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		deliverContractMainWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				deliverContractMainWrapper.like("name", keys);
			}
		}
		IPage<DeliverContractMain> deliverContractMainPage = deliverContractMainService.page(new Page<>(page, limit),
				deliverContractMainWrapper);
		deliverContractMainPageData.setCount(deliverContractMainPage.getTotal());
		deliverContractMainPageData.setData(setUserToDeliverContractMain(deliverContractMainPage.getRecords()));
		return deliverContractMainPageData;
	}

	// 创建者，和修改人
	private List<DeliverContractMain> setUserToDeliverContractMain(List<DeliverContractMain> deliverContractMains) {
		deliverContractMains.forEach(r -> {
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
				String usingContractId = deliverContractMainService.getUsingContractId(r.getClientId());
				// 如果该客户正在使用得合同是该合同
				if (StringUtils.isNotBlank(usingContractId) && usingContractId.equals(r.getId())) {
					r.setUse(true);
				} else {
					r.setUse(false);
				}
			}
		});

		return deliverContractMains;
	}

	// 创建者，和修改人
	private List<DeliverContractMainDetail> setUserToDeliverContractMainDetail(
			List<DeliverContractMainDetail> details) {
		details.forEach(r -> {

			if (StringUtils.isNotBlank(r.getProvinceId())) {
				r.setProvinceName(territoryService.getById(r.getProvinceId()).getName());
			}
			if (StringUtils.isNotBlank(r.getCityId())) {
				r.setCityName(territoryService.getById(r.getCityId()).getName());
			}
			if (StringUtils.isNotBlank(r.getAreaId())) {
				r.setAreaName(territoryService.getById(r.getAreaId()).getName());
			}
		});

		return details;
	}
	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<DeliverContractMain> getById(@RequestParam("pkid")
	// String pkid){
	// return null;
	// }

	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<DeliverContractMainDetail> selectDetail(
			@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "9999") Integer limit, ServletRequest request,
			String contractId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DeliverContractMainDetail> ContDeliverContractMainDetailData = new PageData<>();
		QueryWrapper<DeliverContractMainDetail> DeliverContractMainDetailWrapper = new QueryWrapper<>();
		DeliverContractMainDetailWrapper.eq("contract_id", contractId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("key");
			if (StringUtils.isNotBlank(keys)) {
				DeliverContractMainDetailWrapper.like("name", keys);
			}
		}
		DeliverContractMainDetailWrapper.orderByDesc("create_date ");
		IPage<DeliverContractMainDetail> DeliverContractMainDetailPage = deliverContractMainDetailService
				.page(new Page<>(page, limit), DeliverContractMainDetailWrapper);
		ContDeliverContractMainDetailData.setCount(DeliverContractMainDetailPage.getTotal());
		ContDeliverContractMainDetailData
				.setData(setUserToDeliverContractMainDetail(DeliverContractMainDetailPage.getRecords()));
		return ContDeliverContractMainDetailData;
	}

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		modelMap.put("clientList", basicdataService.selectAll());
		modelMap.put("freightType", CacheUtils.allDicts.get("freight_type"));
		modelMap.put("moneyType", CacheUtils.allDicts.get("money_type"));
		return "client/deliverContractMain/addDeliverContractMain";
	}

	@RequiresPermissions("client:deliverContractMain:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody DeliverContractMain deliverContractMain) {
		if (StringUtils.isBlank(deliverContractMain.getName())) {
			return ResponseEntity.failure("合同名称（不能为空)");
		}
		if (deliverContractMainService.getDeliverContractMainCount(deliverContractMain.getName()) > 0) {
			return ResponseEntity.failure("合同名称（不能重复)");
		}
		//时间验证
		if(deliverContractMain.getStartTime().isAfter(deliverContractMain.getOverTime())){
			return ResponseEntity.failure("结束时间不应在开始时间之前");
		}
		deliverContractMainService.saveDeliverContractMain(deliverContractMain);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:deliverContractMain:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		DeliverContractMain deliverContractMain = deliverContractMainService.getDeliverContractMainById(id);
		deliverContractMainService.deleteDeliverContractMain(deliverContractMain);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:deliverContractMain:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<DeliverContractMain> deliverContractMains) {
		if (deliverContractMains == null || deliverContractMains.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (DeliverContractMain r : deliverContractMains) {
			deliverContractMainService.deleteDeliverContractMain(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		DeliverContractMain deliverContractMain = deliverContractMainService.getDeliverContractMainById(id);
		/**
		 * 自定义代码
		 */
		Map<String, Object> map = new HashMap();
		modelMap.put("deliverContractMain", deliverContractMain);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		modelMap.put("clientList", basicdataService.selectAll());
		modelMap.put("freightType", CacheUtils.allDicts.get("freight_type"));
		modelMap.put("moneyType", CacheUtils.allDicts.get("money_type"));
		return "client/deliverContractMain/editDeliverContractMain";
	}

	@RequiresPermissions("client:deliverContractMain:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody DeliverContractMain deliverContractMain) {
		if (StringUtils.isBlank(deliverContractMain.getId())) {
			return ResponseEntity.failure("关键信息（不能为空)");
		}
		if (StringUtils.isBlank(deliverContractMain.getName())) {
			return ResponseEntity.failure("名称不能为空");
		}
		//时间验证
		if(deliverContractMain.getStartTime().isAfter(deliverContractMain.getOverTime())){
			return ResponseEntity.failure("结束时间不应在开始时间之前");
		}		
		DeliverContractMain oldDeliverContractMain = deliverContractMainService
				.getDeliverContractMainById(deliverContractMain.getId());
		if (!oldDeliverContractMain.getName().equals(deliverContractMain.getName())) {
			if (deliverContractMainService.getDeliverContractMainCount(deliverContractMain.getName()) > 0) {
				return ResponseEntity.failure("名称（不能重复)");
			}
		}
		deliverContractMainService.updateDeliverContractMain(deliverContractMain);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:deliverContractMain:audit")
	@PostMapping("audit")
	@ResponseBody
	@SysLog("审核数据")
	public ResponseEntity audit(@RequestParam(value = "id", required = false) String id, Integer status) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		deliverContractMainService.ChangeAduitStatus(status, id);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("client:deliverContractMain:copy")
	@PostMapping("copy")
	@ResponseBody
	@SysLog("复制车辆配送合同")
	public ResponseEntity copy(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		deliverContractMainService.copyContract(id);
		return ResponseEntity.success("操作成功");
	}
	
	@PostMapping("searchAreaCanDeliver")
	@ResponseBody
	public ResponseEntity searchAreaCanDeliver(@RequestParam String tableId,@RequestParam String proviceId,@RequestParam String cityId,@RequestParam String areaId,@RequestParam Integer type) {
		if (StringUtils.isBlank(tableId)) {
			return ResponseEntity.failure("客户id不能为空");
		}else{
			//根据出库单号找到客户id
			String clientId = "";
			switch (type) {//1是库存发单 2 是线路发单
			case 1:
				clientId = takeoutService.getById(tableId).getClientId();
				break;

			default:
				clientId = lineTakeoutService.getById(tableId).getClientId();
				break;
			}
					
			String usingContractId = deliverContractMainService.getUsingContractId(clientId);
			if(usingContractId!=null){
				DeliverContractMainDetail detail = deliverContractMainDetailService.selectDetailMoneyByInfoNoRange(usingContractId, proviceId, cityId, areaId);
				if(detail!=null){
					return ResponseEntity.success("操作成功");
				}else{
					return ResponseEntity.failure("该配送合同中不含该区域的配送");
				}
			}else{
				return ResponseEntity.failure("该客户无正在使用的配送合同");
			}
		}
	}
}
