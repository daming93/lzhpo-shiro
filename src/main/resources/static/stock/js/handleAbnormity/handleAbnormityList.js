layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t;                  //表格数据变量

    t = {
        elem: '#receiptBillTable',
        url:'/stock/receiptBill/list',
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
            { field:'isExistSlip',title:'送货单',align:'center',width:70,templet:'#isExistSlip'},
            { field:'isExistReceipt',title:'验收单',align:'center',width:70,templet:'#isExistReceipt'},
            { field:'isExistBack',title:'退单',align:'center',width:70,templet:'#isExistBack'},
            { field:'receiptStatusName',title:'回单状态',align:'center',width:70},
            { field:'deliveryReceiptTime',title:'调度回单时间',align:'center',width:170},
            { field:'receiptTime',title:'回单时间',align:'center',width:170},
            { field:'abnormityTypeName',title:'异常类型',align:'center',width:100},
            { field:'abnormityName',title:'异常原因',align:'center',width:100},
            { field:'remarks',title:'备注',align:'center',width:100},
            { field:'createDate',  title: '创建时间',width:'14%', templet:'<span>{{ layui.laytpl.toDateString(d.createDate) }}</span>'}, //单元格内容水平居中
            { fixed:'right', align: 'center',  title: '操作',toolbar: '#receiptBillBar',width:200}
        ]]
    };
    table.render(t);
    //监听工具条
    table.on('tool(receiptBillList)', function(obj){
        var data = obj.data;
        if(obj.event === 'fristEdit'){
                var editIndex = layer.open({
                    title : "调度确认",
                    type : 2,
                    offset: 'rt',
                    area: ['410px','800px'],
                    content : "/stock/receiptBill/edit?type=1&id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回回单列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
        }
        if(obj.event === 'fristBack'){
                layer.confirm("确认撤回首审吗",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/receiptBill/back",{"id":data.id,"status":1},function (res){
                        if(res.success){
                            layer.msg("撤回成功",{time: 1000},function(){
                                table.reload('receiptBillTable', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            );
        }
      if(obj.event === 'finalEdit'){
                var editIndex = layer.open({
                    title : "营管确认",
                    type : 2,
                    offset: 'rt',
                    area: ['410px','800px'],
                    content : "/stock/receiptBill/edit?type=2&id="+data.id,
                    success : function(layero, index){
                        setTimeout(function(){
                            layer.tips('点击此处返回回单列表', '.layui-layer-setwin .layui-layer-close', {
                                tips: 3
                            });
                        },500);
                    }
                });
        }
        if(obj.event === 'finalBack'){
                layer.confirm("确认撤回终审吗",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/receiptBill/back",{"id":data.id,"status":2},function (res){
                        if(res.success){
                            layer.msg("撤回成功",{time: 1000},function(){
                                table.reload('receiptBillTable', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }

                    });
                }
            );
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
        table.reload('receiptBillTable', t);
        return false;
    });

});