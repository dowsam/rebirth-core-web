/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core RequestFlashData.java 2012-2-11 15:51:39 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * The Class RequestFlashData.
 *
 * @author l.xue.nong
 */
public class RequestFlashData {
	/** The Constant FLASH_IN_SESSION_KEY. */
	private static final String FLASH_IN_SESSION_KEY = "__flash__";

	/** The data. */
	private Map<String, String> data = new HashMap<String, String>();

	/** The out. */
	private Map<String, String> out = new HashMap<String, String>();

	/**
	 * Restore.
	 *
	 * @param request the request
	 * @return the request flash data
	 */
	@SuppressWarnings("unchecked")
	public static RequestFlashData restore(HttpServletRequest request) {
		RequestFlashData flash = new RequestFlashData();
		HttpSession session = request.getSession();
		Map<String, String> flashData = (Map<String, String>) session.getAttribute(FLASH_IN_SESSION_KEY);
		if (flashData != null) {
			flash.data = flashData;
		}
		return flash;
	}

	/**
	 * Save.
	 *
	 * @param request the request
	 * @param response the response
	 */
	public void save(HttpServletRequest request, HttpServletResponse response) {
		try {
			HttpSession session = request.getSession();
			if (session != null) {
				session.setAttribute(FLASH_IN_SESSION_KEY, out);
			}
		} catch (Exception e) {
			// ingorr
		}
	}

	// ThreadLocal access
	/** The current. */
	private static ThreadLocal<RequestFlashData> current = new ThreadLocal<RequestFlashData>();

	/**
	 * Current.
	 *
	 * @return the request flash data
	 */
	public static RequestFlashData current() {
		return current.get();
	}

	/**
	 * Sets the current.
	 *
	 * @param f the new current
	 */
	static void setCurrent(RequestFlashData f) {
		current.set(f);
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void put(String key, String value) {
		if (key.contains(":")) {
			throw new IllegalArgumentException("Character ':' is invalid in a flash key.");
		}
		data.put(key, value);
		out.put(key, value);
	}

	/**
	 * Now.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void now(String key, String value) {
		if (key.contains(":")) {
			throw new IllegalArgumentException("Character ':' is invalid in a flash key.");
		}
		data.put(key, value);
	}

	/**
	 * Put.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void put(String key, Object value) {
		if (value == null) {
			put(key, (String) null);
		}
		put(key, value + "");
	}

	/**
	 * Error.
	 *
	 * @param value the value
	 * @param args the args
	 */
	public void error(String value, Object... args) {
		put("error", String.format(value, args));
	}

	/**
	 * Success.
	 *
	 * @param value the value
	 * @param args the args
	 */
	public void success(String value, Object... args) {
		put("success", String.format(value, args));
	}

	/**
	 * Discard.
	 *
	 * @param key the key
	 */
	public void discard(String key) {
		out.remove(key);
	}

	/**
	 * Discard.
	 */
	public void discard() {
		out.clear();
	}

	/**
	 * Keep.
	 *
	 * @param key the key
	 */
	public void keep(String key) {
		if (data.containsKey(key)) {
			out.put(key, data.get(key));
		}
	}

	/**
	 * Keep.
	 */
	public void keep() {
		out.putAll(data);
	}

	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String get(String key) {
		return data.get(key);
	}

	/**
	 * Removes the.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean remove(String key) {
		return data.remove(key) != null;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		data.clear();
	}

	/**
	 * Contains.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean contains(String key) {
		return data.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return data.toString();
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public Map<String, String> getData() {
		return data;
	}
}
