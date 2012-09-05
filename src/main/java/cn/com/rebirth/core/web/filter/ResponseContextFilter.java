/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core-web ResponseContextFilter.java 2012-8-2 13:17:04 l.xue.nong$$
 */
package cn.com.rebirth.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The Class ResponseContextFilter.
 *
 * @author l.xue.nong
 */
public class ResponseContextFilter extends OncePerRequestFilter implements Filter {

	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		RequestContext rc = RequestContext.begin(getServletContext(), request, response);
		try {
			before(request, response, filterChain, rc);
			filterChain.doFilter(request, response);
			after(request, response, filterChain, rc);
		} finally {
			if (rc != null)
				rc.end();
		}
	}

	/**
	 * Before.
	 *
	 * @param request the request
	 * @param response the response
	 * @param filterChain the filter chain
	 * @param rc the rc
	 */
	protected void before(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
			RequestContext rc) {

	}

	/**
	 * After.
	 *
	 * @param request the request
	 * @param response the response
	 * @param filterChain the filter chain
	 * @param rc the rc
	 */
	protected void after(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain,
			RequestContext rc) {

	}
}
