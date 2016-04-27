package com.mebelkart.api.mobileapi.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

/**
 * Created by vinitpayal on 27/04/16.
 */
public interface MobileDao {

    @SqlQuery("select value from ps_configuration where name= :configVarName")
    public String getConfigVarValue(@Bind("configVarName") String configVarName);


}
