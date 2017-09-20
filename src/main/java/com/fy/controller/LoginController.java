package com.fy.controller;

import com.fy.pojo.Order;
import com.fy.pojo.Role;
import com.fy.pojo.User;
import com.fy.service.OrderService;
import com.fy.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/9/13.
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("/tologin")
    //@ResponseBody
    public  String toLogin(){

        return "/sysadmin/login/login";
    }


    @RequestMapping("/login")
    public String login(String userName, String password, Model model){


        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)){
            model.addAttribute("errorInfo","用户名或密码不能为空");
            return "/sysadmin/login/login";
        }

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken(userName,password);

        try{
            subject.login(token);
            User user = (User) subject.getPrincipal();
            Session session = subject.getSession();
            List<String> userRole=(List<String>) userService.findRoleIdList(user.getHhUserId());
            List<Order> checkOutOrderList = new ArrayList<Order>();
            List<Order> checkInOrderList = new ArrayList<Order>();
            for (String roleName:userRole ) {
                System.out.print(roleName);
                if("1".equals(roleName)){
                    int outStatus = 6;
                    int inStatus = 1;
                    checkOutOrderList = orderService.findOrdersByStatus(outStatus);
                    checkInOrderList = orderService.findOrdersByStatus(inStatus);
                    break;
                }

            }
            model.addAttribute("checkOutOrderList", checkOutOrderList);
            model.addAttribute("checkInOrderList", checkInOrderList);

            session.setAttribute("SessionUser",user);


            return "/home/fmain";

        } catch (AuthenticationException a){
            a.printStackTrace();
            model.addAttribute("errorInfo","用户名或密码错误");
            return "/sysadmin/login/login";
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorInfo","未知错误，请联系管理员");
            return "/sysadmin/login/login";
        }
    }



}
