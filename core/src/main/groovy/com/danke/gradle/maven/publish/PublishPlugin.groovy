package com.danke.gradle.maven.publish

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author danke
 * @date 2018/7/25
 */
class PublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        final PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            extension.validate(project)
            project.apply([plugin: 'maven'])
            attachArtifacts(extension, project)
            publish(project, extension)
        }
    }

    private void attachArtifacts(PublishExtension extension, Project project) {
        if (project.plugins.hasPlugin('com.android.library')) {
            project.android.libraryVariants.each { variant ->
                addArtifact(project, new AndroidArtifacts(variant, extension))
            }
        }
        // else if (project.plugins.hasPlugin('java') || project.plugins.hasPlugin('java-library') || project.plugins.hasPlugin('groovy')) {
        else {
            addArtifact(project, new JavaArtifacts(extension))
        }
    }

    private void addArtifact(Project project, Artifacts artifacts) {
        project.artifacts {
            artifacts.all(project.name, project).each {
                delegate.archives it
            }
        }
    }

    private void publish(Project project, PublishExtension extension) {
        project.uploadArchives {
            if (extension.localMaven) {
                repositories {
                    mavenDeployer {
                        snapshotRepository(url: extension.snapshotRepositoryUrl)
                        repository(url: extension.repositoryUrl)

                        pom.packaging = extension.packaging
                        pom.version = extension.publishVersion
                        pom.groupId = extension.groupId
                        pom.artifactId = extension.artifactId
                    }
                }
            } else {
                repositories {
                    mavenDeployer {
                        snapshotRepository(url: extension.snapshotRepositoryUrl) {
                            authentication(userName: extension.username, password: extension.password)
                        }
                        repository(url: extension.repositoryUrl) {
                            authentication(userName: extension.username, password: extension.password)
                        }

                        pom.packaging = extension.packaging
                        pom.version = extension.publishVersion
                        pom.groupId = extension.groupId
                        pom.artifactId = extension.artifactId
                    }
                }
            }
        }
    }
}
