package com.danke.android.maven.publish

import groovy.transform.PackageScope
import org.gradle.api.Project

/**
 * @author danke
 * @date 2018/7/25
 */
class PublishExtension {

    private final ERROR_PREFIX_FORMAT = "%s in 'publish' configuration must not be empty!"

    String packaging = 'aar'
    String groupId
    String artifactId
    /**
     * version conflicts with gradle project.version. replaced by {@link #publishVersion}
     */
    String publishVersion

    String username
    String password

    String repositoryUrl
    String snapshotRepositoryUrl

    boolean localMaven = false
    boolean uploadSourcesJar = true

    @PackageScope
    void validate(Project project) {
        // project.repositories.mavenLocal().url
        final LOCAL_REPOSITORIES = project.uri("${project.rootDir}/.maven")

        StringBuilder stringBuilder = new StringBuilder()
        final String SPLIT = ', '

        if (isEmpty(groupId)) {
            stringBuilder.append('groupId').append(SPLIT)
        }

        if (isEmpty(artifactId)) {
            stringBuilder.append('artifactId').append(SPLIT)
        }

        if (isEmpty(publishVersion)) {
            stringBuilder.append('publishVersion').append(SPLIT)
        }

        if (localMaven) {
            repositoryUrl = LOCAL_REPOSITORIES
            snapshotRepositoryUrl = LOCAL_REPOSITORIES
        } else {
            if (isEmpty(username)) {
                stringBuilder.append('username').append(SPLIT)
            }

            if (isEmpty(password)) {
                stringBuilder.append('password').append(SPLIT)
            }

            if (isEmpty(repositoryUrl)) {
                repositoryUrl = LOCAL_REPOSITORIES
            }

            if (isEmpty(snapshotRepositoryUrl)) {
                snapshotRepositoryUrl = LOCAL_REPOSITORIES
            }
        }

        String error = stringBuilder.toString()
        if (!isEmpty(error)) {
            throw new IllegalStateException(String.format(ERROR_PREFIX_FORMAT, error.substring(0, error.length() - SPLIT.length())))
        }
    }

    private static boolean isEmpty(String s) {
        if (s == null) {
            return true
        } else {
            return s.isEmpty()
        }
    }
}
