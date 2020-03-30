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
import java.util.Collection;
import java.util.Collections;

public class GuildConfig implements GuildSettingsProvider {
    private long auditChannel, mutedRole, guildID;
    private int duplicateThreshold;
    private String prefix, inviteName, fallbackName;
    private Collection<Long> whitelistedRoles, sudoRoles, whitelistedUsers, sudoUsers;
    private Collection<String> BOS, DOS, MOS, enabledAutoMod, whitelistedInvites;

    public GuildConfig setAuditChannel(long auditChannel) {
        this.auditChannel = auditChannel;
        return this;
    }

    public GuildConfig setMutedRole(long mutedRole) {
        this.mutedRole = mutedRole;
        return this;
    }

    public GuildConfig setGuildID(long guildID) {
        this.guildID = guildID;
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

    public GuildConfig setWhitelistedRoles(Collection<Long> whitelistedRoles) {
        this.whitelistedRoles = whitelistedRoles;
        return this;
    }

    public GuildConfig setSudoRoles(Collection<Long> sudoRoles) {
        this.sudoRoles = sudoRoles;
        return this;
    }

    public GuildConfig setWhitelistedUsers(Collection<Long> whitelistedUsers) {
        this.whitelistedUsers = whitelistedUsers;
        return this;
    }

    public GuildConfig setSudoUsers(Collection<Long> sudoUsers) {
        this.sudoUsers = sudoUsers;
        return this;
    }

    public GuildConfig setBOS(Collection<String> BOS) {
        this.BOS = BOS;
        return this;
    }

    public GuildConfig setDOS(Collection<String> DOS) {
        this.DOS = DOS;
        return this;
    }

    public GuildConfig setMOS(Collection<String> MOS) {
        this.MOS = MOS;
        return this;
    }

    public GuildConfig setEnabledAutoMod(Collection<String> enabledAutoMod) {
        this.enabledAutoMod = enabledAutoMod;
        return this;
    }

    public GuildConfig setWhitelistedInvites(Collection<String> whitelistedInvites) {
        this.whitelistedInvites = whitelistedInvites;
        return this;
    }

    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        return Collections.singleton(prefix);
    }

    public long getAuditChannel() {
        return auditChannel;
    }

    public long getMutedRole() {
        return mutedRole;
    }

    public long getGuildID() {
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

    public Collection<Long> getWhitelistedRoles() {
        return whitelistedRoles;
    }

    public Collection<Long> getSudoRoles() {
        return sudoRoles;
    }

    public Collection<Long> getWhitelistedUsers() {
        return whitelistedUsers;
    }

    public Collection<Long> getSudoUsers() {
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

    public Collection<String> getEnabledAutoMod() {
        return enabledAutoMod;
    }

    public Collection<String> getWhitelistedInvites() {
        return whitelistedInvites;
    }
}
