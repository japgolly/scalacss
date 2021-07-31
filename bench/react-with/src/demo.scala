import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.{console, document}

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

object Demo {

  def main(args: Array[String]): Unit = {
    MyStyles.addToDocument()
    TodoApp().renderIntoDOM(document getElementById "todo")
    console.log("hello")
  }

  val TodoList = ScalaComponent.builder[List[String]]
    .render_P(props => {
      def createItem(itemText: String) = <.li(itemText)
      <.ul(props.map(createItem): _*)
    })
    .build

  case class State(items: List[String], text: String)

  final class Backend($: BackendScope[Unit, State]) {

    def acceptChange(e: ReactEventFromInput) =
      $.modState(_.copy(text = e.target.value))

    def handleSubmit(e: ReactEventFromInput) =
      e.preventDefaultCB >> $.modState(s => State(s.items :+ s.text, ""))

    def render(s: State): VdomNode =
      <.div(
        MyStyles.outer,
        <.h3("TODO"),
        TodoList(s.items),
        <.form(
          ^.onSubmit ==> handleSubmit,
          <.input(
            ^.onChange ==> acceptChange,
            ^.value := s.text,
          ),
          <.button("Add #", s.items.length + 1),
        )
      )
  }

  val TodoApp = ScalaComponent.builder[Unit]
    .initialState(State(Nil, ""))
    .renderBackend[Backend]
    .build

}
