package com.mebelkart.api.order.v1.mapper;

import com.mebelkart.api.order.v1.core.*;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vinitpayal on 21/04/16.
 */
public class OrderMapper implements ResultSetMapper<Order> {
    @Override
    public Order map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {

        Customer customer=new Customer(resultSet.getInt("id_customer"),resultSet.getInt("id_gender"), resultSet.getInt("id_default_group"),resultSet.getString("firstname")
                , resultSet.getString("lastname"), resultSet.getString("email"),resultSet.getString("birthday"), resultSet.getBoolean("newsletter"), resultSet.getBoolean("is_guest"), resultSet.getBoolean("deleted")
                , resultSet.getString("date_add"), resultSet.getString("date_upd"));


        Country deliverCountry=new Country(resultSet.getInt("dc.id_country"),resultSet.getInt("dc.id_zone"),resultSet.getInt("dc.id_currency")
                                ,resultSet.getString("dc.iso_code"),resultSet.getInt("dc.call_prefix"),resultSet.getBoolean("dc.active")
                                ,resultSet.getBoolean("dc.contains_states"),resultSet.getBoolean("dc.need_identification_number"),resultSet.getBoolean("dc.need_zip_code")
                                ,resultSet.getString("dc.zip_code_format"),resultSet.getBoolean("dc.display_tax_label"));
        State deliveryState=new State(resultSet.getInt("ds.id_state"),resultSet.getInt("ds.id_country"),resultSet.getInt("ds.id_zone"),resultSet.getString("ds.name"),resultSet.getString("ds.iso_code")
                            ,resultSet.getInt("ds.tax_behavior"),resultSet.getBoolean("ds.active"));

        Country invoiceCountry=new Country(resultSet.getInt("ic.id_country"),resultSet.getInt("ic.id_zone"),resultSet.getInt("ic.id_currency")
                ,resultSet.getString("ic.iso_code"),resultSet.getInt("ic.call_prefix"),resultSet.getBoolean("ic.active")
                ,resultSet.getBoolean("ic.contains_states"),resultSet.getBoolean("ic.need_identification_number"),resultSet.getBoolean("ic.need_zip_code")
                ,resultSet.getString("ic.zip_code_format"),resultSet.getBoolean("ic.display_tax_label"));
        State invoiceState=new State(resultSet.getInt("ist.id_state"),resultSet.getInt("ist.id_country"),resultSet.getInt("ist.id_zone"),resultSet.getString("ist.name"),resultSet.getString("ist.iso_code")
                ,resultSet.getInt("ist.tax_behavior"),resultSet.getBoolean("ist.active"));


        Address deliveryAddress=new Address(resultSet.getInt("da.id_address"),resultSet.getInt("da.id_country"),resultSet.getInt("da.id_state")
                                ,resultSet.getInt("da.id_manufacturer"),resultSet.getInt("da.id_supplier"),resultSet.getString("da.alias")
                                ,resultSet.getString("da.company"),resultSet.getString("da.firstname"),resultSet.getString("da.lastname"),resultSet.getString("da.address1")
                                ,resultSet.getString("da.address2"),resultSet.getString("da.postcode"),resultSet.getString("da.city"),resultSet.getString("da.other"),resultSet.getString("da.phone")
                                ,resultSet.getString("da.phone_mobile"),resultSet.getString("da.vat_number"),resultSet.getString("da.dni"),resultSet.getBoolean("da.is_guest_address")
                                ,resultSet.getString("da.date_add"),resultSet.getString("da.date_upd"),resultSet.getBoolean("da.active"),deliverCountry,deliveryState);

        Address invoiceAddress=new Address(resultSet.getInt("ia.id_address"),resultSet.getInt("ia.id_country"),resultSet.getInt("ia.id_state")
                ,resultSet.getInt("ia.id_manufacturer"),resultSet.getInt("ia.id_supplier"),resultSet.getString("ia.alias")
                ,resultSet.getString("ia.company"),resultSet.getString("ia.firstname"),resultSet.getString("ia.lastname"),resultSet.getString("ia.address1")
                ,resultSet.getString("ia.address2"),resultSet.getString("ia.postcode"),resultSet.getString("ia.city"),resultSet.getString("ia.other"),resultSet.getString("ia.phone")
                ,resultSet.getString("ia.phone_mobile"),resultSet.getString("ia.vat_number"),resultSet.getString("ia.dni"),resultSet.getBoolean("ia.is_guest_address")
                ,resultSet.getString("ia.date_add"),resultSet.getString("ia.date_upd"),resultSet.getBoolean("ia.active"),invoiceCountry,invoiceState);


        /*
        Address(Integer idAddress, Integer idCountry, Integer idState, Integer idManufacturer
            , Integer idSupplier, String alias, String company, String firstName, String lastName
            , String address1, String address2, String postCode, String city, String other, String phone
            , String phoneMobile, String vatNumber, String dni, Boolean isGuestAddress, DateTime dateAdd
            , DateTime dateUpd, Boolean active, Country country, State state)
         */
        return new Order(resultSet.getInt("id_order"),resultSet.getInt("id_customer"),resultSet.getInt("id_cart"),
                resultSet.getInt("id_currency"),resultSet.getString("payment"),resultSet.getString("module"),resultSet.getInt("id_address_delivery"),resultSet.getInt("id_address_invoice"),customer
                ,deliveryAddress,invoiceAddress);
    }
}
