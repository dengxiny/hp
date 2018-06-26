package com.hp.dao.model;


public class IpWeight {

	private String address;
	private int weight=20;
	

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public IpWeight(String address) {
		super();
		this.address = address;
	}
	public IpWeight(String address, int weight) {
		super();
		this.address = address;
		this.weight = weight>=0?weight:0;
	}
	public void reduce(){
		this.weight = weight>=1?(weight-1):0;
	}
	public void reduce(int n){
		this.weight = weight>=n?(weight-n):0;
	}
	@Override
	public String toString() {
		return "IpWeight [address=" + address + ", weight=" + weight + "]";
	}
    /** 
     * set<User></>去重，重新如下两个方法hashCode、equals 
     */  
    @Override  
    public int hashCode(){  
        return address.hashCode();  
    }  
  
    @Override  
    public boolean equals(Object obj){  
        if(obj instanceof IpWeight){  
        	IpWeight ipWeight=(IpWeight)obj;  
            return address.equals(ipWeight.address);  
        }  
        return super.equals(obj);  
    }
}
