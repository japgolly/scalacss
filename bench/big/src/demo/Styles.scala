package demo

import demo.CssSettings._
import japgolly.univeq.UnivEq

sealed trait Live
case object Live extends Live
case object Dead extends Live

sealed trait Validity
case object Valid extends Validity
case object Invalid extends Validity

sealed trait Enabled
case object Enabled extends Enabled
case object Disabled extends Enabled

sealed trait On
case object On extends On
case object Off extends On

object DragToReorder {
  sealed abstract class Status
  case object Normal extends Status
  case object DragSource extends Status
  case object Tombstone extends Status
  implicit def univEq: UnivEq[Status] = UnivEq.derive
}

object Styles extends StyleSheet.Inline {
  import dsl._

  implicit def univEqLive    : UnivEq[Live]     = UnivEq.derive
  implicit def univEqValidity: UnivEq[Validity] = UnivEq.derive
  implicit def univEqEnabled : UnivEq[Enabled]  = UnivEq.derive
  implicit def univEqOn      : UnivEq[On]       = UnivEq.derive

  /** Domains */
  object D {
    val live     = Domain.ofValues[Live]    (Live, Dead)
    val validity = Domain.ofValues[Validity](Valid, Invalid)
    val enabled  = Domain.ofValues[Enabled] (Enabled, Disabled)
    val on       = Domain.ofValues[On]      (On, Off)

    val dragStatus = {
      import DragToReorder._
      Domain.ofValues[Status](Normal, DragSource, Tombstone)
    }

    val `live * on`       = live *** on
    val `live * validity` = live *** validity

    val uci = Domain.ofRange(0 until 5)
  }

  private val dragHnd = style(
    color(c"#000"))

  private val hasErrorBackground =
    backgroundColor(c"#fee")

  private val hasErrorColor = style(
    color(c"#c00"))

  private val errorRedOnRed = mixin(
    hasErrorColor,
    hasErrorBackground)

  private def deadColumnLabel(live: Live) =
    mixinIf(live == Dead)(textDecoration := ^.lineThrough)

  private val hasTitle = Pseudo.Custom("[title]", PseudoType.Element)

  private val hoverShowsInfo = hasTitle(cursor.help)

  private def hasError = errorRedOnRed

  private val deadMixin = mixin(
    textDecoration := ^.lineThrough)

  private def deadMaybeValid(v: Validity) = v match {
    case Valid   => deadAndNotError
    case Invalid => deadAndError
  }

  private val deadAndNotError = mixin(
    deadMixin,
    color(c"#999"))

  private val deadAndError = mixin(
    deadMixin,
    hasError)

  object cfg {
    val deadMnemonic = style(
      color(c"#aaa"),
      textDecoration := ^.lineThrough)
  }

  object blahtable {

    val pubidColumnValue = styleF(D.live)(a => styleS(
      display.inline,
      whiteSpace.nowrap,
      mixinIf(a == Dead)(deadAndNotError)))

    val viewSettingsHeader = style(
      backgroundColor(c"#ffe"))

    object sortCriteriaEditor {
      val inconclusiveCriterionRow = styleF(D.on)(_ => styleS(
        backgroundColor(c"#e2e2e2"),
        marginBottom(0.7 ex),
        paddingRight(1 ex)))

      def dragHnd = Styles.dragHnd

      val inconclusiveSortMethod = style(
        width(28 ex))

      val inconclusiveColumnName = styleF(D.`live * on`) { case (live, on) => styleS(
        marginLeft(1 ex),
        mixinIf(on == Off)(color(c"#999")),
        deadColumnLabel(live)
      )}

      val conclusiveSortMethod = style(
        marginLeft(4 ex))

      val conclusiveColumnName = style(
        marginLeft(1 ex))
    }

    object fed {
      val editor = styleF(D.validity)(v => styleS(
        marginTop(1.6 em),
        width(100 %%),
        height(3 em),
        mixinIf(v == Invalid)(hasErrorBackground)
      ))

      def errorMsg = hasErrorColor
    }

    object sed {

      val outer = style()

      val dragArea = style(
        display.inlineBlock,
        paddingRight(12.ex),
        paddingLeft(1 ex))

      val itemOuter = styleF(D.dragStatus)(status =>
        styleS(
          marginRight(2 ex),
          cursor.pointer,
          (status match {
            case DragToReorder.Normal => mixin(
              display.inlineBlock,
              border(1 px, solid, c"#bbb"))
            case DragToReorder.DragSource => mixin(
              display.inlineBlock,
              opacity(.4),
              border(2 px, dashed, c"#000"))
            case DragToReorder.Tombstone => mixin(
              display.none)
          }): StyleS
        ))

