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
import com.kovacs.database.Database;
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;

import java.util.List;

public class Automod extends Command {
    public Automod() {
        this.name = "Automod";
        this.aliases = new String[]{"am"};
        this.children = new Command[]{new AutomodDisable(), new AutomodEnable()};
    }

    @Override
    protected void execute(CommandEvent event) {

        event.reply(getAutoModSettings(event.getGuild().getId()));

    }

    public static String getAutoModSettings(String guildID){
        GuildConfig config = GuildConfig.get(guildID);
        List<String> automodList = config.getEnabledAutoMod();
        List<String> enabledAutomod = Config.getList("enabledAutoMod");
        StringBuilder builder = new StringBuilder();
        builder.append("**AutoMod**\n");
        int i = 0;
        for(String automod : automodList){
            if(enabledAutomod.contains(automod)){
                builder.append(":green_square: ").append(automod);
            } else {
                builder.append(":red_square: ~~").append(automod).append("~~");
            }
            if(i % 2 != 0) {
                builder.append("\n\n");
            } else {
                builder.append(":black_small_square: :black_small_square: :black_small_square: ");
            }
            i++;
        }
        return builder.toString();
    }
}
