package com.mebelkart.api.admin.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.Privilages;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;
import com.mebelkart.api.admin.v1.mapper.PrivilagesMapper;
import com.mebelkart.api.admin.v1.mapper.UserStatusMapper;

/**
 * @author Tinku
 *
 */
@UseStringTemplate3StatementLocator
public interface AdminDAO {
	/**
	 * This query uses user details for login
	 * @param user_name of admin
	 * @param password of admin
	 * @param level of admin
	 * @return row data of type list
	 */
	@SqlQuery("select a_user_name,a_password,a_admin_level from mk_api_user_admin where a_user_name = :user_name and a_password = :password")
	@Mapper(AdminMapper.class)
	List<Admin> login(@Bind("user_name") String user_name,
			@Bind("password") String password);
	
	/**
	 * This query returns userNames and isActive status of each and every Admin based on type of status
	 * @param status
	 * @return
	 */
	@SqlQuery("select a_user_name,a_is_active from <tableName> where a_is_active = :status order by a_user_name ASC")
	@Mapper(UserStatusMapper.class)
	List<UserStatus> getUsersDetailsWithStatus(@Bind("status") long status,@Define("tableName") String tableName);
	
	/**
	 * This query returns userNames and isActive status of each and every Consumer based on type of status
	 * @param status
	 * @return
	 */
	@SqlQuery("select a_user_name,a_is_active from <tableName> order by a_user_name ASC")
	@Mapper(UserStatusMapper.class)
	List<UserStatus> getUsersDetailsWithoutStatus(@Define("tableName") String tableName);
	
	@SqlQuery("SELECT mk_api_resources.a_resource_name, <tableName>.a_have_get_permission, <tableName>.a_have_post_permission, "
			+"<tableName>.a_have_put_permission, <tableName>.a_have_delete_permission "
			+"FROM <tableName> "
			+"INNER JOIN mk_api_resources "
			+"ON <tableName>.a_resource_id = mk_api_resources.id "
			+"WHERE <tableName>.<colName> = :userId")
	@Mapper(PrivilagesMapper.class)
	List<Privilages> getUserPrivileges(@Bind("userId") long userId,@Define("tableName") String tableName,@Define("colName") String colName);

	/**
	 * This query validates the given header token is valid or not
	 * @param accessToken of admin/superadmin
	 * @return non zero on success
	 */
	@SqlQuery("select a_admin_level from mk_api_user_admin where a_access_token = :accessToken")
	int validate(@Bind("accessToken") String accessToken);
	
	/**
	 * This query checks if admin is in active state or not
	 * @param accessToken of admin/superadmin
	 * @return 0 for Inactive, 1 for Active
	 */
	@SqlQuery("select a_is_active from mk_api_user_admin where a_access_token = :accessToken")
	int isAdminInActiveState(@Bind("accessToken") String accessToken);

	/**
	 * This query adds consumer details to table
	 * @param userName is given by user
	 * @param accessToken is auto generated
	 * @param countAssigned is given by user
	 * @return inserted row id
	 */
	@SqlUpdate("insert into mk_api_consumer (a_user_name,a_access_token,a_count_assigned) values (:userName, :accessToken, :countAssigned)")
	@GetGeneratedKeys
	int addConsumer(@Bind("userName") String userName,
			@Bind("accessToken") String accessToken,
			@Bind("countAssigned") long countAssigned);

	/**
	 * This query adds admin details to table
	 * @param userName is given by user
	 * @param password is auto generated
	 * @param accessToken is auto generated
	 * @return inserted row id
	 */
	@SqlUpdate("insert into mk_api_user_admin (a_user_name,a_access_token,a_password) values (:userName, :accessToken, :password)")
	@GetGeneratedKeys
	int addAdmin(@Bind("userName") String userName,
			@Bind("accessToken") String accessToken,
			@Bind("password") String password);
	
	/**
	 * This query adds permissions to user who is pre-registered
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("insert into <table> (a_resource_id,<userColoumnName>,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :userId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void insertUserPermission(@Bind("resourceId") long resourceId,
			@Bind("userId") long userId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission,
			@Define("table") String table,
			@Define("userColoumnName") String colName);
	
	/**
	 * This query updates permissions to user who is pre-registered
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("update <tableName> set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission where a_resource_id = :resourceId and <userColoumnName> = :userId")
	void updateUserPermission(@Bind("resourceId") long resourceId,
			@Bind("userId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission,
			@Define("tableName") String tableName,
			@Define("userColoumnName") String colName);
	
	/**
	 * This query returns user row id
	 * @param userName of type email
	 * @return id
	 */
	@SqlQuery("select id from <tableName> where a_user_name = :userName")
	int getUserId(@Bind("userName") String userName,@Define("tableName") String tableName);

	/**
	 * This query checks whether user is already registered or not
	 * @param userName of type email
	 * @return String userName of type email
	 */
	@SqlQuery("select a_user_name from <tableName> where a_user_name = :userName")
	String isUserNameAlreadyExists(@Bind("userName") String userName,@Define("tableName") String tableName);
	
	/**
	 * This query updates active status of User
	 * @param userName email of Admin
	 * @param status 1 for isActive/0 for isNotActive
	 */
	@SqlUpdate("update <tableName> set a_is_active = :status where a_user_name = :userName")
	void changeUserActiveStatus(@Bind("userName") String userName,@Bind("status") long status,@Define("tableName") String tableName);
	
	/**
	 * Checks whether the user is given permissions for specific resource id or not
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @return non zero on success
	 */
	@SqlQuery("select a_permission_id from <tableName> where a_resource_id = :resourceId and <userColoumnName> = :consumerId")
	int isUserPermissionExists(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,@Define("tableName")String tableName,@Define("userColoumnName") String colName);
	
	/**
	 * Checks whether resource id is valid api package id or not
	 * @param resourceId consists of api package id
	 * @return non zero on success
	 */
	@SqlQuery("select id from mk_api_resources where id = :resourceId")
	int isValidResource(@Bind("resourceId") long resourceId);
}
