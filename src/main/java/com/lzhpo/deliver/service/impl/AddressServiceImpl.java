package com.lzhpo.deliver.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.mapper.AddressMapper;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;

/**
 * <p>
 * 送货地址表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements IAddressService {
	@Autowired
	private ITerritoryService territoryService;

	@Override
	public long getAddressCount(String name) {
		QueryWrapper<Address> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("address_name", name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Addresss", allEntries = true)
	public Address saveAddress(Address address) {
		baseMapper.insert(address);
		/**
		 * 预留编辑代码
		 */
		return address;
	}

	@Override
	public Address getAddressById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Addresss", allEntries = true)
	public void updateAddress(Address address) {
		baseMapper.updateById(address);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Addresss", allEntries = true)
	public void deleteAddress(Address address) {
		address.setDelFlag(true);
		baseMapper.updateById(address);
	}

	@Override
	@Cacheable("Addresss")
	public List<Address> selectAll() {
		QueryWrapper<Address> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public String upload(MultipartFile file) {
		StringBuffer buffer = new StringBuffer();
		try {
			ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
			reader.addHeaderAlias("送达方名称", "addressName").addHeaderAlias("省", "provinceName")
					.addHeaderAlias("市", "cityName").addHeaderAlias("区", "countiesName")
					.addHeaderAlias("区域级别", "areaLevelStr").addHeaderAlias("送达方类型", "addressTypeStr")
					.addHeaderAlias("地域名称", "descritpion");
			List<Address> items = reader.readAll(Address.class);
			int i = 1;
			for (Address address : items) {
				if (getAddressCount(address.getAddressName()) > 0) {
					buffer.append("第" + i + "条重复名称<br>");
					i++;
					continue;
				}

				if (StringUtils.isNotBlank(address.getProvinceName())) {
					// 先找省
					QueryWrapper<Territory> wrapper = new QueryWrapper<>();
					wrapper.eq("name", address.getProvinceName());// 省应该是独一无二的
					Territory province = territoryService.getOne(wrapper);
					if (province != null) {
						address.setProvinceId(province.getId());
						wrapper = new QueryWrapper<>();
						wrapper.eq("name", address.getCityName());
						wrapper.eq("parent_code", province.getCode());
						Territory city = territoryService.getOne(wrapper);
						if (city != null) {
							address.setCityId(city.getId());
							wrapper = new QueryWrapper<>();
							wrapper.eq("name", address.getCountiesName());
							wrapper.eq("parent_code", city.getCode());
							Territory area = territoryService.getOne(wrapper);
							if (area != null) {
								address.setCountiesId(area.getId());
								address.setAreaName(
										address.getProvinceName() + address.getCityName() + address.getCountiesName());
							} else {
								buffer.append("第" + i + "条区域名有误<br>");
								i++;
								continue;
							}
						} else {
							buffer.append("第" + i + "条城市名有误<br>");
							i++;
							continue;
						}
					} else {
						buffer.append("第" + i + "条省份名有误<br>");
						i++;
						continue;
					}

				} else {// else就是没有找到改客户简称
					buffer.append("第" + i + "条省名为空<br>");
				}
				if (StringUtils.isNotBlank(address.getAddressTypeStr())) {
					address.setAddressType(CommomUtil.nameToValueInDict(address.getAddressTypeStr(), "address_type"));
				} else {
					buffer.append("第" + i + "条送达方类型为空<br>");
					i++;
					continue;
				}
				if (StringUtils.isNotBlank(address.getAreaLevelStr())) {
					address.setAreaLevel(CommomUtil.nameToValueInDict(address.getAreaLevelStr(), "area_level"));
				} else {
					buffer.append("第" + i + "条等级为空<br>");
					i++;
					continue;
				}
				saveAddress(address);
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			buffer.append("文件格式有误(使用导出模板,另存为xls[excel97-2003]格式)");
			return buffer.toString();
		}
		if (buffer.length() < 3) {
			buffer.append("文件上传完毕");
		} else {
			buffer.append("除了以上，已上传完毕，请调整后重新上传");
		}
		return buffer.toString();
	}

	@Override
	public Address getByName(String addressName) {
		if (getAddressCount(addressName) == 1) {
			QueryWrapper<Address> wrapper = new QueryWrapper<>();
			// 下行编辑条件
			wrapper.eq("del_flag", false);
			wrapper.eq("address_name", addressName);
			return baseMapper.selectOne(wrapper);
		}
		return null;
	}

}
