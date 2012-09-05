/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core RenderVariableInterceptor.java 2012-2-11 15:49:55 l.xue.nong$$
 */
package cn.com.rebirth.core.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.com.rebirth.core.web.HttpInclude;
import cn.com.rebirth.core.web.RequestFlashData;

/**
 * The Class RenderVariableInterceptor.
 *
 * @author l.xue.nong
 */
public class RenderVariableInterceptor extends HandlerInterceptorAdapter implements InitializingBean {
	//系统启动并初始化一次的变量
	/** The global render variables. */
	private Map<String, Object> globalRenderVariables = new HashMap<String, Object>();

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView == null) {
			return;
		}
		String viewName = modelAndView.getViewName();
		if (viewName != null && !viewName.startsWith("redirect:")) {
			modelAndView.addAllObjects(globalRenderVariables);
			modelAndView.addAllObjects(perRequest(request, response));
			modelAndView.addObject("httpInclude", new HttpInclude(request, response));
		}
	}

	/**
	 * Per request.
	 *
	 * @param request the request
	 * @param response the response
	 * @return the map
	 */
	protected Map<String, Object> perRequest(HttpServletRequest request, HttpServletResponse response) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("share_current_request_time", new Date());
		model.put("base", getServerPath(request));
		model.put("flash", RequestFlashData.current().getData());
		model.putAll(RequestFlashData.current().getData());
		model.put("url", request.getRequestURI());
		return model;
	}

	/**
	 * Gets the server path.
	 *
	 * @param request the request
	 * @return the server path
	 */
	protected static String getServerPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ ("/".equalsIgnoreCase(request.getContextPath()) ? "" : request.getContextPath());
	}

	//用于初始化 sharedRenderVariables, 全局共享变量请尽量用global前缀
	/**
	 * Inits the shared render variables.
	 */
	private void initSharedRenderVariables() {
		globalRenderVariables.put("global_system_start_time", new Date());

		//也可以存放一些共享的工具类,以便视图使用,如StringUtils

	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		initSharedRenderVariables();
	}

}
