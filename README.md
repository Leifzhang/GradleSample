# GradleTask
 
## 功能介绍
 
repo专门用于多仓库App混合编译使用，通过更改项目内的substitute,把远端的aar替换成本地源代码的形式参与项目编译环节，同时内部使用的是gradle settings includeBuild，所以项目内原先的逻辑不需要变更，可以直接使用。

repo插件同时项目会自动帮各位去同步远端的仓库代码，如果仓库branch没有发生变化则不会同步代码。

dep插件则是负责将项目内的依赖版本更换成远端版本，同时进行version版本号策略清洗。

## 使用简介

1. 打开项目的`settings.gradle`，记住是settings.gradle不是build.gradle
2. 添加下面代码

~~~
buildscript {
    repositories {
        maven {
            url 'https://dl.bintray.com/leifzhang/maven'
        }
    }
    dependencies {
        classpath "com.kronos.plugin:plugin-repo:0.1.7"
    }
}


apply plugin: 'repo-setting'
~~~
3. 项目根目录下添加repo.xml

~~~
    <module name="QrCodeLibrary" branch="master" origin="https://github.com/Leifzhang/QRScaner.git"
        srcBuild="false" substitute="com.github.leifzhang:QrCodeLibrary" />
~~~
其中srcBuild =true的情况代表你需要将这个仓库一起编译， name 代表的是你的仓库的modulename，切记，切记。


## 科普

### substitute 

Gradle依赖替换,在项目开发过程中，不可避免的需要引入一些第三方库，而不同的第三方库之间，可能存在一些依赖关系。例如：你依赖了库A与B，而同时B也依赖于A。这样就可能存在这种情况：你依赖的A的版本与B中依赖的A的版本不同。同时可以把依赖直接变更为project的形式。
