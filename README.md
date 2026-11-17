[![][1]][3] [![][2]][3] [![][4]][5]

# Just Enough Calculation Mod

Hey, this is a small add on to JEI (Just Enough Items), in order to help you
calculate the amount of resources to craft
a specific amount of item. This might be a little confusing, here's a example:
you want to know how many wood log do you need to craft 40 stairs, simply
load the recipe you want to use and it will tell you 13
logs should be enough, that's all. This mod is focused on calculation so
you might find it powerful and high - performance in some complicated
cases. Have fun!

For all the release files, description or videos, refer to the [release page](https://minecraft.curseforge.com/projects/just-enough-calculation).

## Development

- Optional helper mods such as REI are disabled by default for the dev runtime to keep the 1.21.1 NeoForge loader happy. Pass `-Pinclude_rei_runtime=true` when invoking Gradle (e.g. `./gradlew -Pinclude_rei_runtime=true neoforge:runClient`) if you want REI present in your dev client.
- The project targets Java 21 via Gradle’s toolchain configuration. Install any JDK 21 distribution and either set `JAVA_HOME`/`PATH` accordingly or export `ORG_GRADLE_JAVA_HOME=/path/to/jdk-21` before running `./gradlew`—no repository-local path is required.

[1]: http://cf.way2muchnoise.eu/full_just-enough-calculation_downloads.svg
[2]: http://cf.way2muchnoise.eu/versions/just-enough-calculation.svg
[3]: https://minecraft.curseforge.com/projects/just-enough-calculation
[4]: https://img.shields.io/discord/517485644163973120.svg?logo=discord
[5]: https://discord.gg/M3fNfTW
