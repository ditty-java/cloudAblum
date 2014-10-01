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
import com.ibm.cloud.ablum.exceptions.UserRegisteredException;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet(name = "RegisterServlet", urlPatterns = { "/RegisterServlet" })

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Redirect it to weather.jsp
		//response.sendRedirect("index.jsp");
		PrintWriter out = response.getWriter();
		
		String userName = request.getParameter("name");
		String userEmail = request.getParameter("email");
		String userHomePhone = request.getParameter("userHomePhone");
		String userMobile = request.getParameter("userMobile");
		String userSelfDescription = request.getParameter("userSelfDescription");
		String userNote = request.getParameter("userNote");
		String userPassword = request.getParameter("password");
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
			//create user
			code = userDao.registerUser(user);
		}catch(UserRegisteredException e){
			//request.getSession().setAttribute("error_msg", e.getExceptionInfo());
			out.print("fail:" + e.getExceptionInfo());
			return;
		}
		System.out.println("user register code is " + code);
		
		if(code == 1){
			//auto sing-in
			request.getSession().setAttribute("current_user", user);

			//success message setting
			//request.getSession().setAttribute("success_msg", 
			//		"Congratuals! Your have successfully registered as user of cloud photo album, enjoy the moment with your friends!");
			out.print("success");
		}else{
			//request.getSession().setAttribute("error_msg", "Your input has something wrong, please try it again later for registration!");
			out.print("fail: others");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("system join the doPOST() method of RegisterServlet");
		
		String userName = request.getParameter("name");
		String userEmail = request.getParameter("email");
		String userHomePhone = request.getParameter("userHomePhone");
		String userMobile = request.getParameter("userMobile");
		String userSelfDescription = request.getParameter("userSelfDescription");
		String userNote = request.getParameter("userNote");
		String userPassword = request.getParameter("password");
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
			//create user
			code = userDao.registerUser(user);
		}catch(UserRegisteredException e){
			request.getSession().setAttribute("error_msg", e.getExceptionInfo());
			response.sendRedirect("index.jsp");
			return;
		}
		System.out.println("user register code is " + code);
		
		if(code == 1){
			//auto sing-in
			request.getSession().setAttribute("current_user", user);

			//success message setting
			request.getSession().setAttribute("success_msg", 
					"Congratuals! Your have successfully registered as user of cloud photo album, enjoy the moment with your friends!");
			response.sendRedirect("home.jsp");
		}else{
			request.getSession().setAttribute("error_msg", "Your input has something wrong, please try it again later for registration!");
			response.sendRedirect("index.jsp");
		}
	}

}
