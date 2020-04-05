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

import com.kovacs.database.GuildConfig;
import net.dv8tion.jda.api.entities.Message;
import net.ricecode.similarity.JaroWinklerStrategy;
import net.ricecode.similarity.SimilarityStrategy;
import net.ricecode.similarity.StringSimilarityService;
import net.ricecode.similarity.StringSimilarityServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class DupeChecker {
    private static HashMap<String, Duplicate> duplicates = new HashMap<>();

    final static Logger logger = LoggerFactory.getLogger(DupeChecker.class);

    public static boolean addAndCheck(Message message){
        String userID = message.getAuthor().getId();
        String messageStr = message.getContentRaw();
        Duplicate duplicate = duplicates.get(userID);
        if(duplicate == null){
            duplicates.put(userID, new Duplicate(userID, messageStr));
            return false;
        }
        if(areSimilar(duplicate.getMessage(), messageStr)){ //found dupe
            int dupeAmount = duplicate.getAndAddOne();
            if(dupeAmount > GuildConfig.get(message.getGuild().getId()).getDuplicateThreshold()){ //uh oh! more than N duplicate messages!
                duplicates.put(userID, duplicate);
                return true;
            } else {
                duplicates.put(userID, duplicate);
                return false;
            }
        } else { //not dupe
            duplicate.resetWithNewMessage(messageStr);
            duplicates.put(userID, duplicate);
            return false;
        }
    }

    //todo we need to test this
    public static boolean areSimilar(String string1, String string2){
       SimilarityStrategy strategy = new JaroWinklerStrategy();
        StringSimilarityService service = new StringSimilarityServiceImpl(strategy);
        double score = service.score(string1, string2); // Score is 0.90
        logger.debug(String.valueOf(score));
        return score > 0.95;
    }
}
