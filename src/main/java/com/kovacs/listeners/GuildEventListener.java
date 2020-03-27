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

import com.kovacs.tools.Audit;
import com.kovacs.tools.Config;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

public class GuildEventListener extends ListenerAdapter {

final static Logger logger = LoggerFactory.getLogger(GuildEventListener.class);

     @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {

         NameEventListener.scanName(event.getMember(), event.getMember().getEffectiveName());
    }

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        if(Config.canUseBot(event.getMember()) && !Config.arrayContains("enabledAutoMod", "janitor")){
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
                        Audit.log(event.getJDA(), "Janitor Triggered", event.getJDA().getSelfUser().getAsTag(),
                                event.getJDA().getSelfUser().getAvatarUrl(), "Clearing all " +
                                        "invites made by <@" + event.getMember().getId() + "> because they left the server.");
                    }
                } catch (NullPointerException e){
                    //do nothing
                }
            });
    }
}
