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
import com.kovacs.database.ConfigTools;
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Audit;
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

//Dedicated to Bowser. Here's hoping Christmas 2019 doesn't happen again.
public class MessageEventListener  extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(MessageEventListener.class);

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }
        GuildConfig config = GuildConfig.get(event.getGuild().getId());
        if(config.getWhitelistedChannels().contains(event.getChannel().getId())){
            return;
        }
        logger.debug("channel isn't whitelisted!");
        if(config.getEnabledAutoMod().contains("duplicates")){
            boolean dupe = DupeChecker.addAndCheck(event.getMessage());
            if(dupe){
                Mute.mute(event.getGuild(), event.getMember(), "Anti-Duplicate triggered.");
                Audit.log(event.getGuild(), event.getJDA(), "Anti-Duplicate triggered.", event.getJDA().getSelfUser().getAsTag(), event.getJDA().getSelfUser().getAvatarUrl(),
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
        if(ConfigTools.canUseBot(message.getMember())){
            return;
        }

        GuildConfig config = GuildConfig.get(message.getGuild().getId());

        List<AutoModResponse> responses = new ArrayList<>();
        ArrayList<String> enabledAutomod = config.getEnabledAutoMod();

        if(enabledAutomod.contains("bos")){
            responses.add(AutoModder.banOnSight(message.getGuild(), message.getContentRaw()));
        }

        if(enabledAutomod.contains("mos")){
            responses.add(AutoModder.muteOnSight(message.getGuild(), message.getContentRaw()));
        }

        if(enabledAutomod.contains("dos")){
            responses.add(AutoModder.deleteOnSight(message.getGuild(), message));
        }

        if(enabledAutomod.contains("invites")){
            responses.add(AutoModder.invites(message.getGuild(), message.getContentRaw()));
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
            Audit.log(message.getGuild(), message.getJDA(), "Ban on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + banResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());

        } else if(mute) { //need to mute
            logger.debug("Mute Triggered");
            AutoModResponse muteResp = getResponse(responses, "mos");

            //Mute.mute(message.getGuild(), message.getMember(), "Mute on Sight triggered. Trigger: `" + muteResp.getTriggerPhrase() + "`. ");
            Audit.log(message.getGuild(), message.getJDA(), "Mute on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + muteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        }

        if(delete){//need to delete
            logger.debug("Delete Triggered");
            AutoModResponse deleteResp = getResponse(responses, "dos");

            message.delete().queue();
            Audit.log(message.getGuild(), message.getJDA(), "Delete on Sight triggered.", message.getJDA().getSelfUser().getAsTag(),
                    message.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + deleteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + message.getAuthor().getAsMention() +
                            "\nMessage: " + message.getContentRaw());
        } else if (invite){ //message didn't need to be deleted, does it have an invite?
            logger.debug("Invite Triggered");
            AutoModResponse inviteResp = getResponse(responses, "invites");
            message.delete().queue();

            String action = config.getInviteName();
            if(action.equalsIgnoreCase("ban")){
                Objects.requireNonNull(message.getMember()).ban(0).reason("Invite Kick Triggered").queue();
                Audit.log(message.getGuild(), message.getJDA(), "Invite deletion + Ban  triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());

            } else if (action.equalsIgnoreCase("kick")){
                Objects.requireNonNull(message.getMember()).kick().reason("Invite Kick Triggered").queue();
                Audit.log(message.getGuild(), message.getJDA(), "Invite deletion + Kick  triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());
            } else {
                Audit.log(message.getGuild(), message.getJDA(), "Invite deletion triggered.", message.getJDA().getSelfUser().getAsTag(),
                        message.getJDA().getSelfUser().getAvatarUrl(), "Invite: `" + inviteResp.getTriggerPhrase() + "`." +
                                "\nUser: " + message.getAuthor().getAsMention() +
                                "\nMessage: " + message.getContentRaw());
            }


        }
    }

    static AutoModResponse getResponse(List<AutoModResponse> responses, String automod){
        for(AutoModResponse response : responses){
            if(response.getAutoMod().equalsIgnoreCase(automod)){
                return response;
            }
        }
        return new AutoModResponse("", AutoModActions.NOTHING, "", automod);
    }
}
