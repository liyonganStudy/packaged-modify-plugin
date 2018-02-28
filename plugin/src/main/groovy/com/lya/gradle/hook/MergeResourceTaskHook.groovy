package com.lya.gradle.hook

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.tasks.MergeResources
import org.gradle.api.Project

class MergeResourceTaskHook extends GradleTaskHooker<MergeResources> {

    MergeResourceTaskHook(Project project, ApkVariant apkVariant) {
        super(project, apkVariant)
    }

    @Override
    String getTaskName() {
        return "merge${apkVariant.name.capitalize()}Resources"
    }

    @Override
    void afterTaskExecute(MergeResources task) {
        super.afterTaskExecute(task)

        project.copy {
            from project.extensions.findByType(AppExtension).sourceSets.main.res.srcDirs
            into task.outputDir

            include 'values/public.xml'
        }
    }
}
