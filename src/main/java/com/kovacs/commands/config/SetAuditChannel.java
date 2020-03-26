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
import com.kovacs.tools.Config;
import net.dv8tion.jda.api.entities.TextChannel;

import java.io.IOException;

public class SetAuditChannel extends Command {
    public SetAuditChannel() {
        this.name = "SetAuditChannel";
        this.aliases = new String[]{};
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            TextChannel textChannel = event.getMessage().getMentionedChannels().get(0);
            String channelName = textChannel.getName();
            String channelId = textChannel.getId();
            try {
                Config.setString("auditChannel", channelId);
                event.reply(":thumbsup: Set `" + channelName + "` as the audit log channel!");
                Audit.log(this, event, "Audit channel set to: `" + textChannel.getName() + "`");
            } catch (IOException e) {
                event.reply("IOException dummy.");
            }
        } catch (IndexOutOfBoundsException e){
            event.reply("Provide a text channel!");
        }
    }
}
