package com.hp.common;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * fastjson工具类
 * 
 * @author 
 *
 */
public class JsonUtil {
	private static final SerializeConfig config;

	private static Logger logger = Logger.getLogger(JsonUtil.class);

	static {
		config = new SerializeConfig();
		config.put(java.util.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
		config.put(java.sql.Date.class, new JSONLibDataFormatSerializer()); // 使用和json-lib兼容的日期输出格式
	}

	private static final SerializerFeature[] features = { SerializerFeature.WriteMapNullValue, // 输出空置字段
			SerializerFeature.WriteNullListAsEmpty, // list字段如果为null，输出为[]，而不是null
			SerializerFeature.WriteNullNumberAsZero, // 数值字段如果为null，输出为0，而不是null
			SerializerFeature.WriteNullBooleanAsFalse, // Boolean字段如果为null，输出为false，而不是null
			SerializerFeature.WriteNullStringAsEmpty // 字符类型字段如果为null，输出为""，而不是null
	};

	public static String toJSONString(Object object) {
		return JSONObject.toJSONString(object, config, features);
	}

	public static String toJSONNoFeatures(Object object) {
		return JSONObject.toJSONString(object, config);
	}

	public static Object toBean(String text) {
		return JSONObject.parse(text);
	}

	public static <T> T toBean(String text, Class<T> clazz) {
		return JSONObject.parseObject(text, clazz);
	}

	// 转换为数组
	public static <T> Object[] toArray(String text) {
		return toArray(text, null);
	}

	// 转换为数组
	public static <T> Object[] toArray(String text, Class<T> clazz) {
		return JSONObject.parseArray(text, clazz).toArray();
	}

	// 转换为List
	public static <T> List<T> toList(String text, Class<T> clazz) {
		return JSONObject.parseArray(text, clazz);
	}
	
	// List转换为字符串
	public static <T> String toList(List<T> list) {
		return JSONObject.toJSONString(list);
	}

	/**
	 * 将string转化为序列化的json字符串
	 * 
	 * @param keyvalue
	 * @return
	 */
	public static Object textToJson(String text) {
		return JSONObject.parse(text);
	}

	/**
	 * json字符串转化为map
	 * 
	 * @param s
	 * @return
	 */
	public static Map stringToCollect(String s) {
		return JSONObject.parseObject(s);
	}

	/**
	 * 将map转化为string
	 * 
	 * @param m
	 * @return
	 */
	public static String collectToString(Map m) {
		return JSONObject.toJSONString(m);
	}

}
