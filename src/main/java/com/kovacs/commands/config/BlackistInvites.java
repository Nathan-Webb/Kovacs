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
import com.kovacs.tools.Sanitizers;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;

public class BlackistInvites extends Command {
    public BlackistInvites() {
        this.name = "BlackistInvites";
        this.aliases = new String[]{"BlackistInvite", "blinvite", "bli"};
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] invites = Sanitizers.removeUrlKeepInvite(Sanitizers.normalizeSpacesClearCommas(event.getArgs())).split(" ");

        ArrayList<String> whitelistedInvites = GuildConfig.get(event.getGuild().getId()).getWhitelistedInvites();
        if(whitelistedInvites.removeAll(Arrays.asList(invites))){
            Database.updateConfig(event.getGuild().getId(), new BasicDBObject("whitelistedInvites", whitelistedInvites));
        }

        event.reply(":thumbsup: Removed `" + Arrays.toString(invites) + "` from Whitelisted invites.");
        Audit.log(this, event, "Whitelisted invites removed: `" + Arrays.toString(invites) + "`.");

    }
}
