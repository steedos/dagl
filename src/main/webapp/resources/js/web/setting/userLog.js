$(function() {
	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
	initTable();
	initBtn();
	layer.close(index);
});
function initBtn() {
	//点击查看事件
	$("#tableDataCollect").on("click","a.looking",function(){
//		var $fileDiv = $(".file-div");
//		$fileDiv.children().remove();
		var id = $(this).attr("data-id");
//		$("button.dataUpdate").hide();
//		$('[name="dataCollectId"]').val(id);
		$("#dataCollectUpdate").modal("show");
		//根据资料收集id获得相应信息
		$.ajax({
			url : Util.getRootPath()+"/w/setting/getUserLogInfoById",
			type:'POST', //GET
			async:false,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data){
				//输入框赋值
				var dataInfo = data.data;
				$('[name="time"]').val(dataInfo.createTime).attr("readonly","readonly");
				$('[name="type"]').val(dataInfo.type).attr("readonly","readonly");
				$('[name="content"]').val(dataInfo.description).attr("readonly","readonly");
				$('[name="userId"]').val(dataInfo.operator.name).attr("readonly","readonly");
//				$('[name="userName"]').val(dataInfo.userName).attr("readonly","readonly");
				
			},
			error:function(ex) {}
		});
	});
	//删除按钮点击事件
	$("#tableDataCollect").on("click","a.delete",function(){
		var id = $(this).attr("data-id");
		
		$.ajax({
			url : Util.getRootPath()+"/w/setting/deleteUserLogById",
			type:'POST', //GET
			async:true,
			data:{
				id:id
			},
			dataType:'json',
			success:function(data,textStatus,jqXHR){
				if(data.success){
					layer.msg(data.msg,{time: 2000});
					refreshGrid();
				}else{
					layer.alert("删除成功", {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			},
			error:function(ex) {
				layer.alert("删除失败！", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		});
	});
	//备份按钮点击事件
	$(".backup").on('click',function() {
		window.localtion.href = Util.getRootPath()+"/w/setting/backup?fileName=test";
//		$.ajax({
//			url : Util.getRootPath()+"/w/setting/backup",
//			type:'POST', //GET
//			async:false,
//			data:{
////				hostIP : "",
////				userName : "",
//			},
//			dataType:'json',
//			success:function(data){
//				
//			},
//			error:function(ex) {}
//		});
	});
}
//初始化列表
function initTable(){
	grid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/setting/getUserLogList",
	    ajaxLoad : true,
	    gridContainer : 'tableDataCollect',
	    toolbarContainer : 'pagingDataCollect',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : false,
	    pageSizeLimit : [20,50,100,300],
	    columns : [
		       		{
		       			id:'createTime',
		       			title : '<b>日志时间</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'type',
		       			title : '<b>日志类型</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'description',
		       			title : '<b>日志内容</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'operator',
		       			title : '<b>操作者</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       				return value.name;
		       			}
		       		},
//		       		{
//		       			id:'operator.name',
//		       			title : '<b>操作者姓名</b>',
//		       			type:"string",
//		       			columnClass:'text-center'
//		       		},
		       		{
		       			id:'id',
		       			title : '<b>操作</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			resolution:function(value, record, column, grid, dataNo, columnNo){
		       			//存储record类，编辑时使用
		       				var html = ''; 	
		       				html += '<div class="btnDiv">';
		       				html += '	<a title="查看" class="btn btn-default btn-xs looking" data-id="'+value+'">查看</a>';
//	       					html += '	<a title="修改" class="btn btn-primary btn-xs update" data-id="'+value+'">修改</a>';
	       					html += '	<a title="删除" class="btn btn-primary btn-xs delete" data-id="'+value+'">删除</a>';
		       				html += '</div>';
			       			
		       				return html;
		       			}

		       		}
		       		]
	});
	refreshGrid();
}

function refreshGrid() {
	grid.parameters = getParameters();
	grid.load();
}
function getParameters() {
	
}