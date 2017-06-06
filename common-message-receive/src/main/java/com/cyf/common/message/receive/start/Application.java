package com.cyf.common.message.receive.start;

public class Application {

	public static void main(String[] args) {
		String[] customArgs = new String[]{"javaconfig"};//指定启用 JavaConfigContainer
		com.alibaba.dubbo.container.Main.main(customArgs);
	}
}
