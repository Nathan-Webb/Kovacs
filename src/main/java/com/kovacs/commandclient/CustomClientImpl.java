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
package com.kovacs.commandclient;

import com.kovacs.tools.Config;
import com.jagrosh.jdautilities.command.*;
import com.jagrosh.jdautilities.command.impl.CommandClientImpl;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.events.EventException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class CustomClientImpl extends CommandClientImpl {
    final static Logger logger = LoggerFactory.getLogger(CustomClientImpl.class);

    public CustomClientImpl(String ownerId, String[] coOwnerIds, String prefix, String altprefix, Activity activity, OnlineStatus status, String serverInvite, String success, String warning, String error, String carbonKey, String botsKey, ArrayList<Command> commands, boolean useHelp, boolean shutdownAutomatically, Consumer<CommandEvent> helpConsumer, String helpWord, ScheduledExecutorService executor, int linkedCacheSize, AnnotatedModuleCompiler compiler, GuildSettingsManager manager) {
        super(ownerId, coOwnerIds, prefix, altprefix, activity, status, serverInvite, success, warning, error, carbonKey, botsKey, commands, useHelp, shutdownAutomatically, helpConsumer, helpWord, executor, linkedCacheSize, compiler, manager);
    }


    @Override
    public void onEvent(GenericEvent event)
    {
       if(event instanceof MessageReceivedEvent){
           if(((MessageReceivedEvent) event).getAuthor().isBot()){
               return;
           }
           if(Config.cantUseBot(Objects.requireNonNull(((MessageReceivedEvent) event).getMember()))) { //user isn't whitelisted to use the bot - ignore command
               return;
           }
       }
       try {
           super.onEvent(event);
       } catch (Exception e){
           logger.error("Exception: " + e.getMessage());
       }
    }
}
