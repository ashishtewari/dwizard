/**
 * 
 */
package com.mebelkart.api.other.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.other.v1.core.CategoryWrapper;
import com.mebelkart.api.other.v1.core.DealsWrapper;
import com.mebelkart.api.other.v1.mapper.CategoryMapper;
import com.mebelkart.api.other.v1.mapper.DealsMapper;

/**
 * @author Tinku
 *
 */
@UseStringTemplate3StatementLocator
public interface OtherApiDao {

	/**
	 * @param customerId
	 * @return
	 */
//	@SqlQuery("select id_group from ps_customer_group where id_customer = :customerId")
//	List<String> getGroupsStatic(@Bind("customerId") int customerId);

	/**
	 * @param string
	 * @return
	 */
	@SqlQuery("select value from ps_configuration where name = :key")
	Integer getDataFromConfiguration(@Bind("key") String key);

	/**
	 * @return All category Ids corresponding to parent Id
	 */
	@SqlQuery("select c.id_category,cl.name from ps_category c join ps_category_lang cl on c.id_category = cl.id_category where c.id_parent = :id and c.active = 1")
	@Mapper(CategoryMapper.class)
	List<CategoryWrapper> getCategoryIds(@Bind("id") int flashSaleCatId);

	/**
	 * @param custId
	 * @return
	 */
	@SqlQuery("SELECT id_group FROM ps_customer_group WHERE id_customer = :cusId")
	List<String> getCurrentCustomerGroups(@Bind("cusId") int custId);

