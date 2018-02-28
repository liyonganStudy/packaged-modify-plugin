package com.lya.gradle

import com.android.builder.dependency.level2.AndroidDependency
import com.android.builder.dependency.level2.Dependency
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import com.google.common.collect.Lists
import com.lya.gradle.collector.res.ResourceEntry
import com.lya.gradle.collector.res.StyleableEntry

class Extension {
    int packageId
    Collection<String> excludes = new HashSet<>()
    boolean shareCommonAarWithHost = false

    def stripDependencies = [] as Collection<Dependency>
    def stripAarDependencies = [] as Set
    Collection<AndroidDependency> retainedAarLibs = []
    File commonSymbolFile
    /**
     * All resources(e.g. drawable, layout...) this library can access
     * include resources of self-project and dependence(direct&transitive) project
     */
    ListMultimap<String, ResourceEntry> aarResources = ArrayListMultimap.create()
    /**
     * All styleables this library can access, like "aarResources"
     */
    List<StyleableEntry> aarStyleables = Lists.newArrayList()
}