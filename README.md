# Google Storage reader with Akka Stream

It is a library to connect a a google storage and fetch a specific file via Akka Stream.


## Usage

Add the following to `build.sbt`:

    resolvers += "gcs-storage-stream on GitHub" at "https://raw.github.com/emartech/gcs-storage-stream/master/releases"

For versions actual version :

     "com.emarsys"              %% "gcs-storage-stream"       % "1.0.1"

Add application.conf file contains all enviroment properties:

    googleStorage {
            project = {
                name   = ""
                bucket = ""
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

See example implementation is `GoogleStorageReaderExample.scala` object


Creating a release
------------------

Bump the version number in `build.sbt` and run the following command:

    sbt publish

This will build a jar with the new version and place it under the `releases` directory.
