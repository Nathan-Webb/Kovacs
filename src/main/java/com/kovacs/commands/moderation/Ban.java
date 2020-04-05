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

package com.kovacs.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;
import java.util.List;

public class Ban extends Command {
    public Ban() {
        this.name = "Ban";
        this.aliases = new String[]{"b"};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        String reason;
        int delDays;
        String[] args = event.getArgs().split("\\|");
        try{
             reason = args[1];
        } catch (IndexOutOfBoundsException e){
            reason = "No reason given";
        }

        try {
            delDays = Integer.parseInt(args[2].trim());
        } catch (IndexOutOfBoundsException e){
            delDays = 0;
        }
        String toBan = args[0];
        String[] mentions = Sanitizers.extractIDsFromIdealStr(Sanitizers.normalizeSpaces(toBan));

        List<String> banSuccess = new ArrayList<>();
        List<String> banFailures = new ArrayList<>();

        Guild g = event.getGuild();
        for(String id : mentions){
            g.ban(id, delDays, reason).queue(success -> banSuccess.add(id), failure -> banFailures.add(id));
        }

        StringBuilder successFailure = new StringBuilder();
        boolean sendAudit = false;
        if(banSuccess.size() > 0){
            successFailure.append("Banned the following ID's: ").append(banSuccess.toString()).append("\n");
            sendAudit = true;
        } else {
            successFailure.append("No bans were carried out!\n");
        }

        if(banFailures.size() > 0){
            successFailure.append("The following bans weren't carried out: ").append(banFailures.toString()).append("\n");
        }
        event.reply(successFailure.toString());

        if(sendAudit){
            Audit.log(this, event, successFailure.toString());
        }

    }
}
