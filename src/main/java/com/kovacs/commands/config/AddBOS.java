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
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Cache;
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class AddBOS extends Command {
    public AddBOS() {
        this.name = "AddBOS";
        this.aliases = new String[]{"bos"};
    }
final static Logger logger = LoggerFactory.getLogger(AddBOS.class);

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }
        String[] words = Sanitizers.normalizeSpacesClearCommas(event.getArgs().toLowerCase()).split(" ");

    event.reply("Are you __sure__ you want to do this? __All__ of these words will be added: `" + Arrays.toString(words) + "`" +
            "\nTf they are detected in **USERNAMES** or in **MESSAGES** the users responsible will be __**BANNED**__." +
            "\n If you are sure, then respond with `yes`.");

    Kovacs.waiter.waitForEvent(MessageReceivedEvent.class,
            check ->  check.getAuthor().equals(event.getMember().getUser()) && check.getChannel().equals(event.getChannel()) && !check.getMessage().equals(event.getMessage()),
            response -> {
        if(response.getMessage().getContentStripped().toLowerCase().contains("yes")){

                ArrayList<String> bos = GuildConfig.get(event.getGuild().getId()).getBOS();
                if(bos.addAll(Arrays.asList(words))){
                    Database.updateConfig(event.getGuild().getId(), new BasicDBObject("bos", bos));
                    Cache.BOS.put(event.getGuild().getId(), bos);
                }

                event.reply(":thumbsup: Added `" + Arrays.toString(words) + "` to Ban-on-sight list.");
                Audit.log(this, event, "Ban-On-Sight words added: `" + Arrays.toString(words) + "`.");

        } else {
            event.reply("Response was not a `yes`.\n__Not__ adding to Ban-on-sight list!");
        }
    },1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long! Command failed"));


    }
}
