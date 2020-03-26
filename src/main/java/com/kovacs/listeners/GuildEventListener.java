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

import javax.annotation.Nonnull;
import java.util.Objects;

public class GuildEventListener extends ListenerAdapter {
/*
    Priority:
    dehoist
    clean
    Mute
    Delete
    Ban

     */

     @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
         /*if(Config.canUseBot(event.getMember())) { //user can use bot, no need to scan
             return;
         }

         boolean dehoist = Config.arrayContains("enabledAutoMod", "dehoist");
         boolean clean = Config.arrayContains("enabledAutoMod", "clean");
         boolean mute = Config.arrayContains("enabledAutoMod", "mos");
         boolean delete = Config.arrayContains("enabledAutoMod", "dos");
         boolean ban = Config.arrayContains("enabledAutoMod", "ban");

         if(Config.arrayContains("enabledAutoMod", "bos")) {
             List<String> banOnSight = Config.onSightCache.get("bos");
             AutoModder.onSight(banOnSight, event, "ban");
         }

         if(Config.arrayContains("enabledAutoMod", "mos")) {
             List<String> muteOnSight = Config.onSightCache.get("mos");
             AutoModder.onSight(muteOnSight, event, "mute");
         }

         if(Config.arrayContains("enabledAutoMod", "clean"))
             AutoModder.clean(event);
*/
    }

    @Override
    public void onGuildMemberLeave(@Nonnull GuildMemberLeaveEvent event) {
        if(Config.canUseBot(event.getMember())){
            return;
        }
        if(Config.arrayContains("enabledAutoMod", "janitor")){
            event.getGuild().retrieveInvites().complete().forEach(invite ->  {
                try {
                    boolean foundInvites = false;
                    if (Objects.requireNonNull(invite.getInviter()).getId().equals(event.getMember().getId())) {
                        invite.delete().queue();
                        foundInvites = true;
                    }
                    if(foundInvites){
                        Audit.log(event.getJDA(), "Automod", event.getJDA().getSelfUser().getAsTag(),
                                event.getJDA().getSelfUser().getAvatarUrl(), "Janitor Automod: Clearing all invites made by <@" + event.getMember().getId() + "> as they have left.");
                    }
                } catch (NullPointerException e){
                    //do nothing
                }
            });
        }
    }
}
