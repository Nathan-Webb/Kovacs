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

package com.kovacs.commands.toolbox;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.format.DateTimeFormatter;

public class GenerateInvite extends Command {
    public GenerateInvite() {
        this.name = "GenerateInvite";
        this.aliases = new String[]{"gi", "geninvite"};
    }

    @Override
    protected void execute(CommandEvent event) {
        User u;
        if(event.getMessage().getMentionedUsers().size() > 0){
            u = event.getMessage().getMentionedUsers().get(0);
        } else if(event.getArgs().matches("\\d+")){
            u = event.getJDA().retrieveUserById(event.getArgs()).complete();
        } else {
            event.reply("You must provide a bot mention or bot ID!");
            return;
        }
        if(u.isBot()){
            event.reply("<https://discord.com/oauth2/authorize?client_id=" + u.getId() + "&scope=bot&permissions=0>");
        } else {
            event.reply("Not a bot!");
        }
    }

}
