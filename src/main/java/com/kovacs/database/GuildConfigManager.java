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

package com.kovacs.database;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;

public class GuildConfigManager implements GuildSettingsManager {

    @Nullable
    @Override
    public Object getSettings(Guild guild) {
        //todo grab settings from mongodb and return the object
        return null;
    }

    @Override
    public void init() {
        //startup db connection here?
    }

    @Override
    public void shutdown() {
        //close db connection here?
    }
}
