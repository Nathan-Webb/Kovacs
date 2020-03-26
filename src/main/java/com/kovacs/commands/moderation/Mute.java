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

package com.kovacs.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.Objects;

public class Mute extends Command {
    public Mute() {
        this.name = "Mute";
        this.aliases = new String[]{"m"};
        this.userPermissions = new Permission[]{Permission.MANAGE_ROLES};
    }

    @Override
    protected void execute(CommandEvent event) {
        String reason = StringCleaning.removeAllMentions(event.getArgs());
        event.getMessage().getMentionedMembers().forEach(member -> {
            mute(member.getGuild(), member, reason);
        });
        event.reply(":thumbsup:");
    }
    public static void mute(Guild guild, Member member, String reason){
        guild.addRoleToMember(member,
                Objects.requireNonNull(guild.getRoleById(Config.getString("mutedRole"))))
                .reason(reason).queue();
    }

}
