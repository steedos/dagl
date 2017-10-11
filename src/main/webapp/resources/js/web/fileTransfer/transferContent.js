var grid;

$(function(){
	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
	initMethod();
	initGrid("");
	layer.close(index);
});

function initMethod(){
	//点击档案移交按钮
	$('button.transfer').on("click",function(){
		var tablename = $('[name="tableselect"] option:selected').val();//选中的值
		var business = grid.getCheckedRecords();
		if(business.length==0){
			layer.alert("请先选择移交档案！", {skin: 'layui-layer-lan',closeBtn: 0});
			return;
		}

		var businessIds = [];
		for(var i=0;i<business.length;i++){
			businessIds.push(business[i].uuid);
		}
		
		var businessStr = businessIds.join();
		//layui弹出下一级处理人
		var options = {}
		options.url = Util.getRootPath() + "/w/fileTransfer/nextstepView?tablename="+tablename+"&ids="+businessStr+"&flag=NEW";
		options.title = "下一步处理人";
		window.parent.showModal(options);
		
		
	});
	
	$("button.query").on("click",function(){
		var tablename = $('[name="tableselect"] option:selected').val();//选中的值
		var options = {}
		options.url = Util.getRootPath() + "/w/fileTransfer/queryView?tablename="+tablename;
		options.title = "题名查询";
		
    	window.parent.showModal(options);
	});
	
}

function initGrid(tablename){
	$("#tableFileTransfer").children().remove();
	grid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/fileTransfer/getTransferFiles",
	    ajaxLoad : true,
	    gridContainer : 'tableFileTransfer',
	    toolbarContainer : 'pagingFileTransfer',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : true,
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
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'bgqx',
		       			title : '<b>保管期限</b>',
		       			type:"string",
		       			columnClass:'text-center'
		       		},
		       		{
		       			id:'yjzt',
		       			title : '<b>当前状态</b>',
		       			type:"string",
		       			columnClass:'text-center',
		       			isContentEditable: false,
						resolution:function(value, record, column, grid, dataNo, columnNo){
							if(record.yjzt === '0'){
								return "审批中";
							}else if(record.yjzt === '1'){
								return "已同意";
							}else if(record.yjzt === '2'){
								return "已拒绝";
							}else{
								return "未移交" ;
							}
						}
		       		}

		       		]
	});
	refreshGrid(tablename);
}
function refreshGrid(tablename) {
	grid.parameters = getParameters(tablename);
	grid.load();
}
function getParameters(tablename) {
	return{
		tablename : tablename
	}
}