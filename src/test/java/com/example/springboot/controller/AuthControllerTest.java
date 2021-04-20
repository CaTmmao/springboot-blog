package com.example.springboot.controller;

import com.example.springboot.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    private MockMvc mvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authenticationManager, userService)).build();
    }

    @Test
    public void testLogin() throws Exception {
        // auth 接口未登录状态
        mvc.perform(get("/auth"))
                .andExpect(status().isOk())
                .andExpect(result -> assertTrue(result.getResponse().getContentAsString().contains("unLog")));

        // auth/login 登录
        Map<String, String> param = new HashMap<>();
        param.put("username", "user");
        param.put("password", "pwd");
        String requestBody = new ObjectMapper().writeValueAsString(param);

        String encodePwd = bCryptPasswordEncoder.encode("pwd");
        when(userService.loadUserByUsername("user")).thenReturn(new User("user", encodePwd, Collections.EMPTY_LIST));

        MvcResult response = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk())
                .andExpect(r -> assertTrue(r.getResponse().getContentAsString().contains("success")))
                .andReturn();
        HttpSession session = response.getRequest().getSession();

        // 再次检查 /auth 接口，应该是登录状态
        when(userService.getUserByUsername("user")).thenReturn(new com.example.springboot.entities.User(1, "user", encodePwd));
        mvc.perform(get("/auth").session((MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(r -> assertTrue(r.getResponse().getContentAsString().contains("success")));
    }

}