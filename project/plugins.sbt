resolvers += Resolver.url("bintray-sbt-plugin-releases",url("https://dl.bintray.com/content/sbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "1.1.36")

