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

package com.kovacs.commands.generic;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class UserInfo extends Command {
    public UserInfo() {
        this.name = "UserInfo";
        this.aliases = new String[]{"i", "ui", "info"};
    }
    //todo add stuff for if the user is in the guild.
    //todo use member,whatevs
    //add an invite link if they are bots



    @Override
    protected void execute(CommandEvent event) {
            if(event.getMessage().getMentionedUsers().size() > 0){
                event.reply(sendUserInfo(event.getMessage().getMentionedUsers().get(0)));
            } else if(event.getArgs().matches("\\d+")){
                User u = event.getJDA().retrieveUserById(event.getArgs()).complete();
                event.reply(sendUserInfo(u));
            } else {
                event.reply("You must provide a user mention or user ID!");
            }
    }
    private MessageEmbed sendUserInfo(User u){
        return new EmbedBuilder().setColor(Color.GREEN).setTitle(u.getAsTag() + " (" + u.getId() + ")")
                        .setThumbnail(u.getEffectiveAvatarUrl())
                .addField("Joined discord on:", u.getTimeCreated()
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME), false).build();

    }
}
