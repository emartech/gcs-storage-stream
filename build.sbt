lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  organization := "com.emarsys",
  name := "gcs-storage-stream",
  version := "1.0.2"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*).
  settings(

libraryDependencies ++= {
val akkaVersion = "2.5.0"
Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.google.cloud"  %   "google-cloud-storage"  % "0.12.0-beta"
 )
}

)

publishTo := Some(Resolver.file("releases", new File("releases")))