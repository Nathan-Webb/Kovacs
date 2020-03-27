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

package com.kovacs.tools;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.commandclient.CustomClientBuilder;
import net.dv8tion.jda.api.Permission;

public class StringCleaning {
    public static String normalizeSpaces(String s){
        return s.replaceAll(" +", " "); //any spaces more than 1 will be replaced with one space
    }

    public static String removeUrlKeepInvite(String s){
        return s.replaceAll("(https?://)?(www\\.)?((discord|invite)\\.(gg|li|me|io)|discordapp\\.co/invite)/", "");
    }

    public static String normalizeSpacesClearCommas(String s){
        return clearCommas(normalizeSpaces(s));
    }

    public static String clearCommas(String s){
        return s.replaceAll(",", "");
    }

    //assume this is just a bunch of mentions/numbers with uniform spaces, no other stuff to clean up
    public static String[] extractIDsFromIdealStr(String s){
        String[] split = s.split(" ");
        String[] extractedIDs = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            String toClean = split[i];
            toClean = toClean.replaceAll("[<@&!#>]", "");
            extractedIDs[i] = toClean;
        }
        return extractedIDs;
    }

    public static String removeAllMentions(String s){
        return s.replaceAll("<[@&!#]+\\d+>", "");
    }
}




