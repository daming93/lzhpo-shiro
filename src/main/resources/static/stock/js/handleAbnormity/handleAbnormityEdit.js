Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};

layui.use(['form','jquery','layer'],function(){


    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer;
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
        
        form.on('select(excptionId)', function(data){
            $("#excptionName").val(data.elem[data.elem.selectedIndex].text);
        });
   
    form.on("submit(addhandleAbnormity)",function(data){
     
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/handleAbnormity/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("编辑成功！",{time:1000},function(){
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