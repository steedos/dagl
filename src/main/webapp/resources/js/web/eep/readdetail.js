//存储附件的集合
var savFiles = [];

$(function(){
	listenSubmit();
	//加载一览页面原文文件
	initFileBtn();
	initMethod();

});

function listenSubmit(){
	layui.use(['form','jquery'], function(){
	    var $ = layui.jquery,
	    form = layui.form();
		//监听提交
		form.on('submit(save)', function(data){
	       
	        var data = data.field;;
	        //资料收集修改,上传原文
            var fileData = new FormData($('form')[0]);
            if(savFiles.length>0){						//如果保存文件的长度大于0
	            for (var i = 0, j = savFiles.length; i < j; ++i) {
	                fileData.append('attachment[]', savFiles[i]);
	            }
            }
            
	        $.ajax({
	          url: Util.getRootPath()+"/w/example/eep/uploadFile",
	          async : false,	//false为同步，锁死浏览器，在php结束后再执行其他操作。true为异步，可能在php没执行完就执行其他操作。类型：Boolean
              cache: false,		//false为不使用缓存。true为第二次使用会使用缓存。类型：Boolean
              type: 'POST',		//类型：String。POST：请求方式。默认为GET。
              dataType : "json",	//类型：String。预期服务器返回的数据类型。
              data:fileData,		//类型：String。发送到服务器的数据
              processData : false,	//类型：Boolean。false发送DOM信息或者其他不要转换的信息。true变成查询字符串，配合默认内容类型
              contentType : false,	//类型：String。true：发送信息到服务器的编码类型为默认值。false:不默认的吧。。。
	          success: function(data)	//类型：Function。请求成功后回调函数。ajax事件。 
	          {
	        	  if(data.success){
//	        		  var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	        		  layer.msg(data.msg);
	        		  options = {};
	        		  var path = data.datas[0];			//datas索引第一个 赋值到path上
	        		  var filePath = data.datas[1];		//datas索引第二个 赋值到filePath上
	        		  //options.url = Util.getRootPath() + "/w/example/eep/preview" + "/" + path + "/" + filePath;
	        		  var preview = layer.open({
	                      type: 2,
	        			  title:options.title,
	                      area: ['700px', '530px'],
	                      fixed: false, //不固定
	                      //maxmin: true,
	                      content: Util.getRootPath() + "/w/example/eep/preview" + "/" + path + "/" + filePath,
	                      end:function(){
	                    	  savFiles = [];
	                    	  $(".file-div").empty();
	                    	  var attachment = document.getElementById("attachment");
	                    	  attachment.outerHTML=attachment.outerHTML;
	                    	  initFileBtn();
	                      },
	                  });
	        		  layer.full(preview)
	        		  // parent.showModal(options);
	        	  }
	          },
	          error: function() {
	             layer.msg("保存请求失败");
	          }
	        });
	        
	        return false;
		});
	});
}

function initMethod(){
	//删除原文
	$(".file-div").on("click", ".file-remove", function () {
		var $this = $(this);
		var attaId = $this.prev().prev().prev().attr("data-id");
		layer.confirm('是否删除该原文？', {
			  skin: 'layui-layer-lan',
			  btn: ['删除','取消'] //按钮
			}, function(){
				if (attaId == null || attaId == undefined || $.trim(attaId) == "") {
					// 去除该文件
					var name = $this.prev().prev().prev().text();
					savFiles = savFiles.filter(function(file) {
						return file.name !== name;
					});
					
				} 
				$this.parent().parent("div").remove();
				var attachment = document.getElementById("attachment");
          	  	attachment.outerHTML=attachment.outerHTML;
          	    initFileBtn();
				layer.msg('已删除',{time: 1000});
			}, function(){
				layer.msg('已取消',{time: 1000});
			});
	});
}


function initFileBtn(){
	//选择附件事件
	var attachment = document.getElementById("attachment");
	if (attachment != null) {
		attachment.onchange = function() {
			
			var addFiles = attachment.files;
			if(addFiles.length > 1){
				layer.alert("一次只能预览一个文件。");
				return false;
			}
			if (addFiles && addFiles.length) {
				// 原始FileList对象不可更改，所以将其赋予curFiles提供接下来的修改
				Array.prototype.push.apply(savFiles, addFiles);
			}
			var $fileDiv = $(".file-div");
			$(".file-none").remove();
			var html = '';
			for (var i = 0; i < addFiles.length; i++) {
				var wjdx = Util.bytesToSize(addFiles[i].size);
				
				html += '<div>';
				html += '	<div>';
				html += '	<i class="fa fa-file"></i>';
				html += '	<a class="file">'+addFiles[i].name+'</a>';
				html += '	<span class="file-size">'+wjdx+'</span>';
				html += '	<span class="file-download" style="display:none;" title="下载"><i class="fa fa-download"></i></span>';
				html += '	<span class="file-remove"><i class="fa fa-close fa-lg"></i></span>';
				html += '	</div>';
				html += '</div>';
				
			}
			$fileDiv.append(html);
		}
	}
	
}