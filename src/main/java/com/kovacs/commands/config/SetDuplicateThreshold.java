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
import com.mongodb.BasicDBObject;

public class SetDuplicateThreshold extends Command {
    public SetDuplicateThreshold() {
        this.name = "SetDuplicateThreshold";
        this.aliases = new String[]{"setdupethresh", "dupethresh"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        try {
            int i = Integer.parseInt(event.getArgs());
            if(i <= 0){
                event.reply("You must have a duplicate threshold that is at least `1`!");
                return;
            }
            Database.updateConfig(event.getGuild().getId(), new BasicDBObject("duplicateThreshold", i));
            event.reply(":thumbsup: Set the duplicate threshold to `" + i + "`.");
            Audit.log(this, event, "Duplicate threshold set to: `" + i + "`");
        } catch (IndexOutOfBoundsException e){
            event.reply("Provide a valid number!");
        }
    }
}
