package com.mebelkart.api.admin.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;
import com.mebelkart.api.admin.v1.mapper.UserStatusMapper;

/**
 * @author Tinku
 *
 */
public interface AdminDAO {
	/**
	 * This query uses user details for login
	 * @param user_name of admin
	 * @param password of admin
	 * @param level of admin
	 * @return row data of type list
	 */
	@SqlQuery("select a_user_name,a_password,a_admin_level from mk_api_user_admin where a_user_name = :user_name and a_password = :password and a_admin_level = :level")
	@Mapper(AdminMapper.class)
	List<Admin> login(@Bind("user_name") String user_name,
			@Bind("password") String password, @Bind("level") long level);
	
	/**
	 * This query returns userNames and isActive status of each and every Admin based on type of status
	 * @param status
	 * @return
	 */
	@SqlQuery("select a_user_name,a_is_active from mk_api_user_admin where a_is_active = :status")
	@Mapper(UserStatusMapper.class)
	List<UserStatus> getAdminsStatus(@Bind("status") long status);
	
	/**
	 * This query returns userNames and isActive status of each and every Consumer based on type of status
	 * @param status
	 * @return
	 */
	@SqlQuery("select a_user_name,a_is_active from mk_api_consumer where a_is_active = :status")
	@Mapper(UserStatusMapper.class)
	List<UserStatus> getConsumersStatus(@Bind("status") long status);

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
	@SqlUpdate("insert into mk_api_user_admin (a_user_name,a_password,a_access_token) values (:userName, :password, :accessToken)")
	@GetGeneratedKeys
	int addAdmin(@Bind("userName") String userName,
			@Bind("password") String password,
			@Bind("accessToken") String accessToken);

	/**
	 * This query adds permissions to consumer who is pre-registered
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("insert into mk_api_resources_consumer_permission (a_resource_id,a_consumer_id,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :consumerId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void insertConsumerPermission(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);
	
	/**
	 * This query adds permissions to admin who is pre-registered
	 * @param resourceId consists of api package id
	 * @param adminId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("insert into mk_api_resources_admin_permission (a_resource_id,a_admin_id,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :adminId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void insertAdminPermission(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);

	/**
	 * This query updates permissions to consumer who is pre-registered
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("update mk_api_resources_consumer_permission set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission where a_resource_id = :resourceId and a_consumer_id = :consumerId")
	void updateConsumerPermission(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);
	
	/**
	 * This query adds permissions to admin who is pre-registered
	 * @param resourceId consists of api package id
	 * @param adminId consists of user id
	 * @param haveGetPermission consists 1/0
	 * @param havePostPermission consists 1/0
	 * @param havePutPermission consists 1/0
	 * @param haveDeletePermission consists 1/0
	 */
	@SqlUpdate("update mk_api_resources_admin_permission set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission where a_resource_id = :resourceId and a_admin_id = :adminId")
	void updateAdminPermission(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);

	/**
	 * This query returns consumers row id
	 * @param userName of type email
	 * @return id
	 */
	@SqlQuery("select id from mk_api_consumer where a_user_name = :userName")
	int getConsumerId(@Bind("userName") String userName);
	
	/**
	 * This query returns admins row id
	 * @param userName of type email
	 * @return id
	 */
	@SqlQuery("select id from mk_api_user_admin where a_user_name = :userName")
	int getAdminId(@Bind("userName") String userName);

	/**
	 * This query checks whether consumer is already registered or not
	 * @param userName of type email
	 * @return String userName of type email
	 */
	@SqlQuery("select a_user_name from mk_api_consumer where a_user_name = :userName")
	String isConsumerUserNameAlreadyExists(@Bind("userName") String userName);

	/**
	 * This query checks whether admin is already registered or not
	 * @param userName of type email
	 * @return String userName of type email
	 */
	@SqlQuery("select a_user_name from mk_api_user_admin where a_user_name = :userName")
	String isAdminUserNameAlreadyExists(@Bind("userName") String userName);
	
	/**
	 * This query updates active status of Admin
	 * @param userName email of Admin
	 * @param status 1 for isActive/0 for isNotActive
	 */
	@SqlUpdate("update mk_api_user_admin set a_is_active = :status where a_user_name = :userName")
	void changeAdminActiveStatus(@Bind("userName") String userName,@Bind("status") long status);
	
	/**
	 * This query updates active status of Consumer
	 * @param userName email of Consumer
	 * @param status 1 for isActive/0 for isNotActive
	 */
	@SqlUpdate("update mk_api_consumer set a_is_active = :status where a_user_name = :userName")
	void changeConsumerActiveStatus(@Bind("userName") String userName,@Bind("status") long status);

	/**
	 * Checks whether the consumer is given permissions for specific resource id or not
	 * @param resourceId consists of api package id
	 * @param consumerId consists of user id
	 * @return non zero on success
	 */
	@SqlQuery("select a_permission_id from mk_api_resources_consumer_permission where a_resource_id = :resourceId and a_consumer_id = :consumerId")
	int isConsumerPermissionExists(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId);
	
	/**
	 * Checks whether the admin is given permissions for specific resource id or not
	 * @param resourceId consists of api package id
	 * @param adminId consists of user id
	 * @return non zero on success
	 */
	@SqlQuery("select a_permission_id from mk_api_resources_admin_permission where a_resource_id = :resourceId and a_admin_id = :adminId")
	int isAdminPermissionExists(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId);
	
	/**
	 * Checks whether resource id is valid api package id or not
	 * @param resourceId consists of api package id
	 * @return non zero on success
	 */
	@SqlQuery("select id from mk_api_resources where id = :resourceId")
	int isValidResource(@Bind("resourceId") long resourceId);
}
