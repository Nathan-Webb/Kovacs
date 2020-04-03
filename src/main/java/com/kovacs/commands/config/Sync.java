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

import java.io.IOException;
import java.util.*;

public class Sync extends Command {
    public Sync() {
        this.name = "Sync";
        this.aliases = new String[]{"s"};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> combined = new ArrayList<>();
        combined.addAll(Cache.MOS.get(event.getGuild().getId()));
        combined.addAll(Cache.DOS.get(event.getGuild().getId()));
        try {
            Config.addToList("mos",  combined.toArray(new String[]{}));
            Config.addToList("dos", combined.toArray(new String[]{}));
            Set<String> toReload = new HashSet<>();
            toReload.add("mos");
            toReload.add("dos");
            Cache.DOS.reloadAll(Collections.singleton(event.getGuild().getId()), null);
            Cache.DOS.reloadAll(Collections.singleton(event.getGuild().getId()), null);
            event.reply(":thumbsup: Delete-on-Sight and Mute-On-Sight have been synced!");
            Audit.log(this, event, "Delete-on-Sight and Mute-On-Sight synced.");
        } catch (IOException e) {
            event.reply("IOException dummy");
        }

    }
}
