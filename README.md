# Google Storage reader with Akka Stream

[![Codeship Status for emartech/gcs-storage-stream](https://app.codeship.com/projects/b3c90f40-0caa-0135-569d-62f3a1772a99/status?branch=master)](https://app.codeship.com/projects/215376)
![Maven Central](https://img.shields.io/maven-central/v/com.emarsys/gcs-storage-stream_2.12.svg?label=Maven%20Central)
![Maven Central](https://img.shields.io/maven-central/v/com.emarsys/gcs-storage-stream_2.13.svg?label=Maven%20Central)


It is a library to connect a a google storage and fetch a specific file via Akka Stream. All GCS Storage configuration read from ActorSystem except file name.


## Usage

You can override the default configuration, see more in `src/main/resources/reference.conf`.

See example implementation is `GoogleStorageReaderExample.scala` object

    object GoogleStorageReaderExample extends App {
    
        implicit val system       = ActorSystem("gc-example")
        
        implicit val materializer = ActorMaterializer()
        
        lazy val csvLines =
            Flow[ByteString]
                .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 25))
                .groupedWithin(1000, 1 seconds)
                .map(_.map(_.utf8String).mkString(","))
                
        system.log.info("Start streaming file...")
        
         //bucket, projectId comes from .conf
        GoogleStorage.storageSource("ids_only.csv").via(csvLines).runForeach(println)
        
        //or projectId does not come from .conf
        GoogleStorage.storageSource("ids_only.csv", "projectId", "bucket", 64).via(csvLines).runForeach(println)
        
    }


Testing
------------------

Specify the `GCS_PROJECT_NAME`, `GCS_PRIVATE_KEY_ID`, `GCS_READ_KEY`, `GCS_EMAIL_PREFIX` and `GCS_CLIENT_ID` env vars in the `.env` file, then run `sbt it:test`.

Testing
------------------
The GoogleStorageItSpec contains integration tests. it/resources/applicantion.conf file should fill with a proper endpoint of GCS storage. You can run the tests with the following commands:

    sbt it:test

## Creating a release

This library is using [sbt-release-early] for releasing artifacts. Every push will be released to maven central, see the plugins documentation on the versioning schema.

### To cut a final release:

Choose the appropriate version number according to [semver] then create and push a tag with it, prefixed with `v`.
For example:

```
$ git tag -s v1.1.1
$ git push --tag
```

After pushing the tag, while it is not strictly necessary, please [draft a release on github] with this tag too.


[sbt-release-early]: https://github.com/scalacenter/sbt-release-early
[semver]: https://semver.org
[draft a release on github]: https://github.com/emartech/gcs-storage-stream/releases/new

