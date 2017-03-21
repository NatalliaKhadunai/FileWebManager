package com.epam.training.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthenticationController {
    private static final Logger logger = Logger.getLogger(AuthenticationController.class);

    @RequestMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(required = false) String error,
                                     @RequestParam(required = false) String logout) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username and password!");
        }
        if (logout != null) {
            model.addObject("msg", "You've been logged out successfully.");
        }
        model.setViewName("login.jsp");
        logger.debug("IN AUTHENTICATION CONTROLLER");
        return model;
    }
}
