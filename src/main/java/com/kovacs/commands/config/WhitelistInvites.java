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
import com.kovacs.tools.StringCleaning;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class WhitelistInvites extends Command {
    public WhitelistInvites() {
        this.name = "WhitelistInvites";
        this.aliases = new String[]{"WhitelistInvite", "wli", "wlinvite"};
        this.ownerCommand = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        String[] words = StringCleaning.removeUrlKeepInvite(
                StringCleaning.normalizeSpacesClearCommas(event.getArgs())).split(" ");

        try {
            Config.addToList("whitelistedInvites", words);

            event.reply(":thumbsup: Added `" + Arrays.toString(words) + "` to Whitelisted invites.");
            Audit.log(this, event, "Whitelisted invites added: `" + Arrays.toString(words) + "`.");

        }catch (IOException e){
            event.reply("IOException dummy");
        }
    }
}
