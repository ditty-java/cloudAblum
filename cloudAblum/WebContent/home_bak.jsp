<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta charset="utf-8" />
		<title>BlueMix Cloud Photo Album</title>
		<link rel="stylesheet" href="js/jquery/plugins/ui/1.11.0/themes/smoothness/jquery-ui.css"  type="text/css"/>
		<link rel="stylesheet" href="css/mystyle.css" type="text/css"/>

		<!-- Bootstrap styles -->
		<link rel="stylesheet" href="css/upload/bootstrap.css"/>
		<link rel="stylesheet" href="css/jquery.dataTables.css" type="text/css" />
		<link rel="stylesheet" href="css/dataTables.autoFill.css" type="text/css" />
		<!-- blueimp Gallery styles -->
		<link rel="stylesheet" href="css/upload/blueimp-gallery.min.css"/>
		<!-- CSS to style the file input field as button and adjust the Bootstrap progress bars -->
		<link rel="stylesheet" href="css/upload/jquery.fileupload.css"/>
		<link rel="stylesheet" href="css/upload/jquery.fileupload-ui.css"/>
		<!-- CSS adjustments for browsers with JavaScript disabled -->
		<noscript><link rel="stylesheet" href="css/upload/jquery.fileupload-noscript.css"/></noscript>
		<noscript><link rel="stylesheet" href="css/upload/jquery.fileupload-ui-noscript.css"/></noscript>

		<script src="js/jquery/jquery-1.11.1.js"></script>
		<script src="js/jquery/plugins/ui/1.11.0/jquery-ui.js"></script>
		<script src="js/common/jquery.dataTables.js"></script>
		<script src="js/common/dataTables.autoFill.js"></script>

	    <!-- Magnific Popup core CSS file -->
	    <link rel="stylesheet" href="js/jquery/plugins/magnific/magnific-popup.css">
	
	    <!-- Magnific Popup core JS file -->
	    <script src="js/jquery/plugins/magnific/jquery.magnific-popup.js"></script>
    
		<style>
		body { font-size: 62.5%; }
		h1, h2, h3 {line-height: 20px;}
		label, input { display:block; font-size: 10px;}
		.btn {font-size: 1em;}
		input.text { margin-bottom:12px; width:95%; padding: .4em; font-size: 10px;}
		h1 { font-size: 1.2em; margin: .6em 0; }
		.ui-dialog .ui-state-error { padding: .3em; }
		.validateTips { border: 1px solid transparent; padding: 0.3em; }
		/* IE has layout issues when sorting (see #5413) */
		.group {
			zoom: 1
		}
		</style>
		<script src="js/ablum/dialogs.js"></script>

		<script>
		var current_user_id;
		$(function() {
			<%if (session.getAttribute("current_user") == null) {%>
			window.location = "./index.jsp"
			<%} else {%>
			current_user_id = "<%=((UserBean)session.getAttribute("current_user")).getUserId()%>";
			$("#accordion").accordion({
				header : "> div > h3",
				heightStyle: "fill"
			}).sortable({
				axis : "y",
				handle : "h3",
				stop : function(event, ui) {
					// IE doesn't register the blur when sorting
					// so trigger focusout handlers to remove .ui-state-focus
					ui.item.children("h3").triggerHandler("focusout");
				}
			});
			//$('#accordion div').css('height','auto');
			//$("#accordion").accordion('option', 'autoHeight', true);
			
			$("#friendsTable").ready(function() {
				searchFriends();
			});
			
			$("#tbody_files").ready(function(){
				loadPhotos();
			});

			$("#categoriesTable").ready(function(){
				loadCategories();
			});
			<%}%>
		});

		function searchFriends() {
			//search friends
			var searchParam = "user_id="+current_user_id+"&row_limit=100&all_flg=1";
			$.ajax({
				type: "get",
				url: "FriendsServlet?action=search",
				data: searchParam,
				dataType: "json",
				success: function(friends){
		            var data = {"friends": friends};
		            $("#tbody_friends").html(tmpl("template-friends", data));
					var table = $('#friendsTable').DataTable();
					new $.fn.dataTable.AutoFill(table);
					$("input[type='button']").click(addRemoveFriend);
				},
				error: function(){
					//error handle
					//updateTips("Create user failed. Try again later.");
				}
			});
		}
		function addRemoveFriend(event) {
			var button = event.target;
			var action = event.target.getAttribute("name");
			var actionParam = "user_id="+current_user_id+"&friend_id=" + event.target.getAttribute("id");
			$.ajax({
				type: "get",
				url: "FriendsServlet?action="+action,
				data: actionParam,
				dataType: "json",
				success: function(msg){
					if (action == "add") {
						button.setAttribute("name", "remove");
						button.setAttribute("value", "remove");
					} else if (action == "remove") {
						button.setAttribute("name", "add");
						button.setAttribute("value", "add");
					}
				},
				error: function(){
					//error handle
					//updateTips("Create user failed. Try again later.");
				}
			});
		}
		function loadPhotos() {
			$.ajax({
				type: "get",
				url: "PhotosServlet?getall="+current_user_id,
				//data: user,
				success: function (files) {
					var data = {
						"files": files,
						"formatFileSize": function (bytes) {
							var units = [
				                    {size: 1000000000, suffix: ' GB'},
				                    {size: 1000000, suffix: ' MB'},
				                    {size: 1000, suffix: ' KB'}
				            ];
		                    /*if (!angular.isNumber(bytes)) {
		                        return '';
		                    }*/
		                    var unit = true,
		                        i = 0,
		                        prefix,
		                        suffix;
		                    while (unit) {
		                        unit = units[i];
		                        prefix = unit.prefix || '';
		                        suffix = unit.suffix || '';
		                        if (i === units.length - 1 || bytes >= unit.size) {
		                            return prefix + (bytes / unit.size).toFixed(2) + suffix;
		                        }
		                        i += 1;
		                    }
		                }
					};
					//console.log(tmpl("template-download", data));
		        	$("#tbody_files").html(tmpl("template-download", data));
		        	$("tr").addClass("in");
		        }
			});
		}
		function commentPhoto(fileid, comment) {
			$("#commentfile" ).val(fileid);
			$("#pohoto_comment").val(comment);
			$("#photo_comment_div" ).dialog( "open" );
		}
		function loadCategories() {
			$.ajax({
				type: "get",
				url: "CategoryServlet?action=listCategory",
				data: null,
				success: function (categories) {
					var data = {"categories": categories};
		            $("#tbody_categories").html(tmpl("template-categories", data));
					var table = $('#categoriesTable').DataTable();
					new $.fn.dataTable.AutoFill(table);
					$("button[name=edit]","#tbody_categories")
						.click(function(event){
							var button = event.target;
							$("#category_id", "#edit_category_div").val(button.getAttribute("data-id"));
							$("#category_name", "#edit_category_div").val(button.getAttribute("data-name"));
							$("#category_desc", "#edit_category_div").val(button.getAttribute("data-desc"));
							$("#edit_category_div" ).dialog( "open" );
						}
					);
					$("button[name=empty]","#tbody_categories")
						.click(function(event){
							var button = event.target;
							$.ajax({
								type: "get",
								url: "CategoryServlet?action=emptyCategory",
								data: "category_id="+button.getAttribute("data-id"),
								success: function (photos) {
									//NOP
					        	}
							});
						});
					$("button[name=remove]","#tbody_categories")
						.click(function(event){
							var button = event.target;
							$.ajax({
								type: "get",
								url: "CategoryServlet?action=removeCategory",
								data: "category_id="+button.getAttribute("data-id"),
								success: function (photos) {
									loadCategories();
					        	}
							});
						});
					$("button[name=share]","#tbody_categories")
					.click(function(event){
						var button = event.target;
						var categoryId = button.getAttribute("data-id");
						shareCategory(categoryId);
					});
					$("#categories-list").show();
					$("#categories-photos").hide();
		        }
			});
		}
		function loadCategoryPhotos(categoryId) {
			$.ajax({
				type: "get",
				url: "CategoryServlet?action=listPhotos",
				data: "category_id="+categoryId,
				success: function (photos) {
					var data = {"photos": photos};
		            $(".popup-gallery").html(tmpl("template-gallery-photos", data));
		            $('.image-link').magnificPopup({type:'image'});
					$("#categories-list").hide();
					$("#categories-photos").show();
		        }
			});
		}
		function backCategoryList() {
			$("#categories-list").show();
			$("#categories-photos").hide();
		}
		function createCategory() {
			$("#create_category_div" ).dialog( "open" );
		}
		function addToCategory() {
			//get photos' toggle ids
			var toggles = ""; 
			$(".toggle:checked", "#tbody_files").each(function(index, element) {
				toggles = toggles + (index == 0 ? "":",") + element.getAttribute("data-id");
			});
			//check photos selected
			if (toggles == "") {
				alert("Select photos want to add.");
				return;
			}

			//open category select dialog
			$("#add_photos_to_category_div").dialog("open");
			$("#photos", "#add_photos_to_category_div").val(toggles);
			//load categories
			$.ajax({
				type: "get",
				url: "CategoryServlet?action=listCategory",
				data: null,
				success: function (categories) {
					var data = {"categories": categories};
			        $("#tbody_categories_select").html(tmpl("template-categories-select", data));
					var table = $('#categoriesTable_select').DataTable();
					new $.fn.dataTable.AutoFill(table);
			    }
			});
		}
		function shareCategory(categoryId) {
			$(".ui-dialog-buttonpane button").eq(15).hide();
			$(".ui-dialog-buttonpane button").eq(14).show();
			$("#share_to_friends_div" ).dialog( "open" );
			$("#share_categories").val(categoryId);

			//search friends
			var searchParam = "user_id="+current_user_id+"&row_limit=100";
			$.ajax({
				type: "get",
				url: "FriendsServlet?action=search",
				data: searchParam,
				dataType: "json",
				success: function(friends){
		            var data = {"friends": friends};
		            $("#tbody_friends_select").html(tmpl("template-friends-select", data));
					var table = $('#friendsTable_select').DataTable();
					new $.fn.dataTable.AutoFill(table);
					//$("input[type='button']").click(addRemoveFriend);
				},
				error: function(){
					//error handle
					//updateTips("Create user failed. Try again later.");
				}
			});
		}
		function sharePhotos() {
			//get photos' toggle ids
			var toggles = ""; 
			$(".toggle:checked", "#tbody_files").each(function(index, element) {
				toggles = toggles + (index == 0 ? "":",") + element.getAttribute("data-id");
			});
			//check photos selected
			if (toggles == "") {
				alert("Select photos want to share.");
				return;
			}

			$(".ui-dialog-buttonpane button").eq(14).hide();
			$(".ui-dialog-buttonpane button").eq(15).show();
			$("#share_to_friends_div" ).dialog( "open" );
			$("#share_photos").val(toggles);
			//search friends
			var searchParam = "user_id="+current_user_id+"&row_limit=100&all_flg=0";
			$.ajax({
				type: "get",
				url: "FriendsServlet?action=search",
				data: searchParam,
				dataType: "json",
				success: function(friends){
		            var data = {"friends": friends};
		            $("#tbody_friends_select").html(tmpl("template-friends-select", data));
					var table = $('#friendsTable_select').DataTable();
					new $.fn.dataTable.AutoFill(table);
					//$("input[type='button']").click(addRemoveFriend);
				},
				error: function(){
					//error handle
					//updateTips("Create user failed. Try again later.");
				}
			});
		}
