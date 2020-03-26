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
import com.kovacs.tools.StringCleaning;

import java.io.IOException;
import java.util.Collections;

public class AddDOS extends Command {
    public AddDOS() {
        this.name = "AddDOS";
        this.aliases = new String[]{"dos"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] words = StringCleaning.normalizeSpaces(event.getArgs().toLowerCase()).split(" ");

        try {
            Config.addToList("dos", words);
            Config.onSightCache.reloadAll(Collections.singleton("dos"), null); //reload delete on sight

            event.reply(":thumbsup: Added `" + words + "` to Delete-On-Sight list.");
        }catch (IOException e){
            event.reply("IOException dummy");
        }
    }
}
