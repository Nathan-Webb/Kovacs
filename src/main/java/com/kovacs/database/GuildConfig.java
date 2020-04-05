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
import com.kovacs.Kovacs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class GuildConfig implements GuildSettingsProvider {
    private String auditChannel = "";
    private String mutedRole = "";
    private String guildID;
    private int duplicateThreshold = 4;
    private String prefix = Kovacs.config.getString("prefix");
    private String inviteName = "invite";
    private String fallbackName = "fallback";
    private ArrayList<String> whitelistedRoles = new ArrayList<>();
    private ArrayList<String> sudoRoles = new ArrayList<>();
    private ArrayList<String> whitelistedUsers = new ArrayList<>();
    private ArrayList<String> sudoUsers = new ArrayList<>();
    private ArrayList<String> BOS = new ArrayList<>();
    private ArrayList<String> DOS = new ArrayList<>();
    private ArrayList<String> MOS = new ArrayList<>();
    private ArrayList<String> enabledAutoMod = new ArrayList<>();
    private ArrayList<String> whitelistedInvites = new ArrayList<>();
    private ArrayList<String> whitelistedChannels = new ArrayList<>();

    final static Logger logger = LoggerFactory.getLogger(GuildConfig.class);

    public static GuildConfig get(String guildID){
        return Database.configCache.get(guildID);

    }

    GuildConfig(String guildID){ //create with defaults
        this.guildID = guildID;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        return Collections.singleton(getPrefix());
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

    public GuildConfig setWhitelistedChannels(ArrayList<String> whitelistedChannels) {
        this.whitelistedChannels = whitelistedChannels;
        return this;
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

    public ArrayList<String> getWhitelistedRoles() {
        return whitelistedRoles;
    }

    public ArrayList<String> getSudoRoles() {
        return sudoRoles;
    }

    public ArrayList<String> getWhitelistedUsers() {
        return whitelistedUsers;
    }

    public ArrayList<String> getSudoUsers() {
        return sudoUsers;
    }

    public ArrayList<String> getBOS() {
        return BOS;
    }

    public ArrayList<String> getDOS() {
        return DOS;
    }

    public ArrayList<String> getMOS() {
        return MOS;
    }

    public ArrayList<String> getEnabledAutoMod() {
        return enabledAutoMod;
    }

    public ArrayList<String> getWhitelistedInvites() {
        return whitelistedInvites;
    }

    public ArrayList<String> getWhitelistedChannels() {
        return whitelistedChannels;
    }


}