	/**
	 * @param currentTimeStamp
	 * @param langId
	 * @param sqlGroups
	 * @param orderBy
	 * @param orderWay
	 * @param startLimit
	 * @param nbProducts
	 * @param joinQuery
	 * @param catQuery
	 * @param cityQuery
	 */
	@SqlQuery("SELECT fsp.id_product, fs.flash_sale_date_end ,"
			+ " (IF((fs.flash_sale_date_start = '0000-00-00 00:00:00' OR '<now>' >= fs.flash_sale_date_start)"
			+ " AND"
			+ " (fs.flash_sale_date_end = '0000-00-00 00:00:00' OR '<now>' <lte> fs.flash_sale_date_end),1,0)) as fs_availability"
			+ " FROM ps_flash_sale_products fsp"
			+ " LEFT JOIN ps_flash_sale fs ON fsp.id_flash_sale = fs.id_flash_sale"
			+ " LEFT JOIN ps_product p ON fsp.id_product = p.id_product"
			+ " LEFT JOIN ps_product_lang pl ON (p.id_product = pl.id_product AND pl.id_lang = :langId)"
			+ " <joinQuery>"
			+ " LEFT JOIN ps_image i ON (i.id_product = p.id_product AND i.cover = 1)"
			+ " LEFT JOIN ps_image_lang il ON (i.id_image = il.id_image AND il.id_lang = :langId)"
			+ " LEFT JOIN ps_manufacturer m ON (m.id_manufacturer = p.id_manufacturer)"
			+ " LEFT JOIN ps_tax_rule tr ON (p.id_tax_rules_group = tr.id_tax_rules_group"
			+ " AND tr.id_country = 110"
			+ " AND tr.id_state = 0)"
			+ " LEFT JOIN ps_tax t ON (t.id_tax = tr.id_tax)"
			+ " WHERE fsp.active = 1"
			+ " AND fsp.id_product IN ("
			+ " SELECT cp.id_product FROM ps_category_group cg"
			+ " LEFT JOIN ps_category_product cp ON (cp.id_category = cg.id_category)"
			+ " WHERE cg.id_group <sqlGroups> <catQuery>)"
			+ " AND fs.flash_sale_active = 1 AND fsp.active = 1 AND ("
			+ " (fs.flash_sale_date_start = '0000-00-00 00:00:00' OR '<now>' >= fs.flash_sale_date_start)"
			+ " AND"
			+ " (fs.flash_sale_date_end = '0000-00-00 00:00:00' OR '<now>' <lte> fs.flash_sale_date_end))"
			+ " <cityQuery>"
			+ " GROUP BY fsp.id_product"
			+ " ORDER BY fs.flash_sale_active DESC ,fsp.active DESC, <orderBy> <orderWay> LIMIT :startLimit, :endLimit")
	@Mapper(DealsMapper.class)
	List<DealsWrapper> getFlashSaleProductsByCategorySQL(@Define("now") String currentTimeStamp,@Bind("langId") int langId,@Define("sqlGroups") String sqlGroups,
			@Define("orderBy") String orderBy,@Define("orderWay") String orderWay,@Bind("startLimit") int startLimit,
			@Bind("endLimit") int nbProducts,@Define("joinQuery") String joinQuery,@Define("catQuery") String catQuery,@Define("cityQuery") String cityQuery,@Define("lte") String lte);

	
//	/**
//	 * @param currentTimeStamp
//	 * @param langId
//	 * @param sqlGroups
//	 * @param orderBy
//	 * @param orderWay
//	 * @param startLimit
//	 * @param nbProducts
//	 * @param joinQuery
//	 * @param catQuery
//	 * @param cityQuery
//	 */
//	@SqlQuery("SELECT p.id_product,p.id_category_default,p.price,p.wholesale_price, pl.description, pl.description_short, pl.link_rewrite, pl.meta_description, pl.meta_keywords,"
//			+ " pl.meta_title, pl.name, m.name AS manufacturer_name, p.id_manufacturer as id_manufacturer, i.id_image, il.legend, t.rate, pl.meta_keywords,"
//			+ " pl.meta_title, pl.meta_description, fs.flash_sale_date_end , TIMESTAMPDIFF(HOUR, '<now>', fs.flash_sale_date_end) as time_left ,"
//			+ " (IF((fs.flash_sale_date_start = '0000-00-00 00:00:00' OR '<now>' >= fs.flash_sale_date_start)"
//			+ " AND"
//			+ " (fs.flash_sale_date_end = '0000-00-00 00:00:00' OR '<now>' <lte> fs.flash_sale_date_end),1,0)) as fs_availability, fsp.active as flash_sale_product_active"
//			+ " FROM ps_flash_sale_products fsp"
//			+ " LEFT JOIN ps_flash_sale fs ON fsp.id_flash_sale = fs.id_flash_sale"
//			+ " LEFT JOIN ps_product p ON fsp.id_product = p.id_product"
//			+ " LEFT JOIN ps_product_lang pl ON (p.id_product = pl.id_product AND pl.id_lang = :langId)"
//			+ " <joinQuery>"
//			+ " LEFT JOIN ps_image i ON (i.id_product = p.id_product AND i.cover = 1)"
//			+ " LEFT JOIN ps_image_lang il ON (i.id_image = il.id_image AND il.id_lang = :langId)"
//			+ " LEFT JOIN ps_manufacturer m ON (m.id_manufacturer = p.id_manufacturer)"
//			+ " LEFT JOIN ps_tax_rule tr ON (p.id_tax_rules_group = tr.id_tax_rules_group"
//			+ " AND tr.id_country = 110"
//			+ " AND tr.id_state = 0)"
//			+ " LEFT JOIN ps_tax t ON (t.id_tax = tr.id_tax)"
//			+ " WHERE p.active = 1"
//			+ " AND p.id_product IN ("
//			+ " SELECT cp.id_product FROM ps_category_group cg"
//			+ " LEFT JOIN ps_category_product cp ON (cp.id_category = cg.id_category)"
//			+ " WHERE cg.id_group <sqlGroups> <catQuery>)"
//			+ " AND fs.flash_sale_active = 1 AND fsp.active = 1 AND ("
//			+ " (fs.flash_sale_date_start = '0000-00-00 00:00:00' OR '<now>' >= fs.flash_sale_date_start)"
//			+ " AND"
//			+ " (fs.flash_sale_date_end = '0000-00-00 00:00:00' OR '<now>' <lte> fs.flash_sale_date_end))"
//			+ " <cityQuery>"
//			+ " GROUP BY p.id_product"
//			+ " ORDER BY fs.flash_sale_active DESC ,fsp.active DESC, <orderBy> <orderWay> LIMIT :startLimit, :endLimit")
//	List<String> getFlashSaleProductsByCategorySQL(@Define("now") String currentTimeStamp,@Bind("langId") int langId,@Define("sqlGroups") String sqlGroups,
//			@Define("orderBy") String orderBy,@Define("orderWay") String orderWay,@Bind("startLimit") int startLimit,
//			@Bind("endLimit") int nbProducts,@Define("joinQuery") String joinQuery,@Define("catQuery") String catQuery,@Define("cityQuery") String cityQuery,@Define("lte") String lte);

}
