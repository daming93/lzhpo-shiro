Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//整转零
function zero(wholenumber,number,rate){

    return parseInt(wholenumber)*rate+parseInt(number);
}

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
//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#directReturnTime' //指定元素
      ,value: new Date()
  });

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
        depotsData:JSON.parse(document.getElementById('depots').value),
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

        var submitFlag = true;
        $("#clientId").parent().find('input:first').click();
        $("#clientId").parent().find('input:first').focus();    
        //回车操作
        $("#clientCode").keypress(function(e) {
            if (e.which == 13) {
              $("#batch").focus();
            }
        });
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
        //退库批次也默认今天
        var today = new Date();
        $("#batch").val(today.format("yyyy-MM-dd"));

        $("#number").keypress(function(e) {
            
            if (e.which == 13) {
               activeByType('addRow');
               //清空部分输入框
               $("#number").val("");
               $("#itemId").parent().find('input:first').val("");
               $("#depot").parent().find('input:first').val("");
               $("#tray").parent().find('input:first').val("");
               $("#batch").val(today.format("yyyy-MM-dd"));
               $("#batch").focus();
            }

        });
     
        $("form").keypress(function(e) {
             if(e.keyCode==10&&e.ctrlKey) {
                if(submitFlag){
                    $("#adddirectReturn").click();
                    submitFlag=false;
                }else{
                    layer.msg("冷静一下！",{time:1000});
                }
            }
        });
        var takeoutId =     document.getElementById("takeoutId").value;   
        var type =  document.getElementById("type").value;  
        var tableData=new Array(); // 用于存放表格数据
        $.ajax({
          url: "/stock/takeout/selectDetail?takeoutId="+takeoutId+"&limit=2000"
          ,type:"get"
          ,async:false
          ,dataType:"json"
          , success: function(result){
              tableData=result.data;
               type =  document.getElementById("type").value;  
                for (var i = 0; i < tableData.length; i++) {
                    //添加个属性
                    tableData[i].systemCode =  tableData[i].itemCode;
                    tableData[i].name =  tableData[i].itemName;
                    tableData[i].maxWholeNumber =  tableData[i].wholeNum;
                    tableData[i].maxScatteredNumber =  tableData[i].scatteredNum;
                    tableData[i].maxNumber = tableData[i].number;
                    if(type==1){
                        tableData[i].wholeNum = 0;
                        tableData[i].scatteredNum =0;
                        tableData[i].number = 0;
                    }
                }
                console.log(tableData);
            }
        });
        //数据表格实例化           
        var tbWidth = $("#tableRes").width();
        var layTableId = "dataTable";
        var tableIns = table.render({
            elem: '#dataTable',
            id: 'dataTable',
            data:tableData,
            width: tbWidth,
            page: false,
            loading: true,
            even: false, //不开启隔行背景
            limit: 2000, // 数据表格默认全部显示
            cols: [[
                {title: '序号', type: 'numbers'},

                { field:'itemCode',title:'系统物料编号',align:'center',width:160},
                { field:'itemName',title:'品牌系列',align:'center',width:200},
                { field:'depot',title:'储位',align:'center',width:130,templet: function(d){
                    var options = viewObj.renderSelectOptions(viewObj.depotsData, {valueField: "code", textField: "code", selectedValue: d.depot});
                    return '<a lay-event="depot"></a><select name="depot" lay-filter="depot"><option  value="">请选择</option>' + options + '</select>';
                }},
                { field:'batch',title:'批次',align:'center',width:100},
                { field:'wholeNum',title:'整',align:'center',edit: 'select',width:60},
                { field:'scatteredNum',title:'零',align:'center',edit: 'select',width:60},
                { field:'number',title:'合计',align:'center',width:80},
                { field:'maxWholeNumber',title:'出库整数',align:'center',width:80},

                { field:'maxScatteredNumber',title:'出库零数',align:'center',width:80},
                { field:'maxNumber',title:'合计',align:'center',width:80,},
                { field:'rate',title:'换算率',align:'center',width:100,templet: function(d){
                                return '1*'+d.rate;
                            }},
                { field:'itemId',title:'品项id',align:'center',hide:true,width:100},
                {field: 'id', title: '操作', width:70,templet: function(d){
                    return '<a class="layui-btn layui-btn-xs layui-btn-danger" lay-event="del" lay-id="'+ d.id +'"><i class="layui-icon layui-icon-delete"></i>移除</a>';
                }}
            ]],
            done: function(res, curr, count){
               
            }
        });
            //监听单元格编辑
         table.on('edit(dataTable)', function(obj){
            var value = obj.value //得到修改后的值
            ,data = obj.data //得到所在行所有键值
            ,field = obj.field; //得到字段
          

            //  获取单元格编辑之前td的选择器
            var selector = obj.tr.selector+' td[data-field="'+obj.field+'"] div';
            // 单元格编辑之前的值
            var oldtext = $(selector).text();
          
            if(field=="wholeNum"){
                if(!(/(^[1-9]\d*$)/.test(value))){
                    $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    layer.msg("只能输入正整数！");
                     obj.update({
                          wholeNum:oldtext,
                        });
                    return;
                }
                var tempNumber = parseInt(value)*obj.data.rate+parseInt(obj.data.scatteredNum);
                if(tempNumber-parseInt(obj.data.maxNumber)>0){
                  $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                     obj.update({
                      wholeNum:oldtext,
                    });
                   layer.msg("超过出库数");
                   return;
                }
                obj.update({
                      wholeNum:value,
                      number: parseInt(value)*obj.data.rate+parseInt(obj.data.scatteredNum),
                    });
            }
            if(field=="scatteredNum"){
                if(!(/(^[1-9]\d*$)/.test(value))){
                      $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                    layer.msg("只能输入正整数！");
                     obj.update({
                          scatteredNum:oldtext,
                        });
                    return;
                }
               var tempNumber =  parseInt(obj.data.wholeNum)*obj.data.rate+parseInt(value);
                if(tempNumber-parseInt(obj.data.maxNumber)>0){
                $(obj.tr.selector + ' td[data-field="' + obj.field + '"] input').val(oldtext);
                  obj.update({
                      scatteredNum:oldtext,
                    });
                   layer.msg("超过出库数");
                      
                   return;
                }
                obj.update({
                      scatteredNum:value,
                      number: parseInt(obj.data.wholeNum)*obj.data.rate+parseInt(value),
                    });
            }
          });
        //定义事件集合
        var active = {
           
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
     
        
//提交数据代码
        form.on('submit(adddirectReturn)',function(data){
        activeByType('save');    //更新行记录对象
        setTimeout(function(){ //无论是坏都要改状态
                    submitFlag = true;
                },1000);
        data.field.detailSet = table.cache[layTableId];  
        console.log(data.field);
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/directReturn/add",
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
                                    title : "编辑退库",
                                    type : 2,
                                    content : "/stock/directReturn/edit?id="+id,
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
                                 var node = parent.document.getElementById("adddirectReturn");
                             //调用该元素的Click事件
                                node.click();//连续录单
                            }
                         
                        }else{
                          // 刷新父页面
                           parent.location.reload(); 
                        }
                        layer.close(loadIndex);
                        submitFlag = false;//成功这个页面就再也不能提交了
                    });
                }else{
                    layer.msg(res.message);
                }
           
            }
        });
        return false;
    });
    });

