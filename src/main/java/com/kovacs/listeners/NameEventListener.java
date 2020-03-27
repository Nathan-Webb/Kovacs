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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NameEventListener extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(NameEventListener.class);

    //todo this isn't working
    @Override
    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
        scanName(event.getMember(), event.getNewNickname());

    }

    //warning: this is fucked if you have it in more than 2/3 guilds
    @Override
    public void onUserUpdateName(@Nonnull UserUpdateNameEvent event) {
        Member member = event.getJDA().getMutualGuilds(event.getUser()).get(0).getMember(event.getUser());
        scanName(member, event.getNewName());
    }

    static void scanName(Member member, String name){
        if(Config.canUseBot(member)){
            //todo remove this
            //return;
        }

        List<AutoModResponse> responses = new ArrayList<>();

        if(Config.arrayContains("enabledAutoMod", "bos")){
            responses.add(AutoModder.banOnSight(name));
        }

        if(Config.arrayContains("enabledAutoMod", "mos")){
            responses.add(AutoModder.muteOnSight(name));
        }

        if(Config.arrayContains("enabledAutoMod", "clean")){
            responses.add(AutoModder.cleanOnSight(name));
        }
        if(Config.arrayContains("enabledAutoMod", "dehoist")){
            responses.add(AutoModder.dehoistOnSight(name));
        }


        boolean ban = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.BAN));
        boolean mute = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.MUTE));
        boolean clean = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.CLEAN));
        boolean dehoist = responses.stream().anyMatch(resp -> resp.getModerationAction().equals(AutoModActions.DEHOIST));
        if(ban){ //need to ban - dont bother looking for mute / clean
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
            member.getGuild().ban(member, 0, "Ban on Sight triggered. Trigger: `" + banResp.getTriggerPhrase() + "`. ").queue();
            Audit.log(member.getJDA(), "Ban on Sight triggered.", member.getJDA().getSelfUser().getAsTag(),
                    member.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + banResp.getTriggerPhrase() + "`." +
                            "\nUser: " + member.getAsMention() +
                            "\nName: " + name);
            return; //don't need to dehoist or clean since the member is banned

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
            Mute.mute(member.getGuild(), member, "Mute on Sight triggered. Trigger: `" + muteResp.getTriggerPhrase() + "`. ");
            Audit.log(member.getJDA(), "Ban on Sight triggered.", member.getJDA().getSelfUser().getAsTag(),
                    member.getJDA().getSelfUser().getAvatarUrl(), "Trigger: `" + muteResp.getTriggerPhrase() + "`." +
                            "\nUser: " + member.getAsMention() +
                            "\nName: " + name);
        }
        AutoModResponse cleanResp = null;
        AutoModResponse dehoistResp = null;
        for(AutoModResponse response : responses){
            if(response.getAutoMod().equalsIgnoreCase("clean")){
                cleanResp = response;
                break;
            }
        }

        for(AutoModResponse response : responses){
            if(response.getAutoMod().equalsIgnoreCase("dehoist")){
                dehoistResp = response;
                break;
            }
        }
        if(cleanResp == null || dehoistResp == null){
            return;
        }


        if(clean && dehoist){ //both
            logger.debug("Dehoist and Clean triggered.");
            String newName = AutoModder.dehoistOnSight(AutoModder.cleanOnSight(name).getModeratedString())
                    .getModeratedString();

            member.modifyNickname(newName).queue();
            Audit.log(member.getJDA(), "Dehoist+Clean  triggered.", member.getJDA().getSelfUser().getAsTag(),
                    member.getJDA().getSelfUser().getAvatarUrl(), "User: " + member.getAsMention() +
                            "\nName: " + name);

        } else if(clean){ //only one was true, was it clean?
            logger.debug("Clean triggered.");

            String newName = cleanResp.getModeratedString();
            member.modifyNickname(newName).queue();
            Audit.log(member.getJDA(), "Clean triggered.", member.getJDA().getSelfUser().getAsTag(),
                    member.getJDA().getSelfUser().getAvatarUrl(), "User: " + member.getAsMention() +
                            "\nName: " + name);


        } else if(dehoist){//guess not, is it dehoist?
            logger.debug("Dehoist triggered.");
            String newName = dehoistResp.getModeratedString();
            member.modifyNickname(newName).queue();
            Audit.log(member.getJDA(), "Dehoist triggered.", member.getJDA().getSelfUser().getAsTag(),
                    member.getJDA().getSelfUser().getAvatarUrl(), "User: " + member.getAsMention() +
                            "\nName: " + name);

        } //all done!
    }
}
