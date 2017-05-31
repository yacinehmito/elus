package app

import com.github.andrewoma.kwery.core.Session
import com.github.andrewoma.kwery.core.ThreadLocalSession
import com.github.andrewoma.kwery.core.dialect.PostgresDialect
import com.github.andrewoma.kwery.core.interceptor.LoggingInterceptor
import org.postgresql.ds.PGSimpleDataSource
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

object Database {
  val hostname = "localhost"
  val port = 5432
  val database = "elus"
  val user = "dev"
  val password = "dev"

  val url: String
    get() = "jdbc:postgresql://$hostname:$port/$database"

  init {
    Class.forName("org.postgresql.Driver");
  }

  val session: Session by lazy {
    val source = PGSimpleDataSource()
    source.serverName = hostname
    source.databaseName = database
    source.user = user
    source.password = password
    ThreadLocalSession(source, PostgresDialect())
  }
}


