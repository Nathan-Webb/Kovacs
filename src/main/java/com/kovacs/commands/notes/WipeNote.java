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

package com.kovacs.commands.notes;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.database.ConfigTools;

public class WipeNote extends Command {
    public WipeNote() {
        this.name = "WipeNote";
        this.aliases = new String[]{"wipe"};
    }

    @Override
    protected void execute(CommandEvent event) {
        //wipes all notes from user in this server
        if(ConfigTools.isSudo(event.getMember())) {
        } else {
            event.reply("You cannot wipe the notes from this user!");
        }
    }
}
