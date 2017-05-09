scalaVersion := "2.12.1"

libraryDependencies += "com.github.scopt" %% "scopt" % "3.5.0"
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6.1"

resolvers += Resolver.sonatypeRepo("public")
mainClass in (Compile,run) := Some("ru.niner.oneplusll.matrix.Runner")
