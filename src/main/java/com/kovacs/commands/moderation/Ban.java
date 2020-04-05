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

import com.ibm.icu.impl.locale.XCldrStub;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.RegEx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ban extends Command {
    public Ban() {
        this.name = "Ban";
        this.aliases = new String[]{"b"};
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    final static Logger logger = LoggerFactory.getLogger(Ban.class);

    @Override
    protected void execute(CommandEvent event) {
        String reason = Sanitizers.removeMetionsAndIdsFromStart(event.getArgs());
        if(reason.equals("")){
            reason = "No reason given!";
        }
        int delDays = getDelDays(reason);
        String toBan = event.getArgs().replaceFirst(reason, "");

        String[] mentions = Sanitizers.extractIDsFromIdealStr(Sanitizers.normalizeSpaces(toBan));

        List<String> banSuccess = new ArrayList<>();
        List<String> banFailures = new ArrayList<>();

        Guild g = event.getGuild();
        for(String id : mentions){
            g.ban(id, delDays, reason).queue(success -> banSuccess.add(id), failure -> banFailures.add(id));
            banSuccess.add(id);
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

    private static int getDelDays(String reason){
        reason = reason.toLowerCase().trim();
        Pattern pattern = Pattern.compile("d(el(ete)?)? *\\d+$");
        Matcher matcher = pattern.matcher(reason);
        if (matcher.find()){
            return Integer.parseInt(matcher.group().replaceFirst("d(el(ete)?)? ", ""));
        }
        return 0;
    }
}
