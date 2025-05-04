package com.admin.exceptions;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class YamlMessageSource {

    private final Map<String, Object> yamlData;

    public YamlMessageSource(String yamlFileName) {
        Yaml yaml = new Yaml();
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream(yamlFileName)){
            yamlData = yaml.load(inputStream);
        }catch (Exception e){
            throw new RuntimeException("Error loading a Yaml file",e);
        }
    }

    public LinkedHashMap<String,Object> getMessages(String key){
        LinkedHashMap<String, LinkedHashMap<String,Object>> messages = (LinkedHashMap<String, LinkedHashMap<String, Object>>) yamlData.get("error-codes");
        if(messages.containsKey(key)){
            return messages.get(key);
        }
        return messages.get("default");
    }


}
