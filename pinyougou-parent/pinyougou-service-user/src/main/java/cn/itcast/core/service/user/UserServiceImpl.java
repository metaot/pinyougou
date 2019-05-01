package cn.itcast.core.service.user;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import cn.itcast.core.utils.md5.MD5Util;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private UserDao userDao;

    /**
     * 用户获取短信验证码
     *
     * @param phone
     */
    @Override
    public void sendCode(final String phone) {
        // 将获取短信验证码的数据发送到mq中
        // 手机号、验证码、签名、模板
        final String code = RandomStringUtils.randomNumeric(6);
        System.out.println(code);
        //将验证码保存在redis中 注册时用于验证
        redisTemplate.boundValueOps(phone).set(code);
        //设置过期时间
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);

        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // 封装map消息体
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "阮文");
                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateParam", "{\"code\":\"" + code + "\"}");
                return mapMessage;
            }
        });
    }

    /**
     * 商城用户注册
     *
     * @param smscode 短信验证码
     * @param user    注册信息
     */
    @Override
    public void add(String smscode, User user) {
        //判断验证码的合法性
        //从redis中出去phone的验证码
        //  String code = redisTemplate.boundValueOps(user.getPhone()).get();
        //  if (smscode!=null&&"null"!=smscode&&smscode.equals(code)){
        //保存用户
        try {
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password);
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
      /*  }else{
            throw new RuntimeException("验证码错误");
        }*/

    }

    /**
     * 更新头像
     *
     * @param url
     */
    @Override
    public void updatePicSrc(String userId, String url) {
        User user = new User();
        user.setUsername(userId);
        user.setHeadPic(url);
        userDao.updateByPrimaryKeySelective(user);
    }

    /**
     * 修改个人信息数据回显
     *
     * @param username
     * @return
     */
    @Override
    public User findOne(String username) {
        UserQuery query = new UserQuery();
        UserQuery.Criteria criteria = query.createCriteria();
        criteria.andUsernameEqualTo(username);
        return userDao.selectByExample(query).get(0);
    }
    @Transactional
    @Override
    public void update(String username,User user) {
        UserQuery query = new UserQuery();
        UserQuery.Criteria criteria = query.createCriteria();
        criteria.andUsernameEqualTo(username);

        List<User> userList = userDao.selectByExample(query);

        userDao.updateByPrimaryKeySelective(userList.get(0));
    }
}
