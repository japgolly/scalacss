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


scala.io.Source.fromFile("ext1").mkString.split("\n+").sorted.foreach{l =>
  val Array(name,syn) = l.split("\t")
  val vals = syn.split("\\s*\\|\\s*").toVector.sorted
  implicit val sz: Int = vals.map(_.size).max

  println(s"""
  object ${camel(name)} extends TypedAttrBase {
    override val attr = ???
${vals.map(fmtval).map("    "+_) mkString "\n"}
  }""")
}

