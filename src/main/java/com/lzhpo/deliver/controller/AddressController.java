package com.lzhpo.deliver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.WebUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzhpo.admin.service.UserService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.service.ITerritoryService;

/**
 * <p>
 * 送货地址表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
@Controller
@RequestMapping("deliver/address")
public class AddressController {
	@Autowired
	private IAddressService addressService;

	@Autowired
	private ITerritoryService territoryService;

	@Autowired
	UserService userService;

	@GetMapping(value = "list")
	public String list() {
		return "deliver/address/listAddress";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("deliver:address:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Address> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Address> addressPageData = new PageData<>();
		QueryWrapper<Address> addressWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		addressWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				addressWrapper.like("address_name", keys);
			}
		}
		addressWrapper.orderByDesc("create_date");
		IPage<Address> addressPage = addressService.page(new Page<>(page, limit), addressWrapper);
		addressPageData.setCount(addressPage.getTotal());
		addressPageData.setData(setUserToAddress(addressPage.getRecords()));
		return addressPageData;
	}

	// 创建者，和修改人
	private List<Address> setUserToAddress(List<Address> addresss) {
		addresss.forEach(r -> {
			// 待编辑
			if (StringUtils.isNotBlank(r.getProvinceId())) {
				r.setProvinceName(territoryService.getById(r.getProvinceId()).getName());
			}
			if (StringUtils.isNotBlank(r.getCityId())) {
				r.setCityName(territoryService.getById(r.getCityId()).getName());
			}
			if (StringUtils.isNotBlank(r.getCountiesId())) {
				r.setCountiesName(territoryService.getById(r.getCountiesId()).getName());
			}
			if (StringUtils.isNotBlank(r.getAddressType() + "")) {
				r.setAddressTypeStr(CommomUtil.valueToNameInDict(r.getAddressType(), "address_type"));
			}
			if (StringUtils.isNotBlank(r.getAreaLevel() + "")) {
				r.setAreaLevelStr(CommomUtil.valueToNameInDict(r.getAreaLevel(), "area_level"));
			}
		});

		return addresss;
	}

	/**
	 * 根据id查询
	 */
	// @RequestMapping(value = "/getById")
	// public ResponseWeb<Address> getById(@RequestParam("pkid") String pkid){
	// return null;
	// }

	@GetMapping("add")
	public String add(ModelMap modelMap) {
		/**
		 * 自定义传入add页面的数据
		 */
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		// address_type
		modelMap.put("addressType", CacheUtils.allDicts.get("address_type"));
		modelMap.put("arealevel", CacheUtils.allDicts.get("area_level"));
		return "deliver/address/addAddress";
	}

	@RequiresPermissions("deliver:address:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Address address) {
		if (StringUtils.isBlank(address.getAddressName())) {
			return ResponseEntity.failure("地址名称（不能为空)");
		}
		if (addressService.getAddressCount(address.getAddressName()) > 0) {
			return ResponseEntity.failure("地址名称（不能重复)");
		}
		addressService.saveAddress(address);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:address:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		Address address = addressService.getAddressById(id);
		addressService.deleteAddress(address);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("deliver:address:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Address> addresss) {
		if (addresss == null || addresss.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的角色");
		}
		for (Address r : addresss) {
			addressService.deleteAddress(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Address address = addressService.getAddressById(id);
		QueryWrapper<Territory> query = new QueryWrapper<>();
		query.eq("level", 1);
		modelMap.put("provinceList", territoryService.list(query));
		// address_type
		modelMap.put("addressType", CacheUtils.allDicts.get("address_type"));
		modelMap.put("arealevel", CacheUtils.allDicts.get("area_level"));
		
		if (StringUtils.isNotBlank(address.getCityId())) {
			address.setCityName(territoryService.getById(address.getCityId()).getName());
		}
		if (StringUtils.isNotBlank(address.getCountiesId())) {
			address.setCountiesName(territoryService.getById(address.getCountiesId()).getName());
		}
		
		
		modelMap.put("address", address);

		return "deliver/address/editAddress";
	}

	@RequiresPermissions("deliver:address:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Address address) {
		if (StringUtils.isBlank(address.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		if (StringUtils.isBlank(address.getAddressName())) {
			return ResponseEntity.failure("角色名称不能为空");
		}
		Address oldAddress = addressService.getAddressById(address.getId());
		if (!oldAddress.getAddressName().equals(address.getAddressName())) {
			if (addressService.getAddressCount(address.getAddressName()) > 0) {
				return ResponseEntity.failure("地址名称（不能重复)");
			}
		}
		addressService.updateAddress(address);
		return ResponseEntity.success("操作成功");
	}

	@SysLog("上传文件")
	@PostMapping("upload")
	@ResponseBody
	public ResponseEntity uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
		if (file == null) {
			return ResponseEntity.failure("上传文件为空 ");
		}
		Map map = new HashMap();
		String message = addressService.upload(file);
		return ResponseEntity.success(message).setAny("data", map);
	}
}
