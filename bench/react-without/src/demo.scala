import org.scalajs.dom.{console, document}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import japgolly.scalajs.react._, vdom.prefix_<^._, ScalazReact._

@JSExport("Demo")
object Demo {

  @JSExport("main")
  def main(): Unit = {
    React.render(TodoApp(), document getElementById "todo")
    console.log("hello")
  }

  val TodoList = ReactComponentB[List[String]]("TodoList")
    .render(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props map createItem)
    })
    .build

  case class State(items: List[String], text: String)

  val ST = ReactS.Fix[State]

  def acceptChange(e: ReactEventI) =
    ST.mod(_.copy(text = e.target.value))

  def handleSubmit(e: ReactEventI) = (
    ST.retM(e.preventDefaultIO)

    >>
    ST.mod(s => State(s.items :+ s.text, "")).liftIO
  )

  val TodoApp = ReactComponentB[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .renderS(($,_,S) =>
      <.div(
        <.h3("TODO"),
        TodoList(S.items),
        <.form(^.onSubmit ~~> $._runState(handleSubmit))(
          <.input(
            ^.onChange ~~> $._runState(acceptChange),
            ^.value := S.text),
          <.button("Add #", S.items.length + 1)
        )
      )
    ).buildU

}
