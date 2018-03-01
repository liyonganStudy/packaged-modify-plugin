package com.lya.gradle.hook

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.lya.gradle.Extension
import com.lya.gradle.collector.HostClassAndResCollector
import groovy.io.FileType
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
/**
 * Strip Host classes and java resources from project, it's an equivalent of provided compile
 * @author zhengtao
 */
class StripClassAndResTransform extends Transform {

    private boolean showLog = true
    private Project project
    private Extension extension
    private HostClassAndResCollector classAndResCollector

    StripClassAndResTransform(Project project) {
        this.project = project
        this.extension = project.packageIdModifier
        classAndResCollector = new HostClassAndResCollector()
    }

    @Override
    String getName() {
        return 'stripClassAndRes'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_JARS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * Only copy the jars or classes and java resources of retained aar into output directory
     */
    @Override
    void transform(final TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        def stripEntries = classAndResCollector.collect(extension.stripDependencies)
        if (showLog) {
            stripEntries.each {
                if (it.toString().contains("android.support.v4.app.Fragment") || it.toString().contains("android.view.LayoutInflater")) {
                    println '=================classAndResCollector stripEntries: ' + it
                }
            }
        }

        if (!isIncremental()) {
            transformInvocation.outputProvider.deleteAll()
        }

        transformInvocation.inputs.each {

            it.directoryInputs.each { directoryInput ->
                if (showLog) {
//                    println "transformInvocation.directoryInput: " + directoryInput
                }
                directoryInput.file.traverse(type: FileType.FILES) {
                    def entryName = it.path.substring(directoryInput.file.path.length() + 1)
                    def destName = directoryInput.name + '/' + entryName
                    def dest = transformInvocation.outputProvider.getContentLocation(
                            destName, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    if (!stripEntries.contains(entryName)) {
                        if (showLog) {
                            println '========copy directory not in stripEntries' + entryName
                        }
                        FileUtils.copyFile(it, dest)
                    } else {
                        if (showLog) {
//                            println '========do not copy directory in stripEntries: ' + entryName
                        }
                    }
                }
            }

            it.jarInputs.each { jarInput ->
                if (showLog) {
//                    println "transformInvocation.jarInput: " + jarInput
                }
                Set<String> jarEntries = HostClassAndResCollector.unzipJar(jarInput.file)
                if (!stripEntries.containsAll(jarEntries)){
                    def dest = transformInvocation.outputProvider.getContentLocation(jarInput.name,
                            jarInput.contentTypes, jarInput.scopes, Format.JAR)
                    FileUtils.copyFile(jarInput.file, dest)
                    if (showLog) {
                        println '========copy jar not in stripEntries' + jarEntries
                    }
                } else {
                    if (showLog) {
//                        println '========do not copy jar in stripEntries: ' + jarEntries
                    }
                }
            }
        }
    }
}