import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Novelcool"
    versionCode = 1
    // contentWarning = ContentWarning.NSFW // Descomenta esta línea si tu fuente incluye contenido para adultos
}
