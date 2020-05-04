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
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.objects.GuildConfig;
import com.kovacs.database.objects.UserNote;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Cache;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class WipeNote extends Command {
    public WipeNote() {
        this.name = "WipeNote";
        this.aliases = new String[]{"wipe"};
    }

    @Override
    protected void execute(CommandEvent event) {
        //wipes all notes from user in this server
        if(ConfigTools.isSudo(event.getMember())) {
            UserNote userNote = UserNote.findUserNote(event);
            if(userNote == null){
                event.reply("Either there were no notes on the provided user, or you failed to provide a valid user.");
                return;
            }

            event.reply("Are you __sure__ you want to do this? __All__ notes will be deleted and will not be recoverable. [yes/No]");
            Kovacs.waiter.waitForEvent(MessageReceivedEvent.class,
                    check ->  check.getAuthor().equals(event.getMember().getUser()) && check.getChannel().equals(event.getChannel()) && !check.getMessage().equals(event.getMessage()),
                    response -> {
                        if(response.getMessage().getContentStripped().toLowerCase().contains("yes")){
                            Database.wipeNotes(userNote.getTag());
                            event.reply("User notes wiped.");
                        } else {
                            event.reply("Response was not a `yes`.\n__Not__ wiping user notes!");
                        }
                    },1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long! Command failed"));
        } else {
            event.reply("You cannot wipe notes as you are not sudo!");
        }
    }
}
