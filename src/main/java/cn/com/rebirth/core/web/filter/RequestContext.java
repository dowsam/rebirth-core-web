/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core-web RequestContext.java 2012-8-2 13:17:08 l.xue.nong$$
 */
package cn.com.rebirth.core.web.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.com.rebirth.commons.utils.IpUtils;

/**
 * The Class RequestContext.
 *
 * @author l.xue.nong
 */
public final class RequestContext {

	/** The Constant contexts. */
	private final static ThreadLocal<RequestContext> contexts = new ThreadLocal<RequestContext>();

	/** The context. */
	private ServletContext context;

	/** The session. */
	private HttpSession session;

	/** The request. */
	private HttpServletRequest request;

	/** The response. */
	private HttpServletResponse response;

	/** The cookies. */
	private Map<String, Cookie> cookies;

	/**
	 * Begin.
	 *
	 * @param ctx the ctx
	 * @param req the req
	 * @param res the res
	 * @return the request context
	 */
	public static RequestContext begin(ServletContext ctx, HttpServletRequest req, HttpServletResponse res) {
		RequestContext rc = new RequestContext();
		rc.context = ctx;
		rc.request = req;
		rc.response = res;
		rc.session = req.getSession(false);
		rc.cookies = new HashMap<String, Cookie>();
		Cookie[] cookies = req.getCookies();
		if (cookies != null)
			for (Cookie ck : cookies) {
				rc.cookies.put(ck.getName(), ck);
			}
		contexts.set(rc);
		return rc;
	}

	/**
	 * Gets the.
	 *
	 * @return the request context
	 */
	public static RequestContext get() {
		return contexts.get();
	}

	/**
	 * End.
	 */
	public void end() {
		this.context = null;
		this.request = null;
		this.response = null;
		this.session = null;
		this.cookies = null;
		contexts.remove();
	}

	/**
	 * Context.
	 *
	 * @return the servlet context
	 */
	public ServletContext context() {
		return context;
	}

	/**
	 * Session.
	 *
	 * @return the http session
	 */
	public HttpSession session() {
		return session;
	}

	/**
	 * Session.
	 *
	 * @param create the create
	 * @return the http session
	 */
	public HttpSession session(boolean create) {
		return (session == null && create) ? (session = request.getSession(create)) : session;
	}

	/**
	 * Request.
	 *
	 * @return the http servlet request
	 */
	public HttpServletRequest request() {
		return request;
	}

	/**
	 * Response.
	 *
	 * @return the http servlet response
	 */
	public HttpServletResponse response() {
		return response;
	}

	public String requestIp() {
		return IpUtils.getClientIpAddr(request);
	}
}
