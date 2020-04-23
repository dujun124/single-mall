package com.enong.mall.service.impl;

import com.enong.mall.common.exception.BusinessException;
import com.enong.mall.config.properties.RedisKeyPrefixConfig;
import com.enong.mall.domain.UmsMember;
import com.enong.mall.domain.UmsMemberExample;
import com.enong.mall.domain.vo.Register;
import com.enong.mall.mapper.UmsMemberMapper;
import com.enong.mall.service.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(value = RedisKeyPrefixConfig.class)
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private UmsMemberMapper umsMemberMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisKeyPrefixConfig redisKeyPrefixConfig;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String getOtpCode(String phoneNum) throws BusinessException {
        //查询当前用户有没有注册
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(phoneNum);
        List<UmsMember> umsMembers = umsMemberMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(umsMembers)){
            throw new BusinessException("用户已经注册,不能重复注册!");
        }
        //校验60s后有没有再次发送
        if (redisTemplate.hasKey(redisKeyPrefixConfig.getPrefix().getOtpCode()+phoneNum)){
            throw new BusinessException("请60s之后再试!");
        }
        //生产随机校验码,并存入redis
        String otpCode = getRandomNum();
        redisTemplate.opsForValue().set(redisKeyPrefixConfig.getPrefix().getOtpCode()+phoneNum,
                otpCode,redisKeyPrefixConfig.getExpire().getOtpCode(), TimeUnit.SECONDS);

        return otpCode;
    }

    //该注解表示:有异常抛出,则进行事物的回滚操作
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public int registe(Register register) throws BusinessException {

        String otpCode = (String) redisTemplate.opsForValue().get(redisKeyPrefixConfig.getPrefix().getOtpCode() + register.getPhone());

        if (StringUtils.isEmpty(otpCode) || !otpCode.equals(register.getOtpCode())){
            throw new BusinessException("动态校验码不正确");
        }
        UmsMember umsMember = new UmsMember();
        umsMember.setStatus(1);
        umsMember.setMemberLevelId(4L);
        BeanUtils.copyProperties(register,umsMember);

        return umsMemberMapper.insertSelective(umsMember);
    }

    @Override
    public UmsMember login(String userName, String password) throws BusinessException {
        UmsMemberExample umsMemberExample = new UmsMemberExample();
        umsMemberExample.createCriteria().andUsernameEqualTo(userName).andStatusEqualTo(1);
        List<UmsMember> umsMembers = umsMemberMapper.selectByExample(umsMemberExample);
        if (CollectionUtils.isEmpty(umsMembers)){
            throw new BusinessException("用户名或密码不正确");
        }
        if(umsMembers.size() > 1){
            throw new BusinessException("用户名被注册过多次,请联系客服!");
        }
        UmsMember umsMember = umsMembers.get(0);
        if (!passwordEncoder.matches(password,umsMember.getPassword())){
            throw new BusinessException("用户名或密码不正确");
        }
        return umsMember;
    }


    private String getRandomNum(){
        //这种方法的思路是在一个指定的字符串内随机生成一个子字符串；
        StringBuffer flag = new StringBuffer();
        String sources = "1234567890"; // 数字加上一些字母，就可以生成6位的验证码
        Random random = new Random();
        for (int j = 0; j < 6; j++){
            flag.append(sources.charAt(random.nextInt(10)) + "");
        }
        System.out.println("随机数是：   "+flag.toString());
        return flag.toString();
    }
}
