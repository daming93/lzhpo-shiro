package com.lzhpo.charts.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzhpo.charts.entity.Charts;
import com.lzhpo.charts.entity.ClientCountWVQ;
import com.lzhpo.charts.entity.DocumentQuantity;
import com.lzhpo.charts.service.impl.ChartsService;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.base.PageData;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;

import cn.hutool.core.date.DateUtil;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
@Controller
@RequestMapping("/charts/count")
public class ChartsController {
	
	@Autowired
	private IBasicdataService basicdateService;
	
	@Autowired
	private IClientitemService itemService;
	
@Autowired
private ChartsService chartsService ;

	@GetMapping(value = "dayList")
	public String dayList(ModelMap modelMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> name = new ArrayList<String>();
		name.add("入库");name.add("出库");name.add("退库");name.add("拣货单");name.add("转良");name.add("转不良");
		map.put("time", DateUtil.today());
		List<Charts> charts = chartsService.showBillCountAll(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("list", JSONObject.toJSON(charts));
		mapModel.put("namelist", name);
		modelMap.addAttribute("mapModel",JSONObject.toJSON(mapModel));
		return "charts/todayCount";
	}

	/**
	 * 月度入库信息
	 */
	@ResponseBody
	@RequestMapping(value = "/monthStorage")
	public Map<String, Object>  monthStorage() {
		Map<String, Object> map = new HashMap<String, Object>();
		return chartsService.showStorageCountAll(map);
	}
	
	/**
	 * 月度出库信息
	 */
	@ResponseBody
	@RequestMapping(value = "/monthTakout")
	public Map<String, Object>  monthTakout() {
		Map<String, Object> map = new HashMap<String, Object>();
		return chartsService.showTakoutCountAll(map);
	}
	
	/**
	 * 月度退库信息
	 */
	@ResponseBody
	@RequestMapping(value = "/monthReturn")
	public Map<String, Object>  monthReturn() {
		Map<String, Object> map = new HashMap<String, Object>();
		return chartsService.showReturnCountAll(map);
	}
	
	/**
	 * 今日客户配送信息
	 */
	@ResponseBody
	@RequestMapping(value = "/showclientCountAll")
	public Map<String, Object>  showclientCountAll (Integer type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		return chartsService.showclientCountAll(map);
	}
	
	//客户单据量
	@GetMapping(value = "listClientDocumentQuantity")
	public String listClientDocumentQuantity(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		return "charts/clientDocumentQuantity";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("chart:documentQuantity:list")
	@PostMapping("listClientDocumentQuantity")
	@ResponseBody
	public PageData<DocumentQuantity> listClientDocumentQuantity(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<DocumentQuantity> pageData = new PageData<>();
		map.put("start", (page-1)*limit);
		map.put("limit", limit);
		Map<String,Object> mapRes = chartsService.countClientDocumentQuantity(map);
		pageData.setCount((Long) mapRes.get("count"));
		pageData.setData((List<DocumentQuantity>) mapRes.get("list"));
		return pageData;
	}
	
	//单据量
	@GetMapping(value = "listStorageDocumentQuantity")
	public String listStorageDocumentQuantity(ModelMap modelMap,Integer mode) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("mode", mode);
		return "charts/clientDocumentQuantityCount";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/countStorageDocumentQuantity")
	public Map<String, Object>  countStorageDocumentQuantity (Integer type,String startTime,String endTime,String clientId,Integer mode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("clientId", clientId);
		map.put("mode", mode);
	
		return chartsService.listStorageDocumentQuantity(map);
	}


	/**
	 * 	以下是客户总方重
	 */
	
	
	@GetMapping(value = "listClientMQV")
	public String listClientMQV(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		return "charts/clientMQV";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("chart:MQV:list")
	@PostMapping("listClientMQV")
	@ResponseBody
	public PageData<ClientCountWVQ> listClientMQV(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map<String,Object> map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<ClientCountWVQ> pageData = new PageData<>();
		map.put("start", (page-1)*limit);
		map.put("limit", limit);
		Map<String,Object> mapRes = chartsService.showclientCountByTimeCount(map);
		pageData.setCount((Long) mapRes.get("count"));
		pageData.setData((List<ClientCountWVQ>) mapRes.get("list"));
		return pageData;
	}
	
	
	//出入退 总方重
	@GetMapping(value = "showclientCountByTime")
	public String showclientCountByTime(ModelMap modelMap,Integer mode) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("mode", mode);
		return "charts/clientMQVCount";
	}
	//出入退 总方重 数据
	@ResponseBody
	@RequestMapping(value = "/showclientCountByTimeData")
	public Map<String, Object>  showclientCountByTimeData (Integer type,String startTime,String endTime,String clientId,Integer mode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("clientId", clientId);
		map.put("mode", mode);
	
		return chartsService.listClientMQV(map);
	}
	@GetMapping(value = "itemList")
	public String itemList(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas); 
		return "charts/listClientitem";
	}
	//单品出入退量
	@GetMapping(value = "showclientItem")
	public String showclientItem(ModelMap modelMap,Integer mode,String id) {
		Clientitem item = itemService.getById(id);
		modelMap.put("item", JSON.toJSON(item));
		modelMap.put("mode", mode);
		return "charts/listClientitemCount";
	}
	@ResponseBody
	@RequestMapping(value = "/showclientItemCount")
	public Map<String, Object>  showclientItemCount (Integer type,String startTime,String endTime,String itemId,Integer mode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", type);
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		map.put("itemId", itemId);
		map.put("mode", mode);
	
		return chartsService.showclientItemCount(map);
	}
}
