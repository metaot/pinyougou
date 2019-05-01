package entity;

import java.io.Serializable;
/**
 * 返回结果
 * @author Administrator
 *
 */
public class Result implements Serializable{

	private boolean flag;//是否成功
	
	private String message;//返回信息


    public Result(boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
