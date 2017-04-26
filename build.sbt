name := "gcs-storage-stream"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
val akkaVersion = "2.5.0"
Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.google.cloud"  %   "google-cloud-storage"  % "0.12.0-beta"
 )}

publishTo := Some(Resolver.file("releases", new File("releases")))