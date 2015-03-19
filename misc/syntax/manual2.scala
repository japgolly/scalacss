#!/usr/bin/env scala

def camel(s: String) = "[_-]+([a-z\\d])".r.replaceAllIn(s, _.group(1).toUpperCase)
def _Camel(s: String) = "^([a-z])".r.replaceAllIn(camel(s), _.group(1).toUpperCase)

// def pad(s: String) = String.format("%-21s",s)
def pad(s: String)(implicit sz: Int) = String.format("%-"+sz+"s",s)

def fmtConst(s: String)(implicit sz: Int) =
  if (s.matches("^[0-9]+$"))
    // s"final def apply(n: w$s.T): AV = ^(w$s)"
    s"""final def ${pad("_"+s)} = av("$s")"""
  else
    s"final def ${pad(s)} = avl(Values.$s)"

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
  val a = o getOrElse sys.error(s"Where is the attr in this?\n\n$d")
  a.replace("\n", "\n  ")
}

val commentr = """(?s)^(.+\n *\*/) *\n""".r
def comment(d: String): String =
  commentr.findFirstMatchIn(d).map(_ group 1) getOrElse sys.error(s"Where is the comment in this?\n\n$d")

scala.io.Source.fromFile("manual2.tsv").mkString.split("\n+").map(_.trim).sorted.foreach{l =>
  val Array(name,syn) = l.split(" *\t")

  val syntax =
    syn.replace("'","").replaceAll("\\s*\\|\\|\\s*"," ")
      .replace("border_", "br_")

  val parent = syntax match {
    case "<max_width>"    => "TypedAttr_MaxLength"
    case "<min_width>"    => "TypedAttr_MinLength"
    case "<margin_left>"  => "TypedAttr_LenPctAuto"
    case "<padding_left>" => "TypedAttrT1[LenPct] with ZeroLit"
    case "<left>"         => "TypedAttr_LenPctAuto"
  }

  val attr = as(name)

//${comment(attr)}
    // final def ${pad(tokenMethod)} = avt(v)
// if (true) () else
  println(s"""
  object ${camel(name)} extends $parent {
    override val attr = ${attrDfn(attr)}
  }""".replaceFirst("\n\n","\n"))
}

