Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//整转零
function zero(whole,number,rate){
  
    return parseInt(whole)*parseInt(rate)+parseInt(number);
}

function array_remove_repeat(a) { // 去重
    var r = [];
    for(var i = 0; i < a.length; i ++) {
        var flag = true;
        var temp = a[i];
        for(var j = 0; j < r.length; j ++) {
            if(temp === r[j]) {
                flag = false;
                break;
            }
        }
        if(flag) {
            r.push(temp);
        }
    }
    return r;
}

function array_intersection(a, b) { // 交集
    var result = [];
    for(var i = 0; i < b.length; i ++) {
        var temp = b[i];
        for(var j = 0; j < a.length; j ++) {
            if(temp === a[j]) {
                result.push(temp);
                break;
            }
        }
    }
    return array_remove_repeat(result);
}

function array_union(a, b) { // 并集
    return array_remove_repeat(a.concat(b));
}

function array_difference(a, b) { // 差集 a - b
    //clone = a
    var clone = a.slice(0);
    for(var i = 0; i < b.length; i ++) {
        var temp = b[i];
        for(var j = 0; j < clone.length; j ++) {
            if(temp === clone[j]) {
                //remove clone[j]
                clone.splice(j,1);
            }
        }
    }
    return array_remove_repeat(clone);
}

//零转整
function theTurn(number,rate){
    var a = Math.floor(number/rate);
    var b = number%rate;
    return a+"."+b;
}

//时间控件
layui.use('laydate', function(){
  var laydate = layui.laydate;
  
  //执行一个laydate实例
  laydate.render({
    elem: '#takeoutTime' //指定元素
      ,value: new Date()
  });
  var laydate1 = layui.laydate;
  //执行一个laydate实例
  laydate1.render({
    elem: '#deliveryTime' //指定元素
    ,value: new Date()
  });

});
 
