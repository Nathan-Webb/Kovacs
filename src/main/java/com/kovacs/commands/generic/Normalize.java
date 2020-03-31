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
import com.kovacs.tools.Unicode;

public class Normalize extends Command {
    public Normalize() {
        this.name = "Normalize";
        this.aliases = new String[]{"unfuck", "clearMyHeadache"};
    }

    @Override
    protected void execute(CommandEvent event) {
        event.reply(Unicode.dehoist(event.getGuild(), Unicode.cleanEverything(event.getArgs())));
    }
}
