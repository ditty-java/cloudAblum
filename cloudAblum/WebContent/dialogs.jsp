<%@page import="com.ibm.cloud.ablum.beans.UserBean"%>
<!-- Register Dialog -->
<div id="register_div" title="Create new user">
	<form method ="post" action="RegisterServlet" name="user_register" id="register_form">
		<p class="validateTips"></p>
		<fieldset>
			<label for="name">*Name</label>
			<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" />
			<label for="email">*Email</label>
			<input type="text" name="email" id="email" value="" class="text ui-widget-content ui-corner-all" />
			<label for="password">*Password</label>
			<input type="password" name="password" id="password" value="" class="text ui-widget-content ui-corner-all" />
			<label for="userMobile">Mobile</label>
			<input type="text" name="userMobile" id="userMobile" class="text ui-widget-content ui-corner-all" />
			<label for="userHomePhone">Your Home Phone</label>
			<input type="text" name="userHomePhone" id="userHomePhone" class="text ui-widget-content ui-corner-all" />
			<label for="userNote">Leave a Note here</label>
			<input type="text" name="userNote" id="userNote" class="text ui-widget-content ui-corner-all" />
			<label for="userQQ">Your QQ</label>
			<input type="text" name="userQQ" id="userQQ" class="text ui-widget-content ui-corner-all" />
			<label for="userSelfDescription">Leave self description for your friends:</label>
			<input type="text" name="userSelfDescription" id="userSelfDescription" class="text ui-widget-content ui-corner-all" />
		</fieldset>
	</form>
</div>
 
<!-- Sign-Out Dialog (not display) -->
<div id="sign_out_div">
	<form method ="post" action="SignInOutServlet" name="sign_out" id="sign_out_form">
		<input type="hidden" name="sign_out_flg" id="sign_out_flg"/>
	</form>
</div>

<!-- Profile Dialog -->
<div id="profile_div" title="Edit Profile">
	<form method ="post" action="ProfileServlet" name="user_profile" id="profile_form">
		<p class="validateTips"></p>
		<fieldset>
			<%if (session.getAttribute("current_user") != null) {%>
			<label for="profile_name">Name</label>
			<input type="text" name="profile_name" id="profile_name" value="<%=((UserBean)session.getAttribute("current_user")).getUserName()%>" class="text ui-widget-content ui-corner-all" readonly/>
			<label for="profile_email">Email</label>
			<input type="text" name="profile_email" id="profile_email" value="<%=((UserBean)session.getAttribute("current_user")).getEmail()%>" class="text ui-widget-content ui-corner-all"  readonly/>
			<label for="profile_pwd1">Password</label>
			<input type="password" name="profile_pwd1" id="profile_pwd1" value="" class="text ui-widget-content ui-corner-all .validateTips" />
			<label for="profile_pwd2">Password&nbsp;-&nbsp;again</label>
			<input type="password" name="profile_pwd2" id="profile_pwd2" value="" class="text ui-widget-content ui-corner-all" />
			<label for="profile_userMobile">Mobile</label>
			<input type="text" name="profile_userMobile" id="profile_userMobile" value="<%=((UserBean)session.getAttribute("current_user")).getMobile()%>" class="text ui-widget-content ui-corner-all" />
			<label for="profile_userHomePhone">Your Home Phone</label>
			<input type="text" name="profile_userHomePhone" id="profile_userHomePhone" value="<%=((UserBean)session.getAttribute("current_user")).getHomePhoneNum()%>" class="text ui-widget-content ui-corner-all" />
			<label for="profile_userNote">Leave a Note here</label>
			<input type="text" name="profile_userNote" id="profile_userNote" value="<%=((UserBean)session.getAttribute("current_user")).getNote()%>" class="text ui-widget-content ui-corner-all" />
			<label for="profile_userQQ">Your QQ</label>
			<input type="text" name="profile_userQQ" id="profile_userQQ" value="<%=((UserBean)session.getAttribute("current_user")).getQQ()%>" class="text ui-widget-content ui-corner-all" />
			<label for="profile_userSelfDescription">Leave self description for your friends:</label>
			<input type="text" name="profile_userSelfDescription" id="profile_userSelfDescription" value="<%=((UserBean)session.getAttribute("current_user")).getSelfIntroduction()%>" class="text ui-widget-content ui-corner-all" />
			<%}%>
			<input type="hidden" name="profile_pwd" id="profile_pwd"/>
		</fieldset>
	</form>
</div>

<!-- Sign-In dialog -->
<div id="sign_in_div" title="User sign in">
	<form method ="post" action="SignInOutServlet" name="user_sign_in" id="sign_in_form">
		<p class="validateTips"></p>
		<fieldset>
			<label for="sign_in_mail">*Your email</label>
			<input type="text" name="sign_in_mail" id="sign_in_mail" class="text ui-widget-content ui-corner-all" />
			<label for="sign_in_pwd">*Password</label>
			<input type="password" name="sign_in_pwd" id="sign_in_pwd" value="" class="text ui-widget-content ui-corner-all" />
		</fieldset>
	</form>
 </div>
 
 <!-- Photo Comment dialog -->
<div id="photo_comment_div" title="Your photo's comment">
	<form method ="post" action="FileUploadServlet" name="photo_comment" id="photo_comment_form">
		<fieldset>
			<input type="hidden" id="commentfile" name="commentfile" value=""/>
			<label for="pohoto_comment">Your Comment</label>
			<textarea id="pohoto_comment" name="pohoto_comment" class="text ui-widget-content ui-corner-all" style="width:100%, height:100%">
			</textarea>
		</fieldset>
	</form>
 </div>