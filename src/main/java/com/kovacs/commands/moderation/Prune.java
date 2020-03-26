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
import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Prune extends Command {
    public Prune() {
        this.name = "Prune";
        this.aliases = new String[]{"c", "clear", "clean", "p", "purge"};
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    final static Logger logger = LoggerFactory.getLogger(Prune.class);

//todo check if indiscriminate prune works without sudo
    @Override
    protected void execute(CommandEvent event) {
        String args = event.getArgs();
        args = StringCleaning.normalizeSpaces(args);
        logger.debug(args);
        String[] arr = StringCleaning.extractIDsFromIdealStr(args);
        logger.debug(Arrays.deepToString(arr));
        String amount = arr[arr.length - 1];
        String[] idsToPrune = new String[arr.length - 1];
        //leave last arg for max/all/numOfMessages
        System.arraycopy(arr, 0, idsToPrune, 0, arr.length - 1);
        logger.debug(Arrays.deepToString(idsToPrune));


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
            if(Config.isSudo(event.getMember())){
                pruneIndiscriminately(event.getChannel(), amountInt);
                event.reply(":thumbsup:", success -> success.delete().queueAfter(5, TimeUnit.SECONDS));
            } else {
                event.reply("You cannot prune indiscriminately because you are not sudo!" +
                        "\nProvide valid member ID's or mentions!");
            }
        } else {
            pruneFromUsers(event.getChannel(), amountInt, Arrays.asList(idsToPrune));
            event.reply(":thumbsup:", success -> success.delete().queueAfter(5, TimeUnit.SECONDS));
        }
    }

    private void pruneIndiscriminately(MessageChannel channel, int amount){
        List<Message> toPrune = channel.getHistory().retrievePast(amount).complete();
        for (Message message : toPrune) {
            logger.debug(message.getContentDisplay());
            message.delete().queue();
        }
    }

    private void pruneFromUsers(MessageChannel channel, int amount, List<String> ids){
        List<Message> toPrune = new ArrayList<>();
        channel.getHistory().retrievePast(amount).complete().forEach((message) ->
        {
            if (ids.contains(message.getAuthor().getId())) {
                toPrune.add(message);
            }
        });
        for (Message message : toPrune) {
            logger.debug(message.getContentDisplay());
            message.delete().queue();
        }

    }
}
