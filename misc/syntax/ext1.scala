#!/usr/bin/env scala

def camel(s: String) = "[_-]+([a-z\\d])".r.replaceAllIn(s, _.group(1).toUpperCase)
def Camel(s: String) = "^([a-z])".r.replaceAllIn(camel(s), _.group(1).toUpperCase)

// def pad(s: String) = String.format("%-21s",s)
def pad(s: String)(implicit sz: Int) = String.format("%-"+sz+"s",s)

def fmtval(s: String)(implicit sz: Int) =
  if (s.matches("^[0-9]+$"))
    // s"final def apply(n: w$s.T): AV = ^(w$s)"
    s"""final def ${pad("_"+s)} = av("$s")"""
  else
    s"final def ${pad(s)} = av(Values.$s)"

val ar = "(?:Attr.*(?:real|alias)|new AliasAttr).*\"([a-z0-9-]+)\"".r
val as =
  scala.io.Source.fromFile("../../core/src/main/scala/japgolly/scalacss/Attrs.scala").mkString
    .split("\n[ \t]*\n+").toList
    .flatMap(l => ar.findFirstMatchIn(l).map(m => (m.group(1).replace("-","_"), l)).toList)
    .toMap

val aliasr = """(?s)(Attr[. ]*alias *\(.+?\)\))""".r
val realr = """(?s)(Attr[. ]*real *\(.+?\))""".r
val newr = """(?s)(new AliasAttr.+)""".r
val attrrs = List(aliasr,realr,newr)
def attrDfn(d: String): String = {
  val o = attrrs.map(_ findFirstIn d).reduce(_ orElse _)
  o getOrElse sys.error(s"Where is the attr in this?\n\n$d")
}

val commentr = """(?s)^(.+\n *\*/) *\n""".r
def comment(d: String): String =
  commentr.findFirstMatchIn(d).map(_ group 1) getOrElse sys.error(s"Where is the comment in this?\n\n$d")

def fixValue(s: String): String = s.trim match {
  case "clone" => "clone_"
  case "wait"  => "wait_"
  case "super" => "super_"
  case "avoid_colum" => "avoid_column"
  case x => x
}

scala.io.Source.fromFile("ext1").mkString.split("\n+").map(_.trim).sorted.foreach{l =>
  val Array(name,syn) = l.split(" *\t")
  val vals = syn.split("\\|").toVector.map(fixValue).sorted
  implicit val sz: Int = vals.map(_.size).max

  val attr = as(name)

//${comment(attr)}
  println(s"""
  object ${camel(name)} extends TypedAttrBase {
    override val attr = ${attrDfn(attr)}
${vals.map(fmtval).map("    "+_) mkString "\n"}
  }""")
}

