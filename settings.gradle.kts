pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "MediaPlayer"
include(":app")
include(":core:core-common")
include(":core:core-ui")
include(":core:core-navigation")
include(":core:core-domain")
include(":core:core-database")
include(":features:feature-splash")
include(":features:feature-home")
include(":features:feature-videolist")
include(":features:feature-audiolist")
include(":features:feature-imagelist")
include(":features:feature-folders")
include(":features:feature-favorites")
include(":features:feature-videoplayer")
include(":features:feature-audioplayer")
include(":features:feature-imagebrowser")
include(":features:feature-playbackhistory")
include(":features:feature-playlist")
include(":features:feature-settings")
include(":features:feature-search")
include(":data:data-local")
include(":data:data-repository")
include(":data:data-media")
include(":media:media-decoder")
include(":media:media-player")
include(":utils:utils-permission")
include(":utils:utils-storage")
include(":utils:utils-image")
