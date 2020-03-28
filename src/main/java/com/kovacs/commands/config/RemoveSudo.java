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
import com.kovacs.tools.Audit;
import com.kovacs.tools.Config;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveSudo extends Command {
    public RemoveSudo() {
        this.name = "RemoveOwner";
        this.aliases = new String[]{"rmsudo", "delsudo", "remsudo"};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Member> membersToDesudo = event.getMessage().getMentionedMembers();
        List<Role> rolesToDesudo = event.getMessage().getMentionedRoles();
        StringBuilder goodLookingString = new StringBuilder();
        List<String> members = new ArrayList<>();
        membersToDesudo.forEach(member -> {
            String id = member.getId();
            goodLookingString.append("<@").append(id).append(">, ");
            members.add(id);
        });

        List<String> roles = new ArrayList<>();
        rolesToDesudo.forEach(role -> {
                    String id = role.getId();
                    goodLookingString.append("<@&").append(id).append(">, ");
                    roles.add(id);
                }
        );

        try {
            Config.removeFromList("sudo", members.toArray(new String[]{}));
            Config.removeFromList("sudoRoles", roles.toArray(new String[]{}));
            event.reply(":thumbsup:");
            Audit.log(this, event, "Sudo users/roles removed: " + goodLookingString.toString().replaceAll(", $", ""));

        }catch (IOException e){
            event.reply("IOException dummy");
        }
    }
}
