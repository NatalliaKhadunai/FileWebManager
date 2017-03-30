package com.epam.training.controller;

import com.epam.training.dao.UserDAO;
import com.epam.training.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AuthenticationControllerTest.SpringTestConfiguration.class)
public class AuthenticationControllerTest {
    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    private UserDAO userDAO;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Configuration
    public static class SpringTestConfiguration {
        @Bean
        public AuthenticationController authenticationController() {
            return new AuthenticationController();
        }
        @Bean
        public UserDAO userDAO() {
            return mock(UserDAO.class);
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        public AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }
    }

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        reset(userDAO);
    }

    @Test
    public void testGetLoginPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login")).andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "login.jsp");
    }

    @Test
    public void testGetLoginPage_Error() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login")
                .param("error", "")).andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "login.jsp");
        assertTrue(mvcResult.getModelAndView().getModelMap().containsKey("error"));
    }

    @Test
    public void testGetLoginPage_Logout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/login")
                .param("logout", "")).andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "login.jsp");
        assertTrue(mvcResult.getModelAndView().getModelMap().containsKey("msg"));
    }

    @Test
    public void testGetRegistrationPage() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/registration")
                .param("logout", "")).andExpect(status().isOk()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "registration.jsp");
        assertTrue(mvcResult.getModelAndView().getModelMap().containsKey("user"));
    }

    @Test
    public void testRegistry() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/registration")
                .param("username", "admin")
                .param("password", "admin"))
                .andExpect(status().is3xxRedirection()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "redirect:/training/main");
        verify(userDAO, times(1)).save(any(User.class));
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles={"ADMIN"})
    public void testLogout() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection()).andReturn();
        assertEquals(mvcResult.getModelAndView().getViewName(), "redirect:/training/login?logout");
    }

    private String toJSON(Object object) throws Exception {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}