package com.itisacat.basic.framework.rest.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.HttpPutFormContentFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2016/8/3.
 */
@Component
public class PutFilter extends HttpPutFormContentFilter {
    @Override
    protected void doFilterInternal(final HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

}
