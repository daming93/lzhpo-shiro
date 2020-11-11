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
    table.on('toolbar(deliverContractMainList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('deliverContractMain-table')
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
                    var s_code = $("#s_code").val();
                    $.ajax({
                            type:"POST",
                            url:"/client/deliverContractMain/list?limit=999999",
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
                                }, '车辆合同列表' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }


    $("body").keypress(function(e) {
        if(e.keyCode==10&&e.ctrlKey) {
            $("#adddeliverContractMain").click();
        }
    });
    //监听工具条
    table.on('tool(deliverContractMainList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑合同",
                type : 2,
                content : "/client/deliverContractMain/edit?id="+data.id,
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回合同列表', '.layui-layer-setwin .layui-layer-close', {
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
            //檢查合同状态
            if(data.isUse){
                layer.msg("正在使用的合同无法删除");
                return;
            }
            layer.confirm("你确定要删除该合同么？",{btn:['是的,我确定','我再想想']},
                function(){

                    $.post("/client/deliverContractMain/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('deliverContractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
           if(obj.event === "copy"){
            layer.confirm("你确定要复制该合同么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/client/deliverContractMain/copy",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("复制成功",{time: 1000},function(){
                                table.reload('deliverContractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "back"){
            layer.confirm("你确定要撤销该合同么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/client/deliverContractMain/back",{"id":data.id,"status":1},function (res){
                        if(res.success){
                            layer.msg("撤销成功",{time: 1000},function(){
                                table.reload('deliverContractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "audit"){
            layer.confirm("你确定要审核该合同么(请仔细阅读合同条款)？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/client/deliverContractMain/audit",{"id":data.id,"status":1},function (res){
                        if(res.success){
                            layer.msg("审核成功",{time: 1000},function(){
                                table.reload('deliverContractMain-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
    });
    table.on('rowDouble(deliverContractMainList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "合同详情",
            type : 2,
            content : "/client/deliverContractMain/edit?id="+data.id,
            success : function(layero, index){
                setTimeout(function(){
                    layer.tips('点击此处返回合同列表', '.layui-layer-setwin .layui-layer-close', {
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
    t = {
        elem: '#deliverContractMain-table',
        even: true,
        url:'/client/deliverContractMain/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter',  'print'],
        page: { //支持传退 laypage 组件的所有参数（某些参数除外，如：jump/elem） - 详见文档
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
            {field:'name',        title: '合同名称'   },
            {field:'clientName',        title: '合同名称'   },
            {field:'contractCode',        title: '合同编码'   },
            {field:'startTime',  title: '开始时间',    width:'14%'}, 
            {field:'overTime',  title: '结束时间',    width:'14%'}, 
            {field:'remarks',        title: '备注'   },
            {field:'createDate',  title: '创建时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.createDate) }}</div>',unresize: true}, //单元格内容水平居中
            {field:'updateDate',  title: '修改时间',    width:'14%',templet:'<div>{{ layui.laytpl.toDateString(d.updateDate) }}</div>',unresize: true}, //单元格内容水平居中
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#deliverContractMainBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
          addUser : function(){
            addIndex = layer.open({
                title : "添加合同单",
                type : 2,
                content : "/client/deliverContractMain/add",
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回合同单列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }, 
                end: function () {
                    //重新加载当前页面
                    location.reload();
                }
            });
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
            $(window).resize(function(){
                layer.full(addIndex);
            });
            layer.full(addIndex);
        },
        //批量删除
        deleteSome : function(){
            var checkStatus = table.checkStatus('deliverContractMain-table'),
                data = checkStatus.data;
            if(data.length > 0){
                console.log(JSON.stringify(data));
                layer.confirm("你确定要删除这些合同么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/client/deliverContractMain/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('deliverContractMain-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的合同",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('deliverContractMain-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});