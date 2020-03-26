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
import com.kovacs.tools.StringCleaning;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AutomodEnable extends Command {
    public AutomodEnable() {
        this.name = "AutomodEnable";
        this.aliases = new String[]{"enable"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String automod = event.getArgs().toLowerCase();
        String[] splitAutoMod = StringCleaning.normalizeSpacesClearCommas(automod).split(" ");

        List<String> splitList = Arrays.asList(splitAutoMod);
        List<String> autoModList = Config.getList("automod");
        if(autoModList.containsAll(splitList)){
            try {
                Config.addToList("enabledAutoMod", splitAutoMod);
                event.reply("Enabled " + automod);
                Audit.log(this, event, "Automod features enabled: `" + Arrays.toString(splitAutoMod) + "`.");

            } catch (IOException e) {
                event.reply("IOException Dummy.");
            }
        } else {
            event.reply("One of your provided options are not valid! The available options are: `" + autoModList.toString() + "`.");
        }
    }
}
