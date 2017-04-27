package com.emarsys.google.storage

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class GoogleStorageItSpec extends WordSpec with Matchers with ScalaFutures {

  implicit val system = ActorSystem("google-storage-stream")
  implicit val materializer = ActorMaterializer()

  val testConfig = system.settings.config getConfig "googleStorage"
  val testBucket = testConfig.getString("project.bucket")
  val testProject = testConfig.getString("project.name")
  val googleService = GoogleStorageService(testProject)


  "Read file from storage" should {

    "default chunk size is 64" in {
      val list = mutable.MutableList.empty[Int]
        val testFile= getClass.getResourceAsStream("/empty64k")
      googleService.get(testBucket).create("test64",testFile)

      Await.result(GoogleStorage.storageSource("test64").runForeach(element => {
            list += element.size
      }), 3.seconds)

      list.size shouldBe 1
      list.head shouldBe 64*1024
    }

    "chunk size is set 1" in {
      val list = mutable.MutableList.empty[Int]
      val testFile= getClass.getResourceAsStream("/empty64k")
      googleService.get(testBucket).create("test1",testFile)

      Await.result(GoogleStorage.storageSource("test1", 1).runForeach(element => {
        list += element.size
      }), 3.seconds)

      list.size shouldBe 64
      list.head shouldBe 1024
    }
  }

}
