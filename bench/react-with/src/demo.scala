import org.scalajs.dom.{console, document}
import scala.scalajs.js.JSApp
import japgolly.scalajs.react._, vdom.html_<^._, ScalazReact._

object Temp {
  val CssSettings = scalacss.devOrProdDefaults
}
import Temp.CssSettings._
import scalacss.ScalaCssReact._

object MyStyles extends StyleSheet.Inline {
  import dsl._

  val outer = style(
    fontWeight.bold,
    margin(12 px))
}

object Demo extends JSApp {

  override def main(): Unit = {
    MyStyles.addToDocument()
    TodoApp().renderIntoDOM(document getElementById "todo")
    console.log("hello")
  }

  val TodoList = ScalaComponent.builder[List[String]]("TodoList")
    .render_P(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props.map(createItem): _*)
    })
    .build

  case class State(items: List[String], text: String)

  val ST = ReactS.Fix[State]

  def acceptChange(e: ReactEventFromInput) =
    ST.mod(_.copy(text = e.target.value))

  def handleSubmit(e: ReactEventFromInput) = (
    ST.retM(e.preventDefaultCB)

    >>
    ST.mod(s => State(s.items :+ s.text, "")).liftCB
  )

  val TodoApp = ScalaComponent.builder[Unit]("TodoApp")
    .initialState(State(Nil, ""))
    .renderS(($,s) =>
      <.div(
        MyStyles.outer,
        <.h3("TODO"),
        TodoList(s.items),
        <.form(^.onSubmit ==> $.runStateFn(handleSubmit))(
          <.input(
            ^.onChange ==> $.runStateFn(acceptChange),
            ^.value := s.text),
          <.button("Add #", s.items.length + 1)
        )
      )
    ).build

}
