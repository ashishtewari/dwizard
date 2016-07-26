/**
 * 
 */
package com.mebelkart.api.category.v1.dao;


import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * @author Nikhil
 *
 */
public interface CategoryDao {

	/**
	 * @return
	 */
	@SqlQuery("SELECT id_category FROM ps_category WHERE id_parent=:parentId and active=1")
	List<Integer> getCategoryId(@Bind("parentId")int parentId);

}
