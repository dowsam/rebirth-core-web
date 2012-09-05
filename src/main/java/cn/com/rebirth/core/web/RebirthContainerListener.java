/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core RebirthContainerListener.java 2012-7-20 12:28:34 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cn.com.rebirth.commons.RebirthContainer;

/**
 * The listener interface for receiving rebirthContainer events.
 * The class that is interested in processing a rebirthContainer
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addRebirthContainerListener<code> method. When
 * the rebirthContainer event occurs, that object's appropriate
 * method is invoked.
 *
 * @see RebirthContainerEvent
 */
public class RebirthContainerListener implements ServletContextListener {

	/* (non-Javadoc)
	 * @see org.springframework.web.context.ContextLoaderListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		RebirthContainer.getInstance().start();
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.context.ContextLoaderListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		RebirthContainer.getInstance().stop();
	}

}
