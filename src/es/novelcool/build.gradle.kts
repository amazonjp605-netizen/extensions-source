import io.github.keiyoushi.gradle.api.ContentWarning

plugins {
    alias(kei.plugins.extension)
}

keiyoushi {
    name = "Novelcool"
    versionCode = 1

    source {
        name = "Novelcool"
        lang = "es"
        classPath = "es.novelcool.Novelcool" 
        
        // Si tu extensión tiene contenido para adultos, quita las dos barras (//) de la siguiente línea:
        // contentWarning = ContentWarning.NSFW
    }
}
