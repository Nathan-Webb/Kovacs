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

import com.jagrosh.jdautilities.command.GuildSettingsProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GuildConfig implements GuildSettingsProvider {
    private String auditChannel;
    private String mutedRole;
    private String guildID;
    private int duplicateThreshold = 4;
    private String prefix = ".";
    private String inviteName = "invite";
    private String fallbackName = "fallback";
    private ArrayList<String> whitelistedRoles;
    private ArrayList<String> sudoRoles;
    private ArrayList<String> whitelistedUsers;
    private ArrayList<String> sudoUsers;
    private ArrayList<String> BOS;
    private ArrayList<String> DOS;
    private ArrayList<String> MOS;
    private ArrayList<String> enabledAutoMod;
    private ArrayList<String> whitelistedInvites = new ArrayList<>();

    public static GuildConfig get(String guildID){
        return Database.getConfig(guildID);
    }

    GuildConfig(String guildID){ //create with defaults
        this.guildID = guildID;
    }

    public GuildConfig setAuditChannel(String auditChannel) {
        this.auditChannel = auditChannel;
        return this;
    }

    public GuildConfig setMutedRole(String mutedRole) {
        this.mutedRole = mutedRole;
        return this;
    }


    public GuildConfig setDuplicateThreshold(int duplicateThreshold) {
        this.duplicateThreshold = duplicateThreshold;
        return this;
    }

    public GuildConfig setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public GuildConfig setInviteName(String inviteName) {
        this.inviteName = inviteName;
        return this;
    }

    public GuildConfig setFallbackName(String fallbackName) {
        this.fallbackName = fallbackName;
        return this;
    }

    public GuildConfig setWhitelistedRoles(ArrayList<String> whitelistedRoles) {
        this.whitelistedRoles = whitelistedRoles;
        return this;
    }

    public GuildConfig setSudoRoles(ArrayList<String> sudoRoles) {
        this.sudoRoles = sudoRoles;
        return this;
    }

    public GuildConfig setWhitelistedUsers(ArrayList<String> whitelistedUsers) {
        this.whitelistedUsers = whitelistedUsers;
        return this;
    }

    public GuildConfig setSudoUsers(ArrayList<String> sudoUsers) {
        this.sudoUsers = sudoUsers;
        return this;
    }

    public GuildConfig setBOS(ArrayList<String> BOS) {
        this.BOS = BOS;
        return this;
    }

    public GuildConfig setDOS(ArrayList<String> DOS) {
        this.DOS = DOS;
        return this;
    }

    public GuildConfig setMOS(ArrayList<String> MOS) {
        this.MOS = MOS;
        return this;
    }

    public GuildConfig setEnabledAutoMod(ArrayList<String> enabledAutoMod) {
        this.enabledAutoMod = enabledAutoMod;
        return this;
    }

    public GuildConfig setWhitelistedInvites(ArrayList<String> whitelistedInvites) {
        this.whitelistedInvites = whitelistedInvites;
        return this;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        return Collections.singleton(getPrefix());
    }

    public String getAuditChannel() {
        return auditChannel;
    }

    public String getMutedRole() {
        return mutedRole;
    }

    public String getGuildID() {
        return guildID;
    }

    public int getDuplicateThreshold() {
        return duplicateThreshold;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getInviteName() {
        return inviteName;
    }

    public String getFallbackName() {
        return fallbackName;
    }

    public Collection<String> getWhitelistedRoles() {
        return whitelistedRoles;
    }

    public Collection<String> getSudoRoles() {
        return sudoRoles;
    }

    public Collection<String> getWhitelistedUsers() {
        return whitelistedUsers;
    }

    public Collection<String> getSudoUsers() {
        return sudoUsers;
    }

    public Collection<String> getBOS() {
        return BOS;
    }

    public Collection<String> getDOS() {
        return DOS;
    }

    public Collection<String> getMOS() {
        return MOS;
    }

    public ArrayList<String> getEnabledAutoMod() {
        return enabledAutoMod;
    }

    public Collection<String> getWhitelistedInvites() {
        return whitelistedInvites;
    }
}
