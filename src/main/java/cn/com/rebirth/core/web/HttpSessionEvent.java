/*
 * Copyright (c) 2005-2012 www.china-cti.com All rights reserved
 * Info:rebirth-core-web HttpSessionEvent.java 2012-8-15 12:43:11 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import java.io.Serializable;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionListener;

/**
 * The Interface HttpSessionEvent.
 *
 * @author l.xue.nong
 */
public interface HttpSessionEvent extends HttpSessionListener, HttpSessionActivationListener, Serializable {

}
