package com.jayshawn.weixin.util;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;

import com.jayshawn.weixin.jssdk.JSConfig;

import net.sf.json.JSONObject;

/**
 * 
 * @author Jayshawn
 * @date 2017年9月20日 下午11:06:09
 */
public class CheckUtil {
	
	private static final String TOKEN = "jayshawn";
	

	public static boolean checkSignature(String signature, String timestamp, String nonce){
		String[] arr = new String[]{TOKEN,timestamp,nonce};
		Arrays.sort(arr);
		StringBuffer content = new StringBuffer();
		for(String s: arr)
			content.append(s);
		String temp = DigestUtils.shaHex(content.toString());
		return temp.equals(signature);
	}
	
}
