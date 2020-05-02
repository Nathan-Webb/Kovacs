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
import com.kovacs.database.ConfigTools;
import com.kovacs.database.Database;
import com.kovacs.database.objects.GuildConfig;
import com.kovacs.tools.Audit;
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.entities.Role;

public class SetMutedRole extends Command {
    public SetMutedRole() {
        this.name = "SetMutedRole";
        this.aliases = new String[]{};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        try {
            Role r = event.getMessage().getMentionedRoles().get(0);
            String roleName = r.getName();
            String roleID = r.getId();
            if(!roleID.equals(GuildConfig.get(event.getGuild().getId()).getMutedRole())){
                Database.updateConfig(event.getGuild().getId(), new BasicDBObject("mutedRole", roleID));
            }
            event.reply(":thumbsup: Set `" + roleName + "` as the muted role!");
            Audit.log(this, event, "Muted role set to: `" + r.getName() + "`");
        } catch (IndexOutOfBoundsException e){
            event.reply("Provide a role!");
        }
    }
}
