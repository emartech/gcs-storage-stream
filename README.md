# Google Storage reader with Akka Stream

It is a library to connect a a google storage and fetch a specific file via Akka Stream. All GCS Storage configuration read from ActorSystem except file name.


## Usage

Add the following to `build.sbt`:

    resolvers += "gcs-storage-stream on GitHub" at "https://raw.github.com/emartech/gcs-storage-stream/master/releases"

For versions actual version :

     "com.emarsys"              %% "gcs-storage-stream"       % "1.0.9"

Add application.conf file contains all enviroment properties:

    googleStorage {
            chunk-size = 64
            chunk-size = ${?GCS_CHUNK_SIZE}

            project = {
                name   = "projekt name where the bucket is"
                bucket = "bucket where the file is"
            }
        
            secret = {
            type = "service_account",
            project_id = "",
            private_key_id = "",
            private_key= ${?GCS_READ_KEY},
            client_email= "client_email@project.iam.gserviceaccount.com",
            client_id= "1",
            auth_uri= "https=//accounts.google.com/o/oauth2/auth",
            token_uri= "https://accounts.google.com/o/oauth2/token",
            auth_provider_x509_cert_url= "https://www.googleapis.com/oauth2/v1/certs",
            client_x509_cert_url= ""
        }
    }
We suggest GCS Storage read key is read from environment variable.


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
The GoogleStorageItSpec contains integration tests. it/resources/applicantion.conf file should fill with a proper endpoint of GCS storage. You can run the tests with the following commands:

    sbt it:test

Creating a release
------------------

Bump the version number in `build.sbt` and run the following command:

    sbt publish

This will build a jar with the new version and place it under the `releases` directory.
