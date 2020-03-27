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

public class AutoModResponse {
    private String moderatedString;
    private AutoModActions endResult; //kick/ban/mute
    private String triggerPhrase; //if there is one
    private String autoMod; //bos mos

    public AutoModResponse(String moderatedString, AutoModActions getModerationAction, String triggerPhrase, String autoMod) {
        this.moderatedString = moderatedString;
        this.endResult = getModerationAction;
        this.triggerPhrase = triggerPhrase;
        this.autoMod = autoMod;
    }

    public String getModeratedString() {
        return moderatedString;
    }

    public AutoModActions getModerationAction() {
        return endResult;
    }

    public String getTriggerPhrase() {
        return triggerPhrase;
    }

    public String getAutoMod() {
        return autoMod;
    }
}
