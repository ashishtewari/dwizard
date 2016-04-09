/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import com.mebelkart.api.manufacturer.v1.core.ManufacturerDetailsMapper;
import com.mebelkart.api.manufacturer.v1.core.ManufacturerDetailsWrapper;

/**
 * @author Nikky-Akky
 *
 */
public interface ManufacturerDetailsDAO {

	/**
	 * @param manufacturerId
	 * @return
	 */
	//@Mapper(ManufacturerDetailsMapper.class)
	@SqlQuery("select ps_manufacturer.email from ps_manufacturer where ps_manufacturer.id_manufacturer=:manufacturerId")
	List<ManufacturerDetailsWrapper> getManufacturerId(@Bind("manufacturerId")long manufacturerId);

	/**
	 * @param manufacturerId
	 * @return
	 */
	@Mapper(ManufacturerDetailsMapper.class)
	@SqlQuery("select ps_manufacturer.id_manufacturer,ps_product.id_product from ps_manufacturer "
			+ "join ps_product on ps_manufacturer.id_manufacturer=ps_product.id_manufacturer "
			+ "where ps_manufacturer.id_manufacturer = :manufacturerId")
	List<ManufacturerDetailsWrapper> getManufacturerDetails(@Bind("manufacturerId")long manufacturerId);
	
	
	@SqlQuery("")
	List<ManufacturerDetailsWrapper> getAllManufacturerDetails();
	

}
