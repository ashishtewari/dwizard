/**
 * 
 */
package com.mebelkart.api.category.v1.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.github.rkmk.container.FoldingList;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.category.v1.core.CategoryWrapper;

/**
 * @author Nikhil
 *
 */
public interface CategoryDao {

	/**
	 * @return
	 */
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT id_category FROM ps_category WHERE level_depth=:levelDepth")
	FoldingList<CategoryWrapper> getCategoryId(@Bind("levelDepth")int levelDepth);

}
