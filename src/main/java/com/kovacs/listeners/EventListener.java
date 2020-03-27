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

import com.kovacs.commands.moderation.Mute;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Config;
import com.kovacs.tools.Unicode;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;


public class EventListener extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        logger.debug("Bot ready!");
    }

    @Override
    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
        if(event.getRole().getId().equalsIgnoreCase(Config.getString("mutedRole"))){
            Audit.log(event.getJDA(), "Warning!", event.getJDA().getSelfUser().getAsTag(), event.getJDA().getSelfUser().getAvatarUrl(),
                    "The muted role has been deleted! Please set a new one!");
        }
    }

    @Override
    public void onTextChannelDelete(@Nonnull TextChannelDeleteEvent event) {
        if(event.getChannel().getId().equalsIgnoreCase(Config.getString("auditChannel"))){
            Objects.requireNonNull(event.getGuild().getOwner()).getUser().openPrivateChannel().queue(privateChannel ->
                    privateChannel.sendMessage("**Warning!**" +
                            "\nThe audit channel has been deleted! Please set a new one!").queue());
        }

    }
}
