package com.tort.EmpireBattles.Files;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class EmpireUtils {
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }


        return result;
    }

    public static String firstLetterCap(String s){
        String result = s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
        return result;
    }



}