</script>
	</head>
	<body>
		<div style="display:none">
		<%@ include file="dialogs.jsp"%>
		<%@ include file="dialogs/category.html"%>
		<%@ include file="dialogs/friends.html"%>
		</div>
		<div class="main">
			<%@ include file="header.jsp"%>
			<div class="content">
				<div class="content_resize">
					<div class="mainbar">
						<div class="article">
							<div id="accordion" style="height:600px">
								<div class="group">
									<h3>Add friends</h3>
									<div>
										<table id="friendsTable" class="display" cellspacing="0" width="100%">
											<thead>
												<tr>
													<th>Name</th>
													<th>Friend email</th>
													<th>Mobile</th>
													<th>Action</th>
	
												</tr>
											</thead>
											<tbody id='tbody_friends'>
											</tbody>
										</table>
									</div>
								</div>
								<div class="group">
									<h3>Add photos</h3>
									<div>
									    <!-- The file upload form used as target for the file upload widget -->
									    <form id="fileupload" action="PhotosServlet" method="post" enctype="multipart/form-data">
									        <!-- Redirect browsers with JavaScript disabled to the origin page -->
									        <!--noscript><input type="hidden" name="redirect" value="http://blueimp.github.io/jQuery-File-Upload/"></noscript-->
									        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
									        <div class="row fileupload-buttonbar">
									            <div style="white-space:nowrap;">
									                <span class="btn fileinput-button">
									                    <i class="glyphicon glyphicon-plus"></i>
									                    <span>Add files...</span>
									                    <input type="file" name="files[]" multiple/>
									                </span>
									                <button type="submit" class="btn start">
									                    <i class="glyphicon glyphicon-upload"></i>
									                    <span>Start upload</span>
									                </button>
									                <button type="reset" class="btn cancel">
									                    <i class="glyphicon glyphicon-ban-circle"></i>
									                    <span>Cancel upload</span>
									                </button>
									                <button type="button" class="btn delete">
									                    <i class="glyphicon glyphicon-trash"></i>
									                    <span>Delete</span>
									                </button>
									                <button type="button" class="btn" onclick="addToCategory();">
									                    <i class="glyphicon glyphicon-trash"></i>
									                    <span>Add to Category</span>
									                </button>
									                <button type="button" class="btn" onclick="sharePhotos();">
									                    <i class="glyphicon glyphicon-trash"></i>
									                    <span>Share</span>
									                </button>
									                <input type="checkbox" class="toggle"/>
									                <!-- The global file processing state -->
									                <span class="fileupload-process"></span>
									            </div>
									            <!-- The global progress state -->
									            <div class="col-lg-5 fileupload-progress fade">
									                <!-- The global progress bar -->
									                <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
									                    <div class="progress-bar progress-bar-success" style="width:0%;"></div>
									                </div>
									                <!-- The extended global progress state -->
									                <div class="progress-extended">&nbsp;</div>
									            </div>
									        </div>
									        <!-- The table listing the files available for upload/download -->
									        <table role="presentation" class="table table-striped"><tbody id="tbody_files" class="files"></tbody></table>
									    </form>
									</div>
								</div>
								<div class="group">
									<h3>Share/view photos</h3>
									<div>
										<div id="categories-list">
											<!--button onclick="createCategory();">Create Category</button-->
											<!--table id="categoriesTable" class="display" cellspacing="0" width="100%">
												<thead>
													<tr>
														<th>Name</th>
														<th>Description</th>
														<th>Date</th>
														<th>Action</th>
													</tr>
												</thead>
												<tbody id='tbody_categories'>
												</tbody>
											</table-->
										</div>
										<div id="categories-photos">
											<button onclick="backCategoryList();">Back</button>
											<div class="popup-gallery">
											</div>
											<script>
											    $(document).ready(function() {
											        $('.popup-gallery').magnificPopup({
											            delegate: 'a',
											            type: 'image',
											            tLoading: 'Loading image #%curr%...',
											            mainClass: 'mfp-img-mobile',
											            gallery: {
											                enabled: true,
											                navigateByImgClick: true,
											                preload: [0,1] // Will preload 0 - before current, and 1 after the current image
											            },
											            image: {
											                tError: '<a href="%url%">The image #%curr%</a> could not be loaded.',
											                titleSrc: function(item) {
											                    return item.el.attr('title') + '<small>by Marsel Van Oosten</small>';
											                }
											            }
											        });
											    });
											</script>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="sidebar">
	      		<div class="gadget">
	            </div>
	      		<div class="gadget">
			        <h2 class="star"><span>References</span></h2><div class="clr"></div>
			        <ul class="sb_menu">
			          <li><a href="#" title="Ready for learning from guide">Learn more from the development guide,</a><br /> A serials guide based on blueMix to create cloud API apps</li>
			          <li><a href="#" title="Want to be technical author of cloud"> China GDC AIS cloud API community.</a><br /> A platform for you to build up the truly cloud hands on skills.</li>
			          <li><a href="#" title="Crowd sourcing program">What is Cloud API crowd sourcing program?</a><br />Are you ready for creating/reusing an API.</li>
			        </ul>
	      		</div>
	    	</div>
	    	<div class="clr"></div>
	  	</div>
	</div>
	
	<div class="fbg">
	  <div class="fbg_resize">
	    <div class="col c1">
	      <h2><span>About</span></h2>
	      <img src="css/images/white.jpg" width="56" height="56" alt="pix" />
	      <p>BlueMix and Cloud API learning is China GDC AIS service line key initiatives. Kindly learn more from China GDC AIS cloud API community <a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communitystart?communityUuid=ee2ef5b2-f52b-424a-b955-cf34d41156a3">Learn more...</a></p>
	    </div>
	    <div class="col c2">
	      <h2><span>Learn more</span></h2>
	      <ul class="sb_menu">
	        <li><a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communitystart?communityUuid=06a2c4b6-7ca2-4b36-b4c2-ae83a4a63716"> China GDC AIS BlueMix Community</a></li>
	        <li><a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communitystart?communityUuid=0e4495a0-c36f-4dd4-9cda-f97900baadd0"> IBM Cloud community</a></li>
	        <li><a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communityview?communityUuid=d7c4cf37-a0a9-4fe3-a253-503ad90080d2"> GBS Global Cloud community</a></li>
	        <li><a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communityview?communityUuid=6dc12080-88cc-48ef-81da-1fc78737075d"> IBM sales force community</a></li>
	        <li><a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communityview?communityUuid=8223bf5a-697d-4c73-9b0d-d63e844bf7a6">GCG smarter cloud community</a></li>
	        <li><a target="blank" href="http://ibm.biz/ibmrockstar">IBM cloud rock star community</a></li>
	      </ul>
	    </div>
	    <div class="col c3">
	    <ul class="sb_menu">
	      <h2>Contact</h2>
	      <p>If you have any questions regarding the design/development issues, please contact us in Cloud API community.</p>
	      <p>Address: China, LiaoNing, DaLian city, HuangNiChuan software park, JinYang building</p>
	     </ul>
	    </div>
	    <div class="clr"></div>
	  </div>
	</div>
	<div class="footer">
	  <div class="footer_resize">
	    <p class="lf"></p>
	   </div>
	</div>
 </div>
		
		<%@ include file="templates/uploadPhoto.html"%>
		<%@ include file="templates/downloadPhoto.html"%>
		<%@ include file="templates/categories.html"%>
		<%@ include file="templates/categories_select.html"%>
		<%@ include file="templates/friends.html"%>
		<%@ include file="templates/friends_select.html"%>
		<%@ include file="templates/gallery-photos.html"%>
		<!-- The Templates plugin is included to render the upload/download listings -->
		<script src="js/jquery/plugins/upload/tmpl.js"></script>		
		<!-- The Load Image plugin is included for the preview images and image resizing functionality -->
		<script src="js/upload/load-image.min.js"></script>
		<!-- The Canvas to Blob plugin is included for image resizing functionality -->
		<script src="js/upload/canvas-to-blob.min.js"></script>
		<!-- Bootstrap JS is not required, but included for the responsive demo navigation -->
		<script src="js/jquery/plugins/upload/bootstrap.min.js"></script>
		<!-- blueimp Gallery script -->
		<script src="js/jquery/plugins/upload/jquery.blueimp-gallery.min.js"></script>
		<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
		<script src="js/jquery/plugins/upload/jquery.iframe-transport.js"></script>
		<!-- The basic File Upload plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload.js"></script>
		<!-- The File Upload processing plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-process.js"></script>
		<!-- The File Upload image preview & resize plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-image.js"></script>
		<!-- The File Upload audio preview plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-audio.js"></script>
		<!-- The File Upload video preview plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-video.js"></script>
		<!-- The File Upload validation plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-validate.js"></script>
		<!-- The File Upload user interface plugin -->
		<script src="js/jquery/plugins/upload/jquery.fileupload-ui.js"></script>
		<!-- The main application script -->
		<script src="js/ablum/upload.js"></script>
		<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE 8 and IE 9 -->
		<!--[if (gte IE 8)&(lt IE 10)]>
		<script src="js/jquery/plugins/upload/jquery.xdr-transport.js"></script>
		<![endif]-->
	</body>
</html>
