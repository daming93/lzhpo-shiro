package com.lzhpo.charts.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.charts.entity.Charts;
import com.lzhpo.charts.entity.ChartsChlidren;
import com.lzhpo.charts.entity.ClientCountWVQ;
import com.lzhpo.charts.entity.DocumentQuantity;
import com.lzhpo.charts.entity.WeightVolumeQuantity;
import com.lzhpo.charts.mapper.ChartsMapper;
import com.lzhpo.common.util.CommomUtil;

/**
 * <p>
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class ChartsService {
	@Autowired
	private ChartsMapper chartsMapper;

	public List<Charts> showBillCountAll(Map<String, Object> map) {
		List<Charts> charts = chartsMapper.showBillCountAll(map);
		for (Charts c : charts) {
			List<ChartsChlidren> chlid = c.getCharts();
			for (ChartsChlidren chartsChlidren : chlid) {
				if (chartsChlidren.getType() != null && chartsChlidren.getType().equals(4)) {// 拣货单
					chartsChlidren
							.setStatusStr(CommomUtil.valueToNameInDict(chartsChlidren.getStatus(), "is_exsit_pick"));
				} else {
					chartsChlidren
							.setStatusStr(CommomUtil.valueToNameInDict(chartsChlidren.getStatus(), "modify_status"));
				}
			}
		}
		return charts;
	};

	public Map<String, Object> showStorageCountAll(Map<String, Object> map) {

		List<WeightVolumeQuantity> list = chartsMapper.showStorageCountAll(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> dates = new ArrayList<String>();
		List<String> volumes = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> numbers = new ArrayList<String>();
		List<String> totals = new ArrayList<String>();
		for (WeightVolumeQuantity entity : list) {
			dates.add(entity.getDate());
			volumes.add(entity.getVolume());
			weights.add(entity.getWeight());
			numbers.add(entity.getNumber());
			totals.add(entity.getTotal());
		}
		mapModel.put("dates", dates);
		mapModel.put("volumes", volumes);
		mapModel.put("weights", weights);
		mapModel.put("numbers", numbers);
		mapModel.put("totals", totals);
		return mapModel;
	}

	public Map<String, Object> showTakoutCountAll(Map<String, Object> map) {

		List<WeightVolumeQuantity> list = chartsMapper.showTakoutCountAll(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> dates = new ArrayList<String>();
		List<String> volumes = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> numbers = new ArrayList<String>();
		List<String> totals = new ArrayList<String>();
		for (WeightVolumeQuantity entity : list) {
			dates.add(entity.getDate());
			volumes.add(entity.getVolume());
			weights.add(entity.getWeight());
			numbers.add(entity.getNumber());
			totals.add(entity.getTotal());
		}
		mapModel.put("dates", dates);
		mapModel.put("volumes", volumes);
		mapModel.put("weights", weights);
		mapModel.put("numbers", numbers);
		mapModel.put("totals", totals);
		return mapModel;
	}

	//
	public Map<String, Object> showReturnCountAll(Map<String, Object> map) {

		List<WeightVolumeQuantity> list = chartsMapper.showReturnCountAll(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> dates = new ArrayList<String>();
		List<String> volumes = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> numbers = new ArrayList<String>();
		List<String> totals = new ArrayList<String>();
		for (WeightVolumeQuantity entity : list) {
			dates.add(entity.getDate());
			volumes.add(entity.getVolume());
			weights.add(entity.getWeight());
			numbers.add(entity.getNumber());
			totals.add(entity.getTotal());
		}
		mapModel.put("dates", dates);
		mapModel.put("volumes", volumes);
		mapModel.put("weights", weights);
		mapModel.put("numbers", numbers);
		mapModel.put("totals", totals);
		return mapModel;
	}

	public Map<String, Object> showclientCountAll(Map<String, Object> map) {
		List<ClientCountWVQ> list = chartsMapper.showclientCountAll(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> returnVolume = new ArrayList<String>();
		List<String> returnWeight = new ArrayList<String>();
		List<String> returnNumber = new ArrayList<String>();
		List<String> returnTotal = new ArrayList<String>();
		List<String> takeoutVolume = new ArrayList<String>();
		List<String> takeoutWeight = new ArrayList<String>();
		List<String> takeoutNumber = new ArrayList<String>();
		List<String> takeoutTotal = new ArrayList<String>();
		List<String> storageVolume = new ArrayList<String>();
		List<String> storageWeight = new ArrayList<String>();
		List<String> storageNumber = new ArrayList<String>();
		List<String> storageTotal = new ArrayList<String>();
		List<String> clientName =  new ArrayList<String>();
		for (ClientCountWVQ entity : list) {
			returnVolume.add(entity.getReturnVolume());
			returnWeight.add(entity.getReturnWeight());
			returnTotal.add(entity.getReturnTotal());
			returnNumber.add(entity.getReturnNumber());
			takeoutVolume.add(entity.getTakeoutVolume());
			takeoutWeight.add(entity.getTakeoutWeight());
			takeoutTotal.add(entity.getTakeoutTotal());
			takeoutNumber.add(entity.getTakeoutNumber());
			storageVolume.add(entity.getStorageVolume());
			storageWeight.add(entity.getStorageWeight());
			storageNumber.add(entity.getStorageNumber());
			storageTotal.add(entity.getStorageTotal());
			clientName.add(entity.getClientName());
		}
		mapModel.put("returnVolume", returnVolume);
		mapModel.put("returnWeight", returnWeight);
		mapModel.put("returnNumber", returnNumber);
		mapModel.put("returnTotal", returnTotal);
		mapModel.put("takeoutVolume", takeoutVolume);
		mapModel.put("takeoutWeight", takeoutWeight);
		mapModel.put("takeoutNumber", takeoutNumber);
		mapModel.put("takeoutTotal", takeoutTotal);
		mapModel.put("storageVolume", storageVolume);
		mapModel.put("storageWeight", storageWeight);
		mapModel.put("storageNumber", storageNumber);
		mapModel.put("storageTotal", storageTotal);
		mapModel.put("clientName", clientName);
		return mapModel;
	}
	
	public Map<String, Object> countClientDocumentQuantity(Map<String, Object> map) {
		List<DocumentQuantity> list = chartsMapper.listClientDocumentQuantity(map);
		Long count = chartsMapper.countClientDocumentQuantity(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("count", count);
		mapModel.put("list", list);
		return mapModel;
	}
	
	public Map<String, Object> listStorageDocumentQuantity(Map<String, Object> map) {
		List<DocumentQuantity> list = null;
		Integer mode = (Integer) map.get("mode");
		switch (mode) {
			case 1://入库
				list =  chartsMapper.listStorageDocumentQuantity(map);
				break;
			case 2://出库
				list =  chartsMapper.listTakeoutDocumentQuantity(map);
				break;
			case 3://退库
				list =  chartsMapper.listReturnDocumentQuantity(map);
				break;
			case 4://转良库
				list =  chartsMapper.listZLDocumentQuantity(map);
				break;
			case 5://转不良库
				list =  chartsMapper.listZBLDocumentQuantity(map);
				break;
		}
		List<String> time = new ArrayList<String>();
		List<String> count = new ArrayList<String>();
		for (DocumentQuantity documentQuantity : list) {
			time.add(documentQuantity.getTime());
			count.add(documentQuantity.getCount());
		}
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("list", list);
		mapModel.put("time", time);
		mapModel.put("count", count);
		return mapModel;
	}
	
	public Map<String, Object> showclientCountByTimeCount(Map<String, Object> map) {
		List<ClientCountWVQ> list = chartsMapper.showclientCountByTime(map);
		Long count = chartsMapper.showclientCountByTimeCount(map);
		Map<String, Object> mapModel = new HashMap<String, Object>();
		mapModel.put("count", count);
		mapModel.put("list", list);
		return mapModel;
	}
	
	
	public Map<String, Object> listClientMQV(Map<String, Object> map) {
		List<WeightVolumeQuantity> list = null;
		Integer mode = (Integer) map.get("mode");
		switch (mode) {
			case 1://入库
				list =  chartsMapper.showStorageCountAll(map);
				break;
			case 2://出库
				list =  chartsMapper.showTakoutCountAll(map);
				break;
			case 3://退库
				list =  chartsMapper.showReturnCountAll(map);
				break;
		}
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> dates = new ArrayList<String>();
		List<String> volumes = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> numbers = new ArrayList<String>();
		List<String> totals = new ArrayList<String>();
		for (WeightVolumeQuantity entity : list) {
			dates.add(entity.getDate());
			volumes.add(entity.getVolume());
			weights.add(entity.getWeight());
			numbers.add(entity.getNumber());
			totals.add(entity.getTotal());
		}
		mapModel.put("dates", dates);
		mapModel.put("volumes", volumes);
		mapModel.put("weights", weights);
		mapModel.put("numbers", numbers);
		mapModel.put("totals", totals);
		return mapModel;
	}
	public Map<String, Object> showclientItemCount(Map<String, Object> map) {
		List<WeightVolumeQuantity> list = null;
		Integer mode = (Integer) map.get("mode");
		switch (mode) {
			case 1://入库
				list =  chartsMapper.showStorageCountItem(map);
				break;
			case 2://出库
				list =  chartsMapper.showTakeoutCountItem(map);
				break;
			case 3://退库
				list =  chartsMapper.showReturnCountItem(map);
				break;
		}
		Map<String, Object> mapModel = new HashMap<String, Object>();
		List<String> dates = new ArrayList<String>();
		List<String> volumes = new ArrayList<String>();
		List<String> weights = new ArrayList<String>();
		List<String> numbers = new ArrayList<String>();
		List<String> totals = new ArrayList<String>();
		for (WeightVolumeQuantity entity : list) {
			dates.add(entity.getDate());
			volumes.add(entity.getVolume());
			weights.add(entity.getWeight());
			numbers.add(entity.getNumber());
			totals.add(entity.getTotal());
		}
		mapModel.put("dates", dates);
		mapModel.put("volumes", volumes);
		mapModel.put("weights", weights);
		mapModel.put("numbers", numbers);
		mapModel.put("totals", totals);
		return mapModel;
	}
}
