package com.lya.gradle.hook

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.tasks.PrepareDependenciesTask
import com.android.builder.dependency.level2.AndroidDependency
import com.android.builder.dependency.level2.Dependency
import com.android.builder.dependency.level2.JavaDependency
import com.google.common.collect.ImmutableList
import org.gradle.api.Project

/**
 * Gather list of dependencies(aar&jar) need to be stripped&retained after the PrepareDependenciesTask finished.
 * The entire stripped operation throughout the build lifecycle is based on the result of this hooker。
 */
class PrepareDependenciesHooker extends GradleTaskHooker<PrepareDependenciesTask> {
    def commonLibDependencies = [] as Set
    def stripDependencies = [] as Collection<Dependency>
    def stripAarDependencies = [] as Set
    def retainedAarLibs = [] as Set<AndroidDependency>


    PrepareDependenciesHooker(Project project, ApkVariant apkVariant) {
        super(project, apkVariant)
    }

    @Override
    String getTaskName() {
        return "prepare${apkVariant.name.capitalize()}Dependencies"
    }

    /**
     * Collect host dependencies via hostDependenceFile or exclude configuration before PrepareDependenciesTask execute,
     * @param task Gradle Task fo PrepareDependenciesTask
     */
    @Override
    void beforeTaskExecute(PrepareDependenciesTask task) {
        super.beforeTaskExecute(task)
        if (showLog) {
            println "extension.excludes: " + extension.excludes.toString()
        }

        File commonLibs = new File(project.rootDir, "versions.txt")
        if (commonLibs.exists()) {
            commonLibs.splitEachLine('\\s+', { columns ->
                final def module = columns[0].split(':')
                commonLibDependencies.add("${module[0]}:${module[1]}")
            })
        }

        extension.excludes.each { String artifact ->
            final def module = artifact.split(':')
            commonLibDependencies.add("${module[0]}:${module[1]}")
        }
    }

    /**
     * Classify all dependencies into retainedAarLibs & retainedJarLib & stripDependencies
     *
     * @param task Gradle Task fo PrepareDependenciesTask
     */
    @Override
    void afterTaskExecute(PrepareDependenciesTask task) {
        super.afterTaskExecute(task)

        ImmutableList<AndroidDependency> androidDependencyImmutableList = task.variant.variantConfiguration.compileDependencies.allAndroidDependencies
        androidDependencyImmutableList.each {
            if (showLog) {
                println '==============AndroidDependency'
                println "artifactFile: " + it.getArtifactFile()
                println "coordinates: " + it.getCoordinates()
                println "projectPath: " + it.getProjectPath()
                println "extractedFolder: " + it.getExtractedFolder()
                println "variant: " + it.getVariant()
            }

            def mavenCoordinates = it.coordinates
            def aarDependencyKey = "${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}"
            if (commonLibDependencies.contains(aarDependencyKey)) {
                commonLibDependencies.add(aarDependencyKey)
                stripDependencies.add(it)
                stripAarDependencies.add(aarDependencyKey + ":${mavenCoordinates.version}")
            } else {
                retainedAarLibs.add(it)
            }
        }

        ImmutableList<JavaDependency> javaDependencyImmutableList = task.variant.variantConfiguration.compileDependencies.allJavaDependencies
        javaDependencyImmutableList.each {
            if (showLog) {
                println '==============JavaDependency'
                println "artifactFile: " + it.getArtifactFile()
                println "coordinates: " + it.getCoordinates()
                println "projectPath: " + it.getProjectPath()
            }
            def mavenCoordinates = it.coordinates
            if (commonLibDependencies.contains("${mavenCoordinates.groupId}:${mavenCoordinates.artifactId}")) {
                stripDependencies.add(it)
            }
        }

        extension.stripDependencies = stripDependencies
        extension.stripAarDependencies = stripAarDependencies
        extension.retainedAarLibs = retainedAarLibs
        if (showLog) {
            println "stripDependencies: " + extension.stripDependencies
        }
    }

}