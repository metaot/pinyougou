package cn.itcast.core.service.user;

import cn.itcast.core.pojo.user.User;

/**
 * @author wophy
 */
public interface UserService {
    /**
     * 用户获取短信验证码
     * @param phone
     */
    public void sendCode(String phone);

    /**
     * 商城用户注册
     * @param smscode  短信验证码
     * @param user  注册信息
     */
    public void add(String smscode, User user);

    /**
     * 更新头像
     * @param url
     */
    void updatePicSrc(String userId,String url);

    /**
     * 修改个人信息数据回显
     * @param username
     * @return
     */
    User findOne(String username);

    /**
     * 更新
     * @param user
     */
    void update(String username,User user);
}
