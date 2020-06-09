layui.use(['jquery','layer','form','table'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        t; //表格变量

    var code = $("#code").val();    

    t = {
        elem: '#material-table',
        even: true,
        url:'/stock/material/listDepotMaterial',
        where: {depotCode: code} ,
        method:'post',
        toolbar: true ,
        page: false,
        width: $(parent.window).width()-223,
        cols: [[
            {type:'checkbox'},
           /* {field:'id',        title: 'ID'   },*/
            {field:'systemCode',        title: '品项编码'   },
            {field:'depot',        title: '储位'   },
            {field:'itemName',        title: '名称' ,    width:'15%'  },
            {field:'batchNumber',        title: '批次号' ,    width:'10%'   },
            { field:'numZ',title:'数量(整)',align:'center',width:100},
            { field:'availableNum',title:'数量(零)',align:'center',width:100},
            { field:'rate',title:'换算率',align:'center',width:100},
            {field:'materialStatus',    align:'center'  ,  title: '物料冻结状态'   },
            {field:'type',   align:'center'   ,  title: '状态'   },
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