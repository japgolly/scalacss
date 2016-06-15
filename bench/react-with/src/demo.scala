import org.scalajs.dom.{console, document}
import scala.scalajs.js.annotation.JSExport
import japgolly.scalajs.react._, vdom.prefix_<^._, ScalazReact._

import scalacss.Defaults._
import scalacss.ScalaCssReact._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val outer = style(
    fontWeight.bold,
    margin(12 px))
}

@JSExport("Demo")
object Demo {

  @JSExport("main")
  def main(): Unit = {
    MyStyles.addToDocument()
    ReactDOM.render(TodoApp(), document getElementById "todo")
    console.log("hello")
  }

  val TodoList = ReactComponentB[List[String]]("TodoList")
    .render_P(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props map createItem)
    })
    .build

  case class State(items: List[String], text: String)

  val ST = ReactS.Fix[State]

  def acceptChange(e: ReactEventI) =
    ST.mod(_.copy(text = e.target.value))

  def handleSubmit(e: ReactEventI) = (
    ST.retM(e.preventDefaultCB)

    >>
    ST.mod(s => State(s.items :+ s.text, "")).liftCB
  )

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .renderS(($,s) =>
      <.div(
        MyStyles.outer,
        <.h3("TODO"),
        TodoList(s.items),
        <.form(^.onSubmit ==> $._runState(handleSubmit))(
          <.input(
            ^.onChange ==> $._runState(acceptChange),
            ^.value := s.text),
          <.button("Add #", s.items.length + 1)
        )
      )
    ).build

}
