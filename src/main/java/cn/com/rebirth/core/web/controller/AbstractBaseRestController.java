/*
 * Copyright (c) 2005-2012 www.summall.com.cn All rights reserved
 * Info:summall-core AbstractBaseRestController.java 2012-2-11 16:24:50 l.xue.nong$$
 */
package cn.com.rebirth.core.web.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.com.rebirth.commons.exception.RebirthException;
import cn.com.rebirth.commons.utils.ReflectionUtils;

/**
 * 定义标准的rest方法以对应实体对象的操作,以达到统一rest的方法名称, 还可以避免子类需要重复编写@RequestMapping annotation.
 * 子类要实现某功能只需覆盖下面的方法即可. 注意: 覆盖时请使用@Override,以确保不会发生错误
 * <pre>
 * /userinfo                => index()
 * /userinfo/new            => _new()
 * /userinfo/{id}           => show()
 * /userinfo/{id}/edit      => edit()
 * /userinfo        POST    => create()
 * /userinfo/{id}   PUT     => update()
 * /userinfo/{id}   DELETE  => delete()
 * /userinfo        DELETE  => batchDelete()
 * </pre>
 * @param <T> the generic type
 * @param <PK> the generic type
 * @author l.xue.nong
 */
public abstract class AbstractBaseRestController<T, PK extends Serializable> extends AbstractBaseController {

	/** The logger. */
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/** The entity class. */
	protected Class<T> entityClass;

	/**
	 * Instantiates a new abstract base rest controller.
	 */
	public AbstractBaseRestController() {
		super();
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * Instantiates a new abstract base rest controller.
	 *
	 * @param entityClass the entity class
	 */
	public AbstractBaseRestController(Class<T> entityClass) {
		super();
		this.entityClass = entityClass;
	}

	/**
	 * binder用于bean属性的设置.
	 *
	 * @param binder the binder
	 * @throws Exception the exception
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) throws RebirthException {
		super.initBinder(binder);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
		binder.setDisallowedFields("id");
	}

	/**
	 * 增加了@ModelAttribute的方法可以在本controller方法调用前执行,可以存放一些共享变量,如枚举值,或是一些初始化操作.
	 *
	 * @param model the model
	 */
	@ModelAttribute
	public void init(Model model) {
		model.addAttribute("now", new java.sql.Timestamp(System.currentTimeMillis()));
	}

	/**
	 * Gets the model.
	 *
	 * @param id the id
	 * @return the model
	 */
	@ModelAttribute
	public T getModel(@RequestParam(required = false) PK id) {
		T entity = null;
		if (id != null) {
			entity = toModel(id);
		}
		if (entity == null) {
			try {
				entity = entityClass.newInstance();
			} catch (Exception e) {
			}
		}
		return entity;
	}

	/**
	 * To model.
	 *
	 * @param id the id
	 * @return the t
	 */
	protected T toModel(PK id) {
		return null;
	}

	/**
	 * Index.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @return the string
	 */
	@RequestMapping
	public String index(Model model, HttpServletRequest request, HttpServletResponse response) {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * _new.
	 *
	 * @param model the model
	 * @param request the request
	 * @param response the response
	 * @param entity the entity
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/new")
	public String _new(Model model, T entity, HttpServletRequest request, HttpServletResponse response)
			throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Show.
	 *
	 * @param model the model
	 * @param id the id
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/{id}")
	public String show(Model model, @PathVariable PK id, HttpServletRequest request, HttpServletResponse response)
			throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Edits the.
	 *
	 * @param model the model
	 * @param id the id
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/{id}/edit")
	public String edit(Model model, @PathVariable PK id, HttpServletRequest request, HttpServletResponse response)
			throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Creates the.
	 *
	 * @param model the model
	 * @param entity the entity
	 * @param errors the errors
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	public String create(Model model, @Valid T entity, BindingResult errors, HttpServletRequest request,
			HttpServletResponse response) throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Update.
	 *
	 * @param model the model
	 * @param id the id
	 * @param entity the entity
	 * @param errors the errors
	 * @param request the request
	 * @param response the response
	 * @return the string
	 * @throws Exception the exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String update(Model model, @PathVariable PK id, @Valid T entity, BindingResult errors,
			HttpServletRequest request, HttpServletResponse response) throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Delete.
	 *
	 * @param model the model
	 * @param id the id
	 * @return the string
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(Model model, @PathVariable PK id, T entity, HttpServletRequest request,
			HttpServletResponse response) throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}

	/**
	 * Batch delete.
	 *
	 * @param model the model
	 * @param items the items
	 * @return the string
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public String batchDelete(Model model, @RequestParam("items") PK[] items, HttpServletRequest request,
			HttpServletResponse response) throws RebirthException {
		throw new UnsupportedOperationException("not yet implement");
	}
}
