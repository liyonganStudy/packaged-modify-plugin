package com.lya.gradle.hook

import com.android.build.gradle.api.ApkVariant
import com.lya.gradle.Extension
import org.gradle.api.Project
import org.gradle.api.Task

abstract class GradleTaskHooker<T extends Task> {
    protected boolean showLog = false
    private Project project
    private ApkVariant apkVariant
    private Extension extension

    GradleTaskHooker(Project project, ApkVariant apkVariant) {
        this.project = project
        this.apkVariant = apkVariant
        this.extension = project.packageIdModifier
    }

    Project getProject() {
        return this.project
    }

    ApkVariant getApkVariant() {
        return this.apkVariant
    }

    Extension getExtension() {
        return this.extension
    }

    abstract String getTaskName()

    void beforeTaskExecute(T task) {
        if (showLog) {
            println '============beforeTaskExecute: ' + getTaskName()
        }
    }

    void afterTaskExecute(T task) {
        if (showLog) {
            println '============afterTaskExecute: ' + getTaskName()
        }
    }
}