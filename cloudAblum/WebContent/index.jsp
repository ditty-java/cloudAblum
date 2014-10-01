<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta charset="utf-8"/>
	<title>BlueMix Cloud Photo Album</title>
	<link rel="stylesheet" href="js/jquery/plugins/ui/1.11.0/themes/smoothness/jquery-ui.css"  type="text/css"/>
	<link rel="stylesheet" href="css/mystyle.css" type="text/css"/>
	<script src="js/jquery/jquery-1.11.1.js"></script>
	<script src="js/jquery/plugins/ui/1.11.0/jquery-ui.js"></script>

	<script src="js/ablum/dialogs.js"></script>
</head>
<body>
	<%@ include file="dialogs.jsp"%>
	<div class="main">
		<%@ include file="header.jsp"%>
		<div class="content">
	  		<div class="content_resize">
	    		<div class="mainbar">
	      		<div class="article">
				<% 
					String successMsg = (String)session.getAttribute("success_msg");
					String errorMsg = (String)session.getAttribute("error_msg");
					if(successMsg != null){
						out.println("<h3>"+successMsg+"</h3>");
					 	session.removeAttribute("success_msg");
					}
					
					if(errorMsg != null){
						out.println("<h3>"+errorMsg+"</h3>");		
						session.removeAttribute("error_msg");
					}
				%>
			       <h2>Note, supported broswer type:</h2>
			       <p>Firefox 24 or above</p>
			       <p>Chrome 35 or above </p>
			       <p>Doesn't support IE, due to specfic javascript function limitation.</p>
			       <h2>Walmly welcome you to use the cloud photo Album!</h2><div class="clr"></div>
			       
			       <p> What can you do from the Cloud photo Album?.</p>
			       <p> 1. Register as an user of Cloud photo Album.  </p>
			       <p> 2. Upload your faviorte photoes to your Cloud photo Album. </p>
			       <p> 3. Reivew/check your faviorte photoes as you wish.  </p>
			       <p> 4. Move your favirote photoes to your prefered Album.</p>
			       <p> 5. Share your photoes to your friends with messages. </p>
			      
			       <h2>About the Cloud photo Album</h2><div class="clr"></div>
			        <p>Posted by <a href="#">Shawn </a> &nbsp;|&nbsp; dengs@cn.ibm.com <a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communitystart?communityUuid=49e61b3c-60da-4739-a63a-fcc5a0b62fd5">China GDC AIS service line</a>, <a target="blank" href="https://w3-connections.ibm.com/communities/service/html/communitystart?communityUuid=ee2ef5b2-f52b-424a-b955-cf34d41156a3">China GDC AIS Cloud API community</a></p>
			        <img src="css/images/banner3.png" width="613" height="154" alt="image" />
			        <p>This Web site is created by dengs@cn.ibm.com as a cloud album.</p>
			        <p><b>The technology overview:</b></p>
			        <p>(1). Using jquery validation and jquery core for front end UI.</p>
			        <p>(2). Using J2ee servlet 1.3 as controller.</p>
			        <p>(3). Using self created SimplerJDBCHelper as perstistence framework for basic CRUD operation as well as leveraging the BlueMix DB2 service.</p>
			        <p>(4). Using 3rd party data modeling tool for creating logic/physical data model, then generated DDL, finally execute the DDL through IBM DB2 BlueMix web console for creating tables.</p>
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
</body>
</html>
