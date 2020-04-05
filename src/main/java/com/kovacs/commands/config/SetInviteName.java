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
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;


public class SetInviteName extends Command {
    public SetInviteName() {
        this.name = "SetInviteName";
        this.aliases = new String[]{};

    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        String name = event.getMessage().getContentRaw();
        if(name.length() <= 32 && name.length() >= 2){
            if(Sanitizers.isValidName(name)){
                Database.updateConfig(event.getGuild().getId(), new BasicDBObject("inviteName", name));
                event.reply(":thumbsup: Invite name changed to `" + name + "`.");
                Audit.log(this, event, "Invite name changed to `" + name + "`.");
            } else {
                event.reply("You cannot use this nickname! Bad things will happen! (Like infinite loops)");
            }
        } else {
            event.reply("Your chosen name must be between 32 and 2 characters!");
        }
    }
}
