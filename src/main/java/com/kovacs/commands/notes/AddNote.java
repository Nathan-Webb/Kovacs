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
import com.kovacs.Kovacs;
import com.kovacs.database.Database;
import com.kovacs.database.objects.UserNote;
import com.kovacs.tools.Sanitizers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AddNote extends Command {
    public AddNote() {
        this.name = "AddNote";
        this.aliases = new String[]{"add", "set"};
    }

    //max 25 active notes
    @Override
    protected void execute(CommandEvent event) {
        String[] extractedIDs = Sanitizers.extractIDs(event.getArgs());
        String userID;
        try{
            userID = extractedIDs[0];
        } catch (IndexOutOfBoundsException e){
            event.reply("You must provide a valid user!");
            return;
        }
        UserNote note = UserNote.get(event.getGuild().getId(), userID);
        if(note.getNotes().size() < 25){
            Kovacs.waiter.waitForEvent(MessageReceivedEvent.class,
                    check ->  check.getAuthor().equals(event.getMember().getUser()) && check.getChannel().equals(event.getChannel()) && !check.getMessage().equals(event.getMessage()),
                    response -> {
                        Map<String, String> map = note.getNotes();
                        map.put(event.getAuthor().getId(), response.getMessage().getContentRaw());
                        Database.updateNotes(note.getTag(), map);
                    },3, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long! Command failed"));
        } else {
            event.reply("You can only have a maximum of 25 active notes!");
        }
    }
}
