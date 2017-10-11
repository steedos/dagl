$(function(){
	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
	initGrid();
	refreshGrid();
	
	initMethod();
	layer.close(index);
});


//初始化表格
function initGrid(){
	var processId = $.trim($('[name="processId"]').val());
	grid = $.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/dahk/getToDoTasks?processId="+processId,
	    ajaxLoad : true,
	    gridContainer : 'tableDiv',
	    toolbarContainer : 'pagingDiv',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : false,
	    pageSizeLimit : [20,50,100,300],
		columns : [

					
					{
						id:'dh',
						title : '<b>档号</b>',
						type:"string",
						columnClass:'text-center'
					},
		       		{
		       			id:'tm',
		       			title : '<b>题名</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'zrz',
		       			title : '<b>责任者</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'cwrq',
		       			title : '<b>成文日期</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'mj',
		       			title : '<b>密级</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				var bgqx; 
							Util.ajaxJsonSync(
								Util.getRootPath()+"/w/dicmanager/getDicValueByDno?dno=mj&dvno="+value,
								{
								},
								function(result){
									if(result.success){
										bgqx = result.data;
									}
								}
							)
							
							return bgqx;
		       			}
		       		},
		       		{
		       			id:'bgqx',
		       			title : '<b>保管期限</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				var bgqx; 
							Util.ajaxJsonSync(
								Util.getRootPath()+"/w/dicmanager/getDicValueByDno?dno=bgqx&dvno="+value,
								{
								},
								function(result){
									if(result.success){
										bgqx = result.data;
									}
								}
							)
							
							return bgqx;
		       			}

		       		},
		       		
		       		{
						id:'id',
						title : '<b>操作</b>',
						type:"string",
						hideType:'xs',
						columnClass:'text-center',
						isContentEditable: false,
						resolution:function(value, record, column, grid, dataNo, columnNo){
						var btn = "";
						if(record.nodeName !== '部门领导审批'){
							btn += '<a title="移交上级" class="btn btn-primary btn-xs toLeader" data-id="'+record.uuid+'" data-business-id="'+record.businessId+'">移交上级</a>';
						}
						btn += '<a title="同意" class="btn btn-primary btn-xs approve" data-id="'+record.uuid+'"  data-business-id="'+record.businessId+'">同意</a>';
						btn += '<a title="拒绝" class="btn btn-primary btn-xs reject" data-id="'+record.uuid+'" data-business-id="'+record.businessId+'">拒绝</a>';
						return btn;
				}
			},
		]
	});
}


/**
 * 刷新列表数据
 */
function refreshGrid() {
	grid.parameters = getParameters();
	grid.load();
}
/**
 * 获取查询参数
 */
function getParameters() {
	return {
	};
}

function initMethod(){
	$("#tableDiv").on("click",".toLeader",function(){
		var businessId = $.trim($(this).attr("data-business-id"));
		var tablename = businessId.substring(0,businessId.indexOf(','));
		var ids = [];
		ids.push($.trim($(this).attr("data-id")));
		
		ids = ids.join();
		
		var options = {}
		options.url = Util.getRootPath() + "/w/dahk/nextstepView?tablename="+tablename+"&ids="+ids+"&flag=HANDLE";
		options.title = "下一步处理人";
    	window.parent.showModal(options);
//		var url = Util.getRootPath() + "/w/dahk/nextstepView";
//		
//		var options = {}
//		options.url = url;
//		options.data = {
//				tablename : tablename,
//				ids : ids,
//				flag:'HANDLE'
//		};
//		options.title = "下一步处理人";
//    	window.parent.showPost(options);
		
		
		
	});
	
	
	
	$("#tableDiv").on("click",".approve",function(){
		
		var handParam = {};
		//流程定义Id
		handParam.processId = $.trim($('[name="processId"]').val());
		//业务ID
		handParam.businessId = $(this).attr('data-business-id');
		
		handParam.operate = 'APPROVE';
		
		//处理流程
		Util.ajaxJsonSync(
			Util.getRootPath() + "/w/dahk/handProcess",
			{	
				handParam: handParam,
			},
			function(result){
				if (result.success) {
					layer.msg(result.msg,{time: 2000});
					 refreshGrid();
				} else {
					layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			}, function (ex) {
				layer.alert("流程处理失败", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		);
	});
	
	$("#tableDiv").on("click",".reject",function(){
		var handParam = {};
		//流程定义Id
		handParam.processId = $.trim($('[name="processId"]').val());
		//业务ID
		handParam.businessId = $(this).attr('data-business-id');
		
		handParam.operate = 'REJECT';
		
		//处理流程
		Util.ajaxJsonSync(
			Util.getRootPath() + "/w/dahk/handProcess",
			{	
				handParam: handParam,
			},
			function(result){
				if (result.success) {
					layer.msg(result.msg,{time: 2000});
					refreshGrid();
				} else {
					layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			}, function (ex) {
				layer.alert("流程处理失败", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		);
	});
	
	
	$("#tableDiv").on("click", "img", function(){
		var fileid = $(this).attr("dagl-uuid");
		var filename = $(this).attr("title");
		$.ajax({
            type: "GET",
            contentType: "application/json",
            url: Util.getRootPath() + "/w/preview/" + fileid,
            dataType: 'json',
            async: false,	
            success: function(result) {
            	var option = {};
            	option.title = filename;
            		option.url = result.cache;
            		window.parent.showModal(option);
            },
		});
	});
}

