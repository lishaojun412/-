package com.itheima.controller;

import com.itheima.domain.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public String findAll(Model model){
        List<User> list = userService.findAll();
        model.addAttribute("list",list);
        return "list";
    }

    @RequestMapping("findYearSal")
    public String findYearSal(int empno,Model model){
        double yearSal = userService.findYearSal(empno);
        model.addAttribute("yearSal",yearSal);
        return "yearSal";
    }

}
