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
import com.kovacs.tools.Config;

import java.util.List;

public class Automod extends Command {
    public Automod() {
        this.name = "Automod";
        this.aliases = new String[]{};
        this.children = new Command[]{new AutomodDisable(), new AutomodEnable()};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> automodList = Config.getList("automod");
        List<String> enabledAutomod = Config.getList("enabledAutoMod");
        StringBuilder builder = new StringBuilder();
        builder.append("**AutoMod**\n");
        for(String automod : automodList){
            if(enabledAutomod.contains(automod)){
                builder.append(":green_square: ").append(automod);
            } else {
                builder.append(":red_square: ~~").append(automod).append("~~");
            }
            builder.append("\n\n");
        }
        event.reply(builder.toString());

    }
}
