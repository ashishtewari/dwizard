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
import com.mebelkart.api.admin.v1.core.FunctionNames;
import com.mebelkart.api.admin.v1.core.Privilages;
import com.mebelkart.api.admin.v1.core.ResourceNames;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;
import com.mebelkart.api.admin.v1.mapper.FunctionNamesMapper;
import com.mebelkart.api.admin.v1.mapper.PrivilagesMapper;
import com.mebelkart.api.admin.v1.mapper.ResourceNamesMapper;
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
	 * This query is used to get the username of the related accesstoken
	 * @param apikey of admin
	 * @return username
	 */
	@SqlQuery("select a_user_name from mk_api_user_admin where a_access_token = :apikey")
	String getUserNameRelatedToAccessToken(@Bind("apikey") String apikey);
	
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
	
	/**
	 * This query shows the previlages of each type of user
	 * @param userId
	 * @param tableName
	 * @param colName
	 * @return
	 */
	@SqlQuery("SELECT mk_api_resources.id,mk_api_resources.a_resource_name, <tableName>.a_have_get_permission, <tableName>.a_have_post_permission, "
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
	@SqlQuery("select a_admin_level from mk_api_user_admin where a_access_token = :accessToken and a_is_active = 1")
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
	@SqlUpdate("insert into mk_api_consumer (a_user_name,a_access_token,a_count_assigned,a_added_by,a_modified_by) "
			+ "values (:userName, :accessToken, :countAssigned, :addedBy, :addedBy)")
	@GetGeneratedKeys
	int addConsumer(@Bind("userName") String userName,
			@Bind("accessToken") String accessToken,
			@Bind("countAssigned") long countAssigned,
			@Bind("addedBy") String addedBy);

	/**
	 * This query adds admin details to table
	 * @param userName is given by user
	 * @param password is auto generated
	 * @param accessToken is auto generated
	 * @return inserted row id
	 */
	@SqlUpdate("insert into mk_api_user_admin (a_user_name,a_access_token,a_password,a_added_by,a_modified_by) "
			+ "values (:userName, :accessToken, :password, :addedBy, :addedBy)")
	@GetGeneratedKeys
	int addAdmin(@Bind("userName") String userName,
			@Bind("accessToken") String accessToken,
			@Bind("password") String password,
			@Bind("addedBy") String addedBy);
	
	/**
	 * This query adds permissions to user who is pre-registered
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("insert into <table> (a_resource_id,<userColoumnName>,a_have_get_permission,a_have_post_permission,a_have_put_permission) "
			+ "values (:resourceId, :userId, :haveGetPermission, :havePostPermission, :havePutPermission)")
	void insertUserPermission(@Bind("resourceId") long resourceId,
			@Bind("userId") long userId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
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
	@SqlUpdate("update <tableName> set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,"
			+ "a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission "
			+ "where a_resource_id = :resourceId and <userColoumnName> = :userId")
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
	 * @param userName email of user
	 * @param status 1 for isActive/0 for isNotActive
	 */
	@SqlUpdate("update <tableName> set a_is_active = :status,a_modified_by = :modifiedBy where a_user_name = :userName")
	void changeUserActiveStatus(@Bind("userName") String userName,@Bind("status") long status,
			@Define("tableName") String tableName,@Bind("modifiedBy") String modifiedBy);
	
	/**
	 * This query updates countAssigned coloumn of specific user in DB
	 * @param userName email of consumer
	 * @param rateLimit countAssigned in DB
	 * @param modifiedBy admin userName
	 */
	@SqlUpdate("update mk_api_consumer set a_count_assigned = :rateLimit,a_modified_by = :modifiedBy where a_user_name = :userName")
	void changeRateLimit(@Bind("userName") String userName,@Bind("rateLimit") long rateLimit,@Bind("modifiedBy") String modifiedBy);
	
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
	
	/**
	 * It modifies the date in user table when ever there is change in resource permission table
	 * @param tableName admin/consumer table name
	 * @param modifiedDate current time stamp
	 * @param userId unique rowid of user
	 */
	@SqlUpdate("update <tableName> set a_date_modified = :modifiedDate where id = :userId")
	void dateModified(@Define("tableName") String tableName,@Bind("modifiedDate") String modifiedDate,@Bind("userId") long userId);
	
	/**
	 * It modifies the modifiedBy field in user table when ever there is change in resource permission table
	 * @param tableName admin/consumer table name
	 * @param modifiedBy current time stamp
	 * @param userId unique rowid of user
	 */
	@SqlUpdate("update <tableName> set a_modified_by = :modifiedBy where id = :userId")
	void modifiedBy(@Define("tableName") String tableName,@Bind("modifiedBy") String modifiedBy,@Bind("userId") long userId);
	
	/**
	 * This query assigns function permissions to particular resource_id for a user
	 * @param resourceId This is the id of resource
	 * @param userId This is the id of user
	 * @param type This name of the method request 
	 * @param functionName This is the name of the function in a particular resource_id
	 * @param isActive This tells whether the user is having active permissions to this particular function of particular resource_id
	 * @param userColoumnName This is the coloumn name of the consumer or admin in this table
	 */
	@SqlUpdate("insert into <tableName> (<userColoumnName>,a_function_id,a_is_active) "
			+ "values (:userId, :functionId, :isActive)")
	void insertUserFunctionPermissions(@Bind("userId") long userId,@Bind("functionId") long functionId,
			@Bind("isActive") int isActive,@Define("tableName") String tableName,@Define("userColoumnName") String userColoumnName);
	
	/**
	 * This query retrieves all the function name and its Ids with respect to their method type of a particular resource 
	 * @param resourceId This is the resource id
	 * @param type This is the type of the method
	 * @return List of function names
	 */
	@SqlQuery("select id,a_function_name from mk_api_functions where a_resource_id = :resourceId and a_type = :type and a_is_active = 1")
	@Mapper(FunctionNamesMapper.class)
	List<FunctionNames> getFunctionNamesWithIds(@Bind("resourceId") long resourceId,@Bind("type") String type);
	
	/**
	 * This query retrieves all the function name with respect to their method type of a particular resource 
	 * @param resourceId This is the resource id
	 * @param type This is the type of the method
	 * @return List of function names
	 */
	@SqlQuery("select a_function_name from mk_api_functions where a_resource_id = :resourceId and a_type = :type and a_is_active = 1")
	List<String> getFunctionNames(@Bind("resourceId") long resourceId,@Bind("type") String type);
	
	/**
	 * This query deactivates all functions permissions for a specific resource of user
	 * @param resourceId 
	 * @param userId
	 * @param type
	 * @param isActive
	 * @param userColoumnName
	 */
	@SqlUpdate("update <tableName> t1 inner join mk_api_functions t2 on "
			+ "t1.a_function_id = t2.id "
			+ "set t1.a_is_active = :isActive "
			+ "WHERE t2.a_resource_id = :resourceId and t1.<userColoumnName> = :userId and t2.a_type = :type")
	void removeFunctionPermissions(@Bind("resourceId") long resourceId, @Bind("userId") long userId, @Bind("type") String type, 
			@Bind("isActive") int isActive, @Define("userColoumnName") String userColoumnName, @Define("tableName")String tableName);
	
	/**
	 * This query retrieves the function names of user for a particular resource
	 * @param resourceId
	 * @param userId
	 * @param type
	 * @param isActive
	 * @param userColoumnName
	 * @return
	 */
	@SqlQuery("select mk_api_functions.a_function_name from mk_api_functions inner join <tableName> ON mk_api_functions.id = <tableName>.a_function_id "
			+ "where <tableName>.<userColoumnName> = :userId and <tableName>.a_is_active = :isActive and mk_api_functions.a_resource_id = :resourceId "
			+ "and mk_api_functions.a_type = :type and mk_api_functions.a_is_active = 1")
	List<String> getPreAssignedFunctionNames(@Bind("resourceId") long resourceId, @Bind("userId") long userId, @Bind("type") String type, 
			@Bind("isActive") int isActive, @Define("userColoumnName") String userColoumnName, @Define("tableName")String tableName);
	
	/**
	 * This query deactivates the specific function of user for a specific resource
	 * @param resourceId
	 * @param userId
	 * @param type
	 * @param functionName
	 * @param isActive
	 * @param userColoumnName
	 */
	@SqlUpdate("update <tableName> set a_is_active = :isActive where <userColoumnName> = :userId and a_function_id = :functionId")
	void updateSpecificFunctionNames(@Bind("functionId") long functionId, @Bind("userId") long userId, 
			@Bind("isActive") int isActive, @Define("userColoumnName") String userColoumnName, @Define("tableName")String tableName);
	
	/**
	 * This query checks whether user is having specific method permission to specific resource 
	 * @param resourceId
	 * @param userId
	 * @param tableName
	 * @param colName
	 * @param wantedColName
	 * @return
	 */
	@SqlQuery("select <userWantedColoumnName> from <tableName> where a_resource_id = :resourceId and <userColoumnName> = :userId")
	int isMethodPermissionExists(@Bind("resourceId") long resourceId, @Bind("userId") long userId, @Define("tableName")String tableName,@Define("userColoumnName") String colName, @Define("userWantedColoumnName") String wantedColName);
	
	/**
	 * This query returns all the resource names and its Ids related to user(admin or superAdmin)
	 * @param resourceName
	 * @return
	 */
	@SqlQuery("select id,a_resource_name from mk_api_resources WHERE a_resource_name != :resourceName and a_is_active = 1")
	@Mapper(ResourceNamesMapper.class)
	List<ResourceNames> getResourceNamesWithIds(@Bind("resourceName") String resourceName);
	
	/**
	 * This query returns all the resource names related to user(admin or superAdmin)
	 * @param resourceName
	 * @return
	 */
	@SqlQuery("select a_resource_name from mk_api_resources WHERE a_resource_name != :resourceName and a_is_active = 1")
	List<String> getResourceNames(@Bind("resourceName") String resourceName);
	
	/**
	 * This query returns all the resource ids related to user(admin or superAdmin)
	 * @param resourceName
	 * @return
	 */
	@SqlQuery("select id from mk_api_resources WHERE a_resource_name != :resourceName and a_is_active = 1")
	List<Integer> getResourceIds(@Bind("resourceName") String resourceName);
	
	/**
	 * This query returns the id of the resource
	 * @param resourceName
	 * @return
	 */
	@SqlQuery("select id from mk_api_resources WHERE a_resource_name = :resourceName")
	int getResourceId(@Bind("resourceName") String resourceName);

	/**
	 * This query returns non-zero value which means the particular user id is having access to this particular resource id
	 * @param userId
	 * @param resourceId
	 * @return
	 */
	@SqlQuery("select a_permission_id from <tableName> where a_resource_id = :resourceId and <coloumnName> = :userId")
	int hasAccessToThisResource(@Bind("userId") long userId, @Bind("resourceId") long resourceId, @Define("tableName") String tableName, @Define("coloumnName") String coloumnName);
	
	/**
	 * This query checks whether the user having permission to this resource have permission to this function or not
	 * @param userId
	 * @param resourceId
	 * @param methodType
	 * @param functionName
	 * @param coloumnName
	 * @return
	 */
	@SqlQuery("select a_is_active from <tableName> where <coloumnName> = :userId and a_function_id = :functionId")
	int hasAccessToThisFunction(@Bind("userId") long userId, @Bind("functionId") long functionId, @Define("coloumnName") String coloumnName, @Define("tableName") String tableName);
	
	/**
	 * This query updates the coloumn name a_changes_exist if there were any changes made to that user
	 * @param tableName
	 * @param userId
	 * @param changedStatus
	 */
	@SqlUpdate("update <tableName> set a_changes_exist = :changedStatus where id = :userId")
	void updateUserChanges(@Define("tableName") String tableName, @Bind("userId") long userId, @Bind("changedStatus") long changedStatus);

	/**
	 * This query returns the function id of the particular function related to its resourceId and methodType
	 * @param resourceId
	 * @param methodType
	 * @param functionName
	 * @return
	 */
	@SqlQuery("select id from mk_api_functions where a_resource_id = :resourceId and a_function_name = :functionName and a_type = :methodType and a_is_active = 1")
	int getFunctionId(@Bind("resourceId") long resourceId,@Bind("methodType") String methodType, @Bind("functionName")String functionName);

	/**
	 * This query returns the admin level of the admin user
	 * @param userName
	 * @return
	 */
	@SqlQuery("select a_admin_level from mk_api_user_admin where a_user_name = :userName")
	int isUserSuperAdmin(@Bind("userName") String userName);

	/**
	 * This query inserts new resource names into db
	 * @param resourceName
	 */
	@SqlUpdate("insert into mk_api_resources (a_resource_name) values (:resourceName)")
	void addNewResource(@Bind("resourceName") String resourceName);

	/**
	 * This query adds new functions to this table
	 * @param resourceId
	 * @param functionName
	 * @param type
	 */
	@SqlUpdate("insert into mk_api_functions (a_resource_id,a_function_name,a_type) values (:resourceId,:functionName,:type)")
	void insertNewFunction(@Bind("resourceId")long resourceId,@Bind("functionName") String functionName,@Bind("type") String type);

	/**
	 * This query returns the accessToken of the admin
	 * @param userName
	 * @return
	 */
	@SqlQuery("select a_access_token from mk_api_user_admin where a_user_name = :userName")
	String getAccessTokenOfUser(@Bind("userName") String userName);

	/**
	 * This query returns non zero if the user name matches
	 * @param userName
	 * @return
	 */
	@SqlQuery("select id from mk_api_user_admin where a_user_name = :userName")
	int isUserInterfaceUser(@Bind("userName") String userName);
	
	@SqlQuery("SELECT mk_api_functions.id,mk_api_functions.a_function_name from mk_api_functions INNER JOIN <tableName> "
			+ "ON mk_api_functions.id = <tableName>.a_function_id "
			+ "WHERE mk_api_functions.a_resource_id "
			+ "IN (select mk_api_resources.id FROM mk_api_resources WHERE mk_api_resources.a_resource_name = :resourceName) "
			+ "AND mk_api_functions.a_type = :methodType "
			+ "AND <tableName>.<colName> = :userId")
	@Mapper(FunctionNamesMapper.class)
	List<FunctionNames> getFunctionNames(@Bind("userId") long userId,@Define("tableName") String tableName,
			@Bind("resourceName") String resourceName,@Bind("methodType") String methodType,
			@Define("colName") String colName);

	/**
	 * This query retrieves all the function Ids with respect to their method type of a particular resource 
	 * @param resourceId This is the resource id
	 * @param type This is the type of the method
	 * @return List of function names
	 */
	@SqlQuery("select id from mk_api_functions where a_resource_id = :resourceId and a_type = :type and a_is_active = 1")
	List<Integer> getFunctionIds(@Bind("resourceId") long resourceId,@Bind("type") String type);

}
