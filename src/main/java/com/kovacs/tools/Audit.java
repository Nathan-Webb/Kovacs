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

package com.kovacs.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.awt.*;

public class Audit {

    public static void log(Command command, CommandEvent commandEvent, String phrase){
        String auditChannel = Config.getString("auditChannel");
        if(auditChannel.equals("")){
            return;
        }
        MessageChannel channel = commandEvent.getJDA().getTextChannelById(Config.getString("auditChannel"));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(command.getName()).setColor(Color.ORANGE);
        builder.setDescription(phrase);
        builder.setFooter(commandEvent.getAuthor().getAsTag(), commandEvent.getAuthor().getEffectiveAvatarUrl());
        assert channel != null;
        channel.sendMessage(builder.build()).queue();
    }

    public static void log(JDA jda, String title, String author, String avatarUrl, String phrase){
        String auditChannel = Config.getString("auditChannel");
        if(auditChannel.equals("")){
            return;
        }
        if(phrase.length() > 2000){ //we messed up
            phrase = phrase.substring(0, 2000);
        }
        MessageChannel channel = jda.getTextChannelById(Config.getString("auditChannel"));
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title).setColor(Color.ORANGE);
        builder.setDescription(phrase);
        builder.setFooter(author, avatarUrl);
        assert channel != null;
        channel.sendMessage(builder.build()).queue();
    }
}
