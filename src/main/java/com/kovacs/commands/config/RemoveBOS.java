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
import com.kovacs.database.objects.GuildConfig;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Cache;
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RemoveBOS extends Command {
    public RemoveBOS() {
        this.name = "RemoveBOS";
        this.aliases = new String[]{"rbos", "rmbos"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        String[] words = Sanitizers.normalizeSpacesClearCommas(event.getArgs().toLowerCase()).split(" "); //split based on spaces

            ArrayList<String> bos = GuildConfig.get(event.getGuild().getId()).getBOS();
            if(bos.removeAll(Arrays.asList(words))){
                Database.updateConfig(event.getGuild().getId(), new BasicDBObject("bos", bos));
                Cache.BOS.put(event.getGuild().getId(), bos);
            }

            event.reply(":thumbsup: Removed `" + Arrays.toString(words) + "` from Ban-On-Sight list.");
            Audit.log(this, event, "Ban-On-Sight words removed: `" + Arrays.toString(words) + "`.");

    }
}
