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
                $("#abnormityId").empty();
                $("#abnormityId").append(new Option("",""));
                $.each(data,function(index,item){
                    $("#abnormityId").append(new Option(item.cause,item.id));
                });
                layui.form.render("select");
             });
        });
                 
    form.on("submit(editreceiptBill)",function(data){
        if(data.field.id == null){
            layer.msg("ID不存在");
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
        console.log(data.field);
        if(parseFloat(data.field.volume)<=parseFloat(data.field.adjustmentVolume)){
            layer.msg("不能大于等于原体积");
            return false;
        }
        if(parseFloat(data.field.weight)<=parseFloat(data.field.adjustmentWeight)){
            layer.msg("不能大于等于原重量");
            return false;
        }
        data.field.menuSet = menus;
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/receiptBill/edit",
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