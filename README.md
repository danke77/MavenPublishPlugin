# MavenPublishPlugin

[ ![jcenter](https://api.bintray.com/packages/danke/maven/MavenPublishPlugin/images/download.svg) ](https://bintray.com/danke/maven/MavenPublishPlugin/_latestVersion)
[ ![Build Status](https://travis-ci.org/danke77/MavenPublishPlugin.svg?branch=master) ](https://travis-ci.org/danke77/MavenPublishPlugin)

A gradle plugin for Publishing your **Android**, **Java** and **Groovy** libraries to maven in an easy way.

## Usage

Add these configurations to the `build.gradle` of the root project.

``` groovy
buildscript {
    repositories {
        mavenLocal()
        maven { url uri("${rootDir}/.maven") }
        maven { url '<your-maven-repository-url>' }
    }
    
    dependencies {
        classpath 'com.danke.gradle:maven-publish:<latest-version>'
    }
}
```
Use the `publish` closure to set the configurations of your package in the `build.gradle` of the module that will be published.

``` groovy
// must be applied after your artifact generating plugin (eg. java-library / com.android.library)
apply plugin: 'com.danke.gradle.maven-publish'

publish {
    packaging = POM_PACKAGING
    groupId = POM_GROUP_ID
    artifactId = POM_ARTIFACT_ID
    publishVersion = POM_VERSION_NAME
    localMaven = LOCAL_MAVEN
    uploadSourcesJar = UPLOAD_SOURCE
    username = USERNAME
    password = PASSWORD
    repositoryUrl = REPOSITORY_URL
    snapshotRepositoryUrl = SNAPSHOT_REPOSITORY_URL
}
```

Use the task `uploadArchives` to publish (make sure you build the project first).

```bash
$ ./gradlew clean build :your-library:uploadArchives
```

## Configuration

| extension | necessary | default |
|---|---|---|
| packaging | false | aar |
| groupId | true | |
| artifactId | true | |
| publishVersion | true | |
| username | true | |
| password | true | |
| repositoryUrl | true | |
| snapshotRepositoryUrl | true | |
| localMaven | false | false |
| uploadSourcesJar | false | true |
| withPomDependencies | false | true |

## Contribution

* Issues are welcome. Please add a screenshot of bug and code snippet.
* Pull requests are welcome. If you want to change API or making something big better to create issue and discuss it first.

## LICENSE

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

See [`LICENSE`](LICENSE) for full of the license text.

    Copyright (C) 2018 danke77.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.