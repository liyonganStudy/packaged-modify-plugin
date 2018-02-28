package com.lya.gradle.collector

import com.android.builder.dependency.level2.AndroidDependency
import com.android.builder.dependency.level2.Dependency
import com.android.builder.dependency.level2.JavaDependency

import java.util.zip.ZipFile

/**
 * Collector of Class and Java Resource(no-class files in jar) in host apk
 */

class HostClassAndResCollector {

    private def hostJarFiles = [] as LinkedList<File>
    private def hostClassesAndResources = [] as LinkedHashSet<String>

    /**
     * Collect jar entries that already exist in the host apk
     *
     * @param stripDependencies DependencyInfos that exists in the host apk, including AAR and JAR
     * @return set of classes and java resources
     */
    Set<String> collect(Collection<Dependency> stripDependencies) {
        flatToJarFiles(stripDependencies, hostJarFiles)
        hostJarFiles.each {
            hostClassesAndResources.addAll(unzipJar(it))
        }
        hostClassesAndResources
    }

    /**
     * Collect the jar files that are held by the DependenceInfo， including local jars of the DependenceInfo
     * @param stripDependencies Collection of DependenceInfo
     * @param jarFiles Collection used to store jar files
     */
    static flatToJarFiles(Collection<Dependency> stripDependencies, Collection<File> jarFiles) {
        stripDependencies.each {
            if (it instanceof JavaDependency) {
                jarFiles.add(it.artifactFile)
            } else if (it instanceof AndroidDependency) {
                jarFiles.add(it.jarFile)
            }
            if (it instanceof AndroidDependency) {
                it.localJars.each {
                    jarFiles.add(it)
                }
            }
        }
    }

    /**
     * Unzip the entries of Jar
     *
     * @return Set of entries in the JarFile
     */
    static Set<String> unzipJar(File jarFile) {

        def jarEntries = [] as Set<String>

        ZipFile zipFile = new ZipFile(jarFile)
        try {
            zipFile.entries().each {
                jarEntries.add(it.name)
            }
        } finally {
            zipFile.close()
        }

        return jarEntries
    }

}