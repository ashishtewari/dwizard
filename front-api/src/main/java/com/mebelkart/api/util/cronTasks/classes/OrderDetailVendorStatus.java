package com.mebelkart.api.util.cronTasks.classes;

import java.sql.Date;

/**
 * Created by vinitpayal on 28/03/16.
 */
public class OrderDetailVendorStatus {

    private Integer idOrderDetailVendorStatus;
    private String allStatusInfo;
    private String expectedShipmentDate;
    private Integer idCurrentStatus;
    private Date lastStatusUpdatedOn;
    private String courierUrlOrName;
    private boolean isOtherCarrier;
    private String courierTrackingDetail;
    private String pickupRequestInfo;
    private boolean isPickupRequested;
    private boolean isPickupDone;
    private boolean isDelaySmsSent;

    public OrderDetailVendorStatus(Integer idOrderDetailVendorStatus, String allStatusInfo, String expectedShipmentDate
            , Integer idCurrentStatus, Date lastStatusUpdatedOn, String courierUrlOrName, boolean isOtherCarrier
            , String courierTrackingDetail, String pickupRequestInfo, boolean isPickupRequested, boolean isPickupDone
            , boolean isDelaySmsSent) {
        this.idOrderDetailVendorStatus = idOrderDetailVendorStatus;
        this.allStatusInfo = allStatusInfo;
        this.expectedShipmentDate = expectedShipmentDate;
        this.idCurrentStatus = idCurrentStatus;
        this.lastStatusUpdatedOn = lastStatusUpdatedOn;
        this.courierUrlOrName = courierUrlOrName;
        this.isOtherCarrier = isOtherCarrier;
        this.courierTrackingDetail = courierTrackingDetail;
        this.pickupRequestInfo = pickupRequestInfo;
        this.isPickupRequested = isPickupRequested;
        this.isPickupDone = isPickupDone;
        this.isDelaySmsSent = isDelaySmsSent;
    }

    public Integer getIdOrderDetailVendorStatus() {
        return idOrderDetailVendorStatus;
    }

    public void setIdOrderDetailVendorStatus(Integer idOrderDetailVendorStatus) {
        this.idOrderDetailVendorStatus = idOrderDetailVendorStatus;
    }

    public String getAllStatusInfo() {
        return allStatusInfo;
    }

    public void setAllStatusInfo(String allStatusInfo) {
        this.allStatusInfo = allStatusInfo;
    }

    public String getExpectedShipmentDate() {
        return expectedShipmentDate;
    }

    public void setExpectedShipmentDate(String expectedShipmentDate) {
        this.expectedShipmentDate = expectedShipmentDate;
    }

    public Integer getIdCurrentStatus() {
        return idCurrentStatus;
    }

    public void setIdCurrentStatus(Integer idCurrentStatus) {
        this.idCurrentStatus = idCurrentStatus;
    }

    public Date getLastStatusUpdatedOn() {
        return lastStatusUpdatedOn;
    }

    public void setLastStatusUpdatedOn(Date lastStatusUpdatedOn) {
        this.lastStatusUpdatedOn = lastStatusUpdatedOn;
    }

    public String getCourierUrlOrName() {
        return courierUrlOrName;
    }

    public void setCourierUrlOrName(String courierUrlOrName) {
        this.courierUrlOrName = courierUrlOrName;
    }

    public boolean isOtherCarrier() {
        return isOtherCarrier;
    }

    public void setOtherCarrier(boolean otherCarrier) {
        isOtherCarrier = otherCarrier;
    }

    public String getCourierTrackingDetail() {
        return courierTrackingDetail;
    }

    public void setCourierTrackingDetail(String courierTrackingDetail) {
        this.courierTrackingDetail = courierTrackingDetail;
    }

    public String getPickupRequestInfo() {
        return pickupRequestInfo;
    }

    public void setPickupRequestInfo(String pickupRequestInfo) {
        this.pickupRequestInfo = pickupRequestInfo;
    }

    public boolean isPickupRequested() {
        return isPickupRequested;
    }

    public void setPickupRequested(boolean pickupRequested) {
        isPickupRequested = pickupRequested;
    }

    public boolean isPickupDone() {
        return isPickupDone;
    }

    public void setPickupDone(boolean pickupDone) {
        isPickupDone = pickupDone;
    }

    public boolean isDelaySmsSent() {
        return isDelaySmsSent;
    }

    public void setDelaySmsSent(boolean delaySmsSent) {
        isDelaySmsSent = delaySmsSent;
    }
}
