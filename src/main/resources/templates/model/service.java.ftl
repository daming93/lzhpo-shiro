package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};
import java.util.List;
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {
	//获取满足某个条件的记录 以便不插入重复记录
	long get${entity}Count(String name);
	
	//保存实例 返回该实例
	${entity} save${entity}(${entity} ${entity?uncap_first});

	//根据实例Id获取实例
	${entity} get${entity}ById(String id);

	//更新单条记录
	void update${entity}(${entity} ${entity?uncap_first});
	
	//删除一条记录 通常为软删
	void delete${entity}(${entity} ${entity?uncap_first});

	//选取所有记录
	List<${entity}> selectAll();

	//分页查询数据在父类


}
</#if>
