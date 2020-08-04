package com.lzhpo.material.item.controller;

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
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.material.item.entity.Brand;
import com.lzhpo.material.item.entity.Category;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IBrandService;
import com.lzhpo.material.item.service.ICategoryService;
import com.lzhpo.material.item.service.IClientitemService;

/**
 * <p>
 * 品项表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-04-16
 */
@Controller
@RequestMapping("/item/clientitem")
public class ClientitemController {
	@Autowired
	private IClientitemService clientitemService;

	@Autowired
	UserService userService;

	@Autowired
	private IBasicdataService basicdateService;

	@Autowired
	private IBrandService brandService;

	@Autowired
	private ICategoryService categoryService;

	@GetMapping(value = "list")
	public String list(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		modelMap.put("basicDatas", basicDatas); 
		return "material/item/clientitem/listClientitem";
	}

	/**
	 * 查询分页数据
	 */
	@RequiresPermissions("item:clientitem:list")
	@PostMapping("list")
	@ResponseBody
	public PageData<Clientitem> list(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "10") Integer limit, ServletRequest request) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<Clientitem> clientitemPageData = new PageData<>();
		QueryWrapper<Clientitem> clientitemWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		clientitemWrapper.eq("del_flag", false);
		if (!map.isEmpty()) {
			String name = (String) map.get("name");
			if (StringUtils.isNotBlank(name)) {
				clientitemWrapper.like("name", name);
			}
			String code = (String) map.get("code");
			if (StringUtils.isNotBlank(code)) {
				clientitemWrapper.like("code", code);
			}
			String clientId = (String) map.get("clientId");
			if (StringUtils.isNotBlank(clientId)) {
				clientitemWrapper.like("client_id", clientId);
			}
		}
		IPage<Clientitem> clientitemPage = clientitemService.page(new Page<>(page, limit), clientitemWrapper);
		clientitemPageData.setCount(clientitemPage.getTotal());
		clientitemPageData.setData(setUserToClientitem(clientitemPage.getRecords()));
		return clientitemPageData;
	}

	// 创建者，和修改人
	private List<Clientitem> setUserToClientitem(List<Clientitem> clientitems) {
		clientitems.forEach(r -> {
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
				r.setClientId(basicdateService.getById(r.getClientId()).getClientShortName());
			}
		});

		return clientitems;
	}

	/**
	 * 根据客户id查询(出库，退货使用) 出库使用
	 */
	@ResponseBody
	@RequestMapping(value = "/getByClientId")
	public List<Clientitem> getByClientId(@RequestParam("clientId") String clientId) {
		return clientitemService.selectByClientId(clientId);
	}
	/**
	 * 根据客户id查询(入库，退货使用) 全部品项
	 */
	@ResponseBody
	@RequestMapping(value = "/getByClientIdAll")
	public List<Clientitem> getByClientIdAll(@RequestParam("clientId") String clientId) {
		QueryWrapper<Clientitem> clientitemWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		clientitemWrapper.eq("del_flag", false);
		clientitemWrapper.eq("client_id", clientId);
		return clientitemService.list(clientitemWrapper);
	}
	/**
	 * 根据id查询
	 */
	@ResponseBody
	@RequestMapping(value = "/getById")
	public Clientitem getById(@RequestParam("itemId") String itemId) {
		return clientitemService.getById(itemId);
	}

	
	@GetMapping("add")
	public String add(ModelMap modelMap) {
		List<Basicdata> basicDatas = basicdateService.selectAll();
		List<Brand> brands = brandService.selectAll();
		List<Category> categorys = categoryService.selectAll();
		/**
		 * 自定义传入add页面的数据
		 */
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("brands", brands);
		modelMap.put("categorys", categorys);
		return "material/item/clientitem/addClientitem";
	}

	@RequiresPermissions("item:clientitem:add")
	@PostMapping("add")
	@ResponseBody
	@SysLog("保存新增数据")
	public ResponseEntity add(@RequestBody Clientitem clientitem) {
		if (StringUtils.isBlank(clientitem.getName())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		if (clientitemService.getClientitemCount(clientitem.getCode(), clientitem.getClientId()) > 0) {
			return ResponseEntity.failure("同一个客户得品项编号不能相同");
		}
		clientitemService.saveClientitem(clientitem);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("item:clientitem:delete")
	@PostMapping("delete")
	@ResponseBody
	@SysLog("删除数据")
	public ResponseEntity delete(@RequestParam(value = "id", required = false) String id) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("品项ID不能为空");
		}
		Clientitem clientitem = clientitemService.getClientitemById(id);
		clientitemService.deleteClientitem(clientitem);
		return ResponseEntity.success("操作成功");
	}

	@RequiresPermissions("item:clientitem:delete")
	@PostMapping("deleteSome")
	@ResponseBody
	@SysLog("多选删除数据")
	public ResponseEntity deleteSome(@RequestBody List<Clientitem> clientitems) {
		if (clientitems == null || clientitems.size() == 0) {
			return ResponseEntity.failure("请选择需要删除的品项");
		}
		for (Clientitem r : clientitems) {
			clientitemService.deleteClientitem(r);
		}
		return ResponseEntity.success("操作成功");
	}

	@GetMapping("edit")
	public String edit(String id, ModelMap modelMap) {
		Clientitem clientitem = clientitemService.getClientitemById(id);
		List<Basicdata> basicDatas = basicdateService.selectAll();
		List<Brand> brands = brandService.selectAll();
		List<Category> categorys = categoryService.selectAll();
		/**
		 * 自定义传入add页面的数据
		 */
		modelMap.put("basicDatas", basicDatas);
		modelMap.put("brands", brands);
		modelMap.put("categorys", categorys);
		modelMap.put("clientitem", clientitem);

		return "material/item/clientitem/editClientitem";
	}

	@RequiresPermissions("item:clientitem:edit")
	@PostMapping("edit")
	@ResponseBody
	@SysLog("保存编辑数据")
	public ResponseEntity edit(@RequestBody Clientitem clientitem) {
		if (StringUtils.isBlank(clientitem.getId())) {
			return ResponseEntity.failure("修改提示信息（不能为空)");
		}
		if (StringUtils.isBlank(clientitem.getName())) {
			return ResponseEntity.failure("品名不能为空");
		}
		Clientitem oldClientitem = clientitemService.getClientitemById(clientitem.getId());
		if (!oldClientitem.getCode().equals(clientitem.getCode())) {
			if (clientitemService.getClientitemCount(clientitem.getCode(), clientitem.getClientId()) > 0) {
				return ResponseEntity.failure("同一个客户得品项编号不能相同");
			}
		}
		clientitemService.updateClientitem(clientitem);
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
		List<Basicdata> basicDatas = basicdateService.selectAll();
		String message = clientitemService.upload(file, basicDatas);
		return ResponseEntity.success(message).setAny("data", map);
	}
}
