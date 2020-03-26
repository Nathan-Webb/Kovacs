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

package com.kovacs.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.Kovacs;
import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.Collections;

public class AddBOS extends Command {
    public AddBOS() {
        this.name = "AddBOS";
        this.aliases = new String[]{"bos"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] words = StringCleaning.normalizeSpaces(event.getArgs().toLowerCase()).split(" ");

    event.reply("Are you __sure__ you want to do this? __All__ of these words will be added: `" + words + "`" +
            "\nTf they are detected in **USERNAMES** or in **MESSAGES** the users responsible will be __**BANNED**__." +
            "\n If you are sure, then respond with `yes`.");
    Kovacs.waiter.waitForEvent(MessageReceivedEvent.class,
            check -> check.getAuthor().equals(event.getMember().getUser()) && check.getChannel().equals(event.getChannel()) && !check.getMessage().equals(event.getMessage()),
            response -> {
        if(response.getMessage().getContentStripped().toLowerCase().contains("yes")){
            try {
                Config.addToList("bos", words);
                Config.onSightCache.reloadAll(Collections.singleton("bos"), null); //reload ban on sight
                event.reply(":thumbsup: Added `" + words + "` to Ban-on-sight list.");
            }catch (IOException e){
                event.reply("IOException dummy");
            }
        } else {
            event.reply("Response was not a `yes`.\n__Not__ adding to Ban-on-sight list!");
        }
    });


    }
}
