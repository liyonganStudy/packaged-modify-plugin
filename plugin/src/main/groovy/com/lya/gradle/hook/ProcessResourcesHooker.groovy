package com.lya.gradle.hook

import com.android.build.gradle.AndroidConfig
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.google.common.io.Files
import com.lya.gradle.collector.ResourceCollector
import org.gradle.api.Project
/**
 * Filter the host resources out of the plugin apk.
 * Modify the .arsc file to delete host element,
 * rearrange plugin element, hold the new resource IDs
 */
class ProcessResourcesHooker extends GradleTaskHooker<ProcessAndroidResources> {

    /**
     * Collector to gather the sources and styleables
     */
    ResourceCollector resourceCollector
    /**
     * Android config information specified in build.gradle
     */
    AndroidConfig androidConfig

    ProcessResourcesHooker(Project project, ApkVariant apkVariant) {
        super(project, apkVariant)
        androidConfig = project.extensions.findByType(AppExtension)
        showLog = true
    }

    @Override
    String getTaskName() {
        return "process${apkVariant.name.capitalize()}Resources"
    }

    @Override
    void beforeTaskExecute(ProcessAndroidResources aaptTask) {
        super.beforeTaskExecute(aaptTask)
    }

    /**
     * Since we need to remove the host resources and modify the resource ID,
     * we will reedit the AP_ file and repackage it after the task execute
     *
     * @param par Gradle task of process android resources
     */
    @Override
    void afterTaskExecute(ProcessAndroidResources par) {
        super.afterTaskExecute(par)
        def apFile = par.packageOutputFile // resources-debug.ap_
        def resourcesDir = new File(apFile.parentFile, Files.getNameWithoutExtension(apFile.name)) // resources-debug
        if (showLog) {
            println 'ProcessAndroidResources packageOutputFile: ' + apFile.name + " " + resourcesDir.getName()
        }
        resourcesDir.deleteDir()

        project.copy {
            from project.zipTree(apFile)
            into resourcesDir

            include 'AndroidManifest.xml'
            include 'resources.arsc'
            include 'res/**/*'
        }

        resourceCollector = new ResourceCollector(project, par)
        resourceCollector.collect()
//
//        def retainedTypes = convertResourcesForAapt(resourceCollector.pluginResources)
//        def retainedStylealbes = convertStyleablesForAapt(resourceCollector.pluginStyleables)
//        def resIdMap = resourceCollector.resIdMap
//
//        def rSymbolFile = new File(par.textSymbolOutputDir, 'R.txt')
//        def libRefTable = ["${virtualApk.packageId}": par.packageForR]
//        def filteredResources = [] as HashSet<String>
//        def updatedResources = [] as HashSet<String>
//
//        def aapt = new Aapt(resourcesDir, rSymbolFile, androidConfig.buildToolsRevision)
//
//        //Delete host resources, must do it before filterPackage
//        aapt.filterResources(retainedTypes, filteredResources)
//        //Modify the arsc file, and replace ids of related xml files
//        aapt.filterPackage(retainedTypes, retainedStylealbes, virtualApk.packageId, resIdMap, libRefTable, updatedResources)
//
//        /*
//         * Delete filtered entries and then add updated resources into resources-${variant.name}.ap_
//         */
//        com.didi.virtualapk.utils.ZipUtil.with(apFile).deleteAll(filteredResources + updatedResources)
//
//        project.exec {
//            executable par.buildTools.getPath(BuildToolInfo.PathId.AAPT)
//            workingDir resourcesDir
//            args 'add', apFile.path
//            args updatedResources
//            standardOutput = new ByteArrayOutputStream()
//        }
//
//        updateRJava(aapt, par.sourceOutputDir)
    }


}