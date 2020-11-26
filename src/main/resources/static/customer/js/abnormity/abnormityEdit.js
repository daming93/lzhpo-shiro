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
    form.on("submit(editabnormity)",function(data){
        if(data.field.id == null){
            layer.msg("异常ID不存在");
            return false;
        }
        var menus = [];
        var c = $('form').find('input[type="checkbox"]');
        c.each(function(index, customer){
            var m = {};
            if(customer.checked){
                m.id = customer.value;
                menus.push(m);
            }
        });
        data.field.menuSet = menus;
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/customer/abnormity/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("异常编辑成功！",{time:1000},function(){
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