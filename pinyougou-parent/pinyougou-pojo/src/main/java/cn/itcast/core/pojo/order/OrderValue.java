package cn.itcast.core.pojo.order;

import java.io.Serializable;


/**
 * @author wophy
 */
public class OrderValue  implements Serializable {

   /* String startTime, String endTime, String countTime,*/

   private String startTime;
   private String endTime;
   private String countTime;
   private String status;
   private String paymentType;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCountTime() {
        return countTime;
    }

    public void setCountTime(String countTime) {
        this.countTime = countTime;
    }
}
