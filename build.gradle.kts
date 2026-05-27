val lwjglVersion = "3.4.1"
val jomlVersion = "1.10.8"
val `joml-primitivesVersion` = "1.10.0"
val lwjglNatives = "natives-windows"

plugins {
    application
    java
}

application {
    mainClass.set("game.core.Main")
}

repositories {
    mavenCentral()
}


dependencies {
	implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

	implementation("org.lwjgl", "lwjgl")
	implementation("org.lwjgl", "lwjgl-assimp")
	implementation("org.lwjgl", "lwjgl-glfw")
	implementation("org.lwjgl", "lwjgl-openal")
	implementation("org.lwjgl", "lwjgl-opengl")
	implementation("org.lwjgl", "lwjgl-stb")
	implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives)
	implementation ("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
	implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
	implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
	implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
	implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
	implementation("org.joml", "joml", jomlVersion)
	implementation("org.joml", "joml-primitives", `joml-primitivesVersion`)
}