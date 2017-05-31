package app

import com.github.salomonbrys.kotson.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.intellij.lang.annotations.Language
import java.io.File
import java.nio.charset.Charset

class GeoJson: JsonElement() { }

data class CirconscriptionData(
    val codeDpt: String,
    val num: Int,
    val shape: GeoJson
)

fun fixDomTom(codeDpt: String) : String = when(codeDpt) {
  "ZA" -> "971"
  "ZB" -> "972"
  "ZC" -> "973"
  "ZD" -> "974"
  "ZM" -> "976"
  "ZN" -> "988"
  "ZP" -> "987"
  "ZS" -> "975"
  "ZW" -> "986"
  "ZX" -> "977"
  else -> codeDpt
}

fun parseCirconscriptions(file: File): List<CirconscriptionData> {
  val gson = GsonBuilder()
      .registerTypeAdapter<CirconscriptionData> {
        deserialize {
          val props = it.json["properties"].obj
          CirconscriptionData(
              codeDpt = fixDomTom(props["code_dpt"].string),
              num = props["num_circ"].int,
              shape = it.json["geometry"] as GeoJson
          )
        }
      }
      .registerTypeAdapter<List<CirconscriptionData>> {
        deserialize {
          val context = it.context
          it.json["features"].array.toList().map {
            context.deserialize<CirconscriptionData>(it)
          }
        }
      }
      .create()
  return gson.fromJson<List<CirconscriptionData>>(file.reader())
}

fun insertCirconscriptions(circonscriptions: List<CirconscriptionData>) {
  @Language("PostgreSQL")
  val sql = "INSERT INTO elus.circonscriptions VALUES (:code_dpt, :num, st_geomfromgeojson(:shape))"
  val data = circonscriptions.map {
    mapOf(
        "code_dpt" to it.codeDpt,
        "num" to it.num,
        "shape" to it.shape.toString()
    )
  }
  Database.session.batchInsert(sql, data) {}
}

data class DeputeData(
    val idAn: Int,
    val codeDpt: String,
    val numCirco: Int,
    val name: String,
    val firstname: String,
    val lastname: String,
    val slug: String,
    val emails: List<String>
)

fun removeLastBar(str: String): String {
  if (str == "") return ""
  val isLastBar = str[str.length - 1] === '|'
  return str.substring(0, str.length - (if (isLastBar) 1 else 0))
}

fun parseDeputes(file: File): List<DeputeData> {
  val csvFormat = CSVFormat.DEFAULT.withHeader().withDelimiter(';')
  return CSVParser.parse(file, Charset.defaultCharset(), csvFormat).map {
    DeputeData(
        idAn = it["id_an"].toInt(),
        codeDpt = it["num_deptmt"],
        numCirco = it["num_circo"].toInt(),
        name = it["nom"],
        firstname = it["prenom"],
        lastname = it["nom_de_famille"],
        slug = it["slug"],
        emails = removeLastBar(it["emails"]).split('|')
    )
  }.filter {
    it.codeDpt != "999"
  }
}

fun insertDeputes(deputes: List<DeputeData>) {
  @Language("PostgreSQL")
  val sql = """
    INSERT INTO elus.deputes VALUES (:id_an, :code_dpt, :num_circo, :name, :firstname, :lastname, :slug, :email)
  """.trimMargin()
  val data = deputes.map {
    mapOf(
        "id_an" to it.idAn,
        "code_dpt" to it.codeDpt,
        "num_circo" to it.numCirco,
        "name" to it.name,
        "firstname" to it.firstname,
        "lastname" to it.lastname,
        "slug" to it.slug,
        "email" to it.emails.first()
    )
  }
  Database.session.batchInsert(sql, data) {}
}

fun main(args: Array<String>) {
  insertCirconscriptions(parseCirconscriptions(File("./data/circonscriptions2012.json")))
  insertDeputes(parseDeputes(File("./data/nosdeputes.fr_deputes_en_mandat2017-05-30.csv")))
}

