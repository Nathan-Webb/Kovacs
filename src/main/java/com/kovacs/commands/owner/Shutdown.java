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

package com.kovacs.commands.owner;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Shutdown extends Command {
    public Shutdown() {
        this.name = "Shutdown";
        this.aliases = new String[]{};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply("Shutting down! Good luck.");
        event.getJDA().shutdown();
    }
}
