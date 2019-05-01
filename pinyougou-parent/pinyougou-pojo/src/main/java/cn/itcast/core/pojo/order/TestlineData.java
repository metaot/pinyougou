package cn.itcast.core.pojo.order;

import java.io.Serializable;
import java.util.List;

public class TestlineData implements Serializable {

    private String[] xcontent;
    private List date;

    public String[] getXcontent() {
        return xcontent;
    }

    public void setXcontent(String[] xcontent) {
        this.xcontent = xcontent;
    }

    public List getDate() {
        return date;
    }

    public void setDate(List date) {
        this.date = date;
    }
}
