 var grid_2_1_2;
$(function(){
	var index = layer.load(1, {shade: [0.1,'#FFFFFF']});
	initBtn();
	var tablename = $('[name="tablename"]').val();
    refresh();
    layer.close(index);
    function refresh(){
	    	$.ajax({
	             type: "GET",
				 async:false,
	             contentType: "application/json",
	             url: "/dagl/w/example/table/getHeader/" + tablename,
	             dataType: 'json',
	             success: function(result) {
	            	 var headers = result.datas;
	            	 
//	            	 for(var i = 0;i<headers.length;i++){
//	            		 
//	            		 if(headers[i].id == "isarchive"){
//	            			 headers[i].resolution = function(value, record, column, grid, dataNo, columnNo){
//				       			if(value == 0){
//				       				return "未处理";
//				       				
//				       			}else if(value == 1){
//				       				return "审批中";
//				       			}else if(value == 2){
//				       				return "审批未通过";
//				       			}else {
//				       				return "审批通过";
//				       			}
//	            			 };
//	            		 }
//	            		 
//	            	 } 
	            		 var header1 ={};
		            	 header1.id = "id";
		            	 header1.title = "挂接原文";
		            	 header1.type = "string";
		            	 header1.columnClass = "text-center";
		            	 header1.resolution = function(value, record, column, grid, dataNo, columnNo){
	            				 var html = "";
	            				 Util.ajaxJsonSync(
	            					Util.getRootPath()+"/w/example/flow/getYwgj",
            	                        {
            							 id : record.id
            	                        },
            	                        function(result){
            								if(result.success){
//            									layer.close(index);
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
//	            				 layer.close(index);
	            				 return html;
	            		 };
//	            		 layer.close(index);
			       		headers.push(header1);
	            		 
	            		 
	            		//判断字段是原文挂接的处理

	            	 
	            	 var header ={};
	            	 header.id = "id";
	            	 header.title = "操作";
	            	 header.type = "string";
	            	 header.columnClass = "text-center";
	            	 header.resolution = function(value, record, column, grid, dataNo, columnNo){
		       				var html = ''; 	
		       				html += '<div class="btnDiv">';
		       				html += '	<a title="查看" class="btn btn-default btn-xs detail" data-id="'+record.id+'">查看</a>';
	    					html += '	<a title="删除" class="btn btn-danger btn-xs delete" data-id="'+record.id+'">删除</a>';
		       				html += '</div>';
			       			
		       				return html;
		       		};
		       		headers.push(header);
		       		grid=$.fn.DtGrid.init({
		       			loadURL : Util.getRootPath() + "/w/example/table/getList/"+tablename,
		       			ajaxLoad : true,
                        lang : 'zh-cn',
                        exportFileName : '用户列表',
		       		    gridContainer : 'tableDocDiv',
		       		    toolbarContainer : 'pagingDocDiv',
		       		    check:true,
		       		    pageSize :20,
		       		    pageSizeLimit : [20,50,100,300],
		       		    tools:'', 
		       		    columns : headers		       			
		       		});
	                 refreshGrid();
	             }
	    	});
    	}
//  //刷新表格
//    function refreshGrid() {
//    	grid.parameters = getParameters();
//    	grid.load();
//    }
//    function getParameters() {
//    	return{
//    	}
//    }
	
    //弹出模态框查看详情
    $("#tableDocDiv").on("click","a.detail",function(){
    	var id = $.trim($(this).attr("data-id"));
    	var options = {}
    	var url = Util.getRootPath() + "/w/example/table/detail/"+tablename+"/"+id;
    	options.url = url;
    	options.title = "详情展示";
    	window.parent.showModal(options);
    });
    
    //点击删除内容
    $("#tableDocDiv").on("click","a.delete",function(){
    	var id = $.trim($(this).attr("data-id"));
    	
    	layer.confirm('确定要删除档案吗?', {
  		  skin: 'layui-layer-lan',
  		  btn: ['确定','取消'] //按钮
  		}, function(index){
  			
  			$.ajax({
  				type: "GET",
  				contentType: "application/json",
  				url: Util.getRootPath()+"/w/example/table/delete/"+tablename+"/"+id,
  				dataType:'json',
  				success:function(result){
  					
  					if(result.success){
  						layer.msg(result.msg,{time: 2000});
  						layer.close(index);
  						
  						$("#tableDocDiv").children().remove();
  						refresh();
  					}else{
  						layer.alert(result.msg, {skin: 'layui-layer-lan',closeBtn: 0});
  					}
  				}
  			
  			});
  		},function(index){
  			 layer.close(index);
  		});
    	
    	
    });
    //点击预览内容
	   $("#tableDocDiv").on("click","img",function(){
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
//	   layer.close(index);
});
//初始化按钮
function initBtn() {
	/*$(".btn-icon").on("mouseenter", function(){
        $this = $(this);
        $this.find('b').show();
     });
     
     $(".btn-icon").on("mouseleave", function(){
        $this = $(this);
        $this.find('b').hide();
     });*/
	$("button b").show();
     //表名
     var tablename = $('[name="tablename"]').val();
     //当前为OA数据归档表时，显示按钮
     if(tablename == "info_gdwj"){
    	 
    	 $(".btn-icon").removeClass("hide").addClass("show");
     }else {
    	 $(".btn-icon").removeClass("show").addClass("hide");
     }
     //OA数据归档按钮点击事件
     $(".addOAdata").on('click',function() {
    	 var options = {}
     	 var url = Util.getRootPath() + "/w/OAService/OADateChoose";
     	 options.url = url;
     	 options.title = "OA对接起始日期";
     	 window.parent.showModal(options);
     });
}
//刷新表格
function refreshGrid() {
	grid.parameters = getParameters();
	grid.load();
}
function getParameters() {
	return{
	}
}