/**
* Copyright (c) 2005-2011 www.china-cti.com
* Id: JCaptchaFilter.java 2011-6-25 2:05:05 l.xue.nong$$
*/
package cn.com.rebirth.core.web.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;

/**
 * The Class JCaptchaFilter.
 */
public class JCaptchaFilter implements javax.servlet.Filter {

	//web.xml中的参数名定义
	/** The Constant CAPTCHA_PARAMTER_NAME_PARAM. */
	public static final String CAPTCHA_PARAMTER_NAME_PARAM = "captchaParamterName";

	/** The Constant CAPTCHA_SERVICE_ID_PARAM. */
	public static final String CAPTCHA_SERVICE_ID_PARAM = "captchaServiceId";

	/** The Constant FILTER_PROCESSES_URL_PARAM. */
	public static final String FILTER_PROCESSES_URL_PARAM = "filterProcessesUrl";

	/** The Constant FAILURE_URL_PARAM. */
	public static final String FAILURE_URL_PARAM = "failureUrl";

	/** The Constant AUTO_PASS_VALUE_PARAM. */
	public static final String AUTO_PASS_VALUE_PARAM = "autoPassValue";

	//默认值定义
	/** The Constant DEFAULT_FILTER_PROCESSES_URL. */
	public static final String DEFAULT_FILTER_PROCESSES_URL = "/login?jcaptcha=true";

	/** The Constant DEFAULT_CAPTCHA_SERVICE_ID. */
	public static final String DEFAULT_CAPTCHA_SERVICE_ID = "captchaService";

	/** The Constant DEFAULT_CAPTCHA_PARAMTER_NAME. */
	public static final String DEFAULT_CAPTCHA_PARAMTER_NAME = "j_captcha";

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(JCaptchaFilter.class);

	/** The failure url. */
	private String failureUrl;

	/** The captcha service id. */
	private String captchaServiceId = DEFAULT_CAPTCHA_SERVICE_ID;

	/** The captcha service. */
	private CaptchaService captchaService;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(final FilterConfig fConfig) throws ServletException {
		//initParameters(fConfig);
		initCaptchaService(fConfig);
	}

	/**
	 * 从ApplicatonContext获取CaptchaService实例.
	 *
	 * @param fConfig the f config
	 */
	private void initCaptchaService(final FilterConfig fConfig) {
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(fConfig.getServletContext());
		captchaService = (CaptchaService) context.getBean(captchaServiceId);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(final ServletRequest theRequest, final ServletResponse theResponse, final FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) theRequest;
		HttpServletResponse response = (HttpServletResponse) theResponse;
		genernateCaptchaImage(request, response);
	}

	/**
	 * 生成验证码图片.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void genernateCaptchaImage(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {

		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		ServletOutputStream out = response.getOutputStream();
		try {
			String captchaId = request.getSession(true).getId();
			BufferedImage challenge = (BufferedImage) captchaService.getChallengeForID(captchaId, request.getLocale());
			ImageIO.write(challenge, "jpg", out);
			out.flush();
		} catch (CaptchaServiceException e) {
			logger.error(e.getMessage(), e);
		} finally {
			out.close();
		}
	}

	/**
	 * 跳转到失败页面.
	 * 
	 * 可在子类进行扩展，比如在session中放入SpringSecurity的Exception.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void redirectFailureUrl(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException {
		response.sendRedirect(request.getContextPath() + failureUrl);
	}

}
