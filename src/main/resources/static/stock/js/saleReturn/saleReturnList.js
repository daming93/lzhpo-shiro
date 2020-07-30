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
    table.on('toolbar(saleReturnList)', function(obj) {
        var checkStatus = table.checkStatus(obj.config.id),
            data = checkStatus.data; //获取选中的数据
            switch (obj.event) {
                case 'table_export':
                    exportFile('saleReturn-table')
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
                            url:"/stock/saleReturn/list?limit=999999",
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
                                }, '退单列表' + new Date().toLocaleString() + '.xlsx', 'xlsx');

                            }
                    });
    }


    $("body").keypress(function(e) {
        if(e.keyCode==10&&e.ctrlKey) {
            $("#addsaleReturn").click();
        }
    });
    //监听工具条
    table.on('tool(saleReturnList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "编辑退库",
                type : 2,
                content : "/stock/saleReturn/edit?id="+data.id,
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
        if(obj.event === "del"){
            layer.confirm("你确定要删除该退库么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/saleReturn/delete",{"id":data.id},function (res){
                        if(res.success){
                            layer.msg("删除成功",{time: 1000},function(){
                                table.reload('saleReturn-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
        if(obj.event === "back"){
            layer.confirm("你确定要撤销该退库么？",{btn:['是的,我确定','我再想想']},
                function(){
                    $.post("/stock/saleReturn/back",{"id":data.id,"status":1},function (res){
                        if(res.success){
                            layer.msg("撤销成功",{time: 1000},function(){
                                table.reload('saleReturn-table', t);
                            });
                        }else{
                            layer.msg(res.message);
                        }
                    });
                }
            )
        }
         if(obj.event === "history"){
           $('#show').html("");//清空上一个
           $.post("/stock/saleReturn/history",{"saleReturnId":data.id},function (res){
                        var html ='<ul class="layui-timeline">';
                        for(i in res){
                            html+= ' <li class="layui-timeline-item"> <i class="layui-icon layui-timeline-axis">&#xe63f;</i>                <div class="layui-timeline-content layui-text">'
                           +'<h3 class="layui-timeline-title">'+res[i].createDate+'</h3><p>'+
                                '<br>'+res[i].createUser.nickName+'对该张单据进行了'+ res[i].typeStr+'操作'+
                              '</p> </div>  </li>  ';
                        }
                        html += '</ul>';
                        $('#show').html(html);
                        layer.open({
                            type: 1,
                            area: '300px',
                            content: $('#show') //这里content是一个DOM，注意：最好该元素要存放在body最外层，否则可能被其它的相对元素所影响
                        });       
                    });

           


         

         }
        
    });
    table.on('rowDouble(saleReturnList)', function(obj){
        var data = obj.data;
        var editIndex = layer.open({
            title : "退库详情",
            type : 2,
            content : "/stock/saleReturn/edit?id="+data.id,
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
    });
    t = {
        elem: '#saleReturn-table',
        even: true,
        url:'/stock/saleReturn/list',
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
            {field:'returnTime',  title: '退库时间',    width:'10%'}, 
            {field:'clientName',        title: '客户名称'   },
            {field:'clientCode',        title: '客户单号' ,    width:'12%'  },
            {field:'systemCode',        title: '系统单号' ,    width:'13%'   },
            {field:'total',  title: '整'}, 
            {field:'scatteredNum',  title: '零'}, 
            {field:'number',        title: '合计'   },
            {field:'volume',        title: '体积(m³) '   },
            {field:'weight',        title: '重量(kg)'   },
            {field:'trayNum',        title: '件/托(kg)'   },
            {field:'statusStr',        title: '状态'   },
            {field:'remarks',        title: '备注'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#saleReturnBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
    var active={
          addUser : function(){
            var continuity = $("#continuity")[0].checked;
            addIndex = layer.open({
                title : "添加退库单",
                type : 2,
                content : "/stock/saleReturn/add?continuity="+continuity,
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回退库单列表', '.layui-layer-setwin .layui-layer-close', {
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
            var checkStatus = table.checkStatus('saleReturn-table'),
                data = checkStatus.data;
            if(data.length > 0){
                console.log(JSON.stringify(data));
                layer.confirm("你确定要删除这些退库么？",{btn:['是的,我确定','我再想想']},
                    function(){
                        var deleteindex = layer.msg('删除中，请稍候',{icon: 16,time:false,shade:0.8});
                        $.ajax({
                            type:"POST",
                            url:"/stock/saleReturn/deleteSome",
                            dataType:"json",
                            contentType:"application/json",
                            data:JSON.stringify(data),
                            success:function(res){
                                layer.close(deleteindex);
                                if(res.success){
                                    layer.msg("删除成功",{time: 1000},function(){
                                        table.reload('saleReturn-table', t);
                                    });
                                }else{
                                    layer.msg(res.message);
                                }
                            }
                        });
                    }
                )
            }else{
                layer.msg("请选择需要删除的退库",{time:1000});
            }
        }
    };
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('saleReturn-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});