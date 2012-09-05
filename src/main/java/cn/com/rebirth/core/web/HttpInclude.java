/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core HttpInclude.java 2012-3-14 10:55:23 l.xue.nong$$
 */
package cn.com.rebirth.core.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HttpInclude.
 *
 * @author l.xue.nong
 */
public class HttpInclude extends HashMap<String, Object> implements Map<String, Object> {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4308972330817134518L;

	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(getClass());

	/** The session id key. */
	public static String sessionIdKey = "JSESSIONID";

	/** The request. */
	private HttpServletRequest request;

	/** The response. */
	private HttpServletResponse response;

	/**
	 * Instantiates a new http include.
	 *
	 * @param request the request
	 * @param response the response
	 */
	public HttpInclude(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		if (((String) key).equalsIgnoreCase("include")) {
			return this;
		}
		return include((String) key);
	}

	/**
	 * Include.
	 *
	 * @param includePath the include path
	 * @return the string
	 */
	public String include(String includePath) {
		StringWriter sw = new StringWriter(8192);
		include(includePath, sw);
		return sw.toString();
	}

	/**
	 * Include.
	 *
	 * @param includePath the include path
	 * @param writer the writer
	 */
	public void include(String includePath, Writer writer) {
		try {
			if (isRemoteHttpRequest(includePath)) {
				getRemoteContent(includePath, writer);
			} else {
				getLocalContent(includePath, writer);
			}
		} catch (ServletException e) {
			throw new RuntimeException("include error,path:" + includePath + " cause:" + e, e);
		} catch (IOException e) {
			throw new RuntimeException("include error,path:" + includePath + " cause:" + e, e);
		}
	}

	/**
	 * Checks if is remote http request.
	 *
	 * @param includePath the include path
	 * @return true, if is remote http request
	 */
	private static boolean isRemoteHttpRequest(String includePath) {
		return includePath != null
				&& (includePath.toLowerCase().startsWith("http://") || includePath.toLowerCase().startsWith("https://"));
	}

	/**
	 * Gets the local content.
	 *
	 * @param includePath the include path
	 * @param writer the writer
	 * @return the local content
	 * @throws ServletException the servlet exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void getLocalContent(String includePath, Writer writer) throws ServletException, IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(8192);

		CustomOutputHttpServletResponseWrapper customResponse = new CustomOutputHttpServletResponseWrapper(response,
				writer, outputStream);
		request.getRequestDispatcher(includePath).include(request, customResponse);

		customResponse.flushBuffer();
		if (customResponse.useOutputStream) {
			writer.write(outputStream.toString(response.getCharacterEncoding())); //TODO: response.getCharacterEncoding()有可能为null
		}
		writer.flush();
	}

	//TODO handle cookies and http query parameters encoding
	//TODO set inheritParams from request
	/**
	 * Gets the remote content.
	 *
	 * @param urlString the url string
	 * @param writer the writer
	 * @return the remote content
	 * @throws MalformedURLException the malformed url exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void getRemoteContent(String urlString, Writer writer) throws MalformedURLException, IOException {
		URL url = new URL(getWithSessionIdUrl(urlString));
		URLConnection conn = url.openConnection();
		setConnectionHeaders(urlString, conn);
		InputStream input = conn.getInputStream();
		try {
			Reader reader = new InputStreamReader(input, Utils.getContentEncoding(conn, response));
			Utils.copy(reader, writer);
		} finally {
			if (input != null)
				input.close();
		}
		writer.flush();
	}

	/**
	 * Sets the connection headers.
	 *
	 * @param urlString the url string
	 * @param conn the conn
	 */
	private void setConnectionHeaders(String urlString, URLConnection conn) {
		conn.setReadTimeout(6000);
		conn.setConnectTimeout(6000);
		String cookie = getCookieString();
		conn.setRequestProperty("Cookie", cookie);
		//TODO: 用于支持 httpinclude_header.properties
		//		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");
		//		conn.setRequestProperty("Host", url.getHost());
		if (logger.isDebugEnabled()) {
			logger.debug("request properties:" + conn.getRequestProperties() + " for url:" + urlString);
		}
	}

	//TODO add session id with url
	/**
	 * Gets the with session id url.
	 *
	 * @param url the url
	 * @return the with session id url
	 */
	private String getWithSessionIdUrl(String url) {
		return url;
	}

