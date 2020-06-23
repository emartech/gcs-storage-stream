val v2_12 = "2.12.10"
val v2_13 = "2.13.1"

lazy val root = (project in file("."))
  .settings(
    Seq(
      crossScalaVersions := List(v2_13, v2_12),
      organization := "com.emarsys",
      name := "gcs-storage-stream",
      scalafmtOnCompile := true
    ): _*
  )
  .settings(Defaults.itSettings: _*)
  .settings(
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-Yrangepos",
      "-feature",
      "-language:higherKinds",
      "-language:existentials",
      "-unchecked",
      "-Xlint:_,-type-parameter-shadow",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-extra-implicit",
      "-Ywarn-unused:imports",
      "-opt-warnings",
      "-target:jvm-1.8"
    ),
    scalacOptions ++= versionSpecificScalacOptions(scalaVersion.value),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= {
      val akkaVersion = "2.5.31"
      Seq(
        "com.typesafe.akka" %% "akka-stream"         % akkaVersion,
        "com.typesafe.akka" %% "akka-actor"          % akkaVersion,
        "org.scalatest"     %% "scalatest"           % "3.2.0" % Test,
        "com.google.cloud"  % "google-cloud-storage" % "1.102.0"
      )
    }
  )
  .configs(IntegrationTest)

lazy val IntegrationTest = config("it") extend Test

inThisBuild(
  List(
    licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("https://github.com/emartech/gcs-bigquery-scala-api")),
    developers := List(
      Developer("mfawal", "Margit Fawal", "margit.fawal@emarsys.com", url("https://github.com/mfawal")),
      Developer("andrasp3a", "Andras Papp", "andras.papp@emarsys.com", url("https://github.com/andrasp3a")),
      Developer("bkiss1988", "Balazs Kiss", "balazs.kiss@emarsys.com", url("https://github.com/bkiss1988")),
      Developer("itsdani", "Daniel Segesdi", "daniel.segesdi@emarsys.com", url("https://github.com/itsdani")),
      Developer("miklos-martin", "Miklos Martin", "miklos.martin@gmail.com", url("https://github.com/miklos-martin")),
      Developer("suliatis", "Attila Suli", "attila.suli@emarsys.com", url("https://github.com/suliatis")),
      Developer("galatt", "Attila Peter Gal", "attila.gal@emarsys.com", url("https://github.com/galatt"))
    )
  ),
  scmInfo := Some(
      ScmInfo(
        url("https://github.com/emartech/gcs-bigquery-scala-api"),
        "scm:git:git@github.com:emartech/gcs-bigquery-scala-api.git"
      )
    ),
    // These are the sbt-release-early settings to configure
    pgpPublicRing := file("./ci/local.pubring.asc"),
    pgpSecretRing := file("./ci/local.secring.asc"),
    releaseEarlyWith := SonatypePublisher
)

def versionSpecificScalacOptions(scalaV: String) =
  if (scalaV == v2_12)
    Seq(
      "-Xfuture",
      "-Xsource:2.13",
      "-Yno-adapted-args",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ypartial-unification"
    )
  else
    Seq(
      "-Ymacro-annotations"
    )
