/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core BlockTag.java 2012-2-12 16:38:09 l.xue.nong$$
 */
package cn.com.rebirth.core.web.tags;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * The Class BlockTag.
 *
 * @author l.xue.nong
 */
public class BlockTag extends BodyTagSupport {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8246166191638588615L;

	/**
	 * The Class TagContentInfo.
	 *
	 * @author l.xue.nong
	 */
	public static class TagContentInfo implements Serializable {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = -613818482969072393L;

		/** The override. */
		private boolean override = true;

		/** The context. */
		private String context;

		/**
		 * Instantiates a new tag content info.
		 *
		 * @param context the context
		 */
		public TagContentInfo(String context) {
			super();
			this.context = context;
		}

		/**
		 * Instantiates a new tag content info.
		 *
		 * @param override the override
		 * @param context the context
		 */
		public TagContentInfo(boolean override, String context) {
			super();
			this.override = override;
			this.context = context;
		}

		/**
		 * Checks if is override.
		 *
		 * @return true, if is override
		 */
		public boolean isOverride() {
			return override;
		}

		/**
		 * Sets the override.
		 *
		 * @param override the new override
		 */
		public void setOverride(boolean override) {
			this.override = override;
		}

		/**
		 * Gets the context.
		 *
		 * @return the context
		 */
		public String getContext() {
			return context;
		}

		/**
		 * Sets the context.
		 *
		 * @param context the new context
		 */
		public void setContext(String context) {
			this.context = context;
		}

	}

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

	/**
	 * Do start tag.
	 *
	 * @return EVAL_BODY_INCLUDE or EVAL_BODY_BUFFERED or SKIP_BODY
	 * @throws JspException the jsp exception
	 */
	@Override
	public int doStartTag() throws JspException {
		if (getOverriedContent() == null) {
			return EVAL_BODY_INCLUDE;
		} else {
			TagContentInfo contentInfo = getOverriedContent();
			if (contentInfo.isOverride()) {
				return SKIP_BODY;
			} else {
				return EVAL_BODY_BUFFERED;
			}
		}
	}

	/**
	 * Do end tag.
	 *
	 * @return EVAL_PAGE or SKIP_PAGE
	 * @throws JspException the jsp exception
	 */
	@Override
	public int doEndTag() throws JspException {
		TagContentInfo overriedContent = getOverriedContent();
		if (overriedContent == null) {
			return EVAL_PAGE;
		}
		try {
			if (overriedContent.isOverride()) {
				pageContext.getOut().write(overriedContent.getContext());
			} else {

				pageContext.getOut().write(getBodyContent().getString() + overriedContent.getContext());
			}
		} catch (IOException e) {
			throw new JspException("write overridedContent occer IOException,block name:" + name, e);
		}
		return EVAL_PAGE;
	}

	/**
	 * Gets the overried content.
	 *
	 * @return the overried content
	 */
	private TagContentInfo getOverriedContent() {
		String varName = Utils.getOverrideVariableName(name);
		return (TagContentInfo) pageContext.getRequest().getAttribute(varName);
	}
}
