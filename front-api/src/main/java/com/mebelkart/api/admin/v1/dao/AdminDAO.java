package com.mebelkart.api.admin.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;

/**
 * @author Tinku
 *
 */
public interface AdminDAO {
	/**
	 * @param user_name
	 * @param password
	 * @param level
	 * @return
	 */
	@SqlQuery("select id,a_user_name,a_password,a_admin_level from mk_api_user_admin where a_user_name = :user_name and a_password = :password and a_admin_level = :level")
	@Mapper(AdminMapper.class)
	List<Admin> login(@Bind("user_name") String user_name,
			@Bind("password") String password, @Bind("level") long level);

	/**
	 * @param accessToken
	 * @return
	 */
	@SqlQuery("select a_admin_level from mk_api_user_admin where a_access_token = :accessToken")
	int validate(@Bind("accessToken") String accessToken);

	/**
	 * @param userName
	 * @param accessToken
	 * @param countAssigned
	 * @param isActive
	 */
	@SqlUpdate("insert into mk_api_consumer (a_user_name,a_access_token,a_count_assigned) values (:userName, :accessToken, :countAssigned)")
	@GetGeneratedKeys
	int addConsumer(@Bind("userName") String userName,
			@Bind("accessToken") String accessToken,
			@Bind("countAssigned") long countAssigned);

	/**
	 * @param userName
	 * @param password
	 * @param accessToken
	 * @return
	 */
	@SqlUpdate("insert into mk_api_user_admin (a_user_name,a_password,a_access_token) values (:userName, :password, :accessToken)")
	@GetGeneratedKeys
	int addAdmin(@Bind("userName") String userName,
			@Bind("password") String password,
			@Bind("accessToken") String accessToken);

	/**
	 * @param resourceId
	 * @param consumerId
	 * @param haveGetPermission
	 * @param havePostPermission
	 * @param havePutPermission
	 * @param haveDeletePermission
	 */
	@SqlUpdate("insert into mk_api_resources_consumer_permission (a_resource_id,a_consumer_id,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :consumerId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void insertConsumerPermission(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);
	
	/**
	 * @param resourceId
	 * @param adminId
	 * @param haveGetPermission
	 * @param havePostPermission
	 * @param havePutPermission
	 * @param haveDeletePermission
	 */
	@SqlUpdate("insert into mk_api_resources_admin_permission (a_resource_id,a_admin_id,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :adminId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void insertAdminPermission(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);

	/**
	 * @param resourceId
	 * @param consumerId
	 * @param haveGetPermission
	 * @param havePostPermission
	 * @param havePutPermission
	 * @param haveDeletePermission
	 */
	@SqlUpdate("update mk_api_resources_consumer_permission set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission where a_resource_id = :resourceId and a_consumer_id = :consumerId")
	void updateConsumerPermission(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);
	
	@SqlUpdate("update mk_api_resources_admin_permission set a_have_get_permission = :haveGetPermission,a_have_post_permission = :havePostPermission,a_have_put_permission = :havePutPermission,a_have_delete_permission = :haveDeletePermission where a_resource_id = :resourceId and a_admin_id = :adminId")
	void updateAdminPermission(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);

	/**
	 * @param userName
	 * @return id
	 */
	@SqlQuery("select id from mk_api_consumer where a_user_name = :userName")
	int getConsumerId(@Bind("userName") String userName);
	
	/**
	 * @param userName
	 * @return
	 */
	@SqlQuery("select id from mk_api_user_admin where a_user_name = :userName")
	int getAdminId(@Bind("userName") String userName);

	/**
	 * @param userName
	 * @return
	 */
	@SqlQuery("select a_user_name from mk_api_consumer where a_user_name = :userName")
	String isConsumerUserNameAlreadyExists(@Bind("userName") String userName);

	/**
	 * @param userName
	 * @return
	 */
	@SqlQuery("select a_user_name from mk_api_user_admin where a_user_name = :userName")
	String isAdminUserNameAlreadyExists(@Bind("userName") String userName);

	/**
	 * @param resourceId
	 * @param consumerId
	 * @return
	 */
	@SqlQuery("select a_permission_id from mk_api_resources_consumer_permission where a_resource_id = :resourceId and a_consumer_id = :consumerId")
	int isConsumerPermissionExists(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId);
	
	/**
	 * @param resourceId
	 * @param adminId
	 * @return
	 */
	@SqlQuery("select a_permission_id from mk_api_resources_admin_permission where a_resource_id = :resourceId and a_admin_id = :adminId")
	int isAdminPermissionExists(@Bind("resourceId") long resourceId,
			@Bind("adminId") long adminId);
	
	/**
	 * @param resourceId
	 * @return
	 */
	@SqlQuery("select id from mk_api_resources where id = :resourceId")
	int isValidResource(@Bind("resourceId") long resourceId);
}
