package finance.tradista.core.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.springframework.util.ClassUtils;

import finance.tradista.core.common.exception.TradistaBusinessException;
import finance.tradista.core.common.exception.TradistaTechnicalException;
import finance.tradista.core.error.model.Error;
import finance.tradista.core.product.service.ProductBusinessDelegate;
import finance.tradista.core.trade.messaging.TradeEvent;
import finance.tradista.core.transfer.model.TransferManager;

/*
 * Copyright 2018 Olivier Asuncion
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    */

public final class TradistaUtil {

	private static Map<String, TransferManager<TradeEvent<?>>> transferManagersCache = new HashMap<String, TransferManager<TradeEvent<?>>>();

	@SuppressWarnings("unchecked")
	public static <T> List<Class<T>> getAllClassesByType(Class<T> type, String pckg) {
		List<Class<T>> classes = new ArrayList<Class<T>>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(type));
		for (BeanDefinition bd : scanner.findCandidateComponents(pckg)) {
			try {
				Class<T> klass = (Class<T>) Class.forName(bd.getBeanClassName());
				classes.add(klass);
			} catch (ClassNotFoundException cnfe) {
				throw new TradistaTechnicalException(cnfe);
			}

		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllInstancesByType(Class<T> type, String pckg) {
		List<T> instances = new ArrayList<T>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(type));
		for (BeanDefinition bd : scanner.findCandidateComponents(pckg)) {
			try {
				T instance = (T) TradistaUtil.getInstance(Class.forName(bd.getBeanClassName()));
				instances.add(instance);
			} catch (ClassNotFoundException cnfe) {
				throw new TradistaTechnicalException(cnfe);
			}
		}
		return instances;
	}

	public static Set<String> getAvailableNames(Class<?> klass, String pckg) {
		Set<String> names = new HashSet<String>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(klass));
		for (BeanDefinition bd : scanner.findCandidateComponents(pckg)) {
			String fullClassName = bd.getBeanClassName();
			names.add(fullClassName.substring(fullClassName.lastIndexOf(".") + 1));
		}
		return names;
	}

	public static Set<String> getAllErrorTypes() {
		Set<String> names = new HashSet<String>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(Error.class));
		for (BeanDefinition bd : scanner.findCandidateComponents("finance.tradista.**")) {
			String fullClassName = bd.getBeanClassName();
			if (!bd.isAbstract()) {
				names.add(fullClassName.substring(fullClassName.lastIndexOf(".") + 1));
			}
		}
		return names;
	}

	public static List<Class<?>> getAllClassesByRegex(String regex, String pckg) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(regex)));
		for (BeanDefinition bd : scanner.findCandidateComponents(pckg)) {
			try {
				Class<?> klass = (Class<?>) Class.forName(bd.getBeanClassName());
				classes.add(klass);
			} catch (ClassNotFoundException cnfe) {
				throw new TradistaTechnicalException(cnfe);
			}
		}

		return classes;
	}

	public static List<Class<?>> getAllClassesByTypeAndAnnotation(Class<?> type, Class<? extends Annotation> annotation,
			String pckg) {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AssignableTypeFilter(type));
		scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
		for (BeanDefinition bd : scanner.findCandidateComponents(pckg)) {
			try {
				Class<?> klass = Class.forName(bd.getBeanClassName());
				classes.add(klass);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return classes;
	}

	private static Class<?> getClassByProductTypeSubPackageAndName(String productType, String subPackage, String name)
			throws TradistaBusinessException {
		Class<?> _class = null;
		try {
			_class = Class.forName("finance.tradista." + new ProductBusinessDelegate().getProductFamily(productType)
					+ "." + productType.toLowerCase() + "." + subPackage + "." + name);
		} catch (ClassNotFoundException cnfe) {
			throw new TradistaTechnicalException(cnfe);
		}

		return _class;
	}

	@SuppressWarnings("unchecked")
	public static TransferManager<TradeEvent<?>> getTransferManager(String productType)
			throws TradistaBusinessException {

		if (transferManagersCache.containsKey(productType)) {
			return transferManagersCache.get(productType);
		} else {
			TransferManager<TradeEvent<?>> transferManager = (TransferManager<TradeEvent<?>>) TradistaUtil.getInstance(
					getClassByProductTypeSubPackageAndName(productType, "transfer", productType + "TransferManager"));
			transferManagersCache.put(productType, transferManager);
			return transferManager;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T callMethod(String fullClassName, Class<T> returnType, String methodName, Object... params)
			throws TradistaBusinessException {
		T toBeReturned = null;
		Class<?>[] klasses = new Class<?>[params.length];
		try {
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					klasses[i] = params[i].getClass();
				}
			}
			Class<?> klass = Class.forName(fullClassName);
			boolean found = false;
			for (Method method : klass.getMethods()) {
				if (!method.getName().equals(methodName)) {
					continue;
				}
				Class<?>[] parameterTypes = method.getParameterTypes();
				boolean matches = true;
				if (parameterTypes.length != params.length) {
					continue;
				}
				for (int i = 0; i < parameterTypes.length; i++) {
					// Using Spring's ClassUtils because we want primitive types to be also
					// considered
					if (!ClassUtils.isAssignable(parameterTypes[i], klasses[i])) {
						matches = false;
						break;
					}
				}
				if (matches) {
					toBeReturned = (T) method.invoke(TradistaUtil.getInstance(klass), params);
					found = true;
				}
			}
			if (!found) {
				throw new TradistaTechnicalException(
						String.format("%s method with %s parameters has not been found in %s class.", methodName,
								klasses, fullClassName));
			}
		} catch (ClassNotFoundException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			throw new TradistaTechnicalException(e);
		} catch (InvocationTargetException ite) {
			if (ite.getCause() instanceof TradistaBusinessException) {
				throw (TradistaBusinessException) ite.getCause();
			} else {
				throw new TradistaTechnicalException(ite.getCause().getMessage());
			}
		}
		return toBeReturned;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> type, String className) {
		try {
			return (T) getInstance(Class.forName(className));
		} catch (ClassNotFoundException cnfe) {
			throw new TradistaTechnicalException(
					String.format("Could not create instance of %s : %s", className, cnfe));
		}
	}

	public static <T> T getInstance(Class<T> type) {
		try {
			return (T) type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException
				| SecurityException e) {
			throw new TradistaTechnicalException(String.format("Could not create instance of %s : %s", type, e));
		} catch (InvocationTargetException ite) {
			throw new TradistaTechnicalException(
					String.format("Could not create instance of %s : %s", type, ite.getCause().getMessage()));
		}
	}

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException cnfe) {
			throw new TradistaTechnicalException(
					String.format("Could not create class from this name '%s' : %s", className, cnfe));
		}
	}

}