package com.jayshawn.weixin.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Cac {
	public static void main(String[] args) throws Exception {
		FileInputStream in = new FileInputStream("C:\\Users\\458\\Desktop\\new.txt");
		Scanner scanner = new Scanner(in);
		double sum = 0;
		int count = 0;
		double average = 0;
		while (scanner.hasNextLine()) {
			String temp = scanner.nextLine();
			if (!temp.equals("")) {
				sum += (temp.charAt(4) - 48) * 10 + (temp.charAt(5) - 48);
				count++;
			}
		}
		average = sum / count;
		System.out.println("sum:" + sum + "\n" + "count:" + count + "\n" + "average:" + average);
	}
}
