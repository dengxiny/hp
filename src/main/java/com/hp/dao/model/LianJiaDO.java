package com.hp.dao.model;

public class LianJiaDO {
private String url;//链接
private String name;//
private String createTime;
private String buildTime;
private Float totalPrice;//总
private Float unitPrice;//单
private Float size;//大小
private String room;//
private String dirType;//
private String address;
private String span;
private String updateTime;//
private String firstArea;
private String secondArea;
private String type;
private String floor;
private String houseType;
private String base;
private String tradeBase;
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getCreateTime() {
	return createTime;
}
public void setCreateTime(String createTime) {
	this.createTime = createTime;
}
public String getBuildTime() {
	return buildTime;
}
public void setBuildTime(String buildTime) {
	this.buildTime = buildTime;
}

public Float getTotalPrice() {
	return totalPrice;
}
public void setTotalPrice(Float totalPrice) {
	this.totalPrice = totalPrice;
}
public Float getUnitPrice() {
	return unitPrice;
}
public void setUnitPrice(Float unitPrice) {
	this.unitPrice = unitPrice;
}
public Float getSize() {
	return size;
}
public void setSize(Float size) {
	this.size = size;
}
public String getRoom() {
	return room;
}
public void setRoom(String room) {
	this.room = room;
}
public String getDirType() {
	return dirType;
}
public void setDirType(String dirType) {
	this.dirType = dirType;
}
public String getAddress() {
	return address;
}
public void setAddress(String address) {
	this.address = address;
}
public String getSpan() {
	return span;
}
public void setSpan(String span) {
	this.span = span;
}
public String getUpdateTime() {
	return updateTime;
}
public void setUpdateTime(String updateTime) {
	this.updateTime = updateTime;
}
public String getFirstArea() {
	return firstArea;
}
public void setFirstArea(String firstArea) {
	this.firstArea = firstArea;
}
public String getSecondArea() {
	return secondArea;
}
public void setSecondArea(String secondArea) {
	this.secondArea = secondArea;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getFloor() {
	return floor;
}
public void setFloor(String floor) {
	this.floor = floor;
}
public String getHouseType() {
	return houseType;
}
public void setHouseType(String houseType) {
	this.houseType = houseType;
}
public String getBase() {
	return base;
}
public void setBase(String base) {
	this.base = base;
}
public String getTradeBase() {
	return tradeBase;
}
public void setTradeBase(String tradeBase) {
	this.tradeBase = tradeBase;
}
@Override
public String toString() {
	return "LianJiaDO [url=" + url + ", name=" + name + ", createTime=" + createTime + ", buildTime=" + buildTime
			+ ", totalPrice=" + totalPrice + ", unitPrice=" + unitPrice + ", size=" + size + ", room=" + room
			+ ", dirType=" + dirType + ", address=" + address + ", span=" + span + ", updateTime=" + updateTime
			+ ", firstArea=" + firstArea + ", secondArea=" + secondArea + ", type=" + type + ", floor=" + floor
			+ ", houseType=" + houseType + ", base=" + base + ", tradeBase=" + tradeBase + "]";
}






}
