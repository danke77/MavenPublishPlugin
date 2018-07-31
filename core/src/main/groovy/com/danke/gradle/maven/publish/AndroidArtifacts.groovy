package com.danke.gradle.maven.publish

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

/**
 * @author danke
 * @date 2018/7/25
 */
class AndroidArtifacts implements Artifacts {

    def variant
    private PublishExtension extension

    AndroidArtifacts(variant, PublishExtension extension) {
        this.variant = variant
        this.extension = extension
    }

    @Override
    def all(String publicationName, Project project) {
        if (extension.uploadSourcesJar) {
            [sourcesJar(project), javadocJar(project)]
        } else {
            [javadocJar(project)]
        }
    }

    def sourcesJar(Project project) {
        project.task(variant.name + 'AndroidSourcesJar', type: Jar) {
            group = 'jar'
            // android.sourceSets.main.java.srcDirs
            variant.sourceSets.each {
                from it.java.srcDirs
            }
            classifier = 'sources'
        }
    }

    def javadocJar(Project project) {
        def androidJavaDocs = project.task(variant.name + 'AndroidJavaDocs', type: Javadoc) {
            group = 'jar'
            // source = android.sourceSets.main.java.srcDirs
            variant.sourceSets.each {
                delegate.source it.java.srcDirs
            }
            classpath += project.files(project.android.getBootClasspath().join(File.pathSeparator))
            classpath += variant.javaCompile.classpath
            classpath += variant.javaCompile.outputs.files

            excludes += '**/BuildConfig.java'
            excludes += '**/R.java'
            failOnError false
            options {
                encoding 'UTF-8'
                charSet 'UTF-8'
                author true
                version true
                links 'http://docs.oracle.com/javase/8/docs/api'
                title "${extension.artifactId} ${extension.publishVersion}"
            }

            if (JavaVersion.current().isJava8Compatible()) {
                project.allprojects {
                    tasks.withType(Javadoc) {
                        options.addStringOption('Xdoclint:none', '-quiet')
                    }
                }
            }
        }

        project.task(variant.name + 'AndroidJavaDocsJar', type: Jar, dependsOn: androidJavaDocs) {
            group = 'jar'
            from androidJavaDocs.destinationDir
            classifier = 'javadoc'
        }
    }

    def mainJar(Project project) {
        def archiveBaseName = project.hasProperty("archivesBaseName") ?
                project.getProperty("archivesBaseName") : extension.artifactId + '-' + extension.publishVersion
        "$project.buildDir/outputs/aar/${archiveBaseName}-${variant.baseName}.aar"
    }

    def from(Project project) {
        project.components.add(AndroidLibrary.newInstance(project))
        project.components.android
    }
}
