package com.touristchain.controllers;

import com.touristchain.models.User;
import com.touristchain.security.JwtUtil;
import com.touristchain.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Should return JWT token on successful login")
    void loginSuccessReturnsToken(){
        Map<String,String> body = new HashMap<>();
        body.put("email","user@example.com");
        body.put("password","secret");

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtil.generateToken("user@example.com")).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(body);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat((Map<?,?>)response.getBody()).containsEntry("token","jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user@example.com");
    }

    @Test
    @DisplayName("Should return 401 with error when credentials are invalid")
    void loginBadCredentials(){
        Map<String,String> body = new HashMap<>();
        body.put("email","user@example.com");
        body.put("password","wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        ResponseEntity<?> response = authController.login(body);

        assertThat(response.getStatusCodeValue()).isEqualTo(401);
        assertThat((Map<?,?>)response.getBody()).containsEntry("error","Credenciales inválidas");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Should reject registration if email already exists")
    void registerEmailAlreadyExists(){
        User user = new User();
        user.setEmail("existing@example.com");
        when(userService.findByEmail("existing@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.register(user);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
        assertThat((Map<?,?>)response.getBody()).containsEntry("error","Email ya registrado");
        verify(userService, never()).register(any(User.class));
    }

    @Test
    @DisplayName("Should register new user and return id and email")
    void registerSuccess(){
        User toRegister = new User();
        toRegister.setEmail("new@example.com");

        when(userService.findByEmail("new@example.com")).thenReturn(Optional.empty());
        User saved = new User();
        saved.setId(42L);
        saved.setEmail("new@example.com");
        when(userService.register(any(User.class))).thenReturn(saved);

        ResponseEntity<?> response = authController.register(toRegister);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        Map<?,?> body = (Map<?,?>) response.getBody();
        assertThat(body).containsEntry("id", 42L);
        assertThat(body).containsEntry("email", "new@example.com");
        verify(userService).register(any(User.class));
    }

    @Test
    @DisplayName("Should pass provided credentials to AuthenticationManager")
    void loginPassesCredentials(){
        Map<String,String> body = new HashMap<>();
        body.put("email","who@example.com");
        body.put("password","p@ss");

        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(mockAuth);
        when(jwtUtil.generateToken("who@example.com")).thenReturn("t");

        authController.login(body);

        verify(authenticationManager).authenticate(argThat(token ->
                token.getPrincipal().equals("who@example.com") &&
                token.getCredentials().equals("p@ss")
        ));
    }
}
