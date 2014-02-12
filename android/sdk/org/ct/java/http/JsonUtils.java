/**
 * @file JsonUtils.java 
 * @package com.android.ct.comic.utils
 * @create 2013-4-17
 * @author Chuck 
 * @email chucktsao@126.com
 * @description 
 */
package org.ct.java.http;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ct.java.annotation.AttrInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public final class JsonUtils {

	private static final String TAG = "JsonUtils";

	public static final String[] getStringArray(JSONArray array, String key) {
		String[] valueArray = new String[array.length()];
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.opt(i);
			try {
				String id = obj.getString(key);
				valueArray[i] = id;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return valueArray;
	}

	public static final Object[] getArray(JSONArray array, String key) {
		Object[] valueArray = new Object[array.length()];
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = (JSONObject) array.opt(i);
			try {
				Object item = obj.get(key);
				valueArray[i] = item;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return valueArray;
	}

	/**
	 * create request map
	 * @param request
	 * @return
	 */
	public static HashMap<String, Object> getRequestMap(Object request) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Field[] fields = request.getClass().getDeclaredFields();

		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				AttrInject attrInject = field.getAnnotation(AttrInject.class);
				if (attrInject != null) {
					try {
						field.setAccessible(true);
						map.put(attrInject.name(), field.get(request));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return map;
	}

	/**
	 * decode jsonObject to response
	 * the father of response attr can not be private
	 * @param jsonObject
	 * @param response 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessExceptio
	 */
	public static void decodeReponse(JSONObject jsonObject, Object response) throws IllegalArgumentException, IllegalAccessException {

		if (null == jsonObject) {
			return;
		}

		ArrayList<Field> fieldList = new ArrayList<Field>();
		Class<?> tempClass = response.getClass();

		// get gields form super class

		while (tempClass != Object.class) {
			fieldList.addAll(java.util.Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass();
		}

		if (fieldList.size() > 0) {
			for (Field field : fieldList) {
				AttrInject attrInject = field.getAnnotation(AttrInject.class);
				if (attrInject != null) {
					field.setAccessible(true);

					Class<?> elementClass = field.getType();

					boolean isArray = elementClass.isArray();
					boolean isList = List.class.isAssignableFrom(elementClass);

					Log.d(TAG, elementClass.toString() + attrInject.name());

					if (isArray || isList) {

						JSONArray jsonArray = null;
						try {
							jsonArray = jsonObject.getJSONArray(attrInject.name());
						} catch (JSONException e) {
							e.printStackTrace();
							continue;
						}
						if (null == jsonArray) {
							continue;
						}

						int len = jsonArray.length();

						if (len <= 0) {
							continue;
						}
						if (isArray) {
							field.set(response, getArray(elementClass, jsonArray, len));
						} else if (isList) {
							field.set(response, getList(field, jsonArray, len));
						}

					} else {
						String value = null;
						try {
							value = jsonObject.getString(attrInject.name()).trim();
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Object tmp = null;
						if (null != value && !"NULL".equals(value) && !"null".equals(value)) {// "NULL"
																								// and
																								// "null"
																								// is
																								// null
							tmp = getObject(elementClass, value, jsonObject, attrInject.name());
						}
						if (null != tmp) {
							field.set(response, tmp);
						}
					}
				}
			}
		}

	}

	public static Object decodeJsonArrayByTag(Class<?> elementClass, JSONObject jsonObject, String tag) throws IllegalArgumentException,
			IllegalAccessException {
		JSONArray jsonArray = null;
		try {
			jsonArray = jsonObject.getJSONArray(tag);
		} catch (JSONException e) {
			e.printStackTrace();

		}
		if (null == jsonArray) {
			return null;
		}

		return decodeJsonArray(elementClass, jsonArray);

	}

	/**
	 * get data form JSONArray
	 * @param elementClass
	 * @param jsonArray
	 * @param len
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object decodeJsonArray(Class<?> elementClass, JSONArray jsonArray) throws IllegalArgumentException, IllegalAccessException {

		int len = jsonArray.length();
		Object array = Array.newInstance(elementClass, len);

		for (int i = 0; i < len; i++) {
			String value = null;
			try {
				value = jsonArray.getString(i).trim();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				value = null;
			} catch (JSONException e) {
				e.printStackTrace();
				value = null;
			}
			Object tmp = null;
			if (null != value) {

				tmp = getObject(elementClass, value, jsonArray, i);

			}
			if (null != tmp) {
				Array.set(array, i, tmp);
			}
		}
		return array;
	}

	/**
	 * get data form JSONArray
	 * @param elementClass
	 * @param jsonArray
	 * @param len
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static Object getArray(Class<?> elementClass, JSONArray jsonArray, int len) throws IllegalArgumentException, IllegalAccessException {

		Class<?> componentType = elementClass.getComponentType();

		Object array = Array.newInstance(componentType, len);

		for (int i = 0; i < len; i++) {
			String value = null;
			try {
				value = jsonArray.getString(i).trim();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				value = null;
			} catch (JSONException e) {
				e.printStackTrace();
				value = null;
			}
			Object tmp = null;
			if (null != value) {

				tmp = getObject(componentType, value, jsonArray, i);

			}
			if (null != tmp) {
				Array.set(array, i, tmp);
			}
		}
		return array;
	}

	/**
	 * get List from JSONArray
	 * 
	 * @param elementClass
	 * @param jsonArray
	 * @param len
	 * @return
	 */
	private static List<Object> getList(Field field, JSONArray jsonArray, int len) throws IllegalAccessException, IllegalArgumentException {
		List<Object> list = new ArrayList<Object>();

		Type type = field.getGenericType();
		// field.getType().getComponentType();

		ParameterizedType typeP = null;

		if (type instanceof ParameterizedType) {
			typeP = (ParameterizedType) type;
		} else {
			return null;
		}

		Class<?> componentType = (Class<?>) typeP.getActualTypeArguments()[0];

		for (int i = 0; i < len; i++) {

			String value = null;
			try {
				value = jsonArray.getString(i).trim();
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Object tmp = null;
			if (null != value) {
				tmp = getObject(componentType, value, jsonArray, i);

			}
			if (null != tmp) {
				list.add(tmp);
			}

		}
		return list;
	}

	private static Object getObject(Class<?> componentType, String value, JSONObject jsonObject, String name) throws IllegalAccessException,
			IllegalArgumentException {
		return getObject(componentType, value, jsonObject, name, null, 0);
	}

	private static Object getObject(Class<?> componentType, String value, JSONArray jsonArray, int index) throws IllegalAccessException,
			IllegalArgumentException {
		return getObject(componentType, value, null, null, jsonArray, index);
	}

	private static Object getObject(Class<?> componentType, String value, JSONObject jsonObject, String name, JSONArray jsonArray, int index)
			throws IllegalAccessException, IllegalArgumentException {
		Object tmp = null;
		try {
			if (null != value) {
				if (componentType == String.class)
					tmp = value;
				else if (componentType == Boolean.class || componentType == boolean.class)
					tmp = value.equals("1") ? true : Boolean.parseBoolean(value);
				else if (componentType == Byte.class || componentType == byte.class)
					tmp = Byte.parseByte(value);
				else if (componentType == Character.class || componentType == char.class)
					tmp = value.charAt(0);
				else if (componentType == Short.class || componentType == short.class)
					tmp = Short.parseShort(value);
				else if (componentType == Integer.class || componentType == int.class)
					tmp = Integer.parseInt(value);
				else if (componentType == Long.class || componentType == long.class)
					tmp = Long.parseLong(value);
				else if (componentType == Float.class || componentType == float.class)
					tmp = Float.parseFloat(value);
				else if (componentType == Double.class || componentType == double.class)
					tmp = Double.parseDouble(value);
				else if (null != jsonObject) {
					tmp = componentType.newInstance();
					decodeReponse(jsonObject.getJSONObject(name), tmp);
				} else if (null != jsonArray) {
					tmp = componentType.newInstance();
					decodeReponse(jsonArray.getJSONObject(index), tmp);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tmp;
	}

}
