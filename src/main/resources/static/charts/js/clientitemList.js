layui.use(['layer','form','table' ,'upload'], function() {
    var layer = layui.layer,
        $ = layui.jquery,
        form = layui.form,
        table = layui.table,
        upload = layui.upload,
        t; //表格变量

        upload.render({
            elem: '#upload',
            url: '/item/clientitem/upload',
            field: 'file',
            before: function(obj){ //obj参数包含的信息，跟 choose回调完全一致，可参见上文。
                layer.load(); //上传loading
            },
            done: function (res) {
                //如果上传失败
                if (res.success === false) {
                     layer.closeAll('loading');
                    return layer.msg(res.message);
                }else{
                     layer.closeAll('loading');
                     layer.open({
                        title: '提示消息'
                        ,content: res.message
                     });     
                }
            }
        }); 
    //监听工具条
    table.on('tool(clientitemList)', function(obj){
        var data = obj.data;
        if(obj.event === 'edit'){
            var editIndex = layer.open({
                title : "单品入库量",
                type : 2,
                content : "/charts/count/showclientItem?id="+data.id+"&mode=1",
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回品项列表', '.layui-layer-setwin .layui-layer-close', {
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
           if(obj.event === 'editTakeout'){
            var editIndex = layer.open({
                title : "单品出库量",
                type : 2,
                content : "/charts/count/showclientItem?id="+data.id+"&mode=2",
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回品项列表', '.layui-layer-setwin .layui-layer-close', {
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
          if(obj.event === 'editReturn'){
            var editIndex = layer.open({
                title : "单品出库量",
                type : 2,
                content : "/charts/count/showclientItem?id="+data.id+"&mode=3",
                success : function(layero, index){
                    setTimeout(function(){
                        layer.tips('点击此处返回品项列表', '.layui-layer-setwin .layui-layer-close', {
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
        elem: '#clientitem-table',
        even: true,
        url:'/item/clientitem/list',
        method:'post',
        toolbar: "#toolbarDemo" ,
        defaultToolbar: ['filter', 'exports', 'print', { //自定义头部工具栏右侧图标。如无需自定义，去除该参数即可
          title: '提示'
          ,layEvent: 'LAYTABLE_TIPS'
          ,icon: 'layui-icon-tips'
        }],
        title:'客户品项表',
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
            {field:'clientId',        title: '客户'   },
            {field:'code',        title: '编号'   },
            {field:'name',        title: '名称'   },
            {field:'category',        title: '分类'   },
            {field:'brand',        title: '品牌'   },
            {title: '统计',fixed: 'right',  width:'15%',    align: 'center',toolbar: '#clientitemBar'}
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
        table.reload('clientitem-table', {
            page: {curr: 1},
            where: data.field
        });
        return false;
    });
});