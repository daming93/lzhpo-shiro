package com.lzhpo.charts.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.charts.entity.Charts;
import com.lzhpo.charts.entity.ClientCountWVQ;
import com.lzhpo.charts.entity.DocumentQuantity;
import com.lzhpo.charts.entity.WeightVolumeQuantity;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface ChartsMapper extends BaseMapper<Charts> {
	
	public List<Charts> showBillCountAll(Map<String, Object> map);
	
	public List<WeightVolumeQuantity> showStorageCountAll (Map<String, Object> map);
	
	public List<WeightVolumeQuantity> showTakoutCountAll (Map<String, Object> map);
	
	public List<WeightVolumeQuantity> showReturnCountAll (Map<String, Object> map);
	
	
	public List<ClientCountWVQ> showclientCountAll (Map<String, Object> map);
	
	
	//按时间客户总方
	public List<ClientCountWVQ> showclientCountByTime (Map<String, Object> map);
	
	public Long showclientCountByTimeCount (Map<String, Object> map);
	//客户单据量
	public List<DocumentQuantity> listClientDocumentQuantity (Map<String, Object> map);
	
	public Long countClientDocumentQuantity (Map<String, Object> map);
	
	//客户单据量 入库
	public List<DocumentQuantity> listStorageDocumentQuantity (Map<String, Object> map);
	
	//客户单据量 出库
	public List<DocumentQuantity> listTakeoutDocumentQuantity (Map<String, Object> map);
	
	//客户单据量 退
	public List<DocumentQuantity> listReturnDocumentQuantity (Map<String, Object> map);
	
	//客户单据量 转良
	public List<DocumentQuantity> listZLDocumentQuantity (Map<String, Object> map);
	
	//客户单据量 转不良
	public List<DocumentQuantity> listZBLDocumentQuantity (Map<String, Object> map);
	
	/**
	 * 根据品项id 及单品出入库统计
	 */
	public List<WeightVolumeQuantity> showStorageCountItem (Map<String, Object> map);
	
	public List<WeightVolumeQuantity> showTakeoutCountItem (Map<String, Object> map);
	
	public List<WeightVolumeQuantity> showReturnCountItem (Map<String, Object> map);
}


