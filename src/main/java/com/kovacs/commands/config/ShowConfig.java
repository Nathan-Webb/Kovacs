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

package com.kovacs.commands.config;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.Kovacs;
import com.kovacs.tools.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Arrays;

public class ShowConfig extends Command {
    public ShowConfig() {
        this.name = "Config";
        this.aliases = new String[]{"showconfig", "settings", "conf", "showconf"};
        this.ownerCommand = true;
    }
final static Logger logger = LoggerFactory.getLogger(ShowConfig.class);

    @Override
    protected void execute(CommandEvent event) {
        String automod = Automod.getAutoModSettings();
        String[] dos = Config.onSightCache.get("dos").toArray(new String[]{});
        String[] mos = Config.onSightCache.get("mos").toArray(new String[]{});
        String[] bos = Config.onSightCache.get("bos").toArray(new String[]{});
        String[] whiteListedUsers = Config.getList("whitelistedUsers").toArray(new String[]{});
        String[] whiteListedRoles = Config.getList("whitelistedRoles").toArray(new String[]{});
        String[] whitelistedInvites = Config.getList("whitelistedInvites").toArray(new String[]{});
        String[] sudoUsers = Config.getList("sudo").toArray(new String[]{});
        String[] sudoRoles = Config.getList("sudoRoles").toArray(new String[]{});
        String root = Config.getString("root");

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.ORANGE).setTitle("Config");

        StringBuilder userBuilder = new StringBuilder();
        for(String id : whiteListedUsers){
            userBuilder.append(", ").append("<@!").append(id).append(">");;
        }

        StringBuilder rolebuilder = new StringBuilder();
        for(String id : whiteListedRoles){
            rolebuilder.append(", ").append("<@&").append(id).append(">");
        }

        StringBuilder sudoBuilder = new StringBuilder();
        for(String id : sudoUsers){
            sudoBuilder.append(", ").append("<@!").append(id).append(">");
        }

        StringBuilder sudoRoleBuilder = new StringBuilder();
        for(String id : sudoRoles){
            sudoRoleBuilder.append(", ").append("<@&").append(id).append(">");
        }

        String auditChannel = Config.getString("auditChannel");
        String mutedRole = Config.getString("mutedRole");

        String mentionedUsers = userBuilder.toString().replaceFirst(", ", "");
        String mentionedRoles = rolebuilder.toString().replaceFirst(", ", "");
        String mentionedSudo  = sudoBuilder.toString().replaceFirst(", ", "");
        String mentionedSudoRoles  = sudoRoleBuilder.toString().replaceFirst(", ", "");

        String dosStr =  Arrays.deepToString(dos).replaceAll("[\\[\\]]", "");
        String mosStr =  Arrays.deepToString(mos).replaceAll("[\\[\\]]", "");
        String bosStr =  Arrays.deepToString(bos).replaceAll("[\\[\\]]", "");
        String inviteStr =  Arrays.deepToString(whitelistedInvites).replaceAll("[\\[\\]]", "");
        builder.addField("Root", "<@" + root + ">", true)
                .addField("Sudo Users", (mentionedSudo.equals("") ? "None" : mentionedSudo), true)
                .addField("Sudo Roles", (mentionedSudoRoles.equals("") ? "None" : mentionedSudoRoles), true)
                .addField("Whitelisted Users", (mentionedUsers.equals("") ? "None" : mentionedUsers), true)
                .addField("Whitelisted Roles", (mentionedRoles.equals("") ? "None" : mentionedRoles), true)
                .addField("Whitelisted Invites", (inviteStr.equals("") ? "None" : inviteStr), true)
                .addField("Delete-on-sight", (dosStr.equals("") ? "None" : dosStr), false)
                .addField("Mute-on-sight", (mosStr.equals("") ? "None" : mosStr), true)
                .addField("Ban-on-sight", (bosStr.equals("") ? "None" : bosStr), true)
                .addField("Audit Channel", (auditChannel.equals("") ? "None" : "<#" + auditChannel + ">"), true)
                .addField("Muted Role", (mutedRole.equals("") ? "None" : "<@&" + mutedRole + ">"), true)
                .addField("Fallback Name", Config.getString("fallbackName"), true)
                .addField("Invite NickName", Config.getString("inviteName"), true)
                .addField("Duplicate Threshold", String.valueOf(Config.getInt("duplicateThreshold")), true)
                .addField("--", automod, false);
        event.reply(builder.build());




    }
}
