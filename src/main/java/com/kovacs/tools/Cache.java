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

import com.kovacs.database.objects.GuildConfig;
import org.cache2k.Cache2kBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Cache {
    public static org.cache2k.Cache<String, ArrayList<String>> BOS = new Cache2kBuilder<String, ArrayList<String>>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(guildID -> GuildConfig.get(guildID).getBOS())
            .build();

    public static org.cache2k.Cache<String, ArrayList<String>> MOS = new Cache2kBuilder<String, ArrayList<String>>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(guildID -> GuildConfig.get(guildID).getMOS())
            .build();

    public static org.cache2k.Cache<String, ArrayList<String>> DOS = new Cache2kBuilder<String, ArrayList<String>>(){}
            .expireAfterWrite(1, TimeUnit.HOURS)
            .loader(guildID -> GuildConfig.get(guildID).getDOS())
            .build();
}
