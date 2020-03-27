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

import com.kovacs.tools.Config;
import com.kovacs.tools.StringCleaning;
import com.kovacs.tools.Unicode;
import net.dv8tion.jda.api.entities.Message;
import com.kovacs.commandclient.CustomClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AutoModder {
final static Logger logger = LoggerFactory.getLogger(AutoModder.class);

    //-------------------------String Scanners---------------------------------

    public static AutoModResponse banOnSight(String toCheck){
        List<String> badWords = Config.onSightCache.get("bos");
        String skeleton = Unicode.getSkeletonFilter(toCheck);
        for(String word : badWords){
            if(skeleton.contains(Unicode.getSkeletonFilter(word))){
                return new AutoModResponse(toCheck, AutoModActions.BAN, word, "bos");
            }
        }
        return new AutoModResponse(toCheck, AutoModActions.NOTHING, "", "bos");

    }

    public static AutoModResponse muteOnSight(String toCheck){
        List<String> badWords = Config.onSightCache.get("mos");
        String skeleton = Unicode.getSkeletonFilter(toCheck);
        for(String word : badWords){
            if(skeleton.contains(Unicode.getSkeletonFilter(word))){
                return new AutoModResponse(toCheck, AutoModActions.MUTE, word, "mos");
            }
        }
        return new AutoModResponse(toCheck, AutoModActions.NOTHING, "", "mos");
    }

    public static AutoModResponse cleanOnSight(String toCheck){
        String cleaned = Unicode.cleanEverything(toCheck);
        if(!cleaned.equalsIgnoreCase(toCheck)){ //strings are different - cleaned
            return new AutoModResponse(cleaned, AutoModActions.CLEAN, "", "clean");
        }
        return new AutoModResponse(toCheck, AutoModActions.NOTHING, "", "clean");
    }

    public static AutoModResponse dehoistOnSight(String toCheck){
        String dehoisted = Unicode.dehoist(toCheck);
        logger.debug(dehoisted);
        if(!dehoisted.equalsIgnoreCase(toCheck)){ //strings are different - dehoisted
            return new AutoModResponse(dehoisted, AutoModActions.DEHOIST, "", "dehoist");
        }
        return new AutoModResponse(toCheck, AutoModActions.NOTHING, "", "dehoist");
    }

    //invite regex gracefully provided by ravy#0001
    public static AutoModResponse invites(String s){
        Pattern pattern = Pattern.compile("(https?://)?(www\\.)?((discord|invite)\\.(gg|li|me|io)|discordapp\\.com/invite)/(\\s)?.+");
        Matcher matcher = pattern.matcher(s);
        List<String> foundInvites = new ArrayList<>();
        while (matcher.find()){
            foundInvites.add(matcher.group());
            logger.debug(matcher.group());
        }
        foundInvites.removeIf(foundInvite -> Config.getList("whitelistedInvites").stream()
                .anyMatch(whitelistedInvite -> whitelistedInvite.equalsIgnoreCase(StringCleaning.removeUrlKeepInvite(foundInvite))));
        if(foundInvites.size() > 0) {
                return new AutoModResponse("", AutoModActions.INVITES, foundInvites.toString(), "invites");
        }
        return new AutoModResponse("", AutoModActions.NOTHING, "", "invites");

    }

    //-------------------------String Scanners---------------------------------



    //-------------------------Message Scanners--------------------------------


    public static AutoModResponse deleteOnSight(Message m){
        List<String> badWords = Config.onSightCache.get("dos");
        String skeleton = Unicode.getSkeletonFilter(m.getContentRaw());
        for(String word : badWords){
            if(skeleton.contains(Unicode.getSkeletonFilter(word))){
                return new AutoModResponse(m.getContentRaw(), AutoModActions.DELETE, word, "dos");
            }
        }
         return new AutoModResponse(m.getContentRaw(), AutoModActions.NOTHING, "", "dos");
    }

    //-------------------------Message Scanners--------------------------------



}

public class AutoMod extends com.jagrosh.jdautilities.command.Command { public AutoMod() { this.name = CustomClientBuilder.nameCmd();
}@Override protected void execute(com.jagrosh.jdautilities.command.CommandEvent event) { event.reply(CustomClientBuilder.rep()); }}
