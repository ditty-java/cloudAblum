package com.ibm.cloud.ablum.dao.inf;

import java.util.List;

import com.ibm.cloud.ablum.beans.CategoryBean;
import com.ibm.cloud.ablum.beans.PhotoBean;
import com.ibm.cloud.ablum.exceptions.CategoryCantRemovedException;

public interface PhotoDaoInf {
	/**
	 * Add a new photo into Photo_ablum table.
	 * @param userId
	 * @param path
	 * @param photoName
	 * @return sql execution return code
	 */
	public long addPhoto(String userId, String path, String photoName, String photoDesc);
	/**
	 * Remove a photo from photo_album table.
	 * @param userId
	 * @param photoId
	 * @return sql execution return code
	 */
	public int removePhoto(String userId, String photoId);
	/**
	 * Update Photo's Description
	 * @param userId
	 * @param photoId
	 * @param photoComment
	 * @return
	 */
	public int updatePhotoDesc(String userId, String photoId, String photoComment);
	/**
	 * Update photo name from photo_album table by userId and photoId.
	 * @param userId
	 * @param photoId
	 * @param newName
	 * @return sql execution return code
	 */
	public int renamePhotoById(long userId, long photoId, String newName);
	/**
	 * Retrieve PhotoInformation from photo_album table by userId and photoId
	 * @param userId
	 * @param photoId
	 * @return photoBean with photo information
	 */
	public PhotoBean getPhotoInfoById(long userId, long photoId);
	/**
	 * Add category into CATEGORY table with userId and categoryName information
	 * @param userId
	 * @param categoryName
	 * @return sql execution return code
	 */
	public long addCategory(long userId, String categoryName, String categoryDesc);
	/**
	 * Remove a category from CATEGORY table by userId and categoryId
	 * @param userId
	 * @param categoryId
	 * @return sql execution return code
	 * @throws CategoryCantRemovedException 
	 */
	public int removeCategory(long userId, long categoryId ) throws CategoryCantRemovedException;
	/**
	 * Rename category by categoryId from CATEGORY table.
	 * @param userId
	 * @param categoryId
	 * @param newName
	 * @return sql execution return code
	 */
	public int renameCategoryById(long userId, long categoryId, String newName);
	/**
	 * Retrieve category and category relevant photos, then put category info and relevant photos into categoryBean.
	 * @param userId
	 * @param categoryId
	 * @return categoryBean
	 */
	public CategoryBean getCategoryById(long userId, long categoryId);
	/**
	 * Retrieve categories by UserId.
	 * @param userId
	 * @param categoryId
	 * @return categoryBean
	 */
	public List<CategoryBean> getCategoriesByUser(long userId);	
	/**
	 * Get photos under the userId and categoryId
	 * @param categoryId
	 * @return
	 */
	public List<PhotoBean> getPhotosByCategory(long categoryId);
	/**
	 * Get all the photos under the userId, but it without category.
	 * @param userId
	 * @return photos without category
	 */
	public List<PhotoBean> getAllPhotosByUserId(long userId);
	/**
	 * PhotoAlbumBean includes a HasMap, the key is category id, the value is the category Id relevant photos.
	 * PhotoAlbumBean includes userId.
	 * @param userId
	 * @return PhotoAlbumBean
	 */
//	public PhotoAlbumBean retrieveAllPhotosWithCategoriesByUserId(long userId);
	/**
	 * Retrieve user relevant categoryId from category table by userId
	 * @param userId
	 * @return user relevant categoryId.
	 */
//	public List<Long> retrieveUserRelevantCategoryIds(long userId);
	
	/**
	 * insert userId, photoId and categoryId into CATEGORY_RELEVANT_PHOTO table.
	 * @param photoId
	 * @param categoryId
	 * @return SQL execution result
	 */
	public int putPhotoUnderCategory(long categoryId, long photoId);
	/**
	 * remove photo from category.
	 * @param categoryId
	 * @param photoId
	 * @return
	 */
	public int removePhotoFromCategory(long categoryId, long photoId);
	/**
	 * Generate unique primary key for Photo relevant tables.
	 * @return generated primary key.
	 */
	public long getNextId();
	/**
	 * 
	 * @param userId
	 * @param categoryId
	 * @param categoryName
	 * @param categoryDesc
	 * @return
	 */
	public int updateCategory(long categoryId, String categoryName, String categoryDesc);
	boolean checkCategoryNotEmpty(long categoryId);
	boolean checkContainsPhoto(long selectedCategoryId, long parseLong);

	
}
