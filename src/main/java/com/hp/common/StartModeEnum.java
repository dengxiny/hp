package com.hp.common;

public enum StartModeEnum {
	COMMON_AUTO("01"),COMMON_LIST("02"),COMMON_DETAIL("03"),COMMON_ERROR("04"),
	AGENT("11"),AGENT_LIST("12"),AGENT_DETAIL("13"),AGENT_ERROR("14"),
	WEBMAGIC_AUTO("21");
	
	private String desc;

    /**
     * 私有构造,防止被外部调用
     * @param desc
     */
    private StartModeEnum(String desc){
        this.desc=desc;
    }

	public String getDesc() {
		return desc;
	}
    
}
