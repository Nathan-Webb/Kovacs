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
import com.kovacs.tools.Cache;
import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class AddMOS extends Command {
    public AddMOS() {
        this.name = "AddMOS";
        this.aliases = new String[]{"mos"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] words = StringCleaning.normalizeSpacesClearCommas(event.getArgs().toLowerCase()).split(" ");

        try {
            Config.addToList("mos", words);
            Cache.DOS.reloadAll(Collections.singleton(event.getGuild().getId()), null); //reload mute on sight

            event.reply(":thumbsup: Added `" + Arrays.toString(words) + "` to Mute-On-Sight list.");
            Audit.log(this, event, "Mute-On-Sight words added: `" + Arrays.toString(words) + "`.");

        }catch (IOException e){
            event.reply("IOException dummy");
        }
    }
}
