package com.hp.service;

import java.util.List;

public interface TestService{
	List<String> getIP(String url);
	void f1();
	void testUrl(String url);
	void testUrl2(String url);
	String f2 (String proxyHost, String url);
	void telnetIP(List<String> proxyIpMap, String url);
	void executeIpPool(int len);
	void ipWork(String ipUrl);
}