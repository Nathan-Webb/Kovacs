/*
 *    Copyright 2020 Nathan Webb
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.kovacs.tools;

import com.kovacs.Kovacs;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Config {
    final static Logger logger = LoggerFactory.getLogger(Config.class);


    public static void reload() throws IOException {
        Kovacs.config  = open();
    }

    public static void reload(JSONObject jsonObject) throws IOException {
        Kovacs.config  = jsonObject;
    }
    public static JSONObject open() throws IOException{
        return new JSONObject(Files.readString(Paths.get("config.json"), StandardCharsets.UTF_8));
    }

    public static void write(JSONObject json) throws IOException {
        Files.writeString(Paths.get("config.json"), json.toString(4));
    }
    public static void writeAndReload(JSONObject json) throws IOException {
        write(json);
        reload(json);
    }

    public static void addToList(String listName, String... listEntries) throws IOException{
        JSONObject config = open();
        JSONArray array = config.getJSONArray(listName);
        for (String s : listEntries) {
            if(!array.toString().contains(s)){
                array.put(s);
            }
        }
        config.put(listName, array);
        writeAndReload(config);
    }

    public static void removeFromList(String listName, String... listEntries) throws IOException {
        JSONObject config = open();
        List<Object> jsonArr = config.getJSONArray(listName).toList();
        for (String s : listEntries) {
            jsonArr.remove(s);
        }
        config.put(listName, new JSONArray(jsonArr));
        writeAndReload(config);
    }

    public static boolean arrayContains(String arrayName, String entry){
        return Kovacs.config.getJSONArray(arrayName).toList().contains(entry);
    }

    public static List<String> getList(String listName){
        List<String> list = new ArrayList<>();
        Kovacs.config.getJSONArray(listName).toList().forEach(entry -> list.add(entry.toString()));
        return list;
    }

    public static String getString(String key){
        return Kovacs.config.getString(key);
    }

    public static void setString(String key, String value) throws IOException{
        Kovacs.config.put(key, value);
        write(Kovacs.config);
    }

    public static void setInt(String key, int value) throws IOException{
        Kovacs.config.put(key, value);
        write(Kovacs.config);
    }

    public static int getInt(String key){
        return Kovacs.config.getInt(key);
    }
}

