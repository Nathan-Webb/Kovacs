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

import com.kovacs.database.objects.GuildConfig;
import com.kovacs.database.objects.UserNote;
import com.mongodb.*;
import net.dv8tion.jda.api.entities.Member;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Database {
    private static MongoClient client;

    final static Logger logger = LoggerFactory.getLogger(Database.class);

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

    public static Cache<String, UserNote> userNoteCache = new Cache2kBuilder<String, UserNote>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(id -> {
                DBCursor cursor = getCollectionNotes().find(BasicDBObjectBuilder.start("_id", id).get());
                if(cursor.hasNext()){ //send it through

                    DBObject object = cursor.next();
                    return dbObjectToUserNote(object);
                } else { //no notes for that one user for that one server, build one with defaults
                    String[] split = id.split("-");
                    UserNote note = new UserNote(split[0], split[1]);
                    getCollectionNotes().insert(userNoteToDBObject(note)); //insert new config
                    return note;

                }
            })
            .build();

    public static void connect(String uri) {
        client = new MongoClient(new MongoClientURI(uri));
    }

    public static DBCollection getCollectionConfig(){
        return client.getDB("kovacs").getCollection("serverConfig");
    }

    public static DBCollection getCollectionNotes(){
        return client.getDB("kovacs").getCollection("userNotes");
    }

    public static boolean notesExist(String guildID, String memberID){
        return getCollectionNotes().find(BasicDBObjectBuilder.start("_id", guildID + "-" + memberID).get()).hasNext();
    }


    public static void updateConfig(String serverID, DBObject toChange){
        DBObject config = configToDBObject(configCache.get(serverID));
        config.putAll(toChange);
        getCollectionConfig().update(new BasicDBObject("_id", serverID), new BasicDBObject().append("$set", toChange));
        configCache.put(serverID, dbObjectToConfig(config));

    }

    public static void updateNotes(Member targetMember, Map<String, String> toChange){
        updateNotes(UserNote.getTag(targetMember), toChange);

    }

    public static void updateNotes(String tag, Map<String, String> toChange){
        if(toChange.size() == 0){ //no data - no point in having it in db
            wipeNotes(tag);
            userNoteCache.remove(tag);
        } else {
            DBObject basic = new BasicDBObject("notes", toChange);
            DBObject config = userNoteToDBObject(userNoteCache.get(tag));
            config.putAll(basic);
            getCollectionNotes().update(new BasicDBObject("_id", tag), new BasicDBObject().append("$set", basic));
            userNoteCache.put(tag, dbObjectToUserNote(config));
        }
    }

    public static void wipeNotes(String tag){
        getCollectionNotes().remove(new BasicDBObject("_id", tag));
    }



    public static MongoClient getClient(){
        return client;
    }


    public static DBObject userNoteToDBObject(UserNote note){
        return new BasicDBObjectBuilder().add("_id", note.getGuildID() + "-" + note.getUserID())
                .add("notes", note.getNotes()).get();
    }

    @SuppressWarnings("unchecked")
    public static UserNote dbObjectToUserNote(DBObject object){
        String guildID;
        String userID;
        String[] split = ((String) object.get("_id")).split("-");
        return new UserNote(split[0], split[1], (HashMap<String, String>) object.get("notes"));
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
                .add("duplicateThreshold", config.getDuplicateThreshold())
                .add("whitelistedChannels", config.getWhitelistedChannels()).get();
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
                .setDuplicateThreshold((Integer) object.get("duplicateThreshold"))
                .setWhitelistedChannels((ArrayList<String>) getOrNull(object, "whitelistedChannels", new ArrayList<>())); //this has to be the worst way to do this
    }

    private static Object getOrNull(DBObject object, String toGet, Object defaultObj){
        Object o = object.get(toGet);
        if(o == null){
            return defaultObj;
        }
        return o;
    }

    /*
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
     */


}
