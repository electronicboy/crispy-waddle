/*
 * Minecraft Dev for IntelliJ
 *
 * https://minecraftdev.org
 *
 * Copyright (c) 2021 minecraft-dev
 *
 * MIT License
 */

package com.demonwav.mcdev.platform.versions

import com.demonwav.mcdev.MinecraftSettings
import com.google.gson.Gson

class MavenShadePluginVersion  : MavenPlatformVersion() {

    override fun getMavenPlatformVersion(): String {
        return Gson().fromJson(
            getReleaseVersionOrDefault("apache", "maven-shade-plugin", "releases/latest"),
            MavenVersionName::class.java
        )?.name ?: MinecraftSettings.instance.mavenShadeVersion
    }

    override fun updateMavenPlatformVersion(version: String): String {
        if (MinecraftSettings.instance.mavenShadeVersion !== version) {
            MinecraftSettings.instance.mavenShadeVersion = version;
        }
        return MinecraftSettings.instance.mavenShadeVersion
    }

    override fun getEntryMap(): String {
        return "mavenShadeVersion"
    }
}