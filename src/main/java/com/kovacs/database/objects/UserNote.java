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

package com.kovacs.database.objects;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.database.Database;
import com.kovacs.tools.Sanitizers;
import com.mongodb.lang.Nullable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserNote {
    private final String userID;
    private final String guildID;
    private final Map<String, String> notes;
    //id of the mod - their note

    public static UserNote get(Member member){
        return Database.userNoteCache.get(getTag(member));
    }

    public static UserNote get(String guildID, String memberID){
        return Database.userNoteCache.get(guildID + "-" + memberID);
    }

    @Nullable
    public static UserNote findUserNote(CommandEvent event){
        UserNote userNote;
        if(event.getMessage().getMentionedRoles().size() > 0){
            userNote = UserNote.get(event.getMessage().getMentionedMembers().get(0));
        } else {
            try {
                String[] ids = Sanitizers.extractIDs(event.getArgs());
                return findUserNote(event.getGuild().getId(), ids[0]);
            } catch (IndexOutOfBoundsException e){
                return null;
            }
        }
        return userNote;
    }
    @Nullable
    public static UserNote findUserNote(String guildID, String userID){
        UserNote userNote;
        if(userID.matches("\\d+")) { // assume user id
            if(Database.notesExist(guildID, userID)){
                userNote = UserNote.get(guildID, userID);
            } else {
                return null;
            }
        } else {
            return null;
        }
        return userNote;
    }

    public static String getTag(Member m){
        return m.getGuild().getId() + "-" + m.getId();
    }

    public String getTag(){
        return getGuildID() + "-" + getUserID();
    }


    public UserNote(String guildID, String userID) {
        this.userID = userID;
        this.guildID = guildID;
        notes = new HashMap<>();
    }

    public UserNote(String guildID, String userID, Map<String, String> notes) {
        this.userID = userID;
        this.guildID = guildID;
        this.notes = notes;
    }

    public String getUserID() {
        return userID;
    }

    public String getGuildID() {
        return guildID;
    }

    public Map<String, String> getNotes() {
        return notes;
    }

    public void setNote(String userID, String note){
        notes.put(userID, note);
    }


}
