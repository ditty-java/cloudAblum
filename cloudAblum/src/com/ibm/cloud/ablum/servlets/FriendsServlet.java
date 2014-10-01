package com.ibm.cloud.ablum.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.cloud.ablum.beans.UserBean;
import com.ibm.cloud.ablum.dao.FriendDao;
import com.ibm.cloud.ablum.exceptions.FriendCantAddedException;
import com.ibm.cloud.ablum.exceptions.FriendCantRemovedException;
import com.ibm.cloud.ablum.util.Constants;
import com.ibm.commons.util.io.json.JsonException;
import com.ibm.commons.util.io.json.JsonFactory;
import com.ibm.commons.util.io.json.JsonGenerator;
import com.ibm.commons.util.io.json.JsonJavaFactory;

/**
 * Servlet implementation class FriendsServlet
 */
@WebServlet(name = "FriendsServlet", urlPatterns = { "/FriendsServlet" })

public class FriendsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final static String KEY_ACTION = "action";
	private final static String KEY_FRIEND_ID = "friend_id";
	private final static String KEY_ROW_LIMIT = "row_limit";
	private final static String KEY_ALL_FLG = "all_flg";
	private final static String SPLITOR = ",";
	private final static String KEY_SHARE_FRIENDS = "share_friends";
	private final static String KEY_SHARE_CATEGORIES = "share_categories";
	private final static String KEY_SHARE_PHOTOS = "share_photos";
	
	private static enum Action {
		Search("search"),
		Add("add"),
		Remove("remove"),
		SharePhoto("sharePhoto"),
		ShareCategory("shareCategory");
		private String action;
		private Action(String action) {
			this.action = action;
		}

		public boolean equals(String value) {
			return action.equals(value);
		}
	}
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FriendsServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("system join the doGET() method of FriendsServlet");
		UserBean user = (UserBean) request.getSession().getAttribute(Constants.SESSION_KEY_CURRENT_USER);
		String action = request.getParameter(KEY_ACTION);
		PrintWriter out = response.getWriter();
		try{
			if (Action.Search.equals(action)) {
				//search friends
				doSearch(user, request, out);
			} else if (Action.Add.equals(action)) {
				//add friend
				doAddFriend(user, request, out);
			} else if (Action.Remove.equals(action)) {
				//remove friends
				doRemoveFriend(user, request, out);
			} else if (Action.SharePhoto.equals(action)) {
				//remove friends
				doSharePhoto(user, request, out);
			} else if (Action.ShareCategory.equals(action)) {
				//remove friends
				doShareCategory(user, request, out);
			}
		} finally {
			out.close();
		}
	}

	private void doSearch(UserBean user, HttpServletRequest request, PrintWriter out) throws IOException {
		String userId = String.valueOf(user.getUserId());
		String rowLimit = request.getParameter(KEY_ROW_LIMIT);
		String allFlg = request.getParameter(KEY_ALL_FLG);
		try {
			FriendDao friendDao = new FriendDao();
			List<Map<String, String>> friends = friendDao.getFriends(userId, rowLimit, allFlg.equals("1"));

			//request.getSession().setAttribute("friendList", friends);
			JsonFactory jsonFactory = new JsonJavaFactory();
			
			//output Json data
			out.print(JsonGenerator.toJson(jsonFactory, friends));
			System.out.println(JsonGenerator.toJson(jsonFactory, friends));
			return;
		} catch (JsonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doAddFriend(UserBean user, HttpServletRequest request, PrintWriter out) {
		String userId = String.valueOf(user.getUserId());
		String friend_id = request.getParameter(KEY_FRIEND_ID);
		try {
			FriendDao friendDao = new FriendDao();
			int resultCd = friendDao.addFriend(userId, friend_id);

			out.print(resultCd);
			return;
		} catch (FriendCantAddedException e) {
			e.printStackTrace();
			out.print("failed:"+e.getExceptionInfo());
		}
	}

	private void doRemoveFriend(UserBean user, HttpServletRequest request, PrintWriter out) {
		String userId = String.valueOf(user.getUserId());
		String friend_id = request.getParameter(KEY_FRIEND_ID);
		try {
			FriendDao friendDao = new FriendDao();
			int resultCd = friendDao.removeFriend(userId, friend_id);

			out.print(resultCd);
			return;
		} catch (FriendCantRemovedException e) {
			e.printStackTrace();
			out.print("failed:"+e.getExceptionInfo());
		}
	}

	private void doSharePhoto(UserBean user, HttpServletRequest request, PrintWriter out) {
		String friend_id = request.getParameter(KEY_SHARE_FRIENDS);
		String photo_id = request.getParameter(KEY_SHARE_PHOTOS);
		String[] friend_ids = friend_id.split(SPLITOR);
		String[] photo_ids = photo_id.split(SPLITOR);

		FriendDao friendDao = new FriendDao();
		int resultCd = friendDao.sharePhoto2Friends(photo_ids, friend_ids);

		out.print(resultCd);
		return;
	}

	private void doShareCategory(UserBean user, HttpServletRequest request, PrintWriter out) {
		String friend_id = request.getParameter(KEY_SHARE_FRIENDS);
		String category_id = request.getParameter(KEY_SHARE_CATEGORIES);
		String[] friend_ids = friend_id.split(SPLITOR);
		String[] category_ids = category_id.split(SPLITOR);

		FriendDao friendDao = new FriendDao();
		int resultCd = friendDao.shareCategory2Friends(category_ids, friend_ids);

		out.print(resultCd);
		return;
	}	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("system join the doPOST() method of FriendsServlet");
	}
}
