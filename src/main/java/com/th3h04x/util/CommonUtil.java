package com.th3h04x.util;

import burp.api.montoya.utilities.RandomUtils;

import java.util.Random;
import java.util.random.RandomGenerator;

public class CommonUtil {

  public static int countSlashes(String s) {
    if(s.charAt(s.length()-1) == '/'){
      s = s.substring(0, s.length()-1);
    }

    return countOccurences(s, '/');
  }

  public static int countOccurences(String s, char countMe){
    return Math.toIntExact(s.chars().filter(c -> c == countMe).count());
  }

  public static String generateRandomStr(int count){
    return new Random().ints(count, 'a', 'z' + 1)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
