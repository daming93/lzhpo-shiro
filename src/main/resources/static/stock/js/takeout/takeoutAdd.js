Array.prototype.contains = function ( needle ) {
    for (i in this) {
        if (this[i] == needle) return true;
    }
    return false;
};
//整转零
function zero(number,rate){
    var arr = number.split(".");
    var a = arr[0]*rate;
    var b = 0;
    if(arr.length==2){
        b = arr[1];
    }
    return parseInt(a)+parseInt(b);
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
  var laydate2 = layui.laydate;
  
  //执行一个laydate实例
  laydate2.render({
    elem: '#batch' //指定元素
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

        var takeoutIns = table.render({
                    elem: '#takeoutTable',
                    id: 'takeoutTable',
                    width: takeoutWidth,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 100, // 数据表格默认全部显示
                    cols: [[
                            {title: '序号', type: 'numbers'},
                            { field:'systemCode',title:'系统物料编号',align:'center',width:160},
                            { field:'itemName',title:'物料名称',align:'center',width:250},
                            { field:'depot',title:'储位',align:'center',width:130},
                            { field:'batch',title:'批次',align:'center',width:100},
                            { field:'numZ',title:'数量(整)',align:'center',width:100},
                            { field:'number',title:'数量(零)',align:'center',width:100},
                            { field:'rate',title:'换算率',align:'center',width:100},
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

         $("#number").keypress(function(e) {
            
            if (e.which == 13) {
              // activeByType('addRow');
                var oldData = table.cache[layTableId];
                var sum  = 0;
                for(j = 0,len=oldData.length; j < len; j++) {
                    sum += oldData[ j].availableNum;
                }
                var number = $("#number").val();
                //有数据之后 转整
                var numZ = zero(number,rateTemp);
                if(numZ>sum){
                    layer.msg("请输入正确的数量！(不得大于库存数量)");
                    return;
                }
                var tempSum = numZ;//已使用数量
                //        
                var arr = new Array();
                for(j = 0,len=oldData.length; j < len; j++) {
                    var tempNumber = oldData[ j].availableNum;//临时行数量
                    if(tempSum-tempNumber>=0){//如果大于本行数量
                        tempSum = tempSum-tempNumber;
                        var newRow = oldData[ j];
                        newRow.number = newRow.availableNum;
                        newRow.batch = newRow.batchNumber;
                        newRow.material = newRow.id;//编制格式
                        active.addRow(newRow,'takeoutTable');
                        arr.push(oldData[j].id);
                    }else{
                        if(tempSum<=0){
                            break;
                        }
                        var newRow = {
                             id : oldData[j].id,
                             number : tempSum,
                             systemCode:oldData[j].systemCode,
                             itemName:oldData[j].itemName,
                             depot:oldData[j].depot,
                             batch:oldData[j].batchNumber,
                             rate:oldData[j].rate,
                             itemId:oldData[j].itemId,
                             material:oldData[j].id,
                             numZ:theTurn(tempSum,oldData[j].rate)
                        }
                       
                        arr.push(oldData[j].id);
                        active.addRow(newRow,'takeoutTable');
                        tempSum = -1;
                        break;
                    }
                   
                }
                for(j = 0,len=arr.length; j < len; j++) {
                    active.removeEmptyTableCache(oldData[0].id,'layTable');
                    $("#tableRes").find("tr").eq(1).remove();

                    
                }
                $("#number").val("");
                $("#itemId").parent().find('input:first').val("");
                $("#itemId").parent().find('input:first').click();
                $("#itemId").parent().find('input:first').focus();
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
               $("#itemId").parent().find('input:first').click();
               $("#itemId").parent().find('input:first').focus();
            }
        });
       
        $("form").keypress(function(e) {
             if(e.keyCode==10&&e.ctrlKey) {
                $("#addtakeout").click();
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
                    width: tbWidth,
                    page: false,
                    loading: true,
                    even: false, //不开启隔行背景
                    limit: 1000, // 数据表格默认全部显示
                    cols: [[
                            {title: '序号', type: 'numbers'},
                            { field:'systemCode',title:'系统物料编号',align:'center',width:160},
                            { field:'itemName',title:'物料名称',align:'center',width:250},
                            { field:'depot',title:'储位',align:'center',width:130},
                            { field:'batchNumber',title:'批次',align:'center',width:100},
                            { field:'numZ',title:'数量(整)',align:'center',width:100},
                            { field:'number',title:'数量(零)',align:'center',width:100,templet: function(d){
                                return d.availableNum;
                            }},
                            { field:'rate',title:'换算率',align:'center',width:100},
                            { field:'itemId',title:'品项id',align:'center',hide:true,width:100},
                            {field: 'material', title: '物料id', hide:true,width:100,templet: function(d){
                                return d.id;
                            }}
                    ]],
                    done: function(res, curr, count){
                        if(count>0){
                        	rateTemp = res.data[0].rate;
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
             $("#number").parent().find('input:first').focus();
        });
     
      
        
//提交数据代码
        form.on('submit(addtakeout)',function(data){
        activeByType('save');    //更新行记录对象
      
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
                layer.close(loadIndex);
                if(res.success){
                    parent.layer.msg("单据添加成功！",{time:1000},function(){
                        var continuity = $("#continuity").val();
                        // 是否开启连续 录单 
                        if(continuity=="true"){//开启
                            var node = parent.document.getElementById("addtakeout");
                         //调用该元素的Click事件
                            node.click();//连续录单
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

