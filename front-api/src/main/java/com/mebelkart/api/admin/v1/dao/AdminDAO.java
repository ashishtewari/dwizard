package com.mebelkart.api.admin.v1.dao;

import java.util.List;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.mapper.AdminMapper;

@RegisterMapper(AdminMapper.class)
public interface AdminDAO {
	@SqlQuery("select id,a_user_name,a_password,a_admin_level from mk_api_user_admin where a_user_name = :user_name and a_password = :password and a_admin_level = :level")
	List<Admin> login(@Bind("user_name") String user_name,@Bind("password") String password,@Bind("level") long level);
}
