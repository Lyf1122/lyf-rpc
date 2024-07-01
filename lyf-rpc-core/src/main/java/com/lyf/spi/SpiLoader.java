package com.lyf.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.lyf.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpiLoader {
  // interfaceName -> <key, implClass>
  private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
  // cache
  private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();
  // System Spi Content
  private static final String SYSTEM_SPI_CONTENT = "META-INF/rpc/system/";
  // Custom Spi Content
  private static final String CUSTOM_SPI_CONTENT = "META-INF/rpc/custom/";
  // Scan content
  private static final String[] SCAN_DIRS = new String[]{SYSTEM_SPI_CONTENT, CUSTOM_SPI_CONTENT};
  // dramatic loading classes list
  private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

  public static void loadAll() {
    log.info("Loading all the spi for serializer");
    for (Class<?> clazz : LOAD_CLASS_LIST) {
      load(clazz);
    }
  }

  /**
   * 加载某个类型
   * @param loadClass
   * @return
   */
  public static Map<String, Class<?>> load(Class<?> loadClass) {
    log.info("Loading type of {} SPI", loadClass.getName());
    Map<String, Class<?>> keyClassMap = new HashMap<>();
    for (String scanDir : SCAN_DIRS) {
      List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
      // read files in resources
      for (URL resource : resources) {
        try {
          InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
          BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
          String line;
          while ((line = bufferedReader.readLine()) != null) {
            String[] strings = line.split("=");
            if (strings.length > 1) {
              String key = strings[0];
              String className = strings[1];
              keyClassMap.put(key, Class.forName(className));
            }
          }
        } catch (Exception e) {
          log.error("load spi error", e);
        }
      }
    }

    loaderMap.put(loadClass.getName(), keyClassMap);
    return keyClassMap;
  }

  /**
   * 根据 key 获取实例
   * @param clazz
   * @param key
   * @return
   * @param <T>
   */
  public static <T> T getInstance(Class<T> clazz, String key) {
    String clazzName = clazz.getName();
    Map<String, Class<?>> keyClassMap = loaderMap.get(clazzName);
    if (keyClassMap == null) {
      throw new RuntimeException("No spi found for " + clazzName);
    }
    if (!keyClassMap.containsKey(key)) {
      throw new RuntimeException("No spi found for " + clazzName + " with key " + key);
    }
    Class<?> implClass = keyClassMap.get(key);
    if (instanceCache.containsKey(implClass.getName())) {
      return (T) instanceCache.get(implClass.getName());
    } else {
      try {
        T instance = (T) implClass.newInstance();
        instanceCache.put(implClass.getName(), instance);
        return instance;
      } catch (Exception e) {
        String errorMsg = "create instance error" + implClass;
        throw new RuntimeException(errorMsg, e);
      }
    }
  }

}
