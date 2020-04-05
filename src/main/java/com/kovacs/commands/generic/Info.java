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

package com.kovacs.commands.generic;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class Info extends Command {
    public Info() {
        this.name = "Info";
        this.aliases = new String[]{"botinfo", "dont_remove_this_or_else_ill_be_sad"};
    }

    @Override
    protected void execute(CommandEvent event) {

        EmbedBuilder infoEmbed = new EmbedBuilder();
        infoEmbed.setColor(Color.ORANGE)
                .addField("Bot Owner", "<@" + event.getClient().getOwnerId() + ">", false)
                .addField("Library", "[DV8FromTheWorld/JDA](https://github.com/DV8FromTheWorld/JDA)", false)
                .addField("Github", "[Nathan-Webb/Kovacs](https://github.com/Nathan-Webb/Kovacs)", false);
        event.reply(infoEmbed.build());

    }
}
