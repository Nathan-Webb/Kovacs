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
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.io.FileUtils;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Config {
    final static Logger logger = LoggerFactory.getLogger(Config.class);

    public static Cache<String, List<String>> onSightCache = new Cache2kBuilder<String, List<String>>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(Config::getList)
            .build();
    public static void reload() throws IOException {
        Kovacs.config  = open();
    }

    public static void reload(JSONObject jsonObject) throws IOException {
        Kovacs.config  = jsonObject;
    }
    public static JSONObject open() throws IOException{
        return new JSONObject(FileUtils.readFileToString(new File("config.json"), "UTF-8"));
    }

    public static void write(JSONObject json) throws IOException {
        FileUtils.writeStringToFile(new File("config.json"), json.toString(4), "UTF-8");
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


    public static boolean isSudo(Member member){
        return arrayContains("sudo", member.getId()) || getString("root").equals(member.getId());
    }



    public static boolean canUseBot(Member member){
        return !cantUseBot(member);
    }
    public static boolean cantUseBot(Member member){
        String authorID = member.getId();
        if(!Kovacs.config.getJSONArray("sudo").toList().contains(authorID) && !Kovacs.config.getString("root").equals(authorID)){ //user isn't a bot owner
            if(!Kovacs.config.getJSONArray("whitelistedUsers").toList().contains(authorID)){ //user isn't whitelisted
                if(member.getRoles().stream()
                        .noneMatch(role -> Kovacs.config.getJSONArray("sudoRoles").toList().contains(role.getId()))){ //none of the users roles are sudo
                    return member.getRoles().stream()
                            .noneMatch(role -> Kovacs.config.getJSONArray("whitelistedRoles").toList().contains(role.getId())); //are any of the roles whitelisted?
                }

            }
        }
        return false;
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

