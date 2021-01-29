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
  var laydate1 = layui.laydate;
  //执行一个laydate实例
  laydate1.render({
    elem: '#deliveryTime' //指定元素
    ,value: new Date()
  });

});
 
layui.use(['form','jquery','layer'],function(){


    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer;

   
    form.on("submit(addlineTakeout)",function(data){
     
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/lineTakeout/add",
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