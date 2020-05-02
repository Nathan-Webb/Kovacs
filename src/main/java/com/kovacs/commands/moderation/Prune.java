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
import com.kovacs.database.ConfigTools;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Prune extends Command {
    public Prune() {
        this.name = "Prune";
        this.aliases = new String[]{"c", "clear", "clean", "p", "purge"};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    final static Logger logger = LoggerFactory.getLogger(Prune.class);

    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        args = Sanitizers.normalizeSpaces(args);
        String[] extractedIds = Sanitizers.extractIDs(args);
        String amount = extractedIds[extractedIds.length - 1];
        String[] idsToPrune = new String[extractedIds.length - 1];
        //leave last arg for max/all/numOfMessages
        System.arraycopy(extractedIds, 0, idsToPrune, 0, extractedIds.length - 1);


        int amountInt;
        try {
            amountInt = Integer.parseInt(amount);
            if(amountInt > 100){
                amountInt = 100;
            }
        } catch (NumberFormatException e){
            if(amount.equalsIgnoreCase("all") || amount.equalsIgnoreCase("max")){
                amountInt = 100;
            } else {
                event.reply("You need to put a valid number, or `max`, or `all`.");
                return;
            }
        }

        if(idsToPrune.length == 0){
            if(ConfigTools.isSudo(event.getMember())){
                int res = pruneIndiscriminately(event.getChannel(), amountInt);
                event.reply(":thumbsup:", success -> success.delete().queueAfter(5, TimeUnit.SECONDS));
                Audit.log(this, event, "Indiscriminately pruned `" + res + "` messages from " + event.getTextChannel().getAsMention() + ".");
            } else {
                event.reply("You cannot prune indiscriminately because you are not sudo!" +
                        "\nProvide valid member ID's or mentions!");
            }
        } else {
            StringBuilder fancyString = new StringBuilder();
            for(String s : idsToPrune){
                fancyString.append("<@").append(s).append("> ");
            }

            int res = pruneFromUsers(event.getChannel(), amountInt, Arrays.asList(idsToPrune));
            event.reply(":thumbsup:", success -> success.delete().queueAfter(5, TimeUnit.SECONDS));

            Audit.log(this, event, "Pruned `" + res + "` messages from " + event.getTextChannel().getAsMention() + " by the following users: " + fancyString);

        }
    }

    private int pruneIndiscriminately(MessageChannel channel, int amount){
        int amountPruned = 0;

        List<Message> toPrune = channel.getHistory().retrievePast(amount).complete();
        for (Message message : toPrune) {
            message.delete().queue();
            amountPruned++;
        }
        return amountPruned;
    }

    private int pruneFromUsers(MessageChannel channel, int amount, List<String> ids){
        int amountPruned = 0;
        List<Message> toPrune = new ArrayList<>();
        channel.getHistory().retrievePast(amount).complete().forEach((message) ->
        {
            if (ids.contains(message.getAuthor().getId())) {
                toPrune.add(message);
            }
        });
        for (Message message : toPrune) {
            message.delete().queue();
            amountPruned++;
        }
        return amountPruned;
    }
}
