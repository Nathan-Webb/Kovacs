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

import java.util.HashMap;

public class UserNote {
    private final String userID;
    private final String guildID;
    private HashMap<String, String> notes = new HashMap<>();

    public UserNote(String userID, String guildID) {
        this.userID = userID;
        this.guildID = guildID;
    }

    public UserNote(String userID, String guildID, HashMap<String, String> notes) {
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

    public HashMap<String, String> getNotes() {
        return notes;
    }

    public void setNote(String userID, String note){
        notes.put(userID, note);
    }


}
