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
package com.kovacs;

import com.kovacs.commands.moderation.Mute;
import com.kovacs.tools.Config;
import com.kovacs.tools.Unicode;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;


public class EventListener extends ListenerAdapter {
    final static Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        logger.debug("Bot ready!");
    }

    //todo dehoist and clean need to be done in one go, or else the event will trigger over and over again
    /*
    Priority:
    dehoist
    clean
    Mute
    Delete
    Ban

     */

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        //bos -> mos -> dehoist -> clean
        if(Config.canUseBot(event.getMember())) { //user can use bot, no need to scan
            return;
        }
            if(Config.arrayContains("enabledAutoMod", "bos")) {
                List<String> banOnSight = Config.onSightCache.get("bos");
                onSight(banOnSight, event, "ban");
            }

            if(Config.arrayContains("enabledAutoMod", "mos")) {
                List<String> muteOnSight = Config.onSightCache.get("mos");
                onSight(muteOnSight, event, "mute");
            }

            if(Config.arrayContains("enabledAutoMod", "clean"))
                clean(event);

    }


    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if(Config.canUseBot(event.getMember())){
            return;
        }

        String content = event.getMessage().getContentRaw();

        if(Config.arrayContains("enabledAutoMod", "mos")){
            if(muteOrBan(Config.onSightCache.get("mos"), event, "mute", content)){
                if(Config.arrayContains("enabledAutoMod", "dos")){
                    deleteOnSight(event.getMessage());
                    return;
                }
             return;
            }
        }

        if(Config.arrayContains("enabledAutoMod", "bos")){
            if(muteOrBan(Config.onSightCache.get("dos"), event, "ban", content)){
                return;
            }
        }

        if(Config.arrayContains("enabledAutoMod", "dos")){
            if(deleteOnSight(event.getMessage())){
                return;
            }
        }




    }

    private void clean(GenericGuildMemberEvent event){ //nick change and join
        if(event instanceof  GuildMemberUpdateNicknameEvent){
            String nick = ((GuildMemberUpdateNicknameEvent) event).getNewNickname();
            String cleaned = cleanAndDehoist(nick);
            if(!cleaned.equalsIgnoreCase(nick)){
                event.getMember().modifyNickname(nick).queue();
            }
        } else if(event instanceof GuildMemberJoinEvent) {
            String userName = event.getUser().getName();
            String cleaned = cleanAndDehoist(userName);
            if(!cleaned.equalsIgnoreCase(userName)){
                event.getMember().modifyNickname(cleaned).queue();
            }
        }
    }

    private String cleanAndDehoist(String name){
        return clean(deHoist(name));
    }

    private String clean(String nickname){
        String normalized = Unicode.normalizeAndRemoveSpoofs(nickname);
        if(!normalized.equalsIgnoreCase(nickname)){
            return normalized;
        }
        return nickname;
    }

    private void deHoist(GenericGuildMemberEvent event){//nick change and join
        String name = event.getMember().getEffectiveName();
    }

    private String deHoist(String name){
        char cTest = name.charAt(0);
        if(Unicode.isHoistChar(cTest)){ //uh oh, hoister!
            return Unicode.dehoist(name);
        }
        return name;
    }

    private boolean deleteOnSight(Message m){
        List<String> badWords = Config.onSightCache.get("dos");
        String skeletonMessage = Unicode.getCleanedForFilter(m.getContentRaw());
        for(String word : badWords){
            if(skeletonMessage.contains(word)){
                m.delete().queue();
                return true;
            }
        }
        return false;
    }


    private boolean onSight(List<String> naughtyWords, GenericGuildMemberEvent event, String muteOrBan){ //mute or ban on sight - nick change and join
        if(event instanceof GuildMemberUpdateNicknameEvent){ //nickname change - check if the new nick is ok
            String newNickname = ((GuildMemberUpdateNicknameEvent) event).getNewNickname();
            if(newNickname == null){
                logger.debug("Found null. Setting name to their current username.");
                newNickname = event.getUser().getName();
            }
            String normalizedSkeletonNickName = Unicode.getCleanedForFilter(newNickname);
            return muteOrBan(naughtyWords, event, muteOrBan, normalizedSkeletonNickName);
        } else if (event instanceof GuildMemberJoinEvent){ //member joined - check if their username is ok
            String normalizedSkeletonName = Unicode.getCleanedForFilter(event.getUser().getName().toLowerCase());

            return muteOrBan(naughtyWords, event, muteOrBan, normalizedSkeletonName);
        } else {
            return false;
        }
    }

    private boolean muteOrBan(List<String> naughtyWords, GenericGuildMemberEvent event, String muteOrBan, String stringToCheck) {
        return muteOrBan(naughtyWords, event.getMember(), muteOrBan, stringToCheck);
    }



    private boolean muteOrBan(List<String> naughtyWords, MessageReceivedEvent event, String muteOrBan, String stringToCheck) {
     return muteOrBan(naughtyWords, event.getMember(), muteOrBan, stringToCheck);
    }

    private boolean muteOrBan(List<String> naughtyWords, Member member, String muteOrBan, String stringToCheck){
        for (String word : naughtyWords) {
            if (stringToCheck.contains(Unicode.getCleanedForFilter(word))) { //found a word in their nick
                if(muteOrBan.equalsIgnoreCase("mute")) {
                    Mute.mute(member.getGuild(), member, muteOrBan + "-on-Sight word detected: " + word);
                } else if(muteOrBan.equalsIgnoreCase("ban")){
                    member.getGuild().ban(member, 0, muteOrBan + "-on-Sight word detected: " + word).queue();
                }
                return true;
            }
        }
        return false;
    }


}
