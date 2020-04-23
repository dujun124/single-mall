package com.enong.mall.service;

import com.enong.mall.common.exception.BusinessException;
import com.enong.mall.domain.UmsMember;
import com.enong.mall.domain.vo.Register;

public interface MemberService {

    /**
     * 获取手机验证码
     * @param phoneNum
     * @return
     */
    public String getOtpCode(String phoneNum) throws BusinessException;

    /**
     * 用户注册
     * @param register
     * @return
     * @throws BusinessException
     */
    public int registe(Register register) throws BusinessException;

    /**
     * 用户登录
     * @param userName
     * @param password
     * @return
     */
    public UmsMember login(String userName , String password) throws BusinessException;

}
