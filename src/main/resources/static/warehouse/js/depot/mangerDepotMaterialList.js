layui.use(['layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量


    //监听工具条
    table.on('tool(materialList)', function(obj){
        var data = obj.data;
       if(obj.event === 'change'){
               var editIndex = layer.open({
                        title : "储位库存",
                        type : 2,
                        content : "/warehouse/changeMaterial/add?materialDdepotId="+data.id+"&code="+data.systemCode+"&itemName="+data.itemName,
                        success : function(layero, index){
                            setTimeout(function(){
                                layer.tips('点击此处返回列表', '.layui-layer-setwin .layui-layer-close', {
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
    t = {
        elem: '#material-table',
        even: true,
        url:'/stock/material/mangerListDepotMaterial',
        method:'post',
        toolbar: true ,
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
            {field:'systemCode',        title: '品项编码'   },
            {field:'itemName',        title: '名称' ,    width:'15%'  },
            {field:'batchNumber',        title: '批次号' ,    width:'10%'   },
            {field:'depot',        title: '储位' ,    width:'10%'   },
            { field:'wholeNum',title:'数量(整)',align:'center',width:100},
            { field:'scatteredNum',title:'数量(零)',align:'center',width:100},
            { field:'availableNum',title:'合计',align:'center',width:100},
            { field:'rate',title:'换算率',align:'center',width:100},
            // {field:'materialStatus',    align:'center'  ,  title: '物料冻结状态'   },
            {field:'typeStr',   align:'center'   ,  title: '状态'   },
            {title: '操作',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#materialBar'}
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
   
    //搜索
    form.on("submit(searchForm)",function(data){
        table.reload('material-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});