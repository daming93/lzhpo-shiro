layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t;                  //表格数据变量

    t = {
        elem: '#handleAbnormityTable',
        url:'/stock/handleAbnormity/list',
        method:'post',
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
            { type:'checkbox'},
            { field:'clientName',title:'客户名称',align:'center',width:100,templet:'<div>{{  d.takeout.clientName }}</div>'},
            { field:'takeout.deliveryTime',title:'发货时间',align:'center',width:110,templet:'<div>{{  d.takeout.deliveryTime }}</div>'},
            { field:'takeout.takeoutTime',title:'配送时间',align:'center',width:110,templet:'<div>{{  d.takeout.takeoutTime }}</div>'},
            { field:'takeout.clientCode',title:'客户单号',align:'center',width:100,templet:'<div>{{  d.takeout.clientCode }}</div>'},
            { field:'takeout.addressName',title:'送达方',align:'center',width:150,templet:'<div>{{  d.takeout.addressName }}</div>'},
            { field:'takeout.number',title:'数量',align:'center',width:70,templet:'<div>{{  d.takeout.number }}</div>'},
            { field:'waybill.code',title:'路单单号',align:'center',width:70,templet:'<div>{{  d.wayBill.code }}</div>'},
            { field:'abnormityTypeName',title:'异常类型',align:'center',width:100},
            { field:'abnormityName',title:'异常原因',align:'center',width:100},
            { field:'remarks',title:'备注',align:'center',width:100},
            { field:'createDate',  title: '创建时间',width:'14%', templet:'<span>{{ layui.laytpl.toDateString(d.createDate) }}</span>'}, //单元格内容水平居中
            { fixed:'right', align: 'center',  title: '操作',toolbar: '#handleAbnormityBar',width:200}
        ]]
    };
    table.render(t);
    //监听工具条
    table.on('tool(handleAbnormityList)', function(obj){
        var data = obj.data;
       if(obj.event === 'lookAbnormity'){
                var editIndex = layer.open({
                    title : "查看异常",
                       type : 2,
                    offset: 'rt',
                    area: ['410px','800px'],
                    content : "/stock/handleAbnormity/edit?id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回路单列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
            }
            if(obj.event === 'editAbnormity'){
                var editIndex = layer.open({
                    title : "审核异常",
                       type : 2,
                    offset: 'rt',
                    area: ['410px','800px'],
                    content : "/stock/handleAbnormity/edit?id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回路单列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
            }
            if(obj.event === "back"){
                layer.confirm("你确定要退回该异常吗？",{btn:['是的,我确定','我再想想']},
                    function(){
                        $.post("/stock/handleAbnormity/back",{"id":data.id},function (res){
                            if(res.success){
                                layer.msg("退回成功",{time: 1000},function(){
                                    table.reload('handleAbnormityTable', t);
                                });
                            }else{
                                layer.msg(res.message);
                            }
                        });
                    }
                )
            }
             if(obj.event === "delete"){
                layer.confirm("你确定要删除该异常吗？",{btn:['是的,我确定','我再想想']},
                    function(){
                        $.post("/stock/handleAbnormity/delete",{"id":data.id},function (res){
                            if(res.success){
                                layer.msg("退回成功",{time: 1000},function(){
                                    table.reload('handleAbnormityTable', t);
                                });
                            }else{
                                layer.msg(res.message);
                            }
                        });
                    }
                )
            }
           if(obj.event === 'littleBack'){
            var editIndex = layer.open({
                title : "少项直退",
                type : 2,
                content : "/stock/directReturn/add?type=1&takeoutId="+data.takeoutId+"&handAbnormityId="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回退库列表', '.layui-layer-setwin .layui-layer-close', {
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
        if(obj.event === 'moreBack'){
            var editIndex = layer.open({
                title : "多项直退",
                type : 2,
                content : "/stock/directReturn/add?type=2&takeoutId="+data.takeoutId+"&handAbnormityId="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回退库列表', '.layui-layer-setwin .layui-layer-close', {
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

    });
    //功能按钮
    var active={
        
    };

    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });

    //搜索
    form.on("submit(searchForm)",function(data){
        t.where = data.field;
        table.reload('handleAbnormityTable', t);
        return false;
    });

});