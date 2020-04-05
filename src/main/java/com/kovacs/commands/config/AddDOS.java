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
import com.kovacs.tools.Audit;
import com.kovacs.tools.Cache;
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AddDOS extends Command {
    public AddDOS() {
        this.name = "AddDOS";
        this.aliases = new String[]{"dos"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] words = Sanitizers.normalizeSpacesClearCommas(event.getArgs().toLowerCase()).split(" ");

            ArrayList<String> dos = GuildConfig.get(event.getGuild().getId()).getDOS();
            if(dos.addAll(Arrays.asList(words))){
                Database.updateConfig(event.getGuild().getId(), new BasicDBObject("dos", dos));
                Cache.DOS.put(event.getGuild().getId(), dos);

            }

            event.reply(":thumbsup: Added `" + Arrays.toString(words) + "` to Delete-On-Sight list.");
            Audit.log(this, event, "Delete-On-Sight words added: `" + Arrays.toString(words) + "`.");

    }
}
