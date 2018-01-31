package com.hp.dao.model;

public class FailInfoDo {
	private String url;//链接
	private String createTime;
	private String failInfo;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getFailInfo() {
		return failInfo;
	}
	public void setFailInfo(String failInfo) {
		this.failInfo = failInfo;
	}
	@Override
	public String toString() {
		return "FailInfoDo [url=" + url + ", createTime=" + createTime + ", failInfo=" + failInfo + "]";
	}
	
}
