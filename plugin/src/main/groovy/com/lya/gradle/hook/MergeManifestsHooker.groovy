package com.lya.gradle.hook

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.tasks.MergeManifests
import com.android.manifmerger.ManifestProvider
import org.gradle.api.Project

import java.util.function.Predicate

/**
 * Filter the stripped ManifestDependency in the ManifestDependency list of MergeManifests task
 */
class MergeManifestsHooker extends GradleTaskHooker<MergeManifests> {

    public static final String ANDROID_NAMESPACE = 'http://schemas.android.com/apk/res/android'

    MergeManifestsHooker(Project project, ApkVariant apkVariant) {
        super(project, apkVariant)
    }

    @Override
    String getTaskName() {
        return "process${apkVariant.name.capitalize()}Manifest"
    }

    @Override
    void beforeTaskExecute(MergeManifests task) {
        super.beforeTaskExecute(task)
        def manifestDependencies = task.providers

        if (showLog) {
            println 'before remove manifestDependencies: '
            manifestDependencies.each {
                println '========manifestDependencies name: ' + it.name
                println '=========manifestDependencies path: ' + it.manifest.getPath()
            }
        }

        if (showLog) {
            println 'extension.stripAarDependencies: ' + extension.stripAarDependencies
        }
        manifestDependencies.removeIf(new Predicate<ManifestProvider>() {
            @Override
            boolean test(ManifestProvider manifestDependency) {
                return extension.stripAarDependencies.contains("${manifestDependency.name}")
            }
        })

        if (showLog) {
            println 'after remove manifestDependencies: '
            manifestDependencies.each {
                println '=========manifestDependencies name: ' + it.name
                println '=========manifestDependencies path: ' + it.manifest.getPath()
            }
        }
        task.providers = manifestDependencies
    }

    /**
     * Filter specific attributes from <application /> element after MergeManifests task executed
     */
    @Override
    void afterTaskExecute(MergeManifests task) {
        super.afterTaskExecute(task)
        final File xml = task.manifestOutputFile
        if (showLog) {
            println 'task.manifestOutputFile: ' + task.manifestOutputFile
        }
    }
}