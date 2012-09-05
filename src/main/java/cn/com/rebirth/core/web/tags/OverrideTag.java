/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core OverrideTag.java 2012-2-12 16:38:04 l.xue.nong$$
 */
package cn.com.rebirth.core.web.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * The Class OverrideTag.
 *
 * @author l.xue.nong
 */
public class OverrideTag extends BodyTagSupport {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8379959647039117369L;

	/** The name. */
	private String name;

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		return isOverrided() ? SKIP_BODY : EVAL_BODY_BUFFERED;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException {
		if (isOverrided()) {
			return EVAL_PAGE;
		}
		BodyContent b = getBodyContent();
		//		System.out.println("Override.content:"+b.getString());
		String varName = Utils.getOverrideVariableName(name);

		pageContext.getRequest().setAttribute(varName, bulidContent(b));
		return EVAL_PAGE;
	}

	/**
	 * Bulid content.
	 *
	 * @param b the b
	 * @return the object
	 */
	protected Object bulidContent(BodyContent b) {
		return new BlockTag.TagContentInfo(b.getString());
	}

	/**
	 * Checks if is overrided.
	 *
	 * @return true, if is overrided
	 */
	private boolean isOverrided() {
		String varName = Utils.getOverrideVariableName(name);
		return pageContext.getRequest().getAttribute(varName) != null;
	}

}
