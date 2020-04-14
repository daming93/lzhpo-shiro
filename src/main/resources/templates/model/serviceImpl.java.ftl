package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {
	@Override
    public long get${entity}Count(String name) {
        QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "${entity}s", allEntries = true)
    public ${entity} save${entity}(${entity} ${entity?uncap_first}) {
        baseMapper.insert(${entity?uncap_first});
        /**
	*预留编辑代码 
	*/
        return ${entity?uncap_first};
    }

    @Override
    public ${entity} get${entity}ById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "${entity}s", allEntries = true)
    public void update${entity}(${entity} ${entity?uncap_first}) {
        baseMapper.updateById(${entity?uncap_first});
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "${entity}s", allEntries = true)
    public void delete${entity}(${entity} ${entity?uncap_first}) {
        ${entity?uncap_first}.setDelFlag(true);
        baseMapper.updateById(${entity?uncap_first});
    }

    @Override
    @Cacheable("${entity}s")
    public List<${entity}> selectAll() {
        QueryWrapper<${entity}> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
</#if>
