Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
layui.use(['form','layer','jquery'], function(){
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery;
    form.on('select(provinceId)', function(data){
        $("#cityId").empty();
        $("#countiesId").empty();
            //data.value 得到被选中的值
             var url = '/sys/territory/selectCityByProvinceCode?id=' + data.value;
             $.get(url,function(data){
                $("#cityId").empty();
                $("#cityId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#cityId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
             });
             var areaName =  $("#provinceId").find("option:selected").text() + $("#cityId").find("option:selected").text() +$("#countiesId").find("option:selected").text();
             $("#areaName").val(areaName);
    });
    form.on('select(cityId)', function(data){
            //data.value 得到被选中的值
        $("#countiesId").empty();
             var url = '/sys/territory/selectAreaByCityCode?id=' + data.value;
             $.get(url,function(data){
                $("#countiesId").empty();
                $("#countiesId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#countiesId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
             });
             var areaName =  $("#provinceId").find("option:selected").text() + $("#cityId").find("option:selected").text() +$("#countiesId").find("option:selected").text();
             $("#areaName").val(areaName);
    }); 
    form.on('select(countiesId)', function(data){
            //data.value 得到被选中的值
             var areaName =  $("#provinceId").find("option:selected").text() + $("#cityId").find("option:selected").text() +$("#countiesId").find("option:selected").text();
             $("#areaName").val(areaName);
    });          
    form.on('submit(addaddress)',function(data){
        var menus = [];
        var c = $('form').find('input[type="checkbox"]');
        c.each(function(index, item){
            var m = {};
            if(item.checked){
                m.id = item.value;
                menus.push(m);
            }
        });
        data.field.menuSet = menus;
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/deliver/address/add",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("地址添加成功！",{time:1000},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message);
                }
            }
        });
        return false;
    });
});