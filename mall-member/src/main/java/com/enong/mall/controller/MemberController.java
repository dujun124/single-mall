package com.enong.mall.controller;

import com.enong.mall.common.api.CommonResult;
import com.enong.mall.common.exception.BusinessException;
import com.enong.mall.domain.UmsMember;
import com.enong.mall.domain.vo.Register;
import com.enong.mall.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/sso")
public class MemberController extends BaseController{

    @Autowired
    private MemberService memberService;

    @PostMapping("/getOtpCode")
    public CommonResult getOtpCode(@RequestParam String phoneNum) throws BusinessException {

        String otpCode = memberService.getOtpCode(phoneNum);

        return CommonResult.success(otpCode);
    }

    @PostMapping("/registe")   //加Valid注解,告诉spring,当前参数需要进行校验
    public CommonResult registe(@Valid @RequestBody Register register) throws BusinessException {
        int registe = memberService.registe(register);
        if (registe>0){
            return CommonResult.success(null);
        }

        return CommonResult.failed();
    }

    @PostMapping("/login")
    public CommonResult login(@RequestParam String userName,@RequestParam String password) throws BusinessException {
        UmsMember member = memberService.login(userName, password);
        if (member!=null){
            //将member信息存入session
            getHttpSession().setAttribute("member",member);

            return CommonResult.success(member+"登陆成功");
        }

        return CommonResult.failed();
    }
}
