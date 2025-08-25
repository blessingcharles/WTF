package com.th3h04x.db;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemory {

  // used to track seen requests
  public static Set<String> SEEN_REQUESTS = new HashSet<>();
  // contain target scope
  public static Set<String> IN_SCOPE = new HashSet<>();
  // url paths which are non cacheable
  public static Set<String> NON_CACHEABLE_PATH = new HashSet<>();
  // url paths which are cacheable
  public static Set<String> CACHEABLE_PATH = new HashSet<>();
  //seen static directories
  public static Set<String> STATIC_DIR = new HashSet<>();
  // query parameters
  public static Set<String> QUERY_PARAMETERS = new HashSet<>();

}
