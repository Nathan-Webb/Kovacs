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
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.GuildConfig;
import com.mongodb.BasicDBObject;

public class Prefix extends Command {
    public Prefix() {
        this.name = "Prefix";
        this.aliases = new String[]{};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        String args = event.getArgs();
        if(args.length() > 0){
            if(args.length() <= 5){
                String prefix = GuildConfig.get(event.getGuild().getId()).getPrefix();
                if(!prefix.equals(args)){
                    Database.updateConfig(event.getGuild().getId(), new BasicDBObject("prefix", args));
                }

                event.reply(":thumbsup: Prefix set to `" + args + "`" +
                        "\nYou can also use " + event.getSelfMember().getAsMention() + "" +
                        "\nFor example: " + event.getSelfMember().getAsMention() + " help");
            } else {
                event.reply("The maximum prefix length is `5`");
            }
        } else {
            event.reply("The bot prefix is `" + GuildConfig.get(event.getGuild().getId()).getPrefix() + "`" +
                    "\nYou can also use " + event.getSelfMember().getAsMention() + ".");
        }
    }
}
