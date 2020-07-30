//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#startTime' //指定元素
  });
  var laydate2 = layui.laydate;
  
  //执行一个laydate实例
  laydate2.render({
    elem: '#overTime' //指定元素
  });
});


layui.config({
                base:"/static/layui/layui_exts/"
            }).use(['layer','form','table' ,'upload', 'excel'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        excel = layui.excel,
        t; //表格变量
      
    //监听头工具栏事件
    table.on('toolbar(inventoryList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('inventory-table')
                    break;
                };
            });
    /**
                 * by yutons
                 * Array.from() 非常方便的将一个类数组的集合 ==》 数组，直接使用数组身上的方法。例如：常用的map，foreach… 
                 * 但是，问题来了，IE不识别Array.from这个方法。所以写了它兼容IE的写法。
                 */
                if (!Array.from) {
                    Array.from = function(el) {
                        return Array.apply(this, el);
                    }
                }
                
//表格导出
                function exportFile(id) {
                    //根据传入tableID获取表头
                    var headers = $("div[lay-id=" + id + "] .layui-table-box table").get(0);
                    var htrs = Array.from(headers.querySelectorAll('tr'));
                    var titles = {};
                    var fiterArr = [];
                    for (var j = 0; j < htrs.length; j++) {
                        var hths = Array.from(htrs[j].querySelectorAll("th"));
                        for (var i = 0; i < hths.length; i++) {

                            var clazz = hths[i].getAttributeNode('class').value;
                            fiterArr[i] = hths[i].getAttributeNode('data-field').value;
                            if (clazz != ' layui-table-col-special' && clazz != 'layui-hide') {
                                //排除居左、具有、隐藏字段
                                //修改:默认字段data-field+i,兼容部分数据表格中不存在data-field值的问题
                                titles[hths[i].getAttributeNode('data-field').value] = hths[i].innerText;
                            }
                        }
                    }
                    $.ajax({
                            type:"POST",
                            url:"/warehouse/inventory/list?limit=999999",
                            dataType:"json",
                            contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                            data:$('.layui-form').serialize(),
                            success:function(res){
                                  // 假如返回的 res.data 是需要导出的列表数据
                                // 1. 数组头部新增表头
                                res.data.unshift(titles);
                              //  2. 如果需要调整顺序，请执行梳理函数
                                var data = excel.filterExportData(res.data, fiterArr);
                                 //导出excel
                                excel.exportExcel({
                                    sheet1: data
                                }, '盘点列表' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }

    //监听工具条
    table.on('tool(inventoryList)', function(obj){
        var data = obj.data;
         if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑盘点",
                type : 2,
                content : "/warehouse/inventory/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回盘点列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
            $(window).resize(function(){
                layer.full(editIndex);
            });
            layer.full(editIndex);
        }
        if(obj.event === "del"){
            layer.confirm("你确定要删除该盘点么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/warehouse/inventory/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('inventory-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "ensure"){
            layer.confirm("你确定要锁定该盘点么(锁定之后无法撤销)",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/warehouse/inventory/ensure",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("锁定成功",{time: 1000},function(){
                                table.reload('inventory-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        
    });
    t = {
        elem: '#inventory-table',
        even: true,
        url:'/warehouse/inventory/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print'],
        page: { //支持传入 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip'], //自定义分页布局
            //,curr: 5 //设定初始在第 5 页
            groups: 6, //只显示 1 个连续页码
            first: "首页", //显示首页
            last: "尾页", //显示尾页
            limits:[3,10, 20, 30]
        },
        width: $(parent.window).width()-223,
        cols: [[
            {type:'checkbox'},
           /* {field:'id',        title: 'ID'   },*/
            {field:'clientName',        title: '客户'   },
            {field:'auditorTypeStr',        title: '盘点类型' ,    },
            {field:'inventoryTime',        title: '盘点时间' ,    },
            { field:'statusStr',title:'盘点结果',align:'center'},
            { field:'typeStr',title:'状态',align:'center'},
            { field:'inventoryStatusStr',title:'单据状态',align:'center'},
            {field:'createUser',  title: '盘点人',templet:'<div>{{  d.createUser.nickName }}</div>'},
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#inventoryBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };

    table.render(t);
    var active={
          addInventory : function(){
            var continuity =$('input[name="s_continuity"]:checked').val();
            var startTime = $("#startTime").val();
            var endTime = $("#overTime").val();
            var batchStatus = $('input[name="s_mode"]:checked').val();
            var clientId =  $("#clientId").val();
            if(!clientId){
                layer.msg('选择盘点时，客户是必填项'); 
                return;
            }

            if(continuity=="off"){//异动盘点
                if(startTime&&endTime){

                }else{
                     layer.msg('选择异动盘点时，开始时间和结束时间是必填项');
                     return;
                }
            }
            $.ajax({
                type:"POST",
                url:"/warehouse/inventory/add",
                dataType:"json",
                contentType:'application/x-www-form-urlencoded; charset=UTF-8',
                data:{
                    'continuity':continuity,
                    'startTime':startTime,
                    'endTime':endTime,
                    'batchStatus':batchStatus,
                    'clientId':clientId    

                },
                success:function(res){
                    if(res.success){
                        parent.layer.msg("单据添加成功！",{time:1000},function(){
                            location.reload(); 
                        });
                    }else{
                        layer.msg(res.message);
                    }
                }
            });
        },
        //批量删除
        deleteSome : function(){
            var checkStatus = table.checkStatus('inventory-table'),
                data = checkStatus.data;
            if(data.length > 0){
                layer.confirm("你确定要删除这些盘点么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/warehouse/inventory/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('inventory-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的盘点",{time:1000});
            }
        }
    };

    table.on('rowDouble(inventoryList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "盘点详情",
            type : 2,
            content : "/warehouse/inventory/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回盘点列表', '.layui-layer-setwin .layui-layer-close', {
                        tips: 3
                    });
                },500);
            }
        });
        //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
        $(window).resize(function(){
            layer.full(editIndex);
        });
        layer.full(editIndex);
    });
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('inventory-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});