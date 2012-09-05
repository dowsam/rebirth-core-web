/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core-web RebirthCoreWebVersion.java 2012-7-25 8:35:41 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import cn.com.rebirth.commons.AbstractVersion;
import cn.com.rebirth.commons.Version;

/**
 * The Class RebirthCoreWebVersion.
 *
 * @author l.xue.nong
 */
public class RebirthCoreWebVersion extends AbstractVersion implements Version {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5812297032650687370L;

	/* (non-Javadoc)
	 * @see cn.com.rebirth.commons.Version#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "Rebirth-Core-Web";
	}

}
