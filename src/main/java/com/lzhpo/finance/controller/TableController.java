package com.lzhpo.finance.controller;

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
import com.lzhpo.common.annotation.SysLog;
import com.lzhpo.common.base.PageData;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.ResponseEntity;
import com.lzhpo.finance.entity.Table;
import com.lzhpo.finance.entity.TableDetail;
import com.lzhpo.finance.entity.TableOption;
import com.lzhpo.finance.service.ITableDetailService;
import com.lzhpo.finance.service.ITableOptionService;
import com.lzhpo.finance.service.ITableService;
import com.lzhpo.sys.entity.Dictionary;
/**
 * <p>
 * 自定义收入表 前端控制器
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Controller
@RequestMapping("finance/table")
public class TableController {
 	@Autowired
    private ITableService tableService;
 	
 	@Autowired
 	private ITableDetailService tableDetailService;

 	@Autowired
 	private ITableOptionService tableOptionService ;
 	
    @Autowired
    UserService userService;

    @GetMapping(value = "list")
    public String list(){
        return "finance/table/listTable";
    }
	
    /**
     * 查询分页数据
     */
    @RequiresPermissions("finance:table:list")
    @PostMapping("list")
    @ResponseBody
    public PageData<Table> list(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "limit",defaultValue = "10")Integer limit,
                               ServletRequest request){
        Map map = WebUtils.getParametersStartingWith(request, "s_");
        PageData<Table> tablePageData = new PageData<>();
        QueryWrapper<Table> tableWrapper = new QueryWrapper<>();
	    //相当于del_flag = 0;
        tableWrapper.eq("del_flag",false);
        if(!map.isEmpty()){
            String keys = (String) map.get("name");
            if(StringUtils.isNotBlank(keys)) {
                tableWrapper.like("name", keys);
            }
        }
        IPage<Table> tablePage = tableService.page(new Page<>(page,limit),tableWrapper);
        tablePageData.setCount(tablePage.getTotal());
        tablePageData.setData(setUserToTable(tablePage.getRecords()));
        return tablePageData;
    }
    //创建者，和修改人
   private List<Table> setUserToTable(List<Table> tables){
        tables.forEach(r -> {
            if(StringUtils.isNotBlank(r.getCreateId())){
                User u = userService.findUserById(r.getCreateId());
                if(StringUtils.isBlank(u.getNickName())){
                  u.setNickName(u.getLoginName());
                }
                r.setCreateUser(u);
            }
              if(StringUtils.isNotBlank(r.getUpdateId())){
                User u  = userService.findUserById(r.getUpdateId());
                if(StringUtils.isBlank(u.getNickName())){
                    u.setNickName(u.getLoginName());
                }
                r.setUpdateUser(u);
            }
        });

        return tables;
    }	
 
    /**
     * 根据id查询
     */
    //@RequestMapping(value = "/getById")
    //public ResponseWeb<Table> getById(@RequestParam("pkid") String pkid){
    //   return null;
    //}
	
	/**
	 * 根据id查询子表
	 */
	@GetMapping("selectDetail")
	@ResponseBody
	public PageData<TableDetail> selectDetail(@RequestParam(value = "page", defaultValue = "1") Integer page,
			@RequestParam(value = "limit", defaultValue = "1000") Integer limit, ServletRequest request,
			String tableId) {
		Map map = WebUtils.getParametersStartingWith(request, "s_");
		PageData<TableDetail> ContTableDetailData = new PageData<>();
		QueryWrapper<TableDetail> TableDetailWrapper = new QueryWrapper<>();
		// 相当于del_flag = 0;
		TableDetailWrapper.eq("table_id", tableId);
		if (!map.isEmpty()) {
			String keys = (String) map.get("name");
			if (StringUtils.isNotBlank(keys)) {
				TableDetailWrapper.like("name", keys);
			}
		}
		TableDetailWrapper.orderByDesc("create_date ");
		IPage<TableDetail> TableDetailPage = tableDetailService.page(new Page<>(page, limit),
				TableDetailWrapper);
		ContTableDetailData.setCount(TableDetailPage.getTotal());
		ContTableDetailData.setData(TableDetailPage.getRecords());
		return ContTableDetailData;
	}
    @GetMapping("add")
    public String add(ModelMap modelMap){
    	List<TableOption> options = tableOptionService.selectAll();
    	List<Dictionary> modular = CacheUtils.allDicts.get("modular");
    	List<Dictionary> incomeType = CacheUtils.allDicts.get("income_type");
    	//income_type
    	modelMap.put("options", options);
    	modelMap.put("modular", modular);
    	modelMap.put("incomeType", incomeType);
        return "finance/table/addTable";
    }

    @RequiresPermissions("finance:table:add")
    @PostMapping("add")
    @ResponseBody
    @SysLog("保存新增数据")
    public ResponseEntity add(@RequestBody Table table){
        if(StringUtils.isBlank(table.getName())){
            return ResponseEntity.failure("关键Id（不能为空)");
        }
        if(tableService.getTableCount(table.getName())>0){
             return ResponseEntity.failure("名称（不能重复)");
        }
        tableService.saveTable(table);
        return ResponseEntity.success("操作成功");
    }
 
    @RequiresPermissions("finance:table:delete")
    @PostMapping("delete")
    @ResponseBody
    @SysLog("删除数据")
    public ResponseEntity delete(@RequestParam(value = "id",required = false)String id){
        if(StringUtils.isBlank(id)){
            return ResponseEntity.failure("角色ID不能为空");
        }
        Table table = tableService.getTableById(id);
        tableService.deleteTable(table);
        return ResponseEntity.success("操作成功");
    }
 
  @RequiresPermissions("finance:table:delete")
    @PostMapping("deleteSome")
    @ResponseBody
    @SysLog("多选删除数据")
    public ResponseEntity deleteSome(@RequestBody List<Table> tables){
        if(tables == null || tables.size()==0){
            return ResponseEntity.failure("请选择需要删除的角色");
        }
        for (Table r : tables){
            tableService.deleteTable(r);
        }
        return ResponseEntity.success("操作成功");
    }
    @GetMapping("edit")
    public String edit(String id,ModelMap modelMap){
       Table table =  tableService.getTableById(id);
    	List<TableOption> options = tableOptionService.selectAll();
    	List<Dictionary> modular = CacheUtils.allDicts.get("modular");
    	List<Dictionary> incomeType = CacheUtils.allDicts.get("income_type");
    	//income_type
    	modelMap.put("options", options);
    	modelMap.put("modular", modular);
    	modelMap.put("incomeType", incomeType);
        Map<String,Object> map = new HashMap();
        modelMap.put("table", table);
      
        return "finance/table/editTable";
   }

    @RequiresPermissions("finance:table:edit")
    @PostMapping("edit")
    @ResponseBody
    @SysLog("保存编辑数据")
    public ResponseEntity edit(@RequestBody Table table){
        if(StringUtils.isBlank(table.getId())){
            return ResponseEntity.failure("关键Id（不能为空)");
        }
        if(StringUtils.isBlank(table.getName())){
            return ResponseEntity.failure("角色名称不能为空");
        }
        Table oldTable =  tableService.getTableById(table.getId());
        if(!oldTable.getName().equals(table.getName())){
             if( tableService.getTableCount(table.getName())>0){
                return ResponseEntity.failure("名称（不能重复)");
            }
        }
         tableService.updateTable(table);
        return ResponseEntity.success("操作成功");
    }
    
	@RequiresPermissions("finance:table:audit")
	@PostMapping("audit")
	@ResponseBody
	@SysLog("审核数据")
	public ResponseEntity audit(@RequestParam(value = "id", required = false) String id,Integer status) {
		if (StringUtils.isBlank(id)) {
			return ResponseEntity.failure("角色ID不能为空");
		}
		tableService.ChangeAduitStatus(status, id);
		return ResponseEntity.success("操作成功");
	}
}
