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
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ULocale;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.kovacs.commandclient.CustomClientBuilder;
import com.kovacs.commands.generic.Ping;
import com.kovacs.commands.generic.Test;
import com.jagrosh.jdautilities.command.CommandClient;
import com.kovacs.commands.config.*;
import com.kovacs.commands.moderation.*;
import com.kovacs.tools.Config;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static net.dv8tion.jda.api.entities.Activity.*;

public class Kovacs {
    final static Logger logger = LoggerFactory.getLogger(Kovacs.class);
    public static JSONObject config;
    public static JDA bot;
    public static EventWaiter waiter;
    /*
    todo: mute/unmute, delete on sight, sync dos and mos, remove mos, dos, bos, show config, enable and disable automod, audit log
    //test onsight commands
    //where you left off: getting eventlistener set up
     */
    public static void main(String[] args) throws LoginException, IOException {
        config = Config.open();
        waiter = new EventWaiter(new ScheduledThreadPoolExecutor(1), true);
        SpoofChecker checker = new SpoofChecker.Builder().setChecks(SpoofChecker.CONFUSABLE).build();
        Unicode.setNormalizer(Normalizer2.getNFKCInstance());
        Unicode.setSpoofChecker(checker);

        Command[] configCommands = new Command[]{new AddBOS(), new AddDOS(), new AddMOS(), new AddOwner(),
                new Automod(), new Blacklist(), new ReloadConfig(), new RemoveOwner(), new SetMutedRole(),
                new ShowConfig(), new Whitelist()};

        Command[] moderation = new Command[]{new Ban(), new Mute(), new Unban(), new UnMute(), new Prune()};

        Command[] generic = new Command[]{new Ping(), new Test()};

        CommandClient commandClient = new CustomClientBuilder()
                .setOwnerId(config.getString("owner"))
                .setCoOwnerIds(config.getJSONArray("sudo").toList().toArray(new String[]{}))
                .setPrefix(config.getString("prefix"))
                .setAlternativePrefix("@mention")
                .addCommands(configCommands)
                .addCommands(moderation)
                .addCommands(generic) //what to do with this command
                .setActivity(Activity.of(ActivityType.valueOf(ActivityType.class, config.getString("activityType")),
                        config.getString("activityMessage")))
                .useHelpBuilder(false)
                .build();

        bot = new JDABuilder(AccountType.BOT)
                .addEventListeners(new EventListener(), commandClient)
                .setToken(config.getString("token"))
                .build();
    }
}
