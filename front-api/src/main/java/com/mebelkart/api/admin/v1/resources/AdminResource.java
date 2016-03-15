package com.mebelkart.api.admin.v1.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.api.LoginReply;
import com.mebelkart.api.admin.v1.api.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;

@Path("v1.0/admin")
@Produces({ MediaType.APPLICATION_JSON })
public class AdminResource {

	AdminDAO auth;

	public AdminResource(AdminDAO auth) {
		this.auth = auth;
	}

	@GET
	@Path("/login")
	public Object getLoginDetails(@HeaderParam("apikey") String key) {
		JSONParser parser = new JSONParser();
		List<Admin> admin_details = null;
		JSONObject obj = null;
		String username = "";
		String password = "";
		long level;
		try {
			obj = (JSONObject) parser.parse(key);
			username = (String) obj.get("a_user_name");		
			password = MD5Encoding.encrypt((String) obj.get("a_password"));
			level = (Long) obj.get("a_admin_level");
			admin_details = this.auth.login(username, password, level);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		if (admin_details.isEmpty()) {
			return new Reply(401, "Login Fail Invalid Credentials",
					new LoginReply(0));
		} else {
			return new Reply(200, "Test Success", new LoginReply(1));
		}
	}
}
