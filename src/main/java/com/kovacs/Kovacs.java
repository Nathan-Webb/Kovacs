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

import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.SpoofChecker;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.kovacs.commandclient.CustomClientBuilder;
import com.kovacs.commands.generic.*;
import com.jagrosh.jdautilities.command.CommandClient;
import com.kovacs.commands.config.*;
import com.kovacs.commands.moderation.*;
import com.kovacs.commands.notes.CheckNotes;
import com.kovacs.commands.owner.ReloadConfig;
import com.kovacs.commands.owner.Shutdown;
import com.kovacs.commands.owner.Test;
import com.kovacs.database.GuildConfigManager;
import com.kovacs.listeners.*;
import com.kovacs.tools.BotConfig;
import com.kovacs.tools.Unicode;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static net.dv8tion.jda.api.entities.Activity.*;

public class Kovacs {
    final static Logger logger = LoggerFactory.getLogger(Kovacs.class);
    public static JSONObject config;
    public static JDA bot;
    public static EventWaiter waiter;
    private static ScheduledExecutorService eventWaiterScheduler = Executors.newScheduledThreadPool(1);
    public static CommandClient commandClient;
    public static String[] autoMod = new String[]{"bos", "mos", "dos", "dehoist",
            "normalize", "janitor", "invites", "duplicates"};

    //todo migrate to non-deprecated mongo functions
    public static void main(String[] args) throws LoginException, IOException {
        config = BotConfig.open();
        waiter = new EventWaiter(eventWaiterScheduler, false);
        SpoofChecker checker = new SpoofChecker.Builder().setChecks(SpoofChecker.CONFUSABLE).build();
        Unicode.setNormalizer(Normalizer2.getNFKCInstance());
        Unicode.setSpoofChecker(checker);
        commandClient = getCommandClient();

        bot = new JDABuilder(AccountType.BOT)
                .addEventListeners(commandClient, new EventListener(), new GuildEventListener(), new MessageEventListener(), new NameEventListener(), waiter)
                .setToken(config.getString("token"))
                .setGuildSubscriptionsEnabled(true)
                .build();
    }

    public static CommandClient getCommandClient(){
        Command[] configCommands = new Command[]{new AddBOS(), new AddDOS(), new AddMOS(), new Sudo(),
                new RemoveBOS(), new Blacklist(), new WhitelistChannels(), new BlacklistChannels(), new AutoMod(), new SetAuditChannel(),
                new RemoveSudo(), new SetMutedRole(), new ShowConfig(), new Whitelist(), new Sync(),
                new Automod(), new SetDuplicateThreshold(), new RemoveDOS(), new RemoveMOS(), new Prefix(),
                new WhitelistInvites(), new BlackistInvites(), new SetFallbackName(), new SetInviteName()};

        Command[] moderation = new Command[]{new Ban(), new Mute(), new Unban(), new UnMute(), new Prune(),
                new ManageNicks()};

        Command[] generic = new Command[]{new Ping(), new Normalize(), new Help(), new BotInfo(), new UserInfo()};

        Command[] owner = new Command[]{new Shutdown(), new ReloadConfig(), new Test()};

        Command[] notes = new Command[]{new CheckNotes()};

        return new CustomClientBuilder()
                .setOwnerId(config.getString("botOwner"))
                .setCoOwnerIds(config.getJSONArray("coOwners").toList().toArray(new String[]{}))
                .setAlternativePrefix("@mention")
                .addCommands(configCommands)
                .setGuildSettingsManager(new GuildConfigManager())
                .addCommands(moderation)
                .addCommands(generic)
                .addCommands(owner)
                .addCommands(notes)
                .setActivity(Activity.of(ActivityType.valueOf(ActivityType.class, config.getString("activityType")),
                        config.getString("activityMessage")))
                .useHelpBuilder(false)
                .build();
    }

    public static void addIfMissing(Collection<String> target, Collection<String> toAdd){
        toAdd.stream().distinct().forEach(string -> {
            if(!target.contains(string)){
                target.add(string);
            }
        });
    }
}
