//全局变量
var grid;

$(function(){
	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
	initTable();
	initMethod();
	layer.close(index);
})

function initTable(){
	grid=$.fn.DtGrid.init({
		loadURL : Util.getRootPath() + "/w/fileuse/electronicLend/getElectronicLendList",
	    ajaxLoad : true,
	    gridContainer : 'tableElectronicLend',
	    toolbarContainer : 'pagingElectronicLend',
	    lang : 'zh-cn',
	    tools : '',
	    pageSize : 20,
	    check : true,
	    pageSizeLimit : [20,50,100,300],
	    tableStyle:'table-layout: fixed;word-break: break-all; word-wrap: break-word;',
	    columns : [
			{
				id:'jyzt',
				title : '<b>借阅状态</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'dh',
				title : '<b>档号</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'jyr',
				title : '<b>借阅人</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value.name+'" style="cursor: pointer;">'+value.name+'</span>'
				}
			},
			{
				id:'cnbm',
				title : '<b>中文表名</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
				
			},
			{
				id:'wh',
				title:'<b>文号</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'tm',
				title : '<b>题名</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'bz',
				title : '<b>备注</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'jymd',
				title : '<b>借阅目的</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			{
				id:'jysj',
				title : '<b>借阅时间</b>',
				type:"string",
				columnClass:'text-center',
				columnStyle:'-o-text-overflow:ellipsis;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;width:100%;',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					return '<span title="'+value+'" style="cursor: pointer;">'+value+'</span>';
				}
			},
			/*{
				id:'daid',
				title : '<b>挂接原文</b>',
				type:"string",
				columnClass:'text-center',
				resolution:function(value, record, column, grid, dataNo, columnNo){

   				 var html = "";
   				 Util.ajaxJsonSync(
						 Util.getRootPath()+"/w/example/flow/getYwgjByFileId",
	                        {
							 tablename:record.bm,
							 id : value
	                        },
	                        function(result){
								if(result.success){
									
									var datas = result.datas;
									if(datas.length>0){
   									for(var i = 0;i<datas.length;i++){
										var wjlx = datas[i].wjlx;
										var wjm = datas[i].wjm;
										if(wjlx == 'pdf'){
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'"  src="' + Util.getRootPath() + '/resources/img/icon_16/pdf.svg" />';
										}else if(wjlx == 'doc' || wjlx == 'docx'){
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'"  src="' + Util.getRootPath() + '/resources/img/icon_16/word.svg" />';
										}else if(wjlx == 'xlsx' || wjlx == 'xls'){
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'"  src="' + Util.getRootPath() + '/resources/img/icon_16/excel.svg" />';
										}else if(Util.isAudio(wjlx)){
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'" src="' + Util.getRootPath() + '/resources/img/icon_16/音频.svg" />';
										}else if(Util.isVedio(wjlx)){
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'" src="' + Util.getRootPath() + '/resources/img/icon_16/视频.svg" />';
										}else if(Util.isPIC(wjlx)){
   											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'" src="' + Util.getRootPath() + '/resources/img/icon_16/pic.svg" />';
										}else if(wjlx == 'txt'){
   											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'" src="' + Util.getRootPath() + '/resources/img/icon_16/txt.svg" />';

   										}
										else{
											html += '<img dagl-uuid="' + datas[i].id  + '" title="'+wjm+'"  src="' + Util.getRootPath() + '/resources/img/icon_16/文件.svg" />';
										}
									}
									}
								}
	                        }
   				 )
   				 return html;
   			 
				}
				
				
				
			},*/
			{
				id:'id',
				title : '<b>操作</b>',
				type:"string",
				columnClass:'text-center',
				resolution:function(value, record, column, grid, dataNo, columnNo){
					var btn = '';
					btn += '<a title="查看" class="btn btn-primary btn-xs view" data-id="'+record.id+'"  >查看</a>';
					btn += '<a title="修改" class="btn btn-primary btn-xs modify" data-id="'+record.id+'" >修改</a>';
					btn += '<a title="删除" class="btn btn-primary btn-xs delete" data-id="'+record.id+'" >删除</a>'
					return btn;
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
	return{
	}
}


function initMethod(){
	$("#tableElectronicLend").on("click","a.modify",function(){
		var id = $(this).attr("data-id")
		var options = {}
		options.url = Util.getRootPath() + "/w/fileuse/electronicLend/modifyView?id="+id+"&flag=MODIFY";
		options.title = "请填写登记信息";
    	window.parent.showModal(options);
	});
	
	$("#tableElectronicLend").on("click","a.view",function(){
		var id = $(this).attr("data-id")
		var options = {}
		options.url = Util.getRootPath() + "/w/fileuse/electronicLend/modifyView?id="+id+"&flag=VIEW";
		options.title = "请填写登记信息";
    	window.parent.showModal(options);
	});
	
	
	
	//dtgrid点击删除操作
	$("#tableElectronicLend").on("click","a.delete",function(){
		var id = $(this).attr("data-id");
		
		layer.confirm('确定要删除档案吗?', {
	  		  skin: 'layui-layer-lan',
	  		  btn: ['确定','取消'] //按钮
	  		}, function(index){
	  			
		$.ajax({
			url : Util.getRootPath()+"/w/fileuse/electronicLend/delete",
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
					layer.alert(result.msg, {  skin: 'layui-layer-lan',closeBtn: 0});
				}
			},
			error:function(ex) {
				layer.alert("删除失败！", {  skin: 'layui-layer-lan',closeBtn: 0});
			}
		});
	  },function(index){
				 layer.close(index);
	  	});
	});
	
	//点击预览内容
    $("#tableElectronicLend").on("click", "img", function(){
    	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
    	var fileid = $(this).attr("dagl-uuid");
    	var filename = $(this).attr("title");
    	console.log(filename);
    	$.ajax({
            type: "GET",
            url: Util.getRootPath() + "/w/preview/" + fileid,
            dataType: 'json',
            async: true,	
            success: function(result) {
				
				var option = {};
				option.title = filename;
				console.log(result);
				
				layer.close(index);
				//视频格式转成flv
				if(result.type == 'flv'){
					layer.close(index);
					option.url = Util.getRootPath() + "/w/preview/video/" + filename + "/" + result.cache;
					window.parent.showModal(option);
				}else if(result.type == 'mp3'){
					parent.layer.open({
		                type: 2,
		  			  	title:filename,
		                area: ['450px', '200px'],
		                fixed: false, //不固定
		                maxmin: true,
		                content: Util.getRootPath() + "/w/preview/viewAudio/" + fileid,
		              });
				}else if(result.type == 'others'){
					layer.confirm('其他格式的文件不能预览,是否需要下载?', {
						  skin: 'layui-layer-lan',
						  btn: ['是','否'] //按钮
					}, function(index){
						layer.close(index);
						window.location.href = Util.getRootPath() + "/w/common/downLoadAttach?filePath="+result.cache+"&fileName="+result.name;
					},function(index){
						 layer.close(index);
					});
					
				}else if(result.type == 'pdf'){
					parent.layer.open({
			              type: 2,
						  title:filename,
			              area: ['700px', '530px'],
			              fixed: false, //不固定
			              maxmin: true,
			              content: Util.getRootPath()+"/resources/plugins/pdfJs/generic/web/viewer.html?file="+Util.getRootPath()+"/w/preview/displayPDF/"+fileid,
			        });
				}else if(Util.isPIC(result.type)){
					//图片 
					window.open(result.cache);
				}else{
					// word excel txt pdf
					layer.close(index);
					option.url = result.cache;
					window.parent.showModal(option);
				}
			},
            error:function(XMLHttpRequest, textStatus, errorThrown){
            	layer.close(index);
            	layer.msg("文件不存在或被删除");
            }
    	});
    });
}
