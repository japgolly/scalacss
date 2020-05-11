import org.scalajs.dom.{console, document}
import japgolly.scalajs.react._, vdom.html_<^._, ScalazReact._

object Demo {

  def main(args: Array[String]): Unit = {
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

  val ST = ReactS.Fix[State]

  def acceptChange(e: ReactEventFromInput) =
    ST.mod(_.copy(text = e.target.value))

  def handleSubmit(e: ReactEventFromInput) = (
    ST.retM(e.preventDefaultCB)
    >>
    ST.mod(s => State(s.items :+ s.text, "")).liftCB
  )

  val TodoApp = ScalaComponent.builder[Unit]
    .initialState(State(Nil, ""))
    .renderS(($,s) =>
      <.div(
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
