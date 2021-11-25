package EShop.lab6.local

import EShop.lab5.{ProductCatalog, SearchService}
import akka.Done
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.AskPattern.{schedulerFromActorSystem, Askable}
import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

import java.net.URI
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.StdIn
import scala.util.Try

object LocalApp extends App {
  val workHttpServer = new LocalHttpApp()
  workHttpServer.run(Try(args(0).toInt).getOrElse(9000))
}

trait ProductCatalogJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val uriFormat = new JsonFormat[java.net.URI] {
    override def write(obj: java.net.URI): spray.json.JsValue = JsString(obj.toString)
    override def read(json: JsValue): URI =
      json match {
        case JsString(url) => new URI(url)
        case _             => throw new RuntimeException("Parsing exception")
      }
  }

  implicit val itemFormat  = jsonFormat5(ProductCatalog.Item)
  implicit val itemsFormat = jsonFormat1(ProductCatalog.Items)
}

/**
 * The server that distributes all of the requests to the local workers spawned via router pool.
 */
class LocalHttpApp extends ProductCatalogJsonSupport {
  implicit val system: ActorSystem[Nothing]               = ActorSystem(Behaviors.empty, "LocalRouters")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  val workers: ActorRef[ProductCatalog.Query] = {
    val searchService = new SearchService()
    system.systemActorOf(Routers.pool(3)(ProductCatalog(searchService)), "workersRouter")
  }

  implicit val timeout: Timeout = 5.seconds

  def routes: Route = {
    path("products") {
      get {
        parameters(Symbol("keywords").as[String].repeated) { keywords =>
          parameter(Symbol("brand").as[String].withDefault("gerber")) { brand =>
            complete {
              system.log.info(s"/products, brand: ${brand}, keywords: ${keywords}")

              val items: Future[ProductCatalog.Items] =
                workers
                  .ask(ref => ProductCatalog.GetItems(brand, keywords.toList, ref))
                  .mapTo[ProductCatalog.Items]

              items
            }
          }
        }
      }
    }
  }

  def run(port: Int): Unit = {
    val bindingFuture = Http().newServerAt("localhost", port).bind(routes)
    println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
