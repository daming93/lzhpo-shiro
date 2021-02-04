package com.lzhpo.stock.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.mapper.LineTakeoutMapper;
import com.lzhpo.stock.service.ILineTakeoutService;
import com.lzhpo.sys.service.IGenerateNoService;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

/**
 * <p>
 * 线路发单（无关库存的发单) 表单形式和库存发单接近，但是没有子表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2021-01-25
 */
@Service
public class LineTakeoutServiceImpl extends ServiceImpl<LineTakeoutMapper, LineTakeout> implements ILineTakeoutService {
	@Autowired
	private IIncomeService incomeService;
	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private IBasicdataService basicdateService;
	@Autowired
	private IAddressService addressService;
	@Override
	public long getLineTakeoutCount(String clientCode,String clientId) {
		QueryWrapper<LineTakeout> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("client_code",clientCode);
		wrapper.eq("client_id",clientId);
		return baseMapper.selectCount(wrapper);
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public LineTakeout saveLineTakeout(LineTakeout lineTakeout) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();

		//scheduling_status_no 调度未排单
		Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
		lineTakeout.setStatus(modify_status_await);
		lineTakeout.setCode(generateNoService.nextCode("XL"));//线路出单
		lineTakeout.setSchedulingStatus(scheduling_status_no);
		baseMapper.insert(lineTakeout);
		return lineTakeout;
	}

	@Override
	public LineTakeout getLineTakeoutById(String id) {
		LineTakeout r =  baseMapper.selectById(id);
		if (StringUtils.isNotBlank(r.getClientId())) {
			r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
		}
		if (r.getStatus() != null) {
			r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
		}
		if (r.getPickingStatus()!= null) {
			r.setPickStatusStr(CommomUtil.valueToNameInDict(r.getPickingStatus(), "is_exsit_pick"));
		}
		if (r.getTransportationType() != null) {
			r.setTransportationTypeStr(CommomUtil.valueToNameInDict(r.getTransportationType(), "transportation_type"));
		}
		if (StringUtils.isNotBlank(r.getAddressId())) {
			r.setAddressName(addressService.getById(r.getAddressId()).getAddressName());
		}
		return r;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public void updateLineTakeout(LineTakeout lineTakeout) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		lineTakeout.setStatus(modify_status_revocation);
		//调账
		Integer transportation_type_adjustment =  CacheUtils.keyDict.get("transportation_type_adjustment").getValue();
		//由于没有拣货单，在这里就要生成出库得装卸费用
		if(transportation_type_adjustment.equals(lineTakeout.getAdjustment())){//不是调账就计算费用
			//调账就不用生成拣货单和 不拣货也不产生费用
		}else{
			try {
				lineTakeout = incomeService.linetakeoutIncomeMath(lineTakeout);
			} catch (RuntimeJsonMappingException e1){
				throw e1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		baseMapper.updateById(lineTakeout);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public void deleteLineTakeout(LineTakeout lineTakeout) {
		lineTakeout.setDelFlag(true);
		baseMapper.updateById(lineTakeout);
	}

	@Override
	@Cacheable("LineTakeouts")
	public List<LineTakeout> selectAll() {
		QueryWrapper<LineTakeout> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void backTakeout(String id) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		LineTakeout lineTakeout = getLineTakeoutById(id);
		lineTakeout.setStatus(modify_status_await);
		// 还有计算入库装卸费  撤销该费用
		if(incomeService.getById(lineTakeout.getIncomeId())!=null){
			incomeService.deleteIncome(incomeService.getById(lineTakeout.getIncomeId()));//有输入就删除没有就不用管
		}
		lineTakeout.setIncomeId("无");
		baseMapper.updateById(lineTakeout);

	}

	@Override
	public List<LineTakeout> selectAllByDispatchIds(Set<String> dispatchIds) {
		return baseMapper.selectAllByDispatchIds(dispatchIds);
	}

	@Override
	public String upload(MultipartFile file, List<Basicdata> basicDatas) {
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer buffer = new StringBuffer();
		for (Basicdata basicdata : basicDatas) {
			map.put(basicdata.getClientShortName(), basicdata.getId());
		}
		try {
			ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
			reader
			.addHeaderAlias("客户名称", "clientName")
			.addHeaderAlias("出库时间", "takeoutTime")
			.addHeaderAlias("客户单号", "clientCode")
			.addHeaderAlias("出库类型", "adjustmentStr")//是否调账
			.addHeaderAlias("收入类型", "transportationTypeStr")
			.addHeaderAlias("配送类型", "deliverTypeStr")
			.addHeaderAlias("配送地址", "addressName")
			.addHeaderAlias("配送时间", "deliveryTime")
			.addHeaderAlias("体积(m³)", "volume")
			.addHeaderAlias("重量(kg)", "weight")
			.addHeaderAlias("整数量", "total")
			.addHeaderAlias("零数量", "number")
			.addHeaderAlias("备注", "remarks");
			List<LineTakeout> list = reader.readAll(LineTakeout.class);
			int i = 0;
			for (LineTakeout lineTakeout : list) {
				i++;
				if(StringUtils.isNotBlank(lineTakeout.getClientName())){
					try {
						lineTakeout.setClientId(map.get(lineTakeout.getClientName()));
					} catch (Exception e) {
						buffer.append("第"+i+"条请使用准确的客户简称<br>");
						continue;
					}
				}
				Address address = addressService.getByName(lineTakeout.getAddressName());
				if(address!=null){
					lineTakeout.setAddressId(address.getId());
				}else{
					buffer.append("第"+i+"条请使用准确的送货地址<br>");
					continue;
				}
				//还有地址 以及其他格式的验证，之后在去找insert的count是否重复，先验证上传格式
				//几个状态
				if(CommomUtil.nameToValueInDict(lineTakeout.getTransportationTypeStr(), "transportation_type")!=null){
					lineTakeout.setTransportationType(CommomUtil.nameToValueInDict(lineTakeout.getTransportationTypeStr(), "transportation_type"));
				}else{
					buffer.append("第"+i+"条请使用准确的收入类型<br>");
					continue;
				}
				if(CommomUtil.nameToValueInDict(lineTakeout.getAdjustmentStr(), "adjustment")!=null){
					lineTakeout.setAdjustment(CommomUtil.nameToValueInDict(lineTakeout.getAdjustmentStr(), "adjustment"));
				}else{
					lineTakeout.setAdjustment(0);//找不到就默认正常
				}
				if(CommomUtil.nameToValueInDict(lineTakeout.getDeliverTypeStr(), "deliver_type")!=null){
					lineTakeout.setDeliverType(CommomUtil.nameToValueInDict(lineTakeout.getDeliverTypeStr(), "deliver_type"));
				}else{
					buffer.append("第"+i+"条请使用准确的配送类型<br>");
					continue;
				}
				//格式编辑完就看怎么插入记录
				if(getLineTakeoutCount(lineTakeout.getClientCode(), lineTakeout.getClientId())==0){
					saveLineTakeout(lineTakeout);
				}else{
					buffer.append("第"+i+"同一客户同一单号无法重复录入<br>");
					continue;

				}
			}
			buffer.append("文件上传成功<br>");
		} catch (Exception e) {
			e.printStackTrace();
			buffer.append("文件格式有误(使用导出模板,另存为xls[excel2003-2007]格式),或者数字部分填入了汉字，请检查");
			return buffer.toString();
		}
		return buffer.toString();
	}

}