      val itemSortMethod = style(
        border.none,
        backgroundColor(c"#ddd"),
        verticalAlign.middle,
        textAlign.center,
        padding(v = 0.2.ex, h = 0.5.ex))

      val sortMethodFull = style(
        width(0.6.em),
        height(0.6.em))

      private val sortMethodHalf = mixin(
        display.block,
        width(0.55.em),
        height(0.55.em))

      val sortMethodHalfTop = style(
        sortMethodHalf,
        marginBottom(0.2.em))

      val sortMethodHalfBottom = style(
        sortMethodHalf)

      val itemName = styleF(Domain.boolean)(conclusive =>
        styleS(
          border.none,
          backgroundColor(c"#eee"),
          padding(v = 0.4.ex, h = 1.ex),
          verticalAlign.middle,
          mixinIf(conclusive)(fontWeight.bold)))
    }

    val sum = style(
      margin(1 em, `0`),
      padding(0.2 ex, 1 ex),
      color(c"#444"),
      backgroundColor(c"#ded"),
      width(100 %%))

    val table = style(
      marginTop(1.6 ex),
      width(100 %%))

    private val mlen = 9

    val colp   = style(maxWidth((mlen + 5).ex))
    val colrt = style(maxWidth(mlen.ex))

    val `N/A` = style(
      color(c"#666"),
      margin.horizontal(auto)
    )

    private val columnHeaderBase = mixin(
      backgroundColor(c"#e0e8f8"))

    val columnHeader = styleF(D.live *** D.dragStatus) { case (live, status) => styleS(
      columnHeaderBase,
      deadColumnLabel(live),
      cursor.pointer,
      (status match {
        case DragToReorder.Normal => mixin(
          border(1 px, solid, c"#777"))
        case DragToReorder.DragSource | DragToReorder.Tombstone => mixin(
          opacity(.4),
          border(2 px, dashed, c"#779"))
      }): StyleS
    )}

    val selectionRowHeader = style(
      columnHeaderBase)

    object CellStatus {
      sealed trait Status
      case object Normal extends Status
      case object DeadRow extends Status
      case object `N/A` extends Status
      val domain = Domain.ofValues[Status](Normal, DeadRow, `N/A`)
      implicit def univEq: UnivEq[Status] = UnivEq.derive
    }

    val cell = styleF(CellStatus.domain){ status =>
      styleS(
        border(1 px, solid, c"#ccc"),
        &.focus(
          backgroundColor(c"#e9e9ff")),
        (status match {
          case CellStatus.Normal => mixin(
            padding(v = 2.px, h = 4.px))
          case CellStatus.DeadRow => mixin(
            padding(v = 2.px, h = 4.px), backgroundColor(c"#eee"))
          case CellStatus.`N/A` => mixin(
            padding.`0`,
            backgroundColor(c"#eee"),
            textAlign.center,
            verticalAlign.middle)
        }): StyleS
      )
    }

    val ced = styleF(D.validity)(v => styleS(
      borderRadius(4 px),
      width(100 %%),
      boxShadow := "inset 0 1px 1px rgba(0,0,0,.075)",
      transition := "border-color ease-in-out .15s, box-shadow ease-in-out .15s",
      mixinIf(v == Invalid)(hasErrorBackground, &.focus(outlineColor(c"#f88"))),
      padding.horizontal(0.8 ex)
    ))

    val cedErrMsg = style(
      color(c"#a00"))

    val acItemTitle = style(
      fontWeight.bold)

    val acItemTitle2 = style(
      paddingLeft(1 ex),
      color(c"#333"))

    val acItemDesc = style(
      color(c"#444"),
      fontStyle.italic,
      overflow.hidden,
      maxWidth(36 ex))

    val tedp = style(
      padding(h = 0.8.ex, v = 0.2.em),
      border(solid, 1 px, c"#222"),
      minHeight(2 em),
      backgroundColor(c"#efe"))

    object dr {
      val section = style(
        marginTop(2.3 em),
        marginBottom(1 em),
        fontWeight.bold)

      val row = styleF(D.live)(live => styleS(
        mixinIf(live == Dead)(backgroundColor(c"#fee"), color(c"#a00"))
      ))

      val blahDesc =
        style(marginLeft(0.5 ex))

      val impliedByPrefix =
        style(marginRight(0.5 ex))

      val impliedByItem = styleF(D.live)(l => styleS(
        mixinIf(l == Live)(color(c"#111")),
        mixinIf(l == Dead)(
          textDecoration := ^.lineThrough,
          color(c"#daa"))
      ))

      def subCodeCount = impliedByItem
    }
  }

