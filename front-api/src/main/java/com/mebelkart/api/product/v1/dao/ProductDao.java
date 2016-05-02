package com.mebelkart.api.product.v1.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * Created by vinitpayal on 27/04/16.
 */
public interface ProductDao {

    @SqlQuery("select value from ps_configuration where name= :configVarName")
    public String getConfigVarValue(@Bind("configVarName") String configVarName);

    @SqlQuery("select name from ps_category_lang where id_category= :catId")
    public String getNameOfCategory(@Bind("catId") String catId);

}
