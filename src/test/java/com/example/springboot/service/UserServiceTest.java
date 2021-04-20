
package com.example.springboot.service;

import com.example.springboot.entities.User;
import com.example.springboot.mapper.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 引入 mockito 插件
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    BCryptPasswordEncoder mockEncoder;

    @Mock
    UserMapper mockUserMapper;

    @InjectMocks
    UserService userService;

    @Test
    public void testGetUserByUsername() {
        userService.getUserByUsername("user");
        verify(mockUserMapper).findUserByUsername("user");
    }

    @Test
    public void testSaveUser() {
        when(mockEncoder.encode("pwd")).thenReturn("encodePwd");
        userService.saveUser("user", "pwd");
        verify(mockUserMapper).save("user", "encodePwd");
    }

    @Test
    public void throwNotFoundWhenLoadUser() {
        when(mockUserMapper.findUserByUsername("user")).thenReturn(null);
        Assertions.assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("user"));
    }

    @Test
    public void testLoadUserByUsername() {
        when(mockUserMapper.findUserByUsername("user")).thenReturn(new User(1, "user", "pwd"));
        UserDetails userDetails = userService.loadUserByUsername("user");
        Assertions.assertEquals("user", userDetails.getUsername());
        Assertions.assertEquals("pwd", userDetails.getPassword());

    }
}