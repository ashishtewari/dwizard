package com.mebelkart.api.admin.v1.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.api.LoginReply;
import com.mebelkart.api.admin.v1.api.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;

/**
 * @author Tinku
 *
 */
@Path("v1.0/admin")
public class AdminResource {

	/**
	 * auth
	 */
	AdminDAO auth;

	/**
	 * @param auth
	 */
	public AdminResource(AdminDAO auth) {
		this.auth = auth;
	}

	/**
	 * @return
	 */
	@GET
	@Path("")
	public Object noQueryParamsGet() {
		return new Reply(100, "No Path Specified", new LoginReply(0));
	}

	@POST
	@Path("")
	public Object noQueryParamsPost() {
		return new Reply(100, "No Path Specified", new LoginReply(0));
	}

	/**
	 * @param key
	 * @return
	 */
	@GET
	@Path("/login")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getLoginDetails(@HeaderParam("parameters") String key) {
		JSONObject obj = jsonParser(key);
		String username = (String) obj.get("a_user_name");
		String password = MD5Encoding.encrypt((String) obj.get("a_password"));
		long level = (long) obj.get("a_admin_level");
		List<Admin> admin_details = this.auth.login(username, password, level);
		if (admin_details.isEmpty()) {
			return new Reply(401, "Login Fail Invalid Credentials",
					new LoginReply(0));
		} else {
			return new Reply(200, "Test Success", new LoginReply(1));
		}
	}

	/**
	 * @param key
	 * @return
	 */
	@POST
	@Path("/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addUsers(@HeaderParam("accessToken") String key,
			@Context HttpServletRequest request) {
		int accessLevel = this.auth.validate(key);
		JSONObject obj = contextRequest(request);
		System.out.println((String) obj.get("type"));
		if (((String) obj.get("type")).equals("admin") && accessLevel == 1) {

		} else if (((String) obj.get("type")).equals("consumer")
				&& (accessLevel == 1 || accessLevel == 2)) {
			System.out.println("Entered into Consumer");
			int id = addConsumer(obj);
			if (id != 0) {
				int status = addPermission(obj, id);
				return checkStatus(status);
			} else {
				return new Reply(100, "Specify Correct Type Of User Details",
						new LoginReply(0));
			}
			// this.auth.addConsumer(username, password, level);
		} else {
			return new Reply(100, "Specify Correct Type/Key", new LoginReply(0));
		}
		return key;
	}

	@POST
	@Path("/addPermissions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addPermissions(@HeaderParam("accessToken") String key,
			@Context HttpServletRequest request) {
		int accessLevel = this.auth.validate(key);
		JSONObject obj = contextRequest(request);
		if (((String) obj.get("type")).equals("admin") && accessLevel == 1) {

		} else if (((String) obj.get("type")).equals("consumer")
				&& (accessLevel == 1 || accessLevel == 2)) {
			int id = this.auth.getConsumerId((String) obj.get("accessToken"));
			System.out.println(id);
			int status = addPermission(obj, id);
			return checkStatus(status);
			// this.auth.login(username, password, level);
		} else {
			return new Reply(100, "Specify Correct Type", new LoginReply(0));
		}
		return key;
	}

	/**
	 * @param key
	 * @return
	 */
	public JSONObject jsonParser(String key) {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(key);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * @param request
	 * @return
	 */
	public JSONObject contextRequest(HttpServletRequest request) {
		InputStream inputStream = null;
		String theString = null;
		try {
			inputStream = request.getInputStream();
			theString = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonParser(theString);
	}

	public Object checkStatus(int status) {
		if (status == 0) {
			return new Reply(100, "Specify Correct Type Of User Permissions",
					new LoginReply(0));
		} else if (status == 1) {
			return new Reply(401, "Given Permissions Successfully",
					new LoginReply(1));
		} else if (status == 2) {
			return new Reply(100, "Specify Correct Fields in User Permissions",
					new LoginReply(0));
		} else if (status == 3) {
			return new Reply(100,
					"Specify Correct Type Of User resourceId and Permission",
					new LoginReply(0));
		} else if (status == 4) {
			return new Reply(100, "Specify Correct Fields in User Permission",
					new LoginReply(0));
		} else if (status == 5) {
			return new Reply(100, "Specify Correct Type Of User Request",
					new LoginReply(0));
		} else
			return new Reply(100, "Unknown Error in Check Status",
					new LoginReply(0));
	}

	/**
	 * @param obj
	 * @return
	 */
	public int addConsumer(JSONObject obj) {
		System.out.println("Entered into addConsumer");
		if (obj.containsKey("userName") && obj.containsKey("accessToken")
				&& obj.containsKey("countAssigned")) {
			System.out.println("Entered into addConsumer If Conditon");
			int id = this.auth.addConsumer((String) obj.get("userName"),
					(String) obj.get("accessToken"),
					(long) obj.get("countAssigned"));
			System.out
					.println("Entered into addConsumer If Conditon and Executed Query");
			return id;
		} else
			return 0;
	}

	/**
	 * @param obj
	 * @param consumerId
	 * @return
	 */
	@SuppressWarnings("unused")
	public int addPermission(JSONObject obj, int consumerId) {
		if (obj.containsKey("permissions")) {
			JSONArray resources = (JSONArray) obj.get("permissions");
			if (resources.size() == 0) {
				return 2;
			} else {
				System.out.println("permissions array size "+resources.size());
				for (int i = 0; i < resources.size(); i++) {
					System.out.println("In "+(i+1)+" Iteration");
					JSONObject permissions = (JSONObject) resources.get(i);
					if (permissions.containsKey("resourceId")
							&& permissions.containsKey("permission")) {
						long resourceId = (long) permissions.get("resourceId");
						JSONArray requestArray = (JSONArray) permissions
								.get("permission");
						String[] permission = new String[requestArray.size()];
						for (int k = 0; k < permission.length; k++)
							permission[k] = (String) requestArray.get(k);
						System.out.println("ResourceID is "+resourceId);
						if (permission.length == 0) {
							return 4;
						} else {
							for (int j = 0; j < permission.length; j++) {
								if (permission[j].toUpperCase().equals("GET")) {
									this.auth.addPermission(resourceId,
											(long) consumerId, 1, 0, 0, 0);
								} else if (permission[j].toUpperCase().equals(
										"POST")) {
									this.auth.addPermission(resourceId,
											(long) consumerId, 0, 1, 0, 0);
								} else if (permission[j].toUpperCase().equals(
										"PUT")) {
									this.auth.addPermission(resourceId,
											(long) consumerId, 0, 0, 1, 0);
								} else if (permission[j].toUpperCase().equals(
										"DELETE")) {
									this.auth.addPermission(resourceId,
											(long) consumerId, 0, 0, 0, 1);
								} else {
									return 5;
								}
							}
						}
					} else {
						return 3;
					}
				}
				return 1;
			}
		} else
			return 0;
	}
}
