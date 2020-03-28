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

import java.io.IOException;

public class SetInviteName extends Command {
    public SetInviteName() {
        this.name = "SetInviteName";
        this.aliases = new String[]{};
        this.ownerCommand = true;

    }

    @Override
    protected void execute(CommandEvent event) {
        String name = event.getMessage().getContentRaw();
        if(name.length() <= 32 && name.length() >= 2){
            try {
                Config.setString("inviteName", name);
            } catch (IOException e) {
                event.reply("IOException dummy.");
            }
        } else {
            event.reply("Your chosen name must be between 32 and 2 characters!");
        }
    }
}
