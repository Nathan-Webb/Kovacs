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
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutomodDisable extends Command {
    public AutomodDisable() {
        this.name = "AutomodDisable";
        this.aliases = new String[]{"disable", "remove"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }
        String automod = event.getArgs().toLowerCase();
        String[] splitAutoMod = Sanitizers.normalizeSpacesClearCommas(automod).split(" ");

        List<String> splitList = Arrays.asList(splitAutoMod);
        List<String> autoModList = Arrays.asList(Kovacs.autoMod);
        if(autoModList.containsAll(splitList)){
                ArrayList<String> autoMod = GuildConfig.get(event.getGuild().getId()).getEnabledAutoMod();
                autoMod.removeAll(splitList);
                Database.updateConfig(event.getGuild().getId(), new BasicDBObject("enabledAutoMod", autoMod));
                event.reply("Disabled " + automod);
                Audit.log(this, event, "Automod features disabled: `" + Arrays.toString(splitAutoMod) + "`.");


        } else {
            event.reply("One of your provided options are not valid! The available options are: `" + autoModList.toString() + "`.");
        }
    }
}
