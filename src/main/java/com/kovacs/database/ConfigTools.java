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

package com.kovacs.database;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class ConfigTools {

    public static boolean isSudo(Member member){
        if(getOwnerID(member.getGuild()).equals(member.getId())){
            return true;
        }
        if(member.hasPermission(Permission.ADMINISTRATOR)){ //admin
            return true;
        }
        GuildConfig config = GuildConfig.get(member.getGuild().getId());
        ArrayList<String> sudoRoles = config.getSudoRoles();
        ArrayList<String> sudoUsers = config.getSudoUsers();
        if(sudoUsers.contains(member.getId())){
            return true;
        }
        List<Role> roles = member.getRoles();
        return roles.stream().anyMatch(r -> sudoRoles.contains(r.getId()));
    }

    public static String getOwnerID(Guild guild){
        Member owner = guild.getOwner();
        if(owner != null){
            return owner.getId();
        }

        return "";
    }

    public static boolean canUseBot(Member member){
        return !cantUseBot(member);
    }
    public static boolean cantUseBot(Member member){
        String authorID = member.getId();
        String guildID = member.getGuild().getId();
        GuildConfig config = GuildConfig.get(guildID);

        if(isSudo(member)){
            return false;
        }

        if(!config.getWhitelistedUsers().contains(authorID)){ //user isn't whitelisted
            if(member.getRoles().stream()
                    .noneMatch(role -> config.getSudoRoles().contains(role.getId()))){ //none of the users roles are sudo
                return member.getRoles().stream()
                        .noneMatch(role -> config.getWhitelistedRoles().contains(role.getId())); //are any of the roles whitelisted?
            }

        }

        return false;
    }
}
