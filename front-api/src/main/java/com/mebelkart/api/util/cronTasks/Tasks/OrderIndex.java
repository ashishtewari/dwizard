package com.mebelkart.api.util.cronTasks.Tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mebelkart.api.util.cronTasks.classes.*;
import com.mebelkart.api.util.cronTasks.dao.OrderDao;
import com.mebelkart.api.util.factories.ElasticFactory;
import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinitpayal on 29/03/16.
 */
@OnApplicationStart
public class OrderIndex extends Job{

    Client elasticInstance= ElasticFactory.getElasticClient();

    @Override
    public void doJob() {
        /**
         *  Getting elastic client to index in elastic
         */
        try {

            OrderDao orderDaoObject=new OrderDao();

            /**
             * getting all new orders from mysql which are updated or created after last indexing
             */
            ResultSet ordersResultSet = orderDaoObject.getNewOrders();

            ObjectMapper pojoToJsonMapper = new ObjectMapper();

            /**
             * Iterating every order detail and index order detail in elastic
             */
            while (ordersResultSet.next()) {
                List<OrderDetail> subOrderDetails=new ArrayList<OrderDetail>();

                Order order=new Order();
                Integer orderId=ordersResultSet.getInt("id_order");

                /**
                 * setting order details in order object
                 */
                order.setOrderId(orderId);
                order.setCustomerId(ordersResultSet.getInt("id_customer"));
                order.setModule(ordersResultSet.getString("module"));
                order.setModule(ordersResultSet.getString("payment"));
                order.setOrderDate(ordersResultSet.getString("date_add"));

                /**
                 * converting order class to json object which can be indexed in elastic
                 */

                ResultSet orderDetails=orderDaoObject.getOrderDetails(orderId);//orderDetail resultset will contain all suborders of an order and status of all suborders
                while(orderDetails.next()){

                    /**
                     * Intializing all variables required to create an object of orderDetails class
                     *
                     */
                     Integer subOrderId=orderDetails.getInt("id_order_detail");
                     Integer productId=orderDetails.getInt("product_id");
                     Integer productAttributeId=orderDetails.getInt("product_attribute_id");
                     String productName=orderDetails.getString("product_name");
                     Integer productQuantity=orderDetails.getInt("product_quantity");
                     Integer productQuantityInStock=orderDetails.getInt("product_quantity_in_stock");
                     Integer productQuantityRefunded=orderDetails.getInt("product_quantity_refunded");
                     Integer productQuantityReinjected=orderDetails.getInt("product_quantity_reinjected");
                     Float orderDetailWholeSalePrice=orderDetails.getFloat("order_detail_wholesale_price");
                     Float productPrice=orderDetails.getFloat("product_price");
                     Float reductionPercentage=orderDetails.getFloat("reduction_percent");
                     Float reductionAmount=orderDetails.getFloat("reduction_amount");
                     Float groupReduction=orderDetails.getFloat("group_reduction");
                     Float productQuantityDiscount=orderDetails.getFloat("product_quantity_discount");
                     Float orderDetailProductAdvanceAmount=orderDetails.getFloat("order_detail_product_advance_amount");
                     Integer shippedFromDate=orderDetails.getInt("shipped_from_date");
                     Integer shippedToDate=orderDetails.getInt("shipped_to_date");
                     Integer deliveredFromDate=orderDetails.getInt("delivered_from_date");
                     Integer deliveredToDate=orderDetails.getInt("delivered_to_date");
                     String productEan13=orderDetails.getString("product_ean13")+"LOL ";
                     String productUpc=orderDetails.getString("product_upc");
                     String productRefrence=orderDetails.getString("product_reference");
                     String productSupplierRefrence=orderDetails.getString("product_supplier_reference");
                     Float productWeight=orderDetails.getFloat("product_weight");
                     String taxName=orderDetails.getString("tax_name");
                     Float taxRate=orderDetails.getFloat("tax_rate");
                     Float ecoTax=orderDetails.getFloat("ecotax");
                     Float ecotaxRate=orderDetails.getFloat("ecotax_tax_rate");
                     Boolean discountQuantityApplied=orderDetails.getBoolean("discount_quantity_applied");
                     String downloadHash=orderDetails.getString("download_hash");
                     Integer downloadNb=orderDetails.getInt("download_nb");
                     Date downloadDeadline=null;//orderDetails.getDate("download_deadline");

                    /**
                     * Initializing all variables for creating orderDetailVendorStatus object
                     */
                     Integer idOrderDetailVendorStatus=orderDetails.getInt("id_order_detail_vendor_status");
                     String allStatusInfo=orderDetails.getString("all_status_info");
                     String expectedShipmentDate=orderDetails.getString("expected_shipment_date");
                     Integer idCurrentStatus=orderDetails.getInt("id_current_status");
                     Date lastStatusUpdatedOn=null;//orderDetails.getDate("last_status_updated_on");
                     String courierUrlOrName=orderDetails.getString("courierUrlOrName");
                     boolean isOtherCarrier=orderDetails.getBoolean("is_other_carrier");
                     String courierTrackingDetail=orderDetails.getString("courierTrackingDetail");
                     String pickupRequestInfo=orderDetails.getString("pickup_request_info");
                     boolean isPickupRequested=orderDetails.getBoolean("is_pickup_requested");
                     boolean isPickupDone=orderDetails.getBoolean("is_pickup_done");
                     boolean isDelaySmsSent=orderDetails.getBoolean("is_delay_sms_sent");

                    /**
                     * Creating object of orderDetailVendor class
                     */
                    OrderDetailVendorStatus orderDetailVendorStatus=new OrderDetailVendorStatus( idOrderDetailVendorStatus,  allStatusInfo,  expectedShipmentDate
                            ,  idCurrentStatus,  lastStatusUpdatedOn,  courierUrlOrName,  isOtherCarrier
                    , courierTrackingDetail,  pickupRequestInfo,  isPickupRequested,  isPickupDone, isDelaySmsSent);

                    /**
                     * will contain logistic details for current suborderId
                     */
                    ResultSet logisticDetailsResultSet=orderDaoObject.getLogisticDetailsForASuborder(subOrderId);
                    //System.out.println("logistic resultset "+logisticDetailsResultSet.getMetaData());
                    System.out.println("--------------------------------------------");
                    System.out.println("Order Id :"+orderId+" suborderId :"+subOrderId);
                    OrderDetailLogisticService orderDetailLogisticService=null;

                        /**
                         * Creating object of orderDetaiLogisticService class
                         */
                        while (logisticDetailsResultSet.next()) {
                            System.out.println("In logistic details loop for suborder id : "+subOrderId);
                            Integer idOrderDetailLogisticService = logisticDetailsResultSet.getInt("id_order_detail_logistic_service");
                            Integer idOrderDetail = logisticDetailsResultSet.getInt("id_order_detail");
                            String idOrder = logisticDetailsResultSet.getString("id_order");
                            Integer logisticService = logisticDetailsResultSet.getInt("logistic_service");
                            Integer logisticServiceMode = logisticDetailsResultSet.getInt("logistic_service_mode");
                            Integer noOfPackages = logisticDetailsResultSet.getInt("no_of_packages");
                            String shipmentWeight = logisticDetailsResultSet.getString("shipment_weight");
                            String shipmentWeightUnit = logisticDetailsResultSet.getString("shipment_weight_unit");
                            String dimensions = logisticDetailsResultSet.getString("dimensions");
                            String dimensionUnit = logisticDetailsResultSet.getString("dimension_unit");
                            String remarks = logisticDetailsResultSet.getString("remarks");
                            String currentPickupDate = logisticDetailsResultSet.getString("current_pickup_date");
                            String addressCloseTime = logisticDetailsResultSet.getString("address_close_time");
                            String invoiceValue = logisticDetailsResultSet.getString("invoice_value");
                            boolean carrierRiskInfo = logisticDetailsResultSet.getBoolean("carrier_risk_info");
                            boolean active = logisticDetailsResultSet.getBoolean("active");
                            Integer useAmb = logisticDetailsResultSet.getInt("use_amb");
                            boolean migratedToGetit = logisticDetailsResultSet.getBoolean("migrated_to_getit");

                            orderDetailLogisticService = new OrderDetailLogisticService(idOrderDetailLogisticService, idOrderDetail, idOrder
                                    , logisticService, logisticServiceMode, noOfPackages, shipmentWeight
                                    , shipmentWeightUnit, dimensions, dimensionUnit, remarks, currentPickupDate
                                    , addressCloseTime, invoiceValue, carrierRiskInfo, active, useAmb
                                    , migratedToGetit);
                        }

                    /**
                     * populating order shipment details after fetching from orderDao class
                     */
                    OrderDetailShipments orderDetailShipments=null;
                    ResultSet subOrderShipmentDetailResultSet=orderDaoObject.getSuborderShipmentDetails(subOrderId);

                    /**
                     * checking if resultset is not empty
                     */
                    if(subOrderShipmentDetailResultSet.first()) {
                        Integer shipmentId = subOrderShipmentDetailResultSet.getInt("id_shipment");
                        Integer orderDetailId = subOrderShipmentDetailResultSet.getInt("id_order_detail");
                        String shipmentInformation = subOrderShipmentDetailResultSet.getString("shipment_information");
                        Integer logisticServiceId = subOrderShipmentDetailResultSet.getInt("id_logistic_service");
                        Integer logisticServiceModeId = subOrderShipmentDetailResultSet.getInt("id_logistic_service_mode");
                        String carrierName = subOrderShipmentDetailResultSet.getString("carrier_name");
                        String carrierTrackingId = subOrderShipmentDetailResultSet.getString("carrier_tracking_id");
                        boolean isOtherLogisticService = subOrderShipmentDetailResultSet.getBoolean("is_other_logistic_service");
                        boolean isDefault = subOrderShipmentDetailResultSet.getBoolean("is_default");
                        boolean active = subOrderShipmentDetailResultSet.getBoolean("active");

                        /**
                         * creating object of order shipment and populating values in object
                         */
                        orderDetailShipments = new OrderDetailShipments(shipmentId, orderDetailId,
                                shipmentInformation, logisticServiceId,
                                logisticServiceModeId, carrierName,
                                carrierTrackingId, isOtherLogisticService,
                                isDefault, active);
                    }

                    OrderDetailAssignToOtherVendor orderDetailAssignToOtherVendor=null;

                    ResultSet orderDetailAssingedToOtherVendorResultSet=orderDaoObject.getIfSuborderIsAssignedToOtherVendor(subOrderId);
                    /**
                     * Checking if resultset is containing any value if containing then create an object otherwise null will be indexed
                     */
                    if(orderDetailAssingedToOtherVendorResultSet.first()){
                        Integer orderDetailId=orderDetailAssingedToOtherVendorResultSet.getInt("id_order_detail");
                        Integer fromVendorId=orderDetailAssingedToOtherVendorResultSet.getInt("id_from_vendor");
                        Integer toVendorId=orderDetailAssingedToOtherVendorResultSet.getInt("id_to_vendor");

                         orderDetailAssignToOtherVendor=new OrderDetailAssignToOtherVendor(orderDetailId,fromVendorId,toVendorId);
                    }

                    OrderDetailPaymentInfo orderDetailPaymentInfo=null;

                    ResultSet orderDetailPaymentInfoResultSet=orderDaoObject.getPaymentInfoForSubOrder(subOrderId);
                    if(orderDetailPaymentInfoResultSet.first()){
                         Integer transactionId=orderDetailPaymentInfoResultSet.getInt("id_transaction");
                         Integer orderDetailId=orderDetailPaymentInfoResultSet.getInt("id_order_detail");
                         Integer manufacturerId=orderDetailPaymentInfoResultSet.getInt("id_manufacturer");
                         float productPriceToCustomer=orderDetailPaymentInfoResultSet.getFloat("product_price_to_customer");
                         float productPriceToSeller=orderDetailPaymentInfoResultSet.getFloat("product_price_from_seller");
                         float amountCollectedBySeller=orderDetailPaymentInfoResultSet.getFloat("amount_collected_by_seller");
                         float amountCollectedByMebelkart=orderDetailPaymentInfoResultSet.getFloat("amount_collected_by_mebelkart");
                         float advancePaidTOSeller=orderDetailPaymentInfoResultSet.getFloat("advance_paid_to_seller");
                         String transactionDate=orderDetailPaymentInfoResultSet.getString("transaction_date");
                         String transactionMode=orderDetailPaymentInfoResultSet.getString("transaction_mode");
                         String transactionBankReference=orderDetailPaymentInfoResultSet.getString("transaction_bank_reference");
                         float vendorSuggestedTransferPrice=orderDetailPaymentInfoResultSet.getFloat("vendor_suggested_transfer_price");
                         float otherCharges=orderDetailPaymentInfoResultSet.getFloat("other_charges");
                         String transactionComments=orderDetailPaymentInfoResultSet.getString("transaction_comments");
                         float totalAmountTransferred=orderDetailPaymentInfoResultSet.getFloat("total_amount_transferred");
                         boolean isPaymentDone=orderDetailPaymentInfoResultSet.getBoolean("is_payment_done");

                        orderDetailPaymentInfo=new OrderDetailPaymentInfo( transactionId,orderDetailId,manufacturerId
                                , productPriceToCustomer, productPriceToSeller,  amountCollectedBySeller
                                , amountCollectedByMebelkart,  advancePaidTOSeller, transactionDate,  transactionMode,
                                 transactionBankReference,vendorSuggestedTransferPrice,  otherCharges, transactionComments
                                ,  totalAmountTransferred, isPaymentDone) ;
                    }


                    /**
                     * Creating object of orderDetail
                     */
                    OrderDetail orderDetail=new OrderDetail( subOrderId,  orderId,  productId,  productAttributeId,  productName
                            ,  productQuantity,  productQuantityInStock,  productQuantityRefunded
                            ,  productQuantityReinjected,  orderDetailWholeSalePrice,  productPrice,  reductionPercentage
                            ,  reductionAmount,  groupReduction,  productQuantityDiscount,  orderDetailProductAdvanceAmount
                            ,  shippedFromDate,  shippedToDate,  deliveredFromDate,  deliveredToDate,  productEan13
                            ,  productUpc, productRefrence,  productSupplierRefrence,  productWeight,  taxName
                            ,  taxRate, ecoTax, ecotaxRate, discountQuantityApplied,  downloadHash,  downloadNb
                            ,  downloadDeadline, orderDetailVendorStatus,orderDetailLogisticService,orderDetailShipments,orderDetailAssignToOtherVendor,orderDetailPaymentInfo);
                    /**
                     * Adding each suborder in a list which is a list of objects of subOrders
                     */
                    subOrderDetails.add(orderDetail);

                    /**
                     * Order detail while loop ends here
                     */
                }

                order.setOrderDetails(subOrderDetails);
                /**
                 * Converting order object to json
                 */
                byte[] orderJson = pojoToJsonMapper.writeValueAsBytes(order);
                /**
                 * Indexing order json in elastic if order is already existing then
                 * we will update indexed document
                 */
                IndexRequest indexRequest = new IndexRequest("mk", "order", String.valueOf(ordersResultSet.getInt("id_order")))
                        .source(orderJson);
                UpdateRequest updateRequest = new UpdateRequest("mk", "order",String.valueOf(ordersResultSet.getInt("id_order")))
                        .doc(orderJson)
                        .upsert(indexRequest);

                UpdateResponse updateResponse=elasticInstance.update(updateRequest).get();
                /**
                 * Here we will check whether document was succesfuly indexed or not
                 */

            }
        }
        catch (Exception e){
            System.out.println("In ordering index exception occured ");
            e.printStackTrace();
        }
    }
}