	/** The Constant SET_COOKIE_SEPARATOR. */
	private static final String SET_COOKIE_SEPARATOR = "; ";

	/**
	 * Gets the cookie string.
	 *
	 * @return the cookie string
	 */
	private String getCookieString() {
		StringBuffer sb = new StringBuffer(64);
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie c : cookies) {
				if (!sessionIdKey.equals(c.getName())) {
					sb.append(c.getName()).append("=").append(c.getValue()).append(SET_COOKIE_SEPARATOR);
				}
			}
		}

		String sessionId = Utils.getSessionId(request);
		if (sessionId != null) {
			sb.append(sessionIdKey).append("=").append(sessionId).append(SET_COOKIE_SEPARATOR);
		}
		return sb.toString();
	}

	/**
	 * The Class CustomOutputHttpServletResponseWrapper.
	 *
	 * @author l.xue.nong
	 */
	public static class CustomOutputHttpServletResponseWrapper extends HttpServletResponseWrapper {

		/** The use writer. */
		public boolean useWriter = false;

		/** The use output stream. */
		public boolean useOutputStream = false;
		//        
		/** The print writer. */
		private PrintWriter printWriter;

		/** The servlet output stream. */
		private ServletOutputStream servletOutputStream;

		/**
		 * Instantiates a new custom output http servlet response wrapper.
		 *
		 * @param response the response
		 * @param customWriter the custom writer
		 * @param customOutputStream the custom output stream
		 */
		public CustomOutputHttpServletResponseWrapper(HttpServletResponse response, final Writer customWriter,
				final OutputStream customOutputStream) {
			super(response);
			this.printWriter = new PrintWriter(customWriter);
			this.servletOutputStream = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					customOutputStream.write(b);
				}

				@Override
				public void write(byte[] b) throws IOException {
					customOutputStream.write(b);
				}

				@Override
				public void write(byte[] b, int off, int len) throws IOException {
					customOutputStream.write(b, off, len);
				}
			};
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponseWrapper#getWriter()
		 */
		@Override
		public PrintWriter getWriter() throws IOException {
			if (useOutputStream)
				throw new IllegalStateException("getOutputStream() has already been called for this response");
			useWriter = true;
			return printWriter;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
		 */
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			if (useWriter)
				throw new IllegalStateException("getWriter() has already been called for this response");
			useOutputStream = true;
			return servletOutputStream;
		}

		/* (non-Javadoc)
		 * @see javax.servlet.ServletResponseWrapper#flushBuffer()
		 */
		@Override
		public void flushBuffer() throws IOException {
			if (useWriter)
				printWriter.flush();
			if (useOutputStream)
				servletOutputStream.flush();
		}

	}

	/**
	 * The Class Utils.
	 *
	 * @author l.xue.nong
	 */
	static class Utils {

		/**
		 * Gets the content encoding.
		 *
		 * @param conn the conn
		 * @param response the response
		 * @return the content encoding
		 */
		static String getContentEncoding(URLConnection conn, HttpServletResponse response) {
			String contentEncoding = conn.getContentEncoding();
			if (conn.getContentEncoding() == null) {
				contentEncoding = parseContentTypeForCharset(conn.getContentType());
				if (contentEncoding == null) {
					contentEncoding = response.getCharacterEncoding();
				}
			} else {
				contentEncoding = conn.getContentEncoding();
			}
			return contentEncoding;
		}

		/** The p. */
		static Pattern p = Pattern.compile("(charset=)(.*)", Pattern.CASE_INSENSITIVE);

		/**
		 * Parses the content type for charset.
		 *
		 * @param contentType the content type
		 * @return the string
		 */
		private static String parseContentTypeForCharset(String contentType) {
			if (contentType == null)
				return null;
			Matcher m = p.matcher(contentType);
			if (m.find()) {
				return m.group(2).trim();
			}
			return null;
		}

		/**
		 * Copy.
		 *
		 * @param in the in
		 * @param out the out
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		private static void copy(Reader in, Writer out) throws IOException {
			char[] buff = new char[8192];
			while (in.read(buff) >= 0) {
				out.write(buff);
			}
		}

		/**
		 * Gets the session id.
		 *
		 * @param request the request
		 * @return the session id
		 */
		private static String getSessionId(HttpServletRequest request) {
			HttpSession session = request.getSession(false);
			if (session == null) {
				return null;
			}
			return session.getId();
		}
	}
}
