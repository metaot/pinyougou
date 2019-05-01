package cn.itcast.core.pojo.order;

import java.io.Serializable;
import java.util.Objects;

public class OrderCountOfEveryDay implements Serializable {
    /**
     * 统计的时间段 (天)
     */
    private String daytime;

    @Override
    public String toString() {
        return "OrderCountOfEveryDay{" +
                "daytime='" + daytime + '\'' +
                ", dayRows=" + dayRows +
                ", avgDayMoney=" + avgDayMoney +
                ", totleDayMoney=" + totleDayMoney +
                '}';
    }

    /**
     * 每天的订单数
     */
    private Long dayRows;
    /**
     * 平均每日每单价格
     */
    private Double avgDayMoney;
    /**
     * 每日订单总价价格
     */
    private Double totleDayMoney;

    public Long getDayRows() {
        return dayRows;
    }

    public void setDayRows(Long dayRows) {
        this.dayRows = dayRows;
    }

    public Double getAvgDayMoney() {
        return avgDayMoney;
    }

    public void setAvgDayMoney(Double avgDayMoney) {
        //平均每日订单的价格
        /**
         *  每日总金额/每日条数
         */
        if (dayRows!=0){

            this.avgDayMoney =totleDayMoney/dayRows ;
        }else {

            this.avgDayMoney =0.0 ;
        }
    }

    public Double getTotleDayMoney() {
        return totleDayMoney;
    }

    public void setTotleDayMoney(Double totleDayMoney) {
        this.totleDayMoney = totleDayMoney;
    }

    public String getDaytime() {
        return daytime;
    }

    public void setDaytime(String daytime) {
        this.daytime = daytime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCountOfEveryDay that = (OrderCountOfEveryDay) o;
        return Objects.equals(daytime, that.daytime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(daytime);
    }
}
