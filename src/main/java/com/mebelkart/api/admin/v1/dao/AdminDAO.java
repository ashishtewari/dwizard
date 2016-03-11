package com.mebelkart.api.admin.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;

/**
 * @author Tinku
 *
 */
@RegisterMapper(AdminMapper.class)
public interface AdminDAO {
	/**
	 * @param user_name
	 * @param password
	 * @param level
	 * @return
	 */
	@SqlQuery("select id,a_user_name,a_password,a_admin_level from mk_api_user_admin where a_user_name = :user_name and a_password = :password and a_admin_level = :level")
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
	 * @param resourceId
	 * @param consumerId
	 * @param haveGetPermission
	 * @param havePostPermission
	 * @param havePutPermission
	 * @param haveDeletePermission
	 */
	@SqlUpdate("insert into mk_api_resources_consumer_permission (a_resource_id,a_consumer_id,a_have_get_permission,a_have_post_permission,a_have_put_permission,a_have_delete_permission) values (:resourceId, :consumerId, :haveGetPermission, :havePostPermission, :havePutPermission, :haveDeletePermission)")
	void addPermission(@Bind("resourceId") long resourceId,
			@Bind("consumerId") long consumerId,
			@Bind("haveGetPermission") long haveGetPermission,
			@Bind("havePostPermission") long havePostPermission,
			@Bind("havePutPermission") long havePutPermission,
			@Bind("haveDeletePermission") long haveDeletePermission);
	
	/**
	 * @param accessToken
	 * @return
	 */
	@SqlQuery("select id from mk_api_consumer where a_access_token = :accessToken")
	int getConsumerId(@Bind("accessToken") String accessToken);

}
