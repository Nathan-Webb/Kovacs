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

package com.kovacs.listeners;

import com.kovacs.database.ConfigTools;
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Audit;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

//Dedicated to Bowser. Here's hoping Christmas 2019 doesn't happen again.
public class GuildEventListener extends ListenerAdapter {

final static Logger logger = LoggerFactory.getLogger(GuildEventListener.class);

    @Override
    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        String setup = "Thank you for adding Kovacs. Hopefully it exceeds your expectations.\n" +
                "When you invite the bot, only the Guild owner and users with the `Administrator` permission are able to use the bot\n" +
                "They can whitelist users and/or roles with the `whitelist` command. For example: " + event.getJDA().getSelfUser().getAsMention() + "whitelist @user1 @user2 @role1 @role2.\n" +
                "It is recommended to read most if not all of the Wiki: https://github.com/Nathan-Webb/Kovacs/wiki.";
        for(TextChannel channel : event.getGuild().getTextChannels()){
            if(channel.canTalk()){
                channel.sendMessage(setup).queue();
                return;
            }
        }
        //whoops couldn't find any channels to send a message to
        Member m = event.getGuild().getOwner();
        if(m != null){
            m.getUser().openPrivateChannel().queue(c -> c.sendMessage(setup + "\n This message was sent to you because I **do not** have permission to `Send Messages` in any channel. You might want to fix that...").queue());
        }
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
         if(event.getUser().isBot()){
             return;
         }
         NameEventListener.scanName(event.getMember(), event.getMember().getEffectiveName());
    }

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) { //not filtering bots because sometimes they can leave invites lying around
        if(ConfigTools.canUseBot(event.getMember()) && !GuildConfig.get(event.getGuild().getId()).getEnabledAutoMod().contains("janitor")){
            return;
        }
        logger.debug("Janitor Triggered.");
            event.getGuild().retrieveInvites().complete().forEach(invite ->  {
                try {
                    boolean foundInvites = false;
                    if (Objects.requireNonNull(invite.getInviter()).getId().equals(event.getMember().getId())) {
                        invite.delete().queue();
                        foundInvites = true;
                    }
                    if(foundInvites){
                        Audit.log(event.getGuild(), event.getJDA(), "Janitor Triggered", event.getJDA().getSelfUser().getAsTag(),
                                event.getJDA().getSelfUser().getAvatarUrl(), "Clearing all " +
                                        "invites made by <@" + event.getMember().getId() + "> because they left the server.");
                    }
                } catch (NullPointerException e){
                    //do nothing
                }
            });
    }
}
