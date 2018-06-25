package com.hp.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Configuration
public class RedisConfig {
	
	@Value("${redis.host}")
	private String host;
    
	@Value("${redis.port}")
    private String port;
    
	@Value("${redis.password}")
    private String password;
    
	@Value("${redis.timeout}")
    private int timeout;
    
	@Value("${redis.pool.max-active}")
    private int maxActive;
    
	@Value("${redis.pool.max-idle}")
    private int maxIdle;
    
	@Value("${redis.pool.max-wait:100}")
    private int maxWait;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}
	public RedisConfig() {
		
	}
	

}
