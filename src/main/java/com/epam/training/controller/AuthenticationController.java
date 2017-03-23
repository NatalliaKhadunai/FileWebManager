package com.epam.training.controller;

import com.epam.training.dao.UserDAO;
import com.epam.training.dto.UserDTO;
import com.epam.training.entity.Role;
import com.epam.training.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder encoder;

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
        return model;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    private ModelAndView getRegistrationPage() {
        return new ModelAndView("registration.jsp", "user", new UserDTO());
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registry(@ModelAttribute("user") UserDTO userDTO, HttpServletRequest request) {
        User user = createUserObject(userDTO);
        user.addRole(Role.GUEST);
        userDAO.save(user);
        authenticateUserAndSetSession(userDTO, request);
        return "redirect:/training/main";
    }

    private User createUserObject(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPasswordHash(encoder.encode(userDTO.getPassword()));
        return user;
    }

    private void authenticateUserAndSetSession(UserDTO userDTO, HttpServletRequest request) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authenticatedUser = authenticationManager.authenticate(token);

        SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
    }
}
