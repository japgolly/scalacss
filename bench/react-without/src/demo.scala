import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.{console, document}

object Demo {

  def main(args: Array[String]): Unit = {

    // Try to make JS size comparison fairer
    val v = Vector.empty[Int] :+ 1 :+ 2 :+ 3 :+ 4 :+ 5 :+ 6 :+ 7
    val m = Map.empty[Int, Int] ++ v.iterator.map(i => (i, i))
    val s = Set.empty[(Int, Int)] ++ m
    console.log(s.toString)

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
