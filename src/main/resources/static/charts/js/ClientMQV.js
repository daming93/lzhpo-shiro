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
        url:'/charts/count/listClientMQV',
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
            {field:'storageVolume',        title: '入库总体积'    , totalRow: true},
            {field:'storageWeight',        title: '入库总重量'   , totalRow: true },
            {field:'storageNumber',title:'入库总零数',align:'center', totalRow: true},
            {field:'storageTotal',title:'入库总数',align:'center', totalRow: true},
            {field:'takeoutVolume',        title: '出库总体积'    , totalRow: true},
            {field:'takeoutWeight',        title: '出库总重量'   , totalRow: true },
            {field:'takeoutNumber',title:'出库总零数',align:'center', totalRow: true},
            {field:'takeoutTotal',title:'出库总数',align:'center', totalRow: true},
             {field:'returnVolume',        title: '退库总体积'    , totalRow: true},
            {field:'returnWeight',        title: '退库总重量'   , totalRow: true },
            {field:'returnNumber',title:'退库总零数',align:'center', totalRow: true},
            {field:'returnTotal',title:'退库总数',align:'center', totalRow: true},
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
                title : "总方重 ",
                type : 2,
                content : "/charts/count/showclientCountByTime?mode="+mode,
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