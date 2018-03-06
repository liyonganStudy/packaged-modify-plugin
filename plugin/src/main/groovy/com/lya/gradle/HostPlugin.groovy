package com.lya.gradle

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.tasks.ProcessAndroidResources
import com.lya.gradle.utils.FileUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class HostPlugin implements Plugin<Project> {
    Project project
    File hostFiles

    @Override
    void apply(Project project) {
        this.project = project
        hostFiles = new File(project.getBuildDir(), "HostFiles")
        project.afterEvaluate {
            project.android.applicationVariants.each { ApplicationVariant variant ->
                generateDependencies(variant)
                backupHostR(variant)
            }
        }
    }

    void generateDependencies(ApplicationVariant applicationVariant) {
        applicationVariant.javaCompile.doLast {
            FileUtil.saveFile(hostFiles, "versions", {
                List<String> deps = new ArrayList<String>()
                project.configurations.getByName("_${applicationVariant.name}Compile").resolvedConfiguration.resolvedArtifacts.each {
                    deps.add("${it.moduleVersion.id} ${it.file.length()}")
                }
                Collections.sort(deps)
                return deps
            })
        }

    }

    void backupHostR(ApplicationVariant applicationVariant) {
        final ProcessAndroidResources aaptTask = this.project.tasks["process${applicationVariant.name.capitalize()}Resources"]
        aaptTask.doLast {
            project.copy {
                from new File(aaptTask.textSymbolOutputDir, 'R.txt')
                into hostFiles
                rename { "Common_R.txt" }
            }
        }
    }
}