  object blahdetail {
    val header = style(
      fontSize(220 %%),
      display.flex)

    val headerId = style(
      whiteSpace.pre)

    val headerTitle = style(
      marginLeft(1 ex),
      flexGrow(1))

    val mainTable = style(
      width(100 %%),
      marginTop(2 em))

    private def padSizeL = 0.8 ex

    def rowCell = styleS(
      padding.vertical(0.4 em),
      paddingLeft(padSizeL))

    val rowTitle = style(
      rowCell,
      whiteSpace.pre,
      paddingRight(1.4 ex))

    val rowValue = style(
      rowCell,
      paddingRight(padSizeL),
      width(100 %%))

    val generalIsCont = style(
      display.flex,
      alignItems.center,
      width(100 %%))

    val generalIsSide = style(
      border(^.dashed, 1 px),
      minHeight(1.59 em),
      flexBasis := "0",
      flexGrow(1))

    val generalIsMiddle = style(
      margin.horizontal(1 ex))

    object ucs {

      val container = style(
        display.flex,
        justifyContent.flexEnd,
        width(100 %%))

      val header = styleF(D.uci)(lvl =>
        styleS(
          paddingTop(4 px),
          paddingRight(0.8 ex),
          color(c"#444"),
          lvl match {
            case 0 => styleS(fontWeight.bold,    width(5 ex))
            case 1 => styleS(paddingLeft( 4 ex), width(3 ex))
            case 2 => styleS(paddingLeft( 7 ex), width(3 ex))
            case 3 => styleS(paddingLeft(10 ex), width(4 ex))
            case 4 => styleS(paddingLeft(14 ex), width(3 ex))
          }
        )
      )

      val dslbl = style(
        color(c"#bbb"),
        textDecoration := ^.lineThrough)

      val body = style(
        flexGrow(1))

      val ctrls = style(
        width(116 px))

      val ctrl = style(
        addClassNames("btn", "btn-default", "btn-sm"),
        fontSize(16 px),
        lineHeight(16 px),
        padding(4 px),
        width(26 px))
    }
  }

  object widgets {

    private val refColour = color(c"#2363A1")

    val blankLine = style(display.block, height(1 em))

    val ul = style(paddingLeft(2.4 ex))

    private def tagBase(live: Live) = mixin(
      display.inlineBlock,
      mixinIf(live == Dead)(&.not(_.hover)(textDecoration := ^.lineThrough)),
      hoverShowsInfo)

    private def tagLabelSuffix(live: Live) = live match {
      case Live => "primary"
      case Dead => "default"
    }
    val tag = styleF(D.live)(live => styleS(
      tagBase(live),
      addClassName(s"label label-${tagLabelSuffix(live)}")))

    val tagInText = styleF(D.`live * validity`){ case (l, v) => styleS(
      tagBase(l),
      mixinIf(l == Live)(refColour),
      mixinIf(l == Dead)(deadMaybeValid(v)))
    }

    val blahTypeShort = styleF(D.live)(a => styleS(
      hoverShowsInfo,
      mixinIf(a == Dead)(deadAndNotError)))

    val issue = style(hasError)

    val eucsr = style(hasError)

    val issueDesc = style(
      padding.horizontal(0.7 ex))

    val blahRef = styleF(D.`live * validity`){ case (l, v) => styleS(
      mixinIf(l == Live)(refColour),
      mixinIf(l == Dead)(deadMaybeValid(v))
    )}

    def blahCodeGroupRef = blahRef

    def uscrf = blahRef

    val math = style(margin.horizontal(0.8 ex))
    val mathFail = style(math, hasError)

    private val blahCodePre = mixin(
      margin.`0`,
      padding.`0`,
      background := ^.unset,
      border.none,
      fontSize(12 px),
      lineHeight(1 em),
      wordBreak.keepAll,
      wordWrap.normal,
      whiteSpace.pre
    )
    private val blahCodeTreePre = mixin(blahCodePre, display.inline)

    val blahCodeTreeIndent = style(blahCodeTreePre, color(c"#dadada"))
    val blahCodeTreeCode = style(blahCodeTreePre)
    val blahCodeFlat = style(blahCodePre, display.block)

    val usclst = style(
      border.none.important)
  }

  // ===================================================================================================================

  initInnerObjects(
    cfg.deadMnemonic,
    blahtable.sed.dragArea,
    blahtable.sortCriteriaEditor.conclusiveColumnName,
    blahtable.fed.errorMsg,
    blahtable.table,
    blahtable.dr.impliedByItem(Live),
    blahdetail.mainTable,
    blahdetail.ucs.container,
    widgets.issue)
}
