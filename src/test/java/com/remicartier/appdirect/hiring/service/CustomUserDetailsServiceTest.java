package com.remicartier.appdirect.hiring.service;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.openid.OpenIDAuthenticationToken;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: remicartier
 * Date: 2015-04-11
 * Time: 10:06 PM
 */
public class CustomUserDetailsServiceTest {
    @Test
    public void testLoadUserDetails() {
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService();
        OpenIDAuthenticationToken token = mock(OpenIDAuthenticationToken.class);
        when(token.getName()).thenReturn("NAME");
        UserDetails userDetails = customUserDetailsService.loadUserDetails(token);
        assertEquals("NAME", userDetails.getUsername());
        assertEquals("", userDetails.getPassword());
        assertEquals("[ROLE_USER]", userDetails.getAuthorities().toString());
    }
}