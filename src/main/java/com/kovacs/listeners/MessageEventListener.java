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

package com.kovacs.listeners;

import com.kovacs.commands.moderation.Mute;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Config;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class MessageEventListener  extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MessageEventListener.class);

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        scanMessage(event.getMessage());
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        scanMessage(event.getMessage());
    }

    private void scanMessage(Message message){
        if(Config.canUseBot(message.getMember())){
            //todo remove this
            //return;
        }

        List<AutoModResponse> responses = new ArrayList<>();

        if(Config.arrayContains("enabledAutoMod", "bos")){
            responses.add(AutoModder.banOnSight(message));
        }

        if(Config.arrayContains("enabledAutoMod", "mos")){
            responses.add(AutoModder.muteOnSight(message));
        }

        if(Config.arrayContains("enabledAutoMod", "dos")){
            responses.add(AutoModder.deleteOnSight(message));
        }



        //if ban and mute - chose bos
        // if delete, then delete it after ban/mute

        boolean ban = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.BAN));
        boolean mute = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.MUTE));
        boolean delete = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.DELETE));


        if(ban){ //need to ban - dont bother looking for mute
            logger.debug("Ban triggered.");
            AutoModResponse banResp = null;
            for(AutoModResponse response : responses){
                if(response.getAutoMod().equalsIgnoreCase("bos")){
                    banResp = response;
                    break;
                }
            }
            if(banResp == null){
                return;
            }

            //message.getGuild().ban(Objects.requireNonNull(message.getMember()), 0, "Ban on Sight triggered. Trigger: `" + banResp.getTriggerPhrase() + "`. ").queue();
            Audit.log(message.getJDA(), "Ban on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + banResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());

        } else if(mute) { //need to mute
            logger.debug("Mute Triggered");
            AutoModResponse muteResp = null;
            for(AutoModResponse response : responses){
                if(response.getAutoMod().equalsIgnoreCase("mos")){
                    muteResp = response;
                    break;
                }
            }

            if(muteResp == null){
                return;
            }

            //Mute.mute(message.getGuild(), message.getMember(), "Mute on Sight triggered. Trigger: `" + muteResp.getTriggerPhrase() + "`. ");
            Audit.log(message.getJDA(), "Mute on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + muteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        }

        if(delete){//need to delete
            logger.debug("Delete Triggered");
            AutoModResponse deleteResp = null;
            for(AutoModResponse response : responses){
                if(response.getAutoMod().equalsIgnoreCase("dos")){
                    deleteResp = response;
                    break;
                }
            }

            if(deleteResp == null){
                return;
            }

            message.delete().queue();
            Audit.log(message.getJDA(), "Delete on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + deleteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        }
    }
}
