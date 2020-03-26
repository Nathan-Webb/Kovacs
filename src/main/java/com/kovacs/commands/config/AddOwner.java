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
import net.dv8tion.jda.api.entities.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddOwner extends Command {
    public AddOwner() {
        this.name = "AddOwner";
        this.aliases = new String[]{"sudo"};
    }

    @Override
    protected void execute(CommandEvent event) {
        List<Member> membersToWhitelist = event.getMessage().getMentionedMembers();

        List<String> members = new ArrayList<>();
        membersToWhitelist.forEach(member -> members.add(member.getId()));

        try {
            Config.addToList("sudo", members.toArray(new String[]{}));
            event.reply(":thumbsup:");
        }catch (IOException e){
            event.reply("IOException dummy");
        }
    }
}
