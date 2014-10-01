package com.ibm.cloud.ablum.dao.inf;

import java.util.List;
import java.util.Map;

import com.ibm.cloud.ablum.exceptions.FriendCantAddedException;
import com.ibm.cloud.ablum.exceptions.FriendCantRemovedException;

public interface FriendsDAOInf {
	public List<Map<String, String>> getFriends(String userId, String rowLimit, boolean allFlg);
	public int addFriend (String userId, String friendUserId) throws FriendCantAddedException;
	public int removeFriend(String userId, String friendUserId)  throws FriendCantRemovedException;
	public boolean isFriendAdded(String userId, String friendUserId);
	
	public int sharePhoto2Friend(String photoId, String userId);
	public int sharePhoto2Friend(String[] photoId, String userId);
	
	public int shareCategory2Friend(String categoryId, String userId);
	public int shareCategory2Friend(String[] categoryId, String userId);
	
	public int sharePhoto2Friends(String photoId, String[] userIds);
	public int sharePhoto2Friends(String[] photoId, String[] userIds);
	public int shareCategory2Friends(String[] categoryId, String[] userId);
	
	public long getNextId();
	
}
