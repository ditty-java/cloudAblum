package com.ibm.cloud.ablum.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.UserDAO;
import com.ibm.cloud.ablum.exceptions.ProfileUpdateFailedException;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(name = "ProfileServlet", urlPatterns = { "/ProfileServlet" })

public class ProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Redirect it to weather.jsp
		//response.sendRedirect("index.jsp");
		System.out.println("system join the doGET() method of RegisterServlet");
		
		String userName = request.getParameter("profile_name");
		String userEmail = request.getParameter("profile_email");
		String userHomePhone = request.getParameter("profile_userHomePhone");
		String userMobile = request.getParameter("profile_userMobile");
		String userSelfDescription = request.getParameter("profile_userSelfDescription");
		String userNote = request.getParameter("profile_userNote");
		String userPassword = request.getParameter("profile_pwd");
		String userQQ = request.getParameter("profile_userQQ");
		String userBluePageImgUrl = "https://w3-connections.ibm.com/profiles/photo.do?email=" + userEmail;
		
		System.out.println("userName->" + userName);
		PrintWriter out = response.getWriter();
		UserBean user = new UserBean();
		user.setEmail(userEmail);
		user.setHomePhoneNum(userHomePhone);
		user.setMobile(userMobile);
		user.setNote(userNote);
		user.setPassword(userPassword);
		user.setQQ(userQQ);
		user.setSelfIntroduction(userSelfDescription);
		user.setUserName(userName);
		user.setImageBluePgUrl(userBluePageImgUrl);
		
		UserDAO userDao = new UserDAO();
		int code = -100;
		try{
			code = userDao.updateUser(user);
		}catch(ProfileUpdateFailedException e){
			//request.getSession().setAttribute("error_msg", e.getExceptionInfo());
			//response.sendRedirect("index.jsp");
			out.print("failed: " + e.getExceptionInfo());
			return;
		}
		System.out.println("user register code is " + code);
		
		if(code == 1){
			//request.getSession().setAttribute("success_msg", 
			//		"Congratuals! Your have successfully registered as user of cloud photo album, enjoy the moment with your friends!");
			//response.sendRedirect("home.jsp");
			out.print("success");
		}else{
			//request.getSession().setAttribute("error_msg", "Your input has something wrong, please try it again later for registration!");
			//response.sendRedirect("index.jsp");
			out.print("failed: unkonwn error.");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("system join the doPOST() method of RegisterServlet");
		
		String userName = request.getParameter("profile_name");
		String userEmail = request.getParameter("profile_email");
		String userHomePhone = request.getParameter("profile_userHomePhone");
		String userMobile = request.getParameter("profile_userMobile");
		String userSelfDescription = request.getParameter("profile_userSelfDescription");
		String userNote = request.getParameter("profile_userNote");
		String userPassword = request.getParameter("profile_pwd");
		String userQQ = request.getParameter("userQQ");
		String userBluePageImgUrl = "https://w3-connections.ibm.com/profiles/photo.do?email=" + userEmail;
		
		System.out.println("userName->" + userName);
		
		UserBean user = new UserBean();
		user.setEmail(userEmail);
		user.setHomePhoneNum(userHomePhone);
		user.setMobile(userMobile);
		user.setNote(userNote);
		user.setPassword(userPassword);
		user.setQQ(userQQ);
		user.setSelfIntroduction(userSelfDescription);
		user.setUserName(userName);
		user.setImageBluePgUrl(userBluePageImgUrl);
		
		UserDAO userDao = new UserDAO();
		int code = -100;
		try{
			code = userDao.updateUser(user);
		}catch(ProfileUpdateFailedException e){
			request.getSession().setAttribute("error_msg", e.getExceptionInfo());
			response.sendRedirect("index.jsp");
			return;
		}
		System.out.println("user register code is " + code);
		
		if(code == 1){
			request.getSession().setAttribute("success_msg", 
					"Congratuals! Your have successfully registered as user of cloud photo album, enjoy the moment with your friends!");
			response.sendRedirect("home.jsp");
		}else{
			request.getSession().setAttribute("error_msg", "Your input has something wrong, please try it again later for registration!");
			response.sendRedirect("index.jsp");
		}
	}

}