window.viewObj = {
      
    };
    
    //layui 模块化引用
    layui.use(['jquery', 'table','upload', 'layer'], function(){

       
        var $ = layui.$, table = layui.table, form = layui.form, layer = layui.layer,upload = layui.upload;

        var takeoutWidth = $("#takeoutTable").width();

        var tableIns;

        var submitFlag = true;

        var takeoutIns = table.render({
                    elem: '#takeoutTable',
                    id: 'takeoutTable',
                    // width: takeoutWidth,
                    page: false,
                    cellMinWidth:100,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 100, // 数据表格默认全部显示
                    cols: [[
                            {title: '序号', type: 'numbers'},
                            { field:'systemCode',title:'系统物料编号',align:'center'},
                            { field:'itemName',title:'物料名称',align:'center'},
                            { field:'depot',title:'储位',align:'center'},
                            { field:'batch',title:'批次',align:'center'},
                            { field:'wholeNum',title:'数量(整)',align:'center',width:100},
                            { field:'scatteredNum',title:'数量(零)',align:'center',width:100},
                            { field:'number',title:'合计',align:'center',width:100},
                            { field:'rate',title:'换算率',align:'center'},
                            { field:'itemId',title:'品项id',align:'center',hide:true,width:100},
                             {field: 'material', title: '物料id', hide:true,width:100,templet: function(d){
                                return d.id;
                            }}
                    ]],
                    done: function(res, curr, count){
                        if(count>0){
                        }
                    }
                });
        var layTableId = "layTable";

        var rateTemp = 0;
        $("#clientId").parent().find('input:first').click();
        $("#clientId").parent().find('input:first').focus();  
        $("#wholeNumber").keypress(function(e) {
            
            if (e.which == 13) {
                $("#number").focus();
            }
        });
         $("#number").keypress(function(e) {
            
            if (e.which == 13) {
              // activeByType('addRow');
                var oldData = table.cache[layTableId];
                var sum  = 0;
                var sumScatteredNum = 0;
                var sumWholeNum = 0;
                for(j = 0,len=oldData.length; j < len; j++) {
                    sum += oldData[ j].availableNum;
                    sumScatteredNum += oldData[ j].scatteredNum;
                    sumWholeNum += oldData[ j].wholeNum;
                }
                var number = $("#number").val();// 
                var wholeNum =  $("#wholeNumber").val();// 整数
                var clientId =  $("#clientId").val();// 客户Id
                if(!number){ 
                    number = 0;
                }else{
                    number = parseInt(number);
                }
                if(!wholeNum){
                    wholeNum = 0;
                }else{
                   //验证
                   wholeNum = parseInt(wholeNum);
                }
                if(!(/^[0-9]\d*$/.test(number))){
                    layer.msg("请输入正确的零数量！");
                    return;
                }
                 //验证
                if(!(/^[0-9]\d*$/.test(wholeNum))){
                    layer.msg("请输入正确的整数量！");
                    return;
                }
                if(!clientId){
                    layer.msg("请选择客户");
                    return;
                }
                var isWhole = false;//默认不是
                //看是不是整进整出的客户
                $.ajax({
                    type:"POST",
                    url:"/client/contractMain/isWhole?id="+clientId,
                    dataType:"json",

                    contentType:"application/json",
                    success:function(res){
                        if(res.success){
                           isWhole = true;
                        }else{
                            if(res.message=="该客户暂无使用得合同"){
                                layer.msg("该客户暂无使用得合同");
                                return;
                            }
                        }
                        if(isWhole&&number>sumScatteredNum){

                         //如果零数量超了并且 是 整客户 即 不允许拆箱 就直接返回 
                            layer.msg("该客户正在使用的合同是整出客户，不允许拆零，现在零数量超过库存");
                            return;
                        }
                        if(number<=sumScatteredNum){//零数量合适
                            if(wholeNum>sumWholeNum){
                                layer.msg("整数量超过库存");
                                return;
                            }
                              //在不拆零得情况下怎么做
                            //先看怎么出零得
                            //现在零数量够，应该按批次先出 看 列表中哪些有零数量 记录 其中得下标位置
                            var tempScatteredNum = number; //剩余零数量
                            var tempwholeNumber = wholeNum;
                        }    
                        else{//需求零数量超过库存零数量，先进行整零转换，然后与需要库存量比，然后在看是否拆零
                            var numZ = zero(wholeNum,number,rateTemp);
                             if(numZ>sum){
                                 layer.msg("请输入正确的数量！(不得大于库存数量)");
                                 return;
                            }
                            //拆零就是转换下比例
                            var needAddWhole = parseInt(number/rateTemp);//取整数就是转换的数字

                            var tempScatteredNum = number%rateTemp; //剩余零数量
                            //其实在这部再去看零数够不够用 不够用在拆箱
                            if(tempScatteredNum>sumScatteredNum){
                                //这里就是还不够用的样子
                                var tempwholeNumber = parseInt(wholeNum) +parseInt(needAddWhole) ;
                                //然后处理原数组 先拆一箱零数出来
                                for(j = 0,len=oldData.length; j < len; j++) {
                                   //拆一个整数不为0的箱子出来
                                    var tempNumber = oldData[ j].wholeNum;//临时行数量
                                    if(tempNumber>0){
                                        oldData[ j].wholeNum= oldData[ j].wholeNum-1;
                                        oldData[ j].scatteredNum = oldData[ j].scatteredNum +parseInt(rateTemp);
                                        layer.msg("发生拆零行为");
                                        break;//拆零行为只发生一次
                                    }
                                }
                            }else{
                                var tempwholeNumber = needAddWhole;
                            }
                            
                        }    

                            var scatteredArr =  new Array();
                            var wholeArr =  new Array();
                            var wholeDataArr =  new Array();
                            var scatteredDataArr =  new Array();
                            // 然后按此法找到 整数量
                            for(j = 0,len=oldData.length; j < len; j++) {
                                var tempNumber = oldData[ j].scatteredNum;//临时行数量
                                if(tempNumber>0){
                                   if(tempScatteredNum-tempNumber>=0){//如果大于本行数量
                                        var arrScattereStr = {index:j,scatteredNum:tempNumber};
                                        scatteredArr.push(j);
                                        scatteredDataArr.push(arrScattereStr);
                                        tempScatteredNum = tempScatteredNum - tempNumber;
                                    }else{
                                        if(tempScatteredNum<=0){
                                            break;
                                        }else{
                                            var arrScattereStr = {index:j,scatteredNum:tempScatteredNum};
                                            scatteredArr.push(j);
                                            scatteredDataArr.push(arrScattereStr);
                                            break;
                                        } 
                                    }
                                    //这个时候得零数量 得数组已经处理完成
                                }else{
                                   // break;
                                }
                            }
                            for(jk = 0,len=oldData.length; jk < len; jk++) {    
                                var tempWholeArrNumber = oldData[ jk].wholeNum;//临时行数量

                                if(tempWholeArrNumber>0){
                                    if(tempwholeNumber-tempWholeArrNumber>=0){//如果大于本行数量
                                        var arrWholeStr = {index:jk,wholeNum:tempWholeArrNumber};
                                        wholeArr.push(jk);
                                        wholeDataArr.push(arrWholeStr);
                                        tempwholeNumber = tempwholeNumber - tempWholeArrNumber;
                                    }else{
                                        if(tempwholeNumber<=0){
                                            break;
                                        }else{
                                            var arrWholeStr = {index:jk,wholeNum:tempwholeNumber};
                                            wholeArr.push(jk);
                                            wholeDataArr.push(arrWholeStr);
                                            break;
                                        } 
                                    }
                                    //这个时候得整数量 得数组已经处理完成
                                }else{
                                    //break;
                                }
                            }
                             var tempAllArr =  array_union(wholeArr, scatteredArr);//组合数组
                             var allArr = new Array();
                             for(j = 0,len=tempAllArr.length; j < len; j++) {
                                var arrTemp  ={};
                                arrTemp.index = tempAllArr[j];
                                for(k = 0,lensc=scatteredDataArr.length; k < lensc; k++) {
                                    if(tempAllArr[j]==scatteredDataArr[k].index){
                                        arrTemp.scatteredNum=scatteredDataArr[k].scatteredNum
                                        arrTemp.wholeNum=0;
                                    }
                                    
                                }
                                for(k = 0,lensc=wholeDataArr.length; k < lensc; k++) {
                                    if(tempAllArr[j]==wholeDataArr[k].index){
                                        arrTemp.wholeNum=wholeDataArr[k].wholeNum
                                        if(!arrTemp.scatteredNum){
                                             arrTemp.scatteredNum = 0;
                                        }
                                    }
                                    
                                }
                                allArr.push(arrTemp);
                             }
                             for(j = 0,len=oldData.length; j < len; j++) {
                                for(k = 0,lensc=allArr.length; k < lensc; k++) {
                                    if(j ==allArr[k].index  ){
                                        var newRow = oldData[ j];
                                        newRow.wholeNum = allArr[k].wholeNum;
                                        newRow.scatteredNum = allArr[k].scatteredNum;
                                        newRow.number = zero(newRow.wholeNum,newRow.scatteredNum,rateTemp);
                                        newRow.batch = newRow.batchNumber;
                                        newRow.material = newRow.id;//编制格式
                                        active.addRow(newRow,'takeoutTable');
                                    }    
                                }
                             }
                       // }
                       // else{//需要拆零的情况
                            // var numZ = zero(wholeNum,number,rateTemp);
                            // if(numZ>sum){
                            //     layer.msg("请输入正确的数量！(不得大于库存数量)");
                            //     return;
                            // }
                            // var tempSum = numZ;//已使用数量
                            // var tempWhole =    wholeNum;
                            // var tempScattnumber =    number;     
                            // var arr = new Array();
                            // for(j = 0,len=oldData.length; j < len; j++) {
                            //     var tempNumber = oldData[ j].availableNum;//临时行数量
                            //     if(tempSum-tempNumber>=0){//如果大于本行数量
                            //         tempSum = tempSum-tempNumber;
                            //         tempWhole = tempWhole - oldData[ j].wholeNum;
                            //         tempScattnumber = tempScattnumber - oldData[ j].scatteredNum;
                            //         var newRow = oldData[ j];
                            //         newRow.number = newRow.availableNum;
                            //         newRow.batch = newRow.batchNumber;
                            //         newRow.material = newRow.id;//编制格式
                            //         active.addRow(newRow,'takeoutTable');
                            //         arr.push(oldData[j].id);
                            //     }else{
                            //         if(tempSum<=0){
                            //             break;
                            //         }
                            //         var newRow = {
                            //              id : oldData[j].id,
                            //              number : tempSum,
                            //              systemCode:oldData[j].systemCode,
                            //              itemName:oldData[j].itemName,
                            //              depot:oldData[j].depot,
                            //              batch:oldData[j].batchNumber,
                            //              rate:oldData[j].rate,
                            //              itemId:oldData[j].itemId,
                            //              material:oldData[j].id,
                            //              wholeNum:tempWhole,
                            //              scatteredNum:tempScattnumber
                            //         }
                                   
                            //         arr.push(oldData[j].id);
                            //         active.addRow(newRow,'takeoutTable');
                            //         tempSum = -1;
                            //         break;
                            //     }
                               
                            // }
                    //    }
                        
                        for(j = 0,len=oldData.length; j < len; j++) {//全部删除 
                            active.removeEmptyTableCache(oldData[0].id,'layTable');
                            $("#tableRes").find("tr").eq(1).remove();
                        }
                        $("#number").val("");
                        $("#wholeNumber").val("");
                        //执行清空
                        $("#itemId").val("");
                        //重新渲染
                        form.render("select");
                        $("#itemId").parent().find('input:first').val("");
                        $("#itemId").parent().find('input:first').click();
                        $("#itemId").parent().find('input:first').focus();
                    }
                });
               
            }
        });
        //定义事件集合
        var active = {
            addRow: function(newRow,tableId){ //添加一行
                var oldData = table.cache[tableId];
                if(!oldData){
                    oldData = [];
                }
                oldData.push(newRow);
                takeoutIns.reload({
                    data : oldData
                });
            },
            updateRow: function(obj){
                var oldData = table.cache['takeoutTable'];              
                for(var i=0, row; i < oldData.length; i++){
                    row = oldData[i];
                    if(row.tempId == obj.tempId){
                        $.extend(oldData[i], obj);
                        return;
                    }
                }
                takeoutIns.reload({
                    data : oldData
                });
            },
            removeEmptyTableCache: function(rowId,tableId){
                var oldData = table.cache[tableId];  
                for(var i=0, row; i < oldData.length; i++){
                      row = oldData[i];
                      if(row.id==rowId){
                          oldData.splice(i, 1);    //删除一项
                        
                          break;
                      }
                }    
              
               
            },
            save: function(){
                var oldData = table.cache['takeoutTable'];  
            }
        }
        //激活事件
        var activeByType = function (type, arg) {
            if(arguments.length === 2){                 
                active[type] ? active[type].call(this, arg) : '';
            }else{
                active[type] ? active[type].call(this) : '';
            }
        }
        //回车操作
     
        $("#clientCode").keypress(function(e) {
            if (e.which == 13) {
               $("#adjustment").parent().find('input:first').click();
               $("#adjustment").parent().find('input:first').focus();
            }
        });
        $("#batch").keypress(function(e) {
            if (e.which == 13) {
                var itemId =  $("#itemId").val();
                var batch =  $("#batch").val();
               //表格加载
                var tableData=new Array();  
                //数据表格实例化           
                var tbWidth = $("#tableRes").width();
                tableIns = table.render({
                    url:'/stock/material/listByClientIdAndBatch',
                    where:{itemId:itemId,batch:batch,materialType:1},//选择良品出库
                    elem: '#dataTable',
                    id: layTableId,
                    method: 'post',
                   // width: tbWidth,
                    cellMinWidth:100,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                            {title: '序号', type: 'numbers'},
                            { field:'systemCode',title:'系统物料编号',align:'center'},
                            { field:'itemName',title:'物料名称',align:'center'},
                            { field:'depot',title:'储位',align:'center'},
                            { field:'batchNumber',title:'批次',align:'center'},
                            { field:'wholeNum',title:'整',align:'center',width:100},
                            { field:'scatteredNum',title:'零',align:'center',width:100},
                            { field:'number',title:'合计',align:'center',templet: function(d){
                                return d.availableNum;
                            }},
                            { field:'rate',title:'换算率',align:'center',templet: function(d){
                                return '1*'+d.rate;
                            }},
                            { field:'itemId',title:'品项id',align:'center',hide:true},
                            {field: 'material', title: '物料id', hide:true,width:100,templet: function(d){
                                return d.id;
                            }}
                    ]],
                    done: function(res, curr, count){
                        if(count>0){
                            rateTemp = parseInt(res.data[0].rate);
                            var systemCode = res.data[0].systemCode;
                            var oldData = table.cache['takeoutTable'];  
                            for(var i=oldData.length-1, row; i >=0; i--){
                                  row = oldData[i];
                                  if(row.systemCode==systemCode){
                                      oldData.splice(i, 1);    //删除一项
                                  }
                            }
                            takeoutIns.reload({
                                data : oldData
                            });  
                        }
                    }
                });
                $("#wholeNumber").focus();
            }
        });
       form.on('select(adjustment)', function(data){
            if(data.value==1){
                //如果是调账
                 $('#transportationType').attr('disabled', 'disabled'); 
                 $('#deliverType').attr('disabled', 'disabled'); 
                 $('#addressId').attr('disabled', 'disabled'); 
                 form.render('select'); 
                 $("#transportationType").parent().find('input:first').click();
                 $("#transportationType").parent().find('input:first').focus(); 
                 $("#itemId").parent().find('input:first').click();
                $("#itemId").parent().find('input:first').focus();
            }else{
                $('#transportationType').removeAttr("disabled");
                $('#deliverType').removeAttr("disabled");
                $('#addressId').removeAttr("disabled");
                form.render('select');
                $("#transportationType").parent().find('input:first').click();
                $("#transportationType").parent().find('input:first').focus(); 
            }
               
        });
        form.on('select(transportationType)', function(data){
               $("#deliverType").parent().find('input:first').click();
               $("#deliverType").parent().find('input:first').focus();
        });
         form.on('select(deliverType)', function(data){
               $("#addressId").parent().find('input:first').click();
               $("#addressId").parent().find('input:first').focus();
        });
         form.on('select(addressId)', function(data){
               $("#itemId").parent().find('input:first').click();
               $("#itemId").parent().find('input:first').focus();
        });
        $("form").keypress(function(e) {
             if(e.keyCode==10&&e.ctrlKey) {
                if(submitFlag){
                    $("#addtakeout").click();
                    submitFlag=false;
                }else{
                    layer.msg("冷静一下！",{time:1000});
                }
            }
        });
        //注册按钮事件
        $('.layui-btn[data-type]').on('click', function () {
            var type = $(this).data('type');
            activeByType(type);
        });
         //监听工具条
        table.on('tool(dataTable)', function (obj) {
            var data = obj.data, event = obj.event, tr = obj.tr; //获得当前行 tr 的DOM对象;
            switch(event){
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
             var url = '/item/clientitem/getByClientId?clientId=' + data.value;
             $.get(url,function(data){
                $("#itemId").empty();
                $("#itemId").append(new Option("请选择",""));
                $.each(data,function(index,item){
                    $("#itemId").append(new Option(item.code,item.id));
                });
                layui.form.render("select");
             });
            $("#clientCode").focus();
        });

        form.on('select(itemId)', function(data){
            $("#batch").focus();
        });
     
      
        
//提交数据代码
        form.on('submit(addtakeout)',function(data){
        activeByType('save');    //更新行记录对象
        setTimeout(function(){ //无论是坏都要改状态
                    submitFlag = true;
                },1000);
        //这里验证 如果不是调账 其他就是必填项
        if($("#adjustment").val()!=1){
            if(!$("#transportationType").val()){
                layer.msg("收入类型必填");
                return false;
            }
            if(!$("#deliverType").val()){
                layer.msg("配送类型必填");
                return false;
            }
            if(!$("#addressId").val()){
                layer.msg("配送地址必填");
                return false;
            }
        }

        data.field.detailSet = table.cache['takeoutTable'];  
        var loadIndex = layer.load(2, {
            shade: [0.3, '#333']
        });
        $.ajax({
            type:"POST",
            url:"/stock/takeout/add",
            dataType:"json",
            contentType:"application/json",
            data:JSON.stringify(data.field),
            success:function(res){
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
                                    content : "/stock/takeout/edit?id="+id,
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
                                var node = parent.document.getElementById("addtakeout");
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

