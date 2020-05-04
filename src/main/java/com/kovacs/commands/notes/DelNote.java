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

package com.kovacs.commands.notes;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.objects.UserNote;
import com.kovacs.tools.BotConfig;
import com.kovacs.tools.Sanitizers;

import java.util.HashMap;

public class DelNote extends Command {
    public DelNote() {
        this.name = "DelNote";
        this.aliases = new String[]{"remove", "delete", "del"};
    }

    //delete your own note from user, can also delete other notes if sudo
    @Override
    protected void execute(CommandEvent event) {
        UserNote userNote = UserNote.findUserNote(event);
        if(userNote == null){
            event.reply("Either there were no notes on the provided user, or you failed to provide a valid user.");
            return;
        }
        String[] extractedIDs = Sanitizers.extractIDs(event.getArgs());
        String idToRemove = event.getAuthor().getId();
        if(extractedIDs.length == 2 && ConfigTools.isSudo(event.getMember())){
            idToRemove = extractedIDs[1];
        }
        String oldValue = userNote.getNotes().remove(idToRemove);
        if(oldValue != null){
            Database.updateNotes(userNote.getTag(), userNote.getNotes());
            event.reply("Note deleted.");
        } else {
            event.reply("No associated notes were found.");
        }


    }
}
