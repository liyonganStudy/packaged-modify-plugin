package com.lya.gradle

import com.android.builder.dependency.level2.Dependency

class Extension {
    int packageId
    Collection<String> excludes = new HashSet<>()

    def stripDependencies = [] as Collection<Dependency>
    def stripAarDependencies = [] as Set
}