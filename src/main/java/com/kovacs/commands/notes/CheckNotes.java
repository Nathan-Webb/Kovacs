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
import net.dv8tion.jda.api.entities.Member;

public class CheckNotes extends Command {
    public CheckNotes() {
        this.name = "CheckNotes";
        this.aliases = new String[]{"notes"};
        this.children = new Command[]{new AddNote(), new DelNote()};
    }

    @Override
    protected void execute(CommandEvent event) {
        if(event.getMessage().getMentionedRoles().size() > 0){
            Member member = event.getMessage().getMentionedMembers().get(0);

        } else {
            event.reply("You must mention a valid user!");
        }
    }
}
