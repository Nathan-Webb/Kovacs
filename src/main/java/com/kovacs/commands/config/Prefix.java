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
import com.kovacs.tools.Config;

import java.io.IOException;

public class Prefix extends Command {
    public Prefix() {
        this.name = "Prefix";
        this.aliases = new String[]{};
    }

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        if(args.length() > 0){
            if(args.length() <= 5){
                try {
                    Config.setString("prefix", args);
                    event.reply(":thumbsup: Prefix set to `" + args + "`" +
                            "\nYou can also use " + event.getSelfMember().getAsMention() + ".");
                } catch (IOException e) {
                    event.reply("IOException dummy.");
                }
            } else {
                event.reply("The maximum prefix length is `5`");
            }
        } else {
            event.reply("The bot prefix is `" + Config.getString("prefix") + "`" +
                    "\nYou can also use " + event.getSelfMember().getAsMention() + ".");
        }
    }
}
