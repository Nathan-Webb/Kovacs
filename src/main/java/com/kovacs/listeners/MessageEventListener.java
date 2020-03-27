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
import com.kovacs.tools.DupeChecker;
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


public class MessageEventListener  extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MessageEventListener.class);

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }
        if(Config.arrayContains("enabledAutoMod", "duplicates")){
            boolean dupe = DupeChecker.addAndCheck(event.getMessage());
            if(dupe){
                Mute.mute(event.getGuild(), event.getMember(), "Anti-Duplicate triggered.");
                Audit.log(event.getJDA(), "Anti-Duplicate triggered.", event.getJDA().getSelfUser().getAsTag(), event.getJDA().getSelfUser().getAvatarUrl(),
                        "\nUser: " + event.getAuthor().getAsMention() +
                        "\nMessage: " + event.getMessage().getContentRaw());
            }
        }
        scanMessage(event.getMessage());
    }

    @Override
    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }

        scanMessage(event.getMessage());
    }

    private void scanMessage(Message message){
        if(Config.canUseBot(message.getMember())){
            return;
        }

        List<AutoModResponse> responses = new ArrayList<>();

        if(Config.arrayContains("enabledAutoMod", "bos")){
            responses.add(AutoModder.banOnSight(message.getContentRaw()));
        }

        if(Config.arrayContains("enabledAutoMod", "mos")){
            responses.add(AutoModder.muteOnSight(message.getContentRaw()));
        }

        if(Config.arrayContains("enabledAutoMod", "dos")){
            responses.add(AutoModder.deleteOnSight(message));
        }

        if(Config.arrayContains("enabledAutoMod", "invites")){
            responses.add(AutoModder.invites(message.getContentRaw()));
        }



        //if ban and mute - chose bos
        // if delete, then delete it after ban/mute

        boolean ban = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.BAN));
        boolean mute = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.MUTE));
        boolean delete = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.DELETE));
        boolean invite = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.INVITES));


        if(ban){ //need to ban - dont bother looking for mute
            logger.debug("Ban triggered.");
            AutoModResponse banResp = getResponse(responses, "bos");

            //message.getGuild().ban(Objects.requireNonNull(message.getMember()), 0, "Ban on Sight triggered. Trigger: `" + banResp.getTriggerPhrase() + "`. ").queue();
            Audit.log(message.getJDA(), "Ban on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + banResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());

        } else if(mute) { //need to mute
            logger.debug("Mute Triggered");
            AutoModResponse muteResp = getResponse(responses, "mos");

            //Mute.mute(message.getGuild(), message.getMember(), "Mute on Sight triggered. Trigger: `" + muteResp.getTriggerPhrase() + "`. ");
            Audit.log(message.getJDA(), "Mute on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + muteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        }

        if(delete){//need to delete
            logger.debug("Delete Triggered");
            AutoModResponse deleteResp = getResponse(responses, "dos");

            message.delete().queue();
            Audit.log(message.getJDA(), "Delete on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + deleteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        } else if (invite){ //message didn't need to be deleted, does it have an invite?
            logger.debug("Invite Triggered");
            AutoModResponse inviteResp = getResponse(responses, "invites");
            message.delete().queue();

            String action = Config.getString("inviteName");
            if(action.equalsIgnoreCase("ban")){
                Objects.requireNonNull(message.getMember()).ban(0).reason("Invite Kick Triggered").queue();
                Audit.log(message.getJDA(), "Invite deletion + Ban  triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());

            } else if (action.equalsIgnoreCase("kick")){
                Objects.requireNonNull(message.getMember()).kick().reason("Invite Kick Triggered").queue();
                Audit.log(message.getJDA(), "Invite deletion + Kick  triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());
            } else {
                Audit.log(message.getJDA(), "Invite deletion triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());
            }


        }
    }

    private AutoModResponse getResponse(List<AutoModResponse> responses, String automod){
        for(AutoModResponse response : responses){
            if(response.getAutoMod().equalsIgnoreCase(automod)){
                return response;
            }
        }
        return new AutoModResponse("", AutoModActions.NOTHING, "", automod);
    }
}
