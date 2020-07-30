package com.lzhpo.warehouse.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.mapper.DepotMapper;
import com.lzhpo.warehouse.service.IDepotService;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
/**
 * <p>
 * 储位表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
@Service
public class DepotServiceImpl extends ServiceImpl<DepotMapper, Depot> implements IDepotService {
	@Override
    public long getDepotCount(Depot depot) {
		String code = judCode(depot);
        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("code",code);
        if(depot.getId()!=null){
        	 wrapper.ne("id",depot.getId());
        }
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public Depot saveDepot(Depot depot) {
    	String code = judCode(depot);
    	if(code!=null){
    		depot.setCode(code);
    		baseMapper.insert(depot);
    	}
        /**
	*预留编辑代码 
	*/
        return depot;
    }

    @Override
    public Depot getDepotById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public void updateDepot(Depot depot) {
    	String code = judCode(depot);
    	if(code!=null){
    		depot.setCode(code);
    		baseMapper.updateById(depot);
    	}
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public void deleteDepot(Depot depot) {
    	//储位上有东西的时候不能更改3
        depot.setDelFlag(true);
        baseMapper.updateById(depot);
    }

    @Override
    @Cacheable("Depots")
    public List<Depot> selectAll() {
        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public String judCode(Depot depot) {
		StringBuffer buf = new StringBuffer();
		boolean flagA = depot.getSubsectionA()!=null;
		boolean flagB = flagA&&StringUtils.isNotBlank(depot.getSubsectionB());
		boolean flagC = flagB&&StringUtils.isNotBlank(depot.getSubsectionC());
		boolean flagD = flagC&&StringUtils.isNotBlank(depot.getSubsectionD());
		boolean flagE = flagD&&StringUtils.isNotBlank(depot.getSubsectionE());
		boolean flagF = flagE&&StringUtils.isNotBlank(depot.getSubsectionF());
		boolean flagG = flagF&&StringUtils.isNotBlank(depot.getSubsectionG());
		if(flagA){
			buf.append(depot.getSubsectionA());
		}else{
			return null;
		}
		if(flagB){
			buf.append(depot.getSubsectionB());
		}
		if(flagC){
			buf.append(depot.getSubsectionC());
		}
		if(flagD){
			buf.append(depot.getSubsectionD());
		}
		if(flagE){
			buf.append(depot.getSubsectionE());
		}
		if(flagF){
			buf.append(depot.getSubsectionF());
		}
		if(flagG){
			buf.append(depot.getSubsectionG());
		}
		return buf.toString();
	}
	@Override
	public List<Depot> selectByClientId(String clientId) {
		QueryWrapper<Depot> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false).and(depotwrapper -> depotwrapper.like("client_ids", clientId).or().eq("client_ids", ""));
		
		return baseMapper.selectList(wrapper);
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
			.addHeaderAlias("编号", "code")
			.addHeaderAlias("分段A", "subsectionA")
			.addHeaderAlias("分段B", "subsectionB")
			.addHeaderAlias("分段C", "subsectionC")
			.addHeaderAlias("分段D", "subsectionD")
			.addHeaderAlias("分段E", "subsectionE")
			.addHeaderAlias("分段F", "subsectionF")
			.addHeaderAlias("分段G", "subsectionG")
			.addHeaderAlias("归属客户", "clientNames");
			List<Depot> depots = reader.readAll(Depot.class);
			int i = 1;
			for (Depot depot : depots) {
				if(StringUtils.isNotBlank(depot.getClientNames())){
					try {
						String[] names = depot.getClientNames().split(",");
						for (int j = 0; j < names.length; j++) {
							String string = names[j];
							names[j] = map.get(string);
						}
						depot.setClientIds(StringUtils.join(names, ","));
					} catch (Exception e) {
						buffer.append("请使用准确的客户简称，多客户的情况,中间使用英文逗号隔开");
					}
				}
				//处理完客户栏 之后 
				if (getDepotCount(depot) > 0) {
					buffer.append("第"+i+"条重复名称<br>");
				}else{
					saveDepot(depot);
				}
				i++;
				buffer.append("文件上传成功<br>");
			}
		} catch (Exception e) {
			e.printStackTrace();
			buffer.append("文件格式有误(使用导出模板,另存为xls[excel2003-2007]格式)");
			return buffer.toString();
		}
		return buffer.toString();
	}

}
