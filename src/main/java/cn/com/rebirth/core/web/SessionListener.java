/**
* Copyright (c) 2005-2011 www.china-cti.com
* Id: SessionListener.java 2011-5-11 11:57:26 l.xue.nong$$
*/
package cn.com.rebirth.core.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.rebirth.commons.entity.SessionInformations;
import cn.com.rebirth.commons.utils.ClassResolverUtils;
import cn.com.rebirth.commons.utils.ClassResolverUtils.AbstractFindCallback;
import cn.com.rebirth.commons.utils.ClassResolverUtils.FindCallback;
import cn.com.rebirth.commons.utils.ResolverUtils;

import com.google.common.collect.Lists;

/**
 * The listener interface for receiving session events.
 * The class that is interested in processing a session
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSessionListener<code> method. When
 * the session event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SessionEvent
 */
public class SessionListener implements HttpSessionListener, HttpSessionActivationListener, ServletContextListener,
		Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5176922466650346572L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

	/** The Constant SESSION_ACTIVATION_KEY. */
	private static final String SESSION_ACTIVATION_KEY = "rebirth.sessionActivation";

	/** The Constant SESSION_COUNT. */
	private static final AtomicInteger SESSION_COUNT = new AtomicInteger();

	/** The Constant SESSION_MAP_BY_ID. */
	private static final ConcurrentMap<String, HttpSession> SESSION_MAP_BY_ID = new ConcurrentHashMap<String, HttpSession>();

	/** The enabled. */
	private static boolean enabled;

	/** The find callback. */
	private static FindCallback<cn.com.rebirth.core.web.HttpSessionEvent> findCallback = new AbstractFindCallback<cn.com.rebirth.core.web.HttpSessionEvent>() {

		@Override
		protected void doFindType(ResolverUtils<cn.com.rebirth.core.web.HttpSessionEvent> resolverUtils,
				Class<cn.com.rebirth.core.web.HttpSessionEvent> entityClass) {
			resolverUtils.findImplementations(entityClass, StringUtils.EMPTY);
		}

	};
	
	/** The session events. */
	private static List<cn.com.rebirth.core.web.HttpSessionEvent> sessionEvents = Lists.newArrayList();

	/**
	 * The Class SessionInformationsComparator.
	 *
	 * @author l.xue.nong
	 */
	static final class SessionInformationsComparator implements Comparator<SessionInformations>, Serializable {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Compare.
		 *
		 * @param session1 the session1
		 * @param session2 the session2
		 * @return the int
		 * {@inheritDoc}
		 */
		public int compare(SessionInformations session1, SessionInformations session2) {
			if (session1.getLastAccess().before(session2.getLastAccess())) {
				return 1;
			} else if (session1.getLastAccess().after(session2.getLastAccess())) {
				return -1;
			} else {
				return 0;
			}
		}
	}

	/**
	 * Constructeur.
	 */
	public SessionListener() {
		super();
		setEnabled(true);
	}

	/**
	 * Sets the enabled.
	 *
	 * @param newEnabled the new enabled
	 */
	private static void setEnabled(boolean newEnabled) {
		enabled = newEnabled;
	}

	/**
	 * Gets the session count.
	 *
	 * @return the session count
	 */
	public static int getSessionCount() {
		if (!enabled) {
			return -1;
		}
		return SESSION_COUNT.get();
	}

	/**
	 * Gets the session age sum.
	 *
	 * @return the session age sum
	 */
	public static long getSessionAgeSum() {
		if (!enabled) {
			return -1;
		}
		final long now = System.currentTimeMillis();
		long result = 0;
		for (final HttpSession session : SESSION_MAP_BY_ID.values()) {
			try {
				result += now - session.getCreationTime();
			} catch (final Exception e) {
				// Tomcat can throw "java.lang.IllegalStateException: getCreationTime: Session already invalidated"
				continue;
			}
		}
		return result;
	}

	/**
	 * Invalidate all sessions.
	 */
	public static void invalidateAllSessions() {
		for (final HttpSession session : SESSION_MAP_BY_ID.values()) {
			try {
				session.invalidate();
			} catch (final Exception e) {
				// Tomcat can throw "java.lang.IllegalStateException: getLastAccessedTime: Session already invalidated"
				continue;
			}
		}
	}

	/**
	 * Invalidate session.
	 *
	 * @param sessionId the session id
	 */
	public static void invalidateSession(String sessionId) {
		final HttpSession session = SESSION_MAP_BY_ID.get(sessionId);
		if (session != null) {
			session.invalidate();
		}
	}

	/**
	 * Gets the all sessions informations.
	 *
	 * @return the all sessions informations
	 */
	public static List<SessionInformations> getAllSessionsInformations() {
		final Collection<HttpSession> sessions = SESSION_MAP_BY_ID.values();
		final List<SessionInformations> sessionsInformations = new ArrayList<SessionInformations>(sessions.size());
		for (final HttpSession session : sessions) {
			try {
				sessionsInformations.add(new SessionInformations(session, false));
			} catch (final Exception e) {
				// Tomcat can throw "java.lang.IllegalStateException: getLastAccessedTime: Session already invalidated"
				continue;
			}
		}
		sortSessions(sessionsInformations);
		return Collections.unmodifiableList(sessionsInformations);
	}

	/**
	 * Sort sessions.
	 *
	 * @param sessionsInformations the sessions informations
	 * @return the list
	 */
	public static List<SessionInformations> sortSessions(List<SessionInformations> sessionsInformations) {
		if (sessionsInformations.size() > 1) {
			Collections.sort(sessionsInformations, Collections.reverseOrder(new SessionInformationsComparator()));
		}
		return sessionsInformations;
	}

	/**
	 * Gets the session informations by session id.
	 *
	 * @param sessionId the session id
	 * @return the session informations by session id
	 */
	public static SessionInformations getSessionInformationsBySessionId(String sessionId) {
		final HttpSession session = SESSION_MAP_BY_ID.get(sessionId);
		if (session == null) {
			return null;
		}
		return new SessionInformations(session, true);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		final HttpSession session = event.getSession();
		if (session.getAttribute(SESSION_ACTIVATION_KEY) == this) {
			for (final Map.Entry<String, HttpSession> entry : SESSION_MAP_BY_ID.entrySet()) {
				final String id = entry.getKey();
				final HttpSession other = entry.getValue();
				if (!id.equals(other.getId())) {
					SESSION_MAP_BY_ID.remove(id);
				}
			}
		} else {
			session.setAttribute(SESSION_ACTIVATION_KEY, this);

			// pour getSessionCount
			SESSION_COUNT.incrementAndGet();
		}

		// pour invalidateAllSession
		SESSION_MAP_BY_ID.put(session.getId(), session);
		notifySessionCreated(event);
	}

	/**
	 * Invoked when notify session is created.
	 *
	 * @param event the event
	 */
	protected void notifySessionCreated(HttpSessionEvent event) {
		for (cn.com.rebirth.core.web.HttpSessionEvent httpSessionEvent : sessionEvents) {
			httpSessionEvent.sessionCreated(event);
		}
	}

	/**
	 * Notify session destroyed.
	 *
	 * @param event the event
	 */
	protected void notifySessionDestroyed(HttpSessionEvent event) {
		for (cn.com.rebirth.core.web.HttpSessionEvent httpSessionEvent : sessionEvents) {
			httpSessionEvent.sessionDestroyed(event);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		final HttpSession session = event.getSession();

		// plus de removeAttribute
		// (pas nécessaire et Tomcat peut faire une exception "session already invalidated")
		//		session.removeAttribute(SESSION_ACTIVATION_KEY);

		// pour getSessionCount
		SESSION_COUNT.decrementAndGet();

		// pour invalidateAllSession
		SESSION_MAP_BY_ID.remove(session.getId());
		notifySessionDestroyed(event);
	}

	/**
	 * Notify session did activate.
	 *
	 * @param event the event
	 */
	protected void notifySessionDidActivate(HttpSessionEvent event) {
		for (cn.com.rebirth.core.web.HttpSessionEvent httpSessionEvent : sessionEvents) {
			httpSessionEvent.sessionCreated(event);
		}
	}

	/**
	 * Notify session will passivate.
	 *
	 * @param event the event
	 */
	protected void notifySessionWillPassivate(HttpSessionEvent event) {
		for (cn.com.rebirth.core.web.HttpSessionEvent httpSessionEvent : sessionEvents) {
			httpSessionEvent.sessionDestroyed(event);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionActivationListener#sessionDidActivate(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
		// pour getSessionCount
		SESSION_COUNT.incrementAndGet();

		// pour invalidateAllSession
		SESSION_MAP_BY_ID.put(event.getSession().getId(), event.getSession());
		notifySessionDidActivate(event);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionActivationListener#sessionWillPassivate(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
		// pour getSessionCount
		SESSION_COUNT.decrementAndGet();

		// pour invalidateAllSession
		SESSION_MAP_BY_ID.remove(event.getSession().getId());
		notifySessionWillPassivate(event);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		SESSION_MAP_BY_ID.clear();
		SESSION_COUNT.set(0);
		sessionEvents.clear();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		System.getProperty("java.io.tmpdir");
		LOGGER.debug("Rebirth listener init started");
		LOGGER.debug("Rebirth listener init done");
		sessionEvents = ClassResolverUtils.find(findCallback);
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[sessionCount=" + getSessionCount() + ']';
	}

}
