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
import com.kovacs.database.objects.UserNote;
import com.kovacs.tools.Sanitizers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.Map;

public class CheckNotes extends Command {
    public CheckNotes() {
        this.name = "CheckNotes";
        this.aliases = new String[]{"notes", "note"};
        this.children = new Command[]{new AddNote(), new DelNote()};
    }

    //notes @userToCheck @userWhoTookNotes
    //notes 12323434 9879789
    @Override
    protected void execute(CommandEvent event) {
        UserNote userNote = UserNote.findUserNote(event);
        if(userNote == null){
            event.reply("Either there were no notes on the provided user, or you failed to provide a valid user.");
            return;
        }
        String[] extractedIDs = Sanitizers.extractIDs(event.getArgs());
        Map<String, String> allNotes = userNote.getNotes();
        User user = event.getJDA().retrieveUserById(userNote.getUserID()).complete();
        EmbedBuilder builder = new EmbedBuilder();
        builder.setThumbnail(user.getAvatarUrl()).setAuthor(user.getAsTag() + " (" + user.getId() + ")", null, user.getAvatarUrl());

        if(extractedIDs.length == 2){
            if(allNotes.containsKey(extractedIDs[1])){
                User noteTaker = event.getJDA().retrieveUserById(extractedIDs[1]).complete();
                String note = allNotes.get(extractedIDs[1]);
                event.reply(new EmbedBuilder().addField("Complete note", "Created by: " + noteTaker.getAsTag() + " (" + extractedIDs[1] + ")", false)
                .setDescription(note).build()
                );
            } else {
                event.reply("There were no notes taken by the specified user!");
            }
        } else {

            int loopCount = 0;
            for (Map.Entry<String, String> entry : allNotes.entrySet()) {
                String noteTakerID = entry.getKey();
                String note = entry.getValue().substring(0, Math.min(entry.getValue().length(), 197));
                if(entry.getValue().length() > 197){
                    note = note + "...";
                }
                User noteTaker = event.getJDA().retrieveUserById(noteTakerID).complete();
                builder.addField(noteTaker.getAsTag() + " (" + noteTakerID + ")", note, loopCount % 2 == 0);
                loopCount++;
            }
            event.reply(builder.build());
        }


    }
}
