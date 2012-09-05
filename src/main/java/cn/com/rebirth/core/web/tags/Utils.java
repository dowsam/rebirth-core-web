/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core Utils.java 2012-2-12 16:37:16 l.xue.nong$$
 */
package cn.com.rebirth.core.web.tags;

/**
 * The Class Utils.
 *
 * @author l.xue.nong
 */
class Utils {

	/** The BLOCK. */
	public static String BLOCK = "__jsp_override__";

	/**
	 * Gets the override variable name.
	 *
	 * @param name the name
	 * @return the override variable name
	 */
	static String getOverrideVariableName(String name) {
		return BLOCK + name;
	}

}
