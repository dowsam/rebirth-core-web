/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core-web SubTag.java 2012-9-4 16:15:02 l.xue.nong$$
 */
package cn.com.rebirth.core.web.tags;

import javax.servlet.jsp.tagext.BodyContent;

/**
 * The Class SubTag.
 *
 * @author l.xue.nong
 */
public class SubTag extends OverrideTag {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6148737452779424526L;

	/* (non-Javadoc)
	 * @see cn.com.rebirth.core.web.tags.OverrideTag#bulidContent(javax.servlet.jsp.tagext.BodyContent)
	 */
	@Override
	protected Object bulidContent(BodyContent b) {
		return new BlockTag.TagContentInfo(false, b.getString());
	}

}
