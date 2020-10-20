Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#receiveBillTime' //指定元素
      ,value: new Date()
  });
  var laydate2 = layui.laydate;
   laydate2.render({
    elem: '#deliverBillTime' //指定元素
      ,value: new Date()
  });


});
layui.use(['form','jquery','layer'],function(){
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer;

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
    }); 
    form.on('select(receiveProvinceId)', function(data){
        $("#receiveCityId").empty();
        $("#receiveAreaId").empty();
            //data.value 得到被选中的值
             var url = '/sys/territory/selectCityByProvinceCode?id=' + data.value;
             $.get(url,function(data){
                $("#receiveCityId").empty();
                $("#receiveCityId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#receiveCityId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
             });
    });
    form.on('select(receiveCityId)', function(data){
            //data.value 得到被选中的值
        $("#receiveAreaId").empty();
             var url = '/sys/territory/selectAreaByCityCode?id=' + data.value;
             $.get(url,function(data){
                $("#receiveAreaId").empty();
                $("#receiveAreaId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#receiveAreaId").append(new Option(item.name,item.id));
                });
                layui.form.render("select");
             });
    });                   
    form.on("submit(editexpressBill)",function(data){
        if(data.field.id == null){
            layer.msg("单据ID不存在");
            return false;
        }
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
            url:"/deliver/expressBill/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据编辑成功！",{time:1000},function(){
                        //刷新父页面
                        parent.location.reload();
                    });
                }else{
                    layer.msg(res.message,{time:1000},function(){
                        //刷新本页面
                        location.reload();
                    });

                }
            }
        });
        return false;
    });

});