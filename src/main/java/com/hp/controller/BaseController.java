package com.hp.controller;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 直接跳转到页面
 */
@Controller
@RequestMapping("/page")
public class BaseController {
	/**
	 * 跳转
	 * 
	 * @param pageName
	 * @return
	 */
	@RequestMapping("/{pageName}")
	public String redirectPage(@PathVariable String pageName, HttpServletRequest request) {
		setParameters(request);
		return pageName;
	}

	/**
	 * 跳转
	 * 
	 * @param folder
	 * @param pageName
	 * @return
	 */
	@RequestMapping("/{folder}/{pageName}")
	public String redirectPage(@PathVariable String folder, @PathVariable String pageName, HttpServletRequest request) {
		setParameters(request);
		return folder + "/" + pageName;
	}

	/**
	 * 跳转
	 * 
	 * @param folder
	 * @param subfolder
	 * @param pageName
	 * @return
	 */
	@RequestMapping("/{folder}/{subfolder}/{pageName}")
	public String redirectSubJsp(@PathVariable String folder, @PathVariable String subfolder,
			@PathVariable String pageName, HttpServletRequest request) {
		setParameters(request);
		return folder + "/" + subfolder + "/" + pageName;
	}

	/**
	 * setParameter
	 * 
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	private void setParameters(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		boolean userExist = false;
		if (names != null) {
			while (names.hasMoreElements()) {
				String paramName = names.nextElement();
				request.setAttribute(paramName, request.getParameter(paramName));
			}
		}
	}
}
