Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//时间格式化
Date.prototype.format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//整转零
function zero(wholenumber,number,rate){
    return parseInt(wholenumber)*rate+parseInt(number);
}

//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#storageTime' //指定元素
      ,value: new Date()
  });
  // var laydate2 = layui.laydate;
  
  // //执行一个laydate实例
  // laydate2.render({
  //   elem: '#batch' //指定元素
  //     ,value: new Date()
  // });
});
 
window.viewObj = {
        tbData: [{
                        systemCode:"",
                        brand:"",
                        depot:"",
                        tray:"",
                        batch:"",
                        number:"",
                        numL:"",
                        rate:"",
                        adjustment:""
        }],
        typeData:null,
        optionData:null,
        renderSelectOptions: function(data, settings){
            settings =  settings || {};
            var valueField = settings.valueField || 'value',
                textField = settings.textField || 'text',
                selectedValue = settings.selectedValue || "";
            var html = [];
            for(var i=0, item; i < data.length; i++){
                item = data[i];
               
                html.push('<option style="" value="');
                html.push(item[valueField]);
                html.push('"');
                if(selectedValue && item[valueField] == selectedValue ){                        
                    html.push(' selected="selected"');
                }           
                html.push('>');     
                html.push(item[textField]);
                html.push('</option>');
            }
            return html.join('');
        }
    };
    
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;
        $("#clientId").parent().find('input:first').click();
        $("#clientId").parent().find('input:first').focus();    
        //回车操作
        $("#clientCode").keypress(function(e) {
            if (e.which == 13) {
               $("#adjustment").parent().find('input:first').click();
               $("#adjustment").parent().find('input:first').focus();
            }
        });
        var today = new Date();
        $("#batch").val(today.format("yyyy-MM-dd"));
        $("#batch").keypress(function(e) {
            if (e.which == 13) {
               $("#itemId").parent().find('input:first').click();
               $("#itemId").parent().find('input:first').focus();
            }
        });
        $("#wholeNumber").keypress(function(e) {
            if (e.which == 13) {
                $("#number").focus();
            }
        }); 
        $("#number").keypress(function(e) {
            if (e.which == 13) {
               activeByType('addRow');
               //清空部分输入框
               $("#number").val("");
               $("#wholeNumber").val("");
               $("#itemId").parent().find('input:first').val("");
               $("#depot").parent().find('input:first').val("");
               $("#tray").parent().find('input:first').val("");
               $("#batch").val(today.format("yyyy-MM-dd"));
               $("#batch").focus();
            }

        });
        $("form").keypress(function(e) {
             if(e.keyCode==10&&e.ctrlKey) {
                $("#addstorage").click();
            }
        });
       
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "layTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: layTableId,
            data: null,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: Number.MAX_VALUE, // 数据表格默认全部显示
            cols: [[
                    {title: '序号', type: 'numbers'},
                    { field:'systemCode',title:'系统物料编号',align:'center',width:100},
                    { field:'name',title:'品名',align:'center',width:200},
                    { field:'depot',title:'储位',align:'center',width:130},
                    { field:'tray',title:'托盘',align:'center',width:130},
                    { field:'batch',title:'批次',align:'center',width:100},
                    { field:'wholeNum',title:'数量(整)',align:'center',width:100},
                    { field:'scatteredNum',title:'数量(零)',align:'center',width:100},
                    { field:'number',title:'合计',align:'center',width:100},
                    { field:'rate',title:'换算率',align:'center',width:100,templet: function(d){
                                return '1*'+d.rate;
                            }},
                    { field:'itemId',title:'品项id',align:'center',hide:true,width:100},
                    {field: 'tempId', title: '操作', width:100,templet: function(d){
                        return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.tempId +'">移除</a>';
                    }}
            ]],
            done: function(res, curr, count){

            }
        });
        
        //定义事件集合
        var active = {
            addRow: function(){ //添加一行
                var oldData = table.cache[layTableId];
                if(!oldData){
                    oldData = [];
                }
                var _itemCode = $("#itemId").val();
                var _itemCodeName = $("#itemId").find("option:selected").text();
                var wholeNumber = $("#wholeNumber").val().trim();//整数量
                var number = $("#number").val().trim();//零数量
                var batch = $("#batch").val();
                var depot = $("#depot").find("option:selected").text();
                var tray = $("#tray").find("option:selected").text();
                var depot1 = $("#depot").val();
                var tray1 = $("#tray").val();
                var adjustment = $("#adjustment").val();
                if(!number){
                    number = 0;
                }
                if(!wholeNumber){
                    wholeNumber = 0;
                }
                //验证
                if(!(/^[0-9]\d*$/.test(number))){
                    layer.msg("请输入正确的零数量！");
                    return;
                }
                 //验证
                if(!(/^[0-9]\d*$/.test(wholeNumber))){
                    layer.msg("请输入正确的整数量！");
                    return;
                }
                if(!_itemCode||_itemCodeName==_itemCode){
                    layer.msg("请选择物料代码！");
                    return;
                }
                if(!batch||batch==""){
                    layer.msg("请输入批次号！");
                    return;
                }
                if(!(/^(\d{4})\-(\d{2})\-(\d{2})$/.test(batch))){
                    layer.msg("请输入正确的时间格式(如2020-02-02)");
                    return;
                }
                if(!depot1||depot1==-1||depot1==depot){
                    layer.msg("请选择正确的储位号！");
                    return;
                }
                if(!tray1||tray1==-1||tray1==tray){
                    tray = "";
                }
                var url = '/item/clientitem/getById?itemId=' + _itemCode;
                $.get(url,function(data){
                   //获取物料信息
                    var newRow = {
                        systemCode:_itemCodeName,
                        name:data.name,
                        brand:data.brand,
                        depot:depot,
                        tray:tray,
                        batch:batch,
                        wholeNum:wholeNumber,
                        scatteredNum:number,
                        number:zero(wholeNumber,number,data.unitRate) ,//合计
                        rate:data.unitRate,
                        adjustment:adjustment,
                        tempId:new Date(),
                        itemId:_itemCode
                    };
                    oldData.push(newRow);
                    tableIns.reload({
                        data : oldData
                    });
                });
            },
            updateRow: function(obj){
                var oldData = table.cache[layTableId];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.tempId == obj.tempId){
                        $.extend(oldData[i], obj);
                        return;
                    }
                }
                tableIns.reload({
                    data : oldData
                });
            },
            removeEmptyTableCache: function(){
                var oldData = table.cache[layTableId];      
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(!row || !row.tempId){
                        oldData.splice(i, 1);    //删除一项
                    }
                    continue;
                }
                tableIns.reload({
                    data : oldData
                });
            },
            save: function(){
                var oldData = table.cache[layTableId];  
            }
        }
        function clearNoNum(value) {
            value = value.replace(/[^\d.]/g, "");  //清除“数字”和“.”以外的字符   
            value = value.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的   
            value = value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            value = value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数   
            if (value.indexOf(".") < 0 && value != "") {//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额  
               value = parseFloat(value);
            }
            return value;
        }
        //激活事件
        var activeByType = function (type, arg) {
            if(arguments.length === 2){                 
                active[type] ? active[type].call(this, arg) : '';
            }else{
                active[type] ? active[type].call(this) : '';
            }
        }
        
        //注册按钮事件
        $('.layui-btn[data-type]').on('click', function () {
            var type = $(this).data('type');
            activeByType(type);
        });
        
        //监听select下拉选中事件
        form.on('select(type)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='type']").trigger("click");
        });
        //监听select下拉选中事件
        form.on('select(optionId)', function(data){
            var elem = data.elem; //得到select原始DOM对象
            $(elem).prev("a[lay-event='optionId']").trigger("click");
        });
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
                case "type":
                    //console.log(data);
                    var select = tr.find("select[name='type']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'type': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;
                 case "optionId":
                    //console.log(data);
                    var select = tr.find("select[name='optionId']");
                    if(select){                     
                        var selectedVal = select.val();
                        if(!selectedVal){
                            layer.tips("请选择一个分类", select.next('.layui-form-select'), { tips: [3, '#FF5722'] }); //吸附提示
                        }
                        $.extend(obj.data, {'optionId': selectedVal});
                        activeByType('updateRow', obj.data);    //更新行记录对象
                    }
                    break;    
                case "money":
                    var stateVal = tr.find("input[name='money']").val();
                    $.extend(obj.data, {'money': stateVal}) 
                    activeByType('updateRow', obj.data);    //更新行记录对象
                    break;                      
                case "del":
                    layer.confirm('真的删除行么？', function(index){
                      obj.del(); //删除对应行（tr）的DOM结构，并更新缓存
                      layer.close(index);
                      activeByType('removeEmptyTableCache');
                    });
                    break; 
                 case "checkTest":
                    $(obj.tr).find(".layui-table-edit").keyup(function () {
                        var $input = $(this), val = $input.val();
                        $input.val(clearNoNum(val));
                    });
                    break;                         
            }
        });
        //select 联动
        form.on('select(clientId)', function(data){
             //data.value 得到被选中的值
             var url = '/item/clientitem/getByClientIdAll?clientId=' + data.value;
             $.get(url,function(data){
                $("#itemId").empty();
                $("#itemId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#itemId").append(new Option(item.code,item.id));
                });
                layui.form.render("select");
             });
             url = '/warehouse/depot/getByClientId?clientId=' + data.value;
             $.get(url,function(data){
                $("#depot").empty();
                $("#depot").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#depot").append(new Option(item.code,item.id));
                });
                layui.form.render("select");
             });
            $("#clientCode").focus();
        });
        form.on('select(adjustment)', function(data){
              $("#batch").focus();
        });
        form.on('select(itemId)', function(data){
             $("#depot").parent().find('input:first').click();
             $("#depot").parent().find('input:first').focus();
        });
        form.on('select(depot)', function(data){
             $("#tray").parent().find('input:first').click();
             $("#tray").parent().find('input:first').focus();
        });
        form.on('select(tray)', function(data){
             $("#wholeNumber").focus();
        });
      
        
//提交数据代码
        form.on('submit(addstorage)',function(data){
        activeByType('save');    //更新行记录对象
      
        data.field.detailSet = table.cache[layTableId];  
        if(!data.field.detailSet){
              layer.msg("请填写子表");
              return;
        }
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/storage/add",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
                        var continuity = $("#continuity").val();
                        // 是否开启连续 录单 
                        if(continuity=="true"){//开启
                               //然后看是不是有权限
                            //没有权限就继续录单有权限就进入编辑页面
                            var flag = res.flag;
                            var id = res.id;//出库单id
                            if(flag){
                                //有权限 
                                 var editIndex = layer.open({
                                    title : "编辑出库",
                                    type : 2,
                                    content : "/stock/storage/edit?id="+id,
                                    success : function(layero, index){
                                        setTimeout(function(){
                                            layer.tips('点击此处返回出库列表', '.layui-layer-setwin .layui-layer-close', {
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
                            }else{
                                var node = parent.document.getElementById("addStorage");
                                //调用该元素的Click事件
                                node.click();//连续录单
                            }

                           
                        }else{
                          // 刷新父页面
                           parent.location.reload(); 
                        }
                    });
                }else{
                    layer.msg(res.message);
                }
            }
        });
        return false;
    });
    });

