/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core RequestFlashFilter.java 2012-2-11 15:51:50 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The Class RequestFlashFilter.
 *
 * @author l.xue.nong
 */
public class RequestFlashFilter extends OncePerRequestFilter implements Filter {

	/* (non-Javadoc)
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			RequestFlashData.setCurrent(RequestFlashData.restore(request));
			request.setAttribute("flash", RequestFlashData.current().getData());
			filterChain.doFilter(request, response);
		} finally {
			RequestFlashData flash = RequestFlashData.current();
			RequestFlashData.setCurrent(null);
			if (flash != null)
				flash.save(request, response);
		}
	}

}
