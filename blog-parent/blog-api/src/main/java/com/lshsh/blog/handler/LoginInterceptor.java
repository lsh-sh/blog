package com.lshsh.blog.handler;

import com.alibaba.fastjson.JSON;
import com.lshsh.blog.dao.pojo.SysUser;
import com.lshsh.blog.service.LoginService;
import com.lshsh.blog.vo.ErrorCode;
import com.lshsh.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            //判断请求接口路径是否为HandlerMethod（controller方法）
            return true;
        }
        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}", requestURI);
        log.info("request method:{}", request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if (token == null) {
            Result result = Result.fail(ErrorCode.NO_LOGIN);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null) {
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        //是登录状态，放行
        return true;
    }
}
