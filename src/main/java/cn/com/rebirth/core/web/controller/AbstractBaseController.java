/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core AbstractBaseController.java 2012-2-11 16:00:45 l.xue.nong$$
 */
package cn.com.rebirth.core.web.controller;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import cn.com.rebirth.commons.exception.RebirthException;
import cn.com.rebirth.commons.utils.BeanUtils;
import cn.com.rebirth.commons.utils.ExceptionUtils;
import cn.com.rebirth.core.web.RequestFlashData;

/**
 * The Class AbstractBaseController.
 *
 * @author l.xue.nong
 */
public abstract class AbstractBaseController {

	static {
		// 注册converters
		ConvertRegisterHelper.registerConverters();

	}

	/**
	 * Copy properties.
	 *
	 * @param target the target
	 * @param source the source
	 */
	public void copyProperties(Object target, Object source) {
		BeanUtils.copyProperties(target, source);
	}

	/**
	 * Copy properties.
	 *
	 * @param <T> the generic type
	 * @param destClass the dest class
	 * @param orig the orig
	 * @return the t
	 */
	public <T> T copyProperties(Class<T> destClass, Object orig) {
		return BeanUtils.copyProperties(destClass, orig);
	}

	/**
	 * 初始化binder的回调函数.
	 *
	 * @param request the request
	 * @param binder the binder
	 * @throws Exception the exception
	 * @see MultiActionController#createBinder(HttpServletRequest,Object)
	 */
	protected void initBinder(WebDataBinder binder) throws RebirthException {
		binder.registerCustomEditor(Short.class, new CustomNumberEditor(Short.class, true));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
		binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
		binder.registerCustomEditor(Float.class, new CustomNumberEditor(Float.class, true));
		binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, true));
		binder.registerCustomEditor(BigDecimal.class, new CustomNumberEditor(BigDecimal.class, true));
		binder.registerCustomEditor(BigInteger.class, new CustomNumberEditor(BigInteger.class, true));
	}

	/**
	 * 保存消息在request中,messages.jsp会取出来显示此消息
	 *
	 * @param message the message
	 */
	protected static void saveMessage(String message) {
		RequestFlashData.current().success(message);
	}

	/**
	 * 保存错误消息在request中,messages.jsp会取出来显示此消息
	 *
	 * @param errorMsg the error msg
	 */
	protected static void saveError(String errorMsg) {
		RequestFlashData.current().error(errorMsg);
	}

	/**
	 * Gets the or create request attribute.
	 *
	 * @param <T> the generic type
	 * @param request the request
	 * @param key the key
	 * @param clazz the clazz
	 * @return the or create request attribute
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getOrCreateRequestAttribute(HttpServletRequest request, String key, Class<T> clazz) {
		Object value = request.getAttribute(key);
		if (value == null) {
			try {
				value = clazz.newInstance();
			} catch (Exception e) {
				ExceptionUtils.unchecked(e);
			}
			request.setAttribute(key, value);
		}
		return (T) value;
	}

	/**
	 * 取得项目绝对路径.
	 *
	 * @param request the request
	 * @return the server path
	 */
	public static String getServerPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ ("/".equalsIgnoreCase(request.getContextPath()) ? "" : request.getContextPath());
	}

}
