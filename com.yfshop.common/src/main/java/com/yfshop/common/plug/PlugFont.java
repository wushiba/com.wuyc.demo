package com.yfshop.common.plug;

public enum PlugFont {
	Fangsong("仿宋"),Heiti("黑体"),Kaiti("楷体"),Songti("宋体"),Yahei("微软雅黑");
	
	private String name;
	
	private PlugFont(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
