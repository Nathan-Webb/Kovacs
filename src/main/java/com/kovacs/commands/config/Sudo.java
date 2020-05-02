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
import com.kovacs.Kovacs;
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.objects.GuildConfig;
import com.kovacs.tools.Audit;
import com.mongodb.BasicDBObjectBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;

public class Sudo extends Command {
    public Sudo() {
        this.name = "Sudo";
        this.aliases = new String[]{"addsudo"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user or have the `Administrator` permission to run this command!");
            return;
        }


        StringBuilder goodLookingString = new StringBuilder();
        List<Role> rolesToSudo = event.getMessage().getMentionedRoles();
        List<Member> membersToSudo = event.getMessage().getMentionedMembers();

        List<String> memberIDs = new ArrayList<>();
        membersToSudo.forEach(member -> {
            String id = member.getId();
            goodLookingString.append("<@").append(id).append(">, ");
            memberIDs.add(id);
        });


        List<String> roleIDs = new ArrayList<>();
        rolesToSudo.forEach(role -> {
                    String id = role.getId();
                    goodLookingString.append("<@&").append(id).append(">, ");
                    roleIDs.add(id);
                }
        );
         GuildConfig guildConfig = GuildConfig.get(event.getGuild().getId());
         ArrayList<String> sudoUsers = guildConfig.getSudoUsers();
         ArrayList<String> sudoRoles = guildConfig.getSudoRoles();
         Kovacs.addIfMissing(sudoUsers, memberIDs);
         Kovacs.addIfMissing(sudoRoles, roleIDs);

        Database.updateConfig(event.getGuild().getId(), new BasicDBObjectBuilder().add("sudoUsers", sudoUsers)
                .add("sudoRoles", sudoRoles).get());

        event.reply(":thumbsup:");
         Audit.log(this, event, "Sudo users/roles added: " + goodLookingString.toString().replaceAll(", $", ""));
    }
}
