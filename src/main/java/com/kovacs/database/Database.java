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

package com.kovacs.database;

import com.mongodb.*;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Database {
    private static MongoClient client;

    public static Cache<String, GuildConfig> configCache = new Cache2kBuilder<String, GuildConfig>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(id -> {
                DBCursor cursor = getCollectionConfig().find(BasicDBObjectBuilder.start("_id", id).get());
                if(cursor.hasNext()){ //guild already has config, send it through
                    DBObject object = cursor.next();
                    return dbObjectToConfig(object);
                } else { //guild doesn't have config, build one with defaults
                    GuildConfig newConf = new GuildConfig(id);
                    getCollectionConfig().insert(configToDBObject(newConf)); //insert new config
                    return newConf;

                }
            })
            .build();

    public static void connect(String uri) throws UnknownHostException {
        client = new MongoClient(new MongoClientURI(uri));
    }

    public static DBCollection getCollectionConfig(){
        return client.getDB("Kovacs").getCollection("serverConfig");
    }


    public static void updateConfig(String serverID, DBObject toChange){
        getCollectionConfig().update(new BasicDBObject("_id", serverID), toChange);
    }

    public static MongoClient getClient(){
        return client;
    }



    //there has to be a better way to accomplish this
    public static DBObject configToDBObject(GuildConfig config){
        return new BasicDBObjectBuilder().add("_id", config.getGuildID())
                .add("prefix", config.getPrefix())
                .add("whitelistedRoles", config.getWhitelistedRoles())
                .add("whitelistedUsers", config.getWhitelistedUsers())
                .add("sudoRoles", config.getSudoRoles())
                .add("sudoUsers", config.getSudoUsers())
                .add("BOS", config.getBOS())
                .add("DOS", config.getBOS())
                .add("MOS", config.getMOS())
                .add("enabledAutoMod", config.getEnabledAutoMod())
                .add("whitelistedInvites", config.getWhitelistedInvites())
                .add("inviteName", config.getInviteName())
                .add("fallbackName", config.getFallbackName())
                .add("auditChannel", config.getAuditChannel())
                .add("mutedRole", config.getMutedRole())
                .add("duplicateThreshold", config.getDuplicateThreshold()).get();
    }

    @SuppressWarnings("unchecked")
    //these unchecked casts make me nervous. is there a way to turn an object into an ArrayList safely?
    public static GuildConfig dbObjectToConfig(DBObject object){
        return new GuildConfig((String) object.get("_id"))
                .setPrefix((String) object.get("prefix"))
                .setWhitelistedRoles((ArrayList<String>) object.get("whitelistedRoles"))
                .setWhitelistedUsers((ArrayList<String>) object.get("whitelistedUsers"))
                .setSudoRoles((ArrayList<String>) object.get("sudoRoles"))
                .setSudoUsers((ArrayList<String>) object.get("sudoUsers"))
                .setBOS((ArrayList<String>) object.get("BOS"))
                .setMOS((ArrayList<String>) object.get("MOS"))
                .setDOS((ArrayList<String>) object.get("DOS"))
                .setEnabledAutoMod((ArrayList<String>) object.get("enabledAutoMod"))
                .setWhitelistedInvites((ArrayList<String>) object.get("whitelistedInvites"))
                .setInviteName((String) object.get("inviteName"))
                .setFallbackName((String) object.get("fallbackName"))
                .setAuditChannel((String) object.get("auditChannel"))
                .setMutedRole((String) object.get("mutedRole"))
                .setDuplicateThreshold((Integer) object.get("duplicateThreshold"));
    }


}
