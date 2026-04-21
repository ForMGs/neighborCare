package com.neighbor.care.session.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class SessionDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        System.out.println("==== SESSION DEBUG ====");
        System.out.println("URI = " + request.getRequestURI());
        System.out.println("requestedSessionId = " + request.getRequestedSessionId());
        System.out.println("isRequestedSessionIdValid = " + request.isRequestedSessionIdValid());

        if(session != null){
            System.out.println("Session ID: "+ session.getId());

            Enumeration<String> names = session.getAttributeNames();
            while(names.hasMoreElements()){
                String name = names.nextElement();
                System.out.println(name + " = "+ session.getAttribute(name));
            }
        }else{
            System.out.println("No Session");
        }
        filterChain.doFilter(request, response);
    }
}
