package com.example.security.config;

import com.example.security.exceptions.CustomNoHeaderException;
import com.example.security.models.TokenUtility;
import com.example.security.services.IUserService;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.security.contants.Constants.*;

public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private IUserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthProvider authProvider;

    @Autowired
    private TokenUtilityProvider tokenUtilityProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = validateTokenHeader(httpServletRequest);
            TokenUtility tokenUtility = validateTokenUtility(token);
            if (tokenUtility.isValidateToken()) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(tokenUtility.getEmail());
                //see junit test
                if(userDetails!=null){
                    List<SimpleGrantedAuthority> simpleGrantedAuthorities
                            = tokenUtility.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority(AUTHORITY_PREFIX + role))
                            .collect(Collectors.toCollection(ArrayList::new));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, simpleGrantedAuthorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (CustomNoHeaderException ex) {
            //NPE or if token not found on header no status to return
            //rejected if @Secure on controller
            ex.printStackTrace();
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String validateTokenHeader(HttpServletRequest httpServletRequest) {
        try {
            final String requestTokenHeader = httpServletRequest.getHeader(HEADER_STRING);
            String token = requestTokenHeader.replace(TOKEN_PREFIX, "");
            System.out.println("token : " + token);
            if (token == null) {
                throw new CustomNoHeaderException("token not found on header");
            }
            return token;
        } catch (NullPointerException ex) {
            throw new CustomNoHeaderException("token not found on header");
        }
    }

    private TokenUtility validateTokenUtility(String token) throws IOException {
        return tokenUtilityProvider.getTokenUtility(token);
    }
}
