package com.naeggeodo.util;


public class MyUtility {
	public static int getFileSizeInBase64StringWithKB(String base64String) {
		double stringLength = base64String.length() - "data:image/png;base64,".length();
		double sizeInBytes = 4 * Math.ceil((stringLength/3)) *0.5624896334383812;
		return (int) (sizeInBytes/1000);
	}
}
