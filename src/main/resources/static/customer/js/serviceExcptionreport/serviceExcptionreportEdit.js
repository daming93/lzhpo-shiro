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
    elem: '#takeoutTime' //指定元素
     ,value: new Date()
  });
  var laydate2 = layui.laydate;
  
  //执行一个laydate实例
  laydate2.render({
    type:"datetime",
    elem: '#timeStart' //指定元素
    ,value: new Date()
  });

 var laydate3 = layui.laydate;
  //执行一个laydate实例
  laydate3.render({
     type:"datetime",
    elem: '#timeEnd' //指定元素
    ,value: new Date()
  });
});
layui.use(['form','jquery','layer'],function(){
      var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer;
        form.on('select(abnormityTypeId)', function(data){
             var url = '/customer/abnormity/selectAbnorityByType?type=' + data.value;
             $.get(url,function(data){
                $("#excptionId").empty();
                $("#excptionId").append(new Option("",""));
                $.each(data,function(index,item){
                    $("#excptionId").append(new Option(item.cause,item.id));
                });
                layui.form.render("select");
             });
             $("#abnormityTypeName").val(data.elem[data.elem.selectedIndex].text);
        });
        form.on('select(clientId)', function(data){
            $("#clientName").val(data.elem[data.elem.selectedIndex].text);
        });

        form.on('select(addressId)', function(data){
            $("#addressName").val(data.elem[data.elem.selectedIndex].text);
        });

        form.on('select(excptionId)', function(data){
            $("#excptionName").val(data.elem[data.elem.selectedIndex].text);
        });

    form.on("submit(editserviceExcptionreport)",function(data){
        if(data.field.id == null){
            layer.msg("选项ID不存在");
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
            url:"/customer/serviceExcptionreport/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("选项编辑成功！",{time:1000},function(){
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