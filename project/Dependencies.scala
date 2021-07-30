import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object Ver {

    // Exported
    val scala2          = "2.13.6"
    val scalaJsDom      = "1.1.0"
    val scalaJsReact    = "1.7.7"
    val scalatags       = "0.9.4"
    val univEq          = "1.4.0"

    // Internal
    val microlibs       = "2.6"
    val nyaya           = "0.10.0"
    val reactJs         = "16.14.0"
    val scalaz          = "7.2.32"
    val utest           = "0.7.10"
  }

  object Dep {
    val microlibsTestUtil  = Def.setting("com.github.japgolly.microlibs"     %%% "test-util"               % Ver.microlibs)
    val nyayaGen           = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-gen"               % Ver.nyaya)
    val nyayaProp          = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-prop"              % Ver.nyaya)
    val nyayaTest          = Def.setting("com.github.japgolly.nyaya"         %%% "nyaya-test"              % Ver.nyaya)
    val scalaCompiler      = Def.setting("org.scala-lang"                      % "scala-compiler"          % scalaVersion.value)
    val scalaJsDom         = Def.setting("org.scala-js"                      %%% "scalajs-dom"             % Ver.scalaJsDom)
    val scalaJsReactCore   = Def.setting("com.github.japgolly.scalajs-react" %%% "core"                    % Ver.scalaJsReact)
    val scalaJsReactExtra  = Def.setting("com.github.japgolly.scalajs-react" %%% "extra"                   % Ver.scalaJsReact)
    val scalaJsReactScalaz = Def.setting("com.github.japgolly.scalajs-react" %%% "ext-scalaz72"            % Ver.scalaJsReact)
    val scalaJsReactTest   = Def.setting("com.github.japgolly.scalajs-react" %%% "test"                    % Ver.scalaJsReact)
    val scalaReflect       = Def.setting("org.scala-lang"                      % "scala-reflect"           % scalaVersion.value)
    val scalatags          = Def.setting("com.lihaoyi"                       %%% "scalatags"               % Ver.scalatags)
    val scalaz             = Def.setting("org.scalaz"                        %%% "scalaz-core"             % Ver.scalaz)
    val univEq             = Def.setting("com.github.japgolly.univeq"        %%% "univeq"                  % Ver.univEq)
    val utest              = Def.setting("com.lihaoyi"                       %%% "utest"                   % Ver.utest)
  }

}
