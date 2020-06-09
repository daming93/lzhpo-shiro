Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//整转零
function zero(number,rate){
    var arr = number.split(".");
    var a = arr[0]*rate;
    var b = 0;
    if(arr.length==2){
        b = arr[1];
    }
    return parseInt(a)+parseInt(b);
}

//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  //执行一个laydate实例
  laydate.render({
    elem: '#takeoutTime' //指定元素
  });
});
    
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;

        var takeoutId =     document.getElementById("takeoutId").value;   

        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/stock/takeout/selectDetail?takeoutId="+takeoutId
          ,type:"get"
          ,async:false
          ,dataType:"json"
          , success: function(result){
              tableData=result.data;
            }
        });
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "layTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: 'dataTable',
            data:tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: 2000, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},

                { field:'itemCode',title:'系统物料编号',align:'center',width:160},
                { field:'itemName',title:'品牌系列',align:'center',width:200},
                { field:'depot',title:'储位',align:'center',width:130},
                { field:'batch',title:'批次',align:'center',width:100},
                { field:'numZ',title:'数量(整)',edit: 'text',event:'numZ',align:'center',width:80},
                { field:'number',title:'数量(零)',align:'center',width:80},
                { field:'maxNumber',title:'剩余库存',align:'center',width:80},
                { field:'rate',title:'换算率',align:'center',width:100},
                {field: 'id', title: '操作', width:70,templet: function(d){
                    return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.id +'"><i class="layui-icon layui-icon-delete"></i>移除</a>';
                }}
            ]],
            done: function(res, curr, count){
            }
        });
         //监听单元格编辑
          table.on('edit(dataTable)', function(obj){
            var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
          
            //  获取单元格编辑之前td的选择器
            var selector = obj.tr.selector+' td[data-field="'+obj.field+'"] div';
            // 单元格编辑之前的值
            var oldtext = $(selector).text();
            obj.update({
                      number: zero(value,obj.data.rate),
                      maxNumber:obj.data.maxNumber+parseInt(zero(oldtext,obj.data.rate)-zero(value,obj.data.rate))
                    });
             $.ajax({
            type:"POST",
            url:"/stock/takeout/editDetail",
            dataType:"json",
            contentType:"application/json",
            data: JSON.stringify(obj.data) ,
            success:function(res){
                if(res.success){
                    
                   // layer.msg('[ID: '+ data.itemId +']数量更改为：'+ value);
                }else{
                    layer.msg(res.message);
                }
            }
           });
          });
         //监听工具条 
        table.on('tool(dataTable)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
          var data = obj.data; //获得当前行数据
          var layEvent = obj.event; //获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
          var tr = obj.tr; //获得当前行 tr 的 DOM 对象（如果有的话）
         
          if(layEvent === 'del'){ //删除
            layer.confirm('真的删除行么', function(index){
              obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
              layer.close(index);
              //向服务端发送删除指令
               $.ajax({
                    type:"POST",
                    url:"/stock/takeout/deleteDetail",
                    dataType:"json",
                    contentType:"application/json",
                    data: JSON.stringify(obj.data) ,
                    success:function(res){
                        if(res.success){
                           layer.msg('删除成功');
                        }else{
                            layer.msg(res.message);
                        }
                    }
                   });
            });
          } 
        });
        form.on('submit(edittakeout)',function(data){
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/takeout/edit",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
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

