package com.emarsys.google.storage

import java.io.ByteArrayInputStream
import java.net.InetSocketAddress

import com.emarsys.google.storage.Config.{GoogleConfig, GoogleProjectConfig, GoogleStorageConfig}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.services.storage.StorageScopes
import com.google.auth.http.HttpTransportFactory
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.http.HttpTransportOptions.DefaultHttpTransportFactory
import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

object Config {

  val default: Config = new Config()

  case class GoogleConfig(
      useWorkloadIdentityAuth: Boolean,
      storage: GoogleStorageConfig
  )

  case class GoogleStorageConfig(
      project: GoogleProjectConfig,
      chunkSize: Int,
      proxyHost: String,
      proxyPort: Int
  )

  case class GoogleProjectConfig(
      name: String,
      bucket: String
  )
}

class Config() {
  private val config       = ConfigFactory.load()
  private val googleConfig = config.getConfig("google")

  private val googleStorageConfig = config.getConfig("googleStorage")
  private val googleProjectConfig = googleStorageConfig.getConfig("project")

  lazy val google: GoogleConfig = GoogleConfig(
    useWorkloadIdentityAuth = googleConfig.getBoolean("use-workload-identity-auth"),
    GoogleStorageConfig(
      project = GoogleProjectConfig(
        name = googleProjectConfig.getString("name"),
        bucket = googleProjectConfig.getString("bucket")
      ),
      chunkSize = googleStorageConfig.getInt("chunk-size"),
      proxyHost = googleStorageConfig.getString("proxy-host"),
      proxyPort = googleStorageConfig.getInt("proxy-port")
    )
  )

  lazy val credentials: GoogleCredentials = {
    val inputStream = new ByteArrayInputStream(configAsJson("secret").getBytes)
    GoogleCredentials.fromStream(inputStream, httpTransportFactory).createScoped(StorageScopes.all())
  }

  val useProxy: Boolean = google.storage.proxyHost != ""

  val httpTransportFactory: HttpTransportFactory = {
    if (!useProxy) {
      new DefaultHttpTransportFactory()
    } else {
      new HttpTransportFactory {
        override def create() = {
          val socketAddress = new InetSocketAddress(google.storage.proxyHost, google.storage.proxyPort)
          val proxy         = new java.net.Proxy(java.net.Proxy.Type.HTTP, socketAddress)
          new NetHttpTransport.Builder().setProxy(proxy).build()
        }
      }
    }
  }

  private def configAsJson(properties: String) = {
    googleStorageConfig
      .getValue(properties)
      .render(
        ConfigRenderOptions.defaults().setJson(true).setOriginComments(false)
      )
      .replace("\\\\", "\\")
  }
}
