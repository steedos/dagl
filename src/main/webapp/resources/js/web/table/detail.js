$(function(){
	initDetailMethod();
	
	listenSubmit();
	
	DhUtil.checkDhAndRule();
	
	setDah();
});

//如果没有档号，则填入根据规则生成的档号
function setDah() {
	if(dh == "") {
		$("input[name='dh']").attr("value",dhRule);
	}
}

//与档号相关的字段数组
var fieldArr = [];
//与档号相关字段的下标
var fieldIdx = [];
//档号数组
var dhArr = [];
//已有档号
var dh = $("input[name='dh']").attr("value");
//根据档号规则生成的档号
var dhRule = "";
//判断档号是否手动修改过的标识,0表示未修改过
var flag = 0;


function listenSubmit(){
	layui.use(['form','jquery','laydate','layer'], function(){
		var layer = layui.layer
	    ,$ = layui.jquery
	    ,form = layui.form()
	    ,laydate = layui.laydate;
	    
	    var isAdd = false;
	    var isNext = false;
		//监听提交
		form.on('submit(save)', function(data){
			var tablename = $.trim($('[name="tablename"]').val());
			
			var detailId = $.trim($('[name="detailId"]').val());
			var isProcessState = false;		
			if(!Util.checkNull(detailId)){
				var businessId = tablename + ',' + detailId;
				$.ajax({
					url :  Util.getRootPath() + "/w/example/flow/checkFlowState",
					type:'POST', //GET
					async:false,
					data:{
						businessId:businessId
					},
					dataType:'json',
					success:function(data,textStatus,jqXHR){
						if(data.success){
							if(data.datas.length>0){
								isProcessState = true;
								layer.open({
									  type: 4,
									  content: ['当前记录已有流程记录', '.saveOrUpdate'] //数组第二项即吸附元素选择器或者DOM
								}); 
							}
						}
					},
					error:function(ex) {
						layer.alert("删除失败！", {  skin: 'layui-layer-lan',closeBtn: 0});
					}
				})
			}
			if(isProcessState){
				return false;
			}
			
			
			var loadIndex = layer.load(1, {shade: [0.1,'#FFFFFF']});
			var fileUUID;
	        //删除附件
	        if (removeFileIds.length > 0) {
	            Util.ajaxJsonSync(
	                Util.getRootPath() + "/w/ywgj/removeFiles",
	                {
	                    attaIds: removeFileIds
	                },
	                function(result){
	                    if(!result.success){
	                        layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
	                    }
	                }, function(ex){
	                    layer.alert("删除附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
	                }
	            );
	        }
	        
	        //资料收集修改,上传原文
	        if (savFiles.length > 0) {
	            var fileData = new FormData($('form')[0]);
	            for (var i = 0, j = savFiles.length; i < j; ++i) {
	                fileData.append('attachment[]', savFiles[i]);
	            }
	            //资料id
	            fileData.append('dataCollectId',$.trim($('[name="detailId"]').val()));
	            $.ajax({
	                async : true,
	                cache: false,
	                type: 'POST',
	                dataType : "json",
	                data:fileData,
	                processData : false,
	                contentType : false,
	                url: Util.getRootPath() + "/w/example/table/uploadFile",
	                success: function(result){
	                	layer.close(loadIndex);
	                    if(!result.success){
	                        layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
	                    }else{
	                        fileUUID = result.data;
	                        //增加或修改操作
		             	       $.ajax({
		             	          url: Util.getRootPath()+"/w/example/table/saveOrUpdate/"+tablename+"/"+fileUUID,
		             	          type: "POST",
		             	          dataType: "json",
		             	          data: data.field,
		             	          async: true,
		             	          success: function(data) {
		             	        	 layer.close(loadIndex);
		             	            var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
		             	            if(data.success){
		             	            	layer.close(index);
		             	            	layer.msg("保存成功");
		             	            	if(!isAdd && !isNext){
		             	            		parent.layer.close(index);
		             	            	}
		             	            	if(isAdd && !isNext){
		             	            		window.location.href = Util.getRootPath() + "/w/example/table/adddetail/"+tablename;
		             	            	}	
		             	            	
		             	            	
		             	 	            parent.mainIframe.refreshGrid();
		             	            }else{
		             	            	layer.close(index);
		             	            	layer.msg(data.msg);
		             	            }
		             	          },
		             	          error: function() {
		             	        	 layer.close(loadIndex);
		             	             layer.msg("保存请求失败，请稍后再试。");
		             	          }
		             	        });
	                        
	                    }
	                },
	                error: function(ex) {
	                	layer.close(loadIndex);
	                    layer.alert("上传附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
	                }
	                
	            });
	        }else{
	        	//增加或修改操作
	 	       $.ajax({
	 	          url: Util.getRootPath()+"/w/example/table/saveOrUpdate/"+tablename+"/"+fileUUID,
	 	          type: "POST",
	 	          dataType: "json",
	 	          data: data.field,
	 	          async: true,
	 	          success: function(data) {
	 	            var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	 	            if(data.success){
	 	            	layer.close(loadIndex);
	 	            	layer.close(index);
	 	            	layer.msg("保存成功");
	 	            	if(!isAdd && !isNext){
	 	            		parent.layer.close(index);
	 	            	}
	 	            	if(isAdd && !isNext){
	 	            		window.location.href = Util.getRootPath() + "/w/example/table/adddetail/"+tablename;
	 	            	}	
	 	            	
	 	            	
	 	 	            parent.mainIframe.refreshGrid();
	 	            }else{
	 	            	layer.close(index);
	 	            	layer.msg(data.msg);
	 	            }
	 	          },
	 	          error: function() {
	 	             layer.msg("保存请求失败，请稍后再试。");
	 	          }
	 	        });
	        }
	        
	        
	        return false;
		});
		
		form.on('checkbox(isAdd)', function(data){
			isAdd = data.elem.checked;
			
		}); 
		form.on('checkbox(isNext)', function(data){
			isNext= data.elem.checked;
		}); 
		
		
	});
}



function initDetailMethod(){
	//关闭
	$(".closeup").on("click",function(){
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
        parent.layer.close(index);
	});
	
	
	/*$(".saveOrUpdate").on("click",function(){
		var fileUUID;
		//删除附件
		if (removeFileIds.length > 0) {
			Util.ajaxJsonSync(
				Util.getRootPath() + "/w/ywgj/removeFiles",
				{
					attaIds: removeFileIds
				},
				function(result){
					if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        	}
				}, function(ex){
					layer.alert("删除附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			);
		}
		
		//资料收集修改,上传原文
		if (savFiles.length > 0) {
			var fileData = new FormData($('form')[0]);
			for (var i = 0, j = savFiles.length; i < j; ++i) {
				fileData.append('attachment[]', savFiles[i]);
			}
			//资料id
			fileData.append('dataCollectId',$.trim($('[name="detailId"]').val()));
			$.ajax({
		        async : false,
		        cache: false,
		        type: 'POST',
		        dataType : "json",
		        data:fileData,
				processData : false,
				contentType : false,
		        url: Util.getRootPath() + "/w/zlzl/zlsj/uploadFile",
		        success: function(result){
		        	if(!result.success){
		        		layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
		        	}else{
		        		fileUUID = result.data;
		        	}
		        },
		        error: function(ex) {
		        	layer.alert("上传附件请求失败", {  skin: 'layui-layer-lan',closeBtn: 0});
		        }
			});
		}
		//修改
		var tablename = $.trim($('[name="tablename"]').val());
		var action = Util.getRootPath()+"/w/example/table/saveOrUpdate/"+tablename+"/"+fileUUID;
		$("form").attr("action",action);
		
		$("form").submit(function(e){
			//关闭弹出框
			var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
			parent.layer.close(index);
			
			//调用iframe刷新表格方法
			parent.mainIframe.refresh();
		});
		$("form").submit();
	})*/
}