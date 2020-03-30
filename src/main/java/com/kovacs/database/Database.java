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

import java.net.UnknownHostException;
import java.util.Collection;

public class Database {
    private static MongoClient client;

    public static void connect(String uri) throws UnknownHostException {
        client = new MongoClient(new MongoClientURI(uri));
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

    //these unchecked casts make me nervous. is there a way to turn a bsonArray into a collection?
    public static GuildConfig dbObjectToConfig(DBObject object){
        return new GuildConfig().setGuildID((Long) object.get("_id"))
                .setPrefix((String) object.get("prefix"))
                .setWhitelistedRoles((Collection<Long>) object.get("whitelistedRoles"))
                .setWhitelistedUsers((Collection<Long>) object.get("whitelistedUsers"))
                .setSudoRoles((Collection<Long>) object.get("sudoRoles"))
                .setSudoUsers((Collection<Long>) object.get("sudoUsers"))
                .setBOS((Collection<String>) object.get("BOS"))
                .setMOS((Collection<String>) object.get("MOS"))
                .setDOS((Collection<String>) object.get("DOS"))
                .setEnabledAutoMod((Collection<String>) object.get("enabledAutoMod"))
                .setWhitelistedInvites((Collection<String>) object.get("whitelistedInvites"))
                .setInviteName((String) object.get("inviteName"))
                .setFallbackName((String) object.get("fallbackName"))
                .setAuditChannel((Long) object.get("auditChannel"))
                .setMutedRole((Long) object.get("mutedRole"))
                .setDuplicateThreshold((Integer) object.get("duplicateThreshold"))
                ;
    }

}
