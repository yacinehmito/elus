package app

import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import org.intellij.lang.annotations.Language

fun findDepute(address: String) {
  val context = GeoApiContext().setApiKey("API_KEY")
  val result = GeocodingApi.geocode(context, address).await()[0]
  @Language("PostgreSQL")
  val sql = "SELECT * FROM elus.research WHERE st_contains(shape, st_point(:lon, :lat))"
  val data = mapOf(
      "lon" to result.geometry.location.lng,
      "lat" to result.geometry.location.lat
  )
  println(data)
  Database.session.select(sql, data) {
    println(it.string("name") + " " + it.string("emails"))
  }
}



fun main(args: Array<String>) {
  findDepute("22 Rue du 19 Mars 1962, Grand-Couronne, France")
}
