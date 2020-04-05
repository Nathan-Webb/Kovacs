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

package com.kovacs.commands.moderation;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.kovacs.database.ConfigTools;
import com.kovacs.database.GuildConfig;
import com.kovacs.tools.Audit;
import com.kovacs.tools.Sanitizers;
import com.kovacs.tools.Unicode;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ManageNicks extends Command {
    public ManageNicks() {
        this.name = "ManageNicks";
        this.aliases = new String[]{"nicks", "nick"};
    }

    final static Logger logger = LoggerFactory.getLogger(ManageNicks.class);

    @Override
    protected void execute(CommandEvent event) {
        if(!ConfigTools.isSudo(event.getMember())){
            event.reply("You must be a sudo user to run this command!");
            return;
        }

        String thingToDo = Sanitizers.removeAllMentions(event.getArgs()).toLowerCase().trim();

        List<Member> mentionedMembers = event.getMessage().getMentionedMembers();
        if(mentionedMembers.size() == 0){
            mentionedMembers = event.getGuild().getMembers();
        }
        if(!thingToDo.equals("dehoist") && !thingToDo.equals("normalize") && !thingToDo.equals("clean")){
            event.reply("You must provide either `dehoist`, `normalize`, or `clean`");
            return;
        }

        int count = 0;
        for(Member member : mentionedMembers){
            String name = member.getEffectiveName();
            if(thingToDo.equals("dehoist") || thingToDo.equals("clean")){ //if we are dehoisting or cleaning as a selected option
                name = Unicode.dehoist(event.getGuild(), name);
            }

            if(thingToDo.equals("normalize") || thingToDo.equals("clean")) { //if we are normalizing or cleaning
                name = Unicode.cleanEverything(event.getGuild(), name);
            }

            if(name.equals("")){ //uh oh! empty nickname
                name = GuildConfig.get(event.getGuild().getId()).getFallbackName();
            }
            if(!name.equalsIgnoreCase(member.getEffectiveName())){ //ended up with different name
                if(event.getSelfMember().canInteract(member)){ // can we actually modify their nick?
                    member.modifyNickname(name).queue();
                    try{
                        Thread.sleep(1000);
                    } catch (InterruptedException e){
                        //do nothing
                    }
                    count++;
                }
            }
        }
        event.reply("Changed the nicknames of " + count + " users!");
        Audit.log(this, event, "Managed the nicknames of `" + count + "` members. Action: `" + thingToDo + "`.");
    }
}
