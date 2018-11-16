package com.eriklievaart.osgi.toolkit.impl;

import com.eriklievaart.toolkit.lang.api.str.Str;

public class OsgiFilter {

	public static String byType(Class<?> type) {
		return Str.sub("(objectclass=$)", type.getName());
	}
}
