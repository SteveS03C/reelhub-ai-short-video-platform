package com.show.admin.scetc.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author 信息苏
 * @date:   2019年4月26日 上午9:31:45
 * @version V1.0
 */
@Configuration
public class ShiroConfig {
    /**
     * 配置shiro过滤器
     * @author zhengkai
     */
    @Bean("shiroFilter")                                                                                                                                                                                                               
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        //1.定义shiroFactoryBean
        ShiroFilterFactoryBean shiroFilterFactoryBean=new ShiroFilterFactoryBean();
        //2.设置securityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //3.LinkedHashMap是有序的，进行顺序拦截器配置
        Map<String,String> filterChainMap = new LinkedHashMap<String,String>();
        //4.配置不需要认证的URL
        filterChainMap.put("/login", "anon");
        filterChainMap.put("/adminUser/loginSubmit", "anon");
        filterChainMap.put("/other/imageCode.do", "anon");
        filterChainMap.put("/static/**", "anon");
        //5.配置logout过滤器
        filterChainMap.put("/logout", "logout");
        //6.所有url必须通过认证才可以访问
        filterChainMap.put("/**", "authc");
        //7.设置默认登录的url
        shiroFilterFactoryBean.setLoginUrl("/adminUser/login");
        //8.设置成功之后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        //9.设置未授权界面
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        //10.设置shiroFilterFactoryBean的FilterChainDefinitionMap
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        return shiroFilterFactoryBean;
    }
    /**
     * 配置安全管理器   
     * @author zhengkai
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(myShiroRealm());
        return securityManager;
    }
    
    //将自己的验证方式加入容器
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        return myShiroRealm;
    }

}



