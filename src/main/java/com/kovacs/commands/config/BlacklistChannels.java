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
import com.mongodb.BasicDBObject;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class BlacklistChannels extends Command {
    public BlacklistChannels() {
        this.name = "BlacklistChannels";
        this.aliases = new String[]{"blchannel", "blchan"};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }
        ArrayList<String> channelIDs = new ArrayList<>();
        List<TextChannel> channels = event.getMessage().getMentionedChannels();
        StringBuilder fancyNames = new StringBuilder();
        channels.forEach(c -> {
            channelIDs.add(c.getId());
            fancyNames.append(c.getName()).append(", ");
        });
        ArrayList<String> whitelistedChannels = GuildConfig.get(event.getGuild().getId()).getWhitelistedChannels();
        if(whitelistedChannels.removeAll(channelIDs)) {
            Database.updateConfig(event.getGuild().getId(), new BasicDBObject("whitelistedChannels", whitelistedChannels));
        }
        String names = fancyNames.toString().replaceFirst(", $", "");
        event.reply(":thumbsup: Removed `" + names + "` from Whitelisted channels.");
        Audit.log(this, event, "Whitelisted channels removed: `" + names + "`.");

    }
}
