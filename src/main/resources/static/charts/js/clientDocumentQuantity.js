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


layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量


    t = {
        elem: '#trunover-table',
        even: true,
        url:'/charts/count/listClientDocumentQuantity',
        method:'post',
        toolbar: true ,
        totalRow: true,
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
            {type:'checkbox', totalRowText: '合计'},
           /* {field:'id',        title: 'ID'   },*/
            {field:'clientName',        title: '客户名称'   },
            {field:'storageCount',        title: '入库单据量'    , totalRow: true},
            {field:'takeoutCount',        title: '出库单据量'   , totalRow: true },
            {field:'returnCout',title:'退库单据量',align:'center', totalRow: true},
            {field:'zlCount',title:'转良单据量',align:'center', totalRow: true},
            {field:'zblCount',title:'转不良单据量',align:'center', totalRow: true},
            {field:'allCount',    align:'center'  ,  title: '合计'  , totalRow: true },
            // {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#materialBar'}
        ]]/*,
        done: function () {
            $("[data-field='id']").css('display','none');
        }*/
    };
    table.render(t);
  
    $('.layui-inline .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
   
     var active={
        count : function(){
            var mode = $('input:radio:checked').val();
            addIndex = layer.open({
                title : "入库单据量",
                type : 2,
                content : "/charts/count/listStorageDocumentQuantity?mode="+mode,
                success : function(layero, addIndex){
                    setTimeout(function(){
                        layer.tips('点击此处返回单据量列表', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    },500);
                }
            });
            //改变窗口大小时，重置弹窗的高度，防止超出可视区域（如F12调出debug的操作）
            $(window).resize(function(){
                layer.full(addIndex);
            });
            layer.full(addIndex);
        }
     }   


    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('trunover-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});