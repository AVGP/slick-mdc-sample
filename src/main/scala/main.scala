// Undertow web server
import io.undertow.Undertow
import io.undertow.server.{HttpHandler, HttpServerExchange}
import io.undertow.util.Headers

// Execution context related things
import de.geekonaut.slickmdc._
import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors

// Slick database layer & Hikari connection pool
import slick.driver.SQLiteDriver.api._
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}

// Logging and UUID utils
import com.typesafe.scalalogging.StrictLogging
import org.slf4j.MDC
import java.util.UUID

import scala.concurrent.duration._
import scala.concurrent._

object Application extends StrictLogging {

  implicit lazy val executionContext = new MdcExecutionContext(
    ExecutionContext.fromExecutor(
      Executors.newWorkStealingPool(10)
    )
  )


  def main(args: Array[String]) {

    val config = new HikariConfig()
    config.setPoolName("AuthMeSQLitePool");
    config.setDriverClassName("org.sqlite.JDBC");
    config.setJdbcUrl("jdbc:sqlite:users.db");
    config.setConnectionTestQuery("SELECT 1");
    config.setMaxLifetime(60000); // 60 Sec
    config.setIdleTimeout(45000); // 45 Sec
    config.setMaximumPoolSize(10); // 50 Connections (including idle connections)
    val connectionPool = new HikariDataSource(config)
    val db = Database.forDataSource(connectionPool, MdcAsyncExecutor.apply("Database", 10))

    val users = TableQuery[Users]

    val server = Undertow.builder()
                   .addHttpListener(8080, "localhost")
                   .setHandler(new HttpHandler() {
                      override def handleRequest(exchange:HttpServerExchange): Unit = {
                        logger.info("Got a request")
                        MDC.put("requestId", UUID.randomUUID.toString)
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain")

                        try {
                          val setup = DBIO.seq(
                            (users.schema).create
                          )
                          val setupFuture = db.run(setup)
                          val query = for(u <- users) yield u.name
                          val name = Await.result(db.run(query.result.head), 30 seconds)
                          logger.info(s"--> YAAAY $name <--")
                          exchange.getResponseSender().send(s"Hello $name!")
                        } catch {
                          case e: Exception => exchange.getResponseSender().send("Whoops")
                        }

                      }
                   }).build()
    server.start()
  }
}

class Users(tag: Tag) extends Table[(Int, String)](tag, "Users") {
  def id = column[Int]("id", O.PrimaryKey)
  def name = column[String]("name")
  def * = (id, name)
}
