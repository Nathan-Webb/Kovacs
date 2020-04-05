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
import com.kovacs.database.Database;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Cache;
import com.mongodb.BasicDBObjectBuilder;

import java.io.CharArrayReader;
import java.util.*;

public class Sync extends Command {
    public Sync() {
        this.name = "Sync";
        this.aliases = new String[]{"s"};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<String> combined = new ArrayList<>(Cache.MOS.get(event.getGuild().getId()));
        Kovacs.addIfMissing(combined, Cache.DOS.get(event.getGuild().getId()));
        Database.updateConfig(event.getGuild().getId(), new BasicDBObjectBuilder().add("mos", combined)
                .add("dos", combined).get());
        Cache.DOS.reloadAll(Collections.singleton(event.getGuild().getId()), null);
        Cache.DOS.reloadAll(Collections.singleton(event.getGuild().getId()), null);

        event.reply(":thumbsup: Delete-on-Sight and Mute-On-Sight have been synced!");
        Audit.log(this, event, "Delete-on-Sight and Mute-On-Sight synced.");


    }
}
