package scalacss.internal

import japgolly.microlibs.testutil.TestUtil._
import nyaya.gen._
import nyaya.prop._
import nyaya.test.PropTest._
import nyaya.test.{Domain => NDomain}
import scalacss.internal.AttrCmp.{Overlap, Unrelated}
import scalacss.internal.Attrs._
import scalacss.internal.Dsl.ToAVToAV
import scalacss.internal.ValueT.Rules._
import sourcecode.Line
import utest._

object AttrTest extends TestSuite {

  def laws1: Prop[Attr] = (
    Prop.equal[Attr, AttrCmp]("cmp is reflexive: a.cmp(a) = Same", a => a cmp a, _ => AttrCmp.Same)
      & Prop.test("id is populated", _.id.nonEmpty)
    )

  def laws2: Prop[(Attr, Attr)] =
    Prop.equal("cmp is commutative: a.cmp(b) = b.cmp(a)", t => t._1 cmp t._2, t => t._2 cmp t._1)

  val builtInAttrs: NDomain[Attr] =
    NDomain.ofValues(Attrs.values.whole: _*)

  val builtInAttrPairs: NDomain[(Attr, Attr)] =
    builtInAttrs.pair

  val builtInAttrTriplets: Gen[(Attr, Attr, Attr)] =
    RandomData.attr.triple

  def px(n: Int) = Length(n, LengthUnit.px)
  val length = px(3)
  val pct = Percentage(5)
  val style = Literal.Typed.dashed
  val colour = Color.green

  def testGen(l: CssKV.Lens)(a: Attr, v: Value, exp: String*)(implicit q: Line): Unit = {
    val x = a.gen(Env.empty)(v).map(l.get).sorted
    val y = exp.toVector.sorted
    assertEq(x, y)
  }

  override def tests = Tests {
    "laws1" - laws1.mustBeProvedBy(builtInAttrs)
    "laws2" - laws2.mustBeProvedBy(builtInAttrPairs)
//    'laws3 - Attr.laws3.mustBeSatisfiedBy(builtInAttrTriplets)

    "overlap" - {
      def test(e: AttrCmp, a: Attr, b: Attr)(implicit q: Line): Unit =
        assertEq(s"$a cmp $b", a cmp b, e)

      test(Unrelated, padding,     margin)
      test(Overlap,   padding,     paddingLeft)
      test(Overlap,   all,         padding)
      test(Overlap,   all,         paddingLeft)
      test(Overlap,   border,      borderLeft)
      test(Overlap,   border,      borderColor)
      test(Overlap,   borderLeft,  borderColor)
      test(Unrelated, borderStyle, borderColor)
      test(Unrelated, borderLeft,  borderRightColor)
      test(Unrelated, borderLeft,  borderRight)

      "logicalProps" - {
        "size" - {
          "base" - test(Unrelated, blockSize,    inlineSize)
          "min"  - test(Unrelated, minBlockSize, minInlineSize)
          "max"  - test(Unrelated, maxBlockSize, maxInlineSize)
        }

        "margin" - {
          "blockStart_blockEnd"   - test(Unrelated, marginBlockStart,  marginBlockEnd)
          "blockStart_inlineStart"- test(Unrelated, marginBlockStart,  marginInlineStart)
          "blockStart_inlineEnd"  - test(Unrelated, marginBlockStart,  marginInlineEnd)
          "blockEnd_inlineStart"  - test(Unrelated, marginBlockEnd,    marginInlineStart)
          "blockEnd_inlineEnd"    - test(Unrelated, marginBlockEnd,    marginInlineEnd)
          "inlineStart_inlineEnd" - test(Unrelated, marginInlineStart, marginInlineEnd)
          "block_inline"          - test(Unrelated, marginBlock,       marginInline)
          "block_inlineStart"     - test(Unrelated, marginBlock,       marginInlineStart)
          "inline_blockEnd"       - test(Unrelated, marginInline,      marginBlockEnd)
        }

        "padding" - {
          "blockStart_blockEnd"   - test(Unrelated, paddingBlockStart,  paddingBlockEnd)
          "blockStart_inlineStart"- test(Unrelated, paddingBlockStart,  paddingInlineStart)
          "blockStart_inlineEnd"  - test(Unrelated, paddingBlockStart,  paddingInlineEnd)
          "blockEnd_inlineStart"  - test(Unrelated, paddingBlockEnd,    paddingInlineStart)
          "blockEnd_inlineEnd"    - test(Unrelated, paddingBlockEnd,    paddingInlineEnd)
          "inlineStart_inlineEnd" - test(Unrelated, paddingInlineStart, paddingInlineEnd)
          "block_inline"          - test(Unrelated, paddingBlock,       paddingInline)
          "block_inlineStart"     - test(Unrelated, paddingBlock,       paddingInlineStart)
          "inline_blockEnd"       - test(Unrelated, paddingInline,      paddingBlockEnd)
        }

        "inset" - {
          "blockStart_blockEnd"   - test(Unrelated, insetBlockStart,  insetBlockEnd)
          "blockStart_inlineStart"- test(Unrelated, insetBlockStart,  insetInlineStart)
          "blockStart_inlineEnd"  - test(Unrelated, insetBlockStart,  insetInlineEnd)
          "blockEnd_inlineStart"  - test(Unrelated, insetBlockEnd,    insetInlineStart)
          "blockEnd_inlineEnd"    - test(Unrelated, insetBlockEnd,    insetInlineEnd)
          "inlineStart_inlineEnd" - test(Unrelated, insetInlineStart, insetInlineEnd)
          "block_inline"          - test(Unrelated, insetBlock,       insetInline)
          "block_inlineStart"     - test(Unrelated, insetBlock,       insetInlineStart)
          "inline_blockEnd"       - test(Unrelated, insetInline,      insetBlockEnd)
        }

        "offset" - {
          @scala.annotation.nowarn("cat=deprecation")
          def checkDeprecatedOffset(): Unit = {
            test(Unrelated, offsetBlockStart,  offsetBlockEnd)
            test(Unrelated, offsetBlockStart,  offsetInlineStart)
            test(Unrelated, offsetBlockStart,  offsetInlineEnd)
            test(Unrelated, offsetBlockEnd,    offsetInlineStart)
            test(Unrelated, offsetBlockEnd,    offsetInlineEnd)
            test(Unrelated, offsetInlineStart, offsetInlineEnd)
          }
          checkDeprecatedOffset()
        }

        "correctOverlap" - {
          "shorthand" - {
            "margin" - {
              test(Overlap, margin, marginBlock)
              test(Overlap, margin, marginInline)
              test(Overlap, margin, marginBlockStart)
              test(Overlap, margin, marginBlockEnd)
              test(Overlap, margin, marginInlineStart)
              test(Overlap, margin, marginInlineEnd)
            }
            "padding" - {
              test(Overlap, padding, paddingBlock)
              test(Overlap, padding, paddingInline)
              test(Overlap, padding, paddingBlockStart)
              test(Overlap, padding, paddingBlockEnd)
              test(Overlap, padding, paddingInlineStart)
              test(Overlap, padding, paddingInlineEnd)
            }
            "container" - {
              test(Overlap, container, containerName)
              test(Overlap, container, containerType)
            }
            "mask" - {
              test(Overlap, mask, maskClip)
              test(Overlap, mask, maskComposite)
              test(Overlap, mask, maskImage)
              test(Overlap, mask, maskMode)
              test(Overlap, mask, maskOrigin)
              test(Overlap, mask, maskPosition)
              test(Overlap, mask, maskRepeat)
              test(Overlap, mask, maskSize)
            }
            "placeContent" - {
              test(Overlap, placeContent, alignContent)
              test(Overlap, placeContent, justifyContent)
            }
            "placeItems" - {
              test(Overlap, placeItems, alignItems)
              test(Overlap, placeItems, justifyItems)
            }
            "all" - {
              test(Overlap, all, blockSize)
              test(Overlap, all, inlineSize)
              test(Overlap, all, marginBlockStart)
              test(Overlap, all, paddingInlineEnd)
            }
          }

          "physicalProperties" - {
            "blockSize" - {
              test(Overlap, blockSize, height)
              test(Overlap, blockSize, width)
            }
            "inlineSize" - {
              test(Overlap, inlineSize, height)
              test(Overlap, inlineSize, width)
            }
            "minSize" - {
              test(Overlap, minBlockSize,  minHeight)
              test(Overlap, minBlockSize,  minWidth)
              test(Overlap, minInlineSize, minHeight)
              test(Overlap, minInlineSize, minWidth)
            }
            "maxSize" - {
              test(Overlap, maxBlockSize,  maxHeight)
              test(Overlap, maxBlockSize,  maxWidth)
              test(Overlap, maxInlineSize, maxHeight)
              test(Overlap, maxInlineSize, maxWidth)
            }
            "margins" - {
              test(Overlap, marginBlockStart, marginTop)
              test(Overlap, marginBlockEnd,   marginBottom)
              test(Overlap, marginBlock,      marginBlockStart)
              test(Overlap, marginInline,     marginInlineEnd)
            }
            "paddings" - {
              test(Overlap, paddingBlockStart, paddingTop)
              test(Overlap, paddingBlockEnd,   paddingBottom)
              test(Overlap, paddingBlock,      paddingBlockStart)
              test(Overlap, paddingInline,     paddingInlineEnd)
            }
            "inset" - {
              test(Overlap, inset,            top)
              test(Overlap, inset,            insetBlock)
              test(Overlap, inset,            insetInline)
              test(Overlap, inset,            insetBlockStart)
              test(Overlap, insetBlock,       insetBlockStart)
              test(Overlap, insetBlock,       insetBlockEnd)
              test(Overlap, insetInline,      insetInlineStart)
              test(Overlap, insetInline,      insetInlineEnd)
              test(Overlap, insetBlockStart,  top)
              test(Overlap, insetInlineEnd,   right)
              test(Overlap, insetBlock,       bottom)
              test(Overlap, insetInline,      left)
              test(Overlap, all,              inset)
              test(Overlap, all,              insetBlock)
              test(Overlap, all,              insetBlockStart)
            }
          }
        }
      }
    }

    "conflicts" - {
      "transitive" - {
        val a = AV(marginLeft, "1px")
        val b = AV(margin, "2px")
        val c = AV(marginRight, "3px")
        val actual = AttrCmp.conflicts(Vector(a, b, c))
        assertEq(actual, Set(a, b, c))
      }
      "transitive4" - {
        val a = AV(marginLeft, "1px")
        val b = AV(margin, "2px")
        val c = AV(marginRight, "3px")
        val d = AV(marginTop, "4px")
        val actual = AttrCmp.conflicts(Vector(a, b, c, d))
        assertEq(actual, Set(a, b, c, d))
      }
    }

    "keyPrefixes" - {
      def test(a: Attr, exp: String*)(implicit q: Line): Unit =
        testGen(CssKV.key)(a, "x", exp: _*)
      test(textAlign,           "text-align")
      test(borderRadius,        "border-radius", "-webkit-border-radius")
      test(flexWrap,            "flex-wrap", "-ms-flex-wrap", "-webkit-flex-wrap")
      test(overscrollBehavior,  "overscroll-behavior")
      test(overscrollBehaviorX, "overscroll-behavior-x")
      test(overscrollBehaviorY, "overscroll-behavior-y")
      test(rowGap,              "row-gap")
      test(boxReflect,          "-o-box-reflect", "-webkit-box-reflect")
      test(backgroundOrigin,    "background-origin", "-webkit-background-origin")
      test(backgroundSize,      "background-size", "-webkit-background-size")
      test(overflowX,           "overflow-x")
      test(overflowY,           "overflow-y")
      test(alignContent,        "align-content", "-ms-align-content", "-webkit-align-content")
    }

    "prefixPlan" - {
      "unprefixedExcludedWhenNotSupported" - {
        assert(!CanIUse2.prefixPlan(CanIUse.reflections).contains(None))
      }
      "unprefixedIncludedWhenSupported" - {
        assert(CanIUse2.prefixPlan(CanIUse.borderRadius).contains(None))
      }
    }

    "prefixed" - {
      import CanIUse2.prefixed
      "positive" - {
        assert(prefixed("-webkit-transform"))
        assert(prefixed("-moz-available"))
        assert(prefixed("-ms-flex"))
        assert(prefixed("-o-text-overflow"))
        assert(prefixed("-webkit-linear-gradient(red, blue)"))
        assert(prefixed("url('foo.png'), -webkit-linear-gradient(red, blue)"))
        assert(prefixed("-moz-zoom-in !important"))
      }
      "negative" - {
        assert(!prefixed("transform"))
        assert(!prefixed("url(logo-o-2.png), linear-gradient(red, blue)"))
        assert(!prefixed("url('button-ms-normal.png'), linear-gradient(red, blue)"))
        assert(!prefixed("url('foo-moz-bar.svg')"))
        assert(!prefixed("foo-o-bar"))
        assert(!prefixed("logo-o-2.png"))
        assert(!prefixed("button-ms-normal"))
        assert(!prefixed("--o-custom-prop"))
        assert(!prefixed("linear-gradient(red, blue)"))
      }
      "backgroundImagePrefixingWithUrlContainingSubstring" - {
        val av = AV(backgroundImage, "url(logo-o-2.png), linear-gradient(red, blue)")
        val actual = av(Env.empty).map(_.value)
        val expected = Vector(
          "url(logo-o-2.png), -o-linear-gradient(red, blue)",
          "url(logo-o-2.png), -webkit-linear-gradient(red, blue)",
          "url(logo-o-2.png), linear-gradient(red, blue)",
        )
        assertEq(actual, expected)
      }
      "multiLineGradient" - {
        val av = AV(backgroundImage, "linear-gradient(\n  red,\n  blue\n)")
        val actual = av(Env.empty).map(_.value)
        val expected = Vector(
          "-o-linear-gradient(\n  red,\n  blue\n)",
          "-webkit-linear-gradient(\n  red,\n  blue\n)",
          "linear-gradient(\n  red,\n  blue\n)",
        )
        assertEq(actual, expected)
      }
      "imageSet" - {
        val samsungEnv = Env(Env.Platform.empty(None).copy(name = Some("Samsung Internet")))
        val chromeEnv  = Env(Env.Platform.empty(None).copy(name = Some("Chrome")))
        val imgSet     = "image-set(url(a.png) 1x, url(b.png) 2x)"
        val prefixed   = s"-webkit-$imgSet"
        assertEq(AV(backgroundImage, imgSet)(Env.empty).map(_.value),  Vector(prefixed, imgSet))
        assertEq(AV(background, imgSet)(Env.empty).map(_.value),       Vector(prefixed, imgSet))
        assertEq(AV(backgroundImage, imgSet)(samsungEnv).map(_.value), Vector(prefixed, imgSet))
        assertEq(AV(backgroundImage, imgSet)(chromeEnv).map(_.value),  Vector(imgSet))
      }
      "backgroundImageUrlContainsKeyword" - {
        assertEq(
          AV(backgroundImage, "url(linear-gradient.png)")(Env.empty).map(_.value),
          Vector("url(linear-gradient.png)")
        )
        assertEq(
          AV(backgroundImage, "url(linear-gradient.png), linear-gradient(red, blue)")(Env.empty).map(_.value),
          Vector(
            "url(linear-gradient.png), -o-linear-gradient(red, blue)",
            "url(linear-gradient.png), -webkit-linear-gradient(red, blue)",
            "url(linear-gradient.png), linear-gradient(red, blue)",
          )
        )
      }
      "backgroundImageCombinedFunctions" - {
        assertEq(
          AV(backgroundImage, "linear-gradient(red, blue), image-set(url(a.png) 1x)")(Env.empty).map(_.value),
          Vector(
            "-o-linear-gradient(red, blue), image-set(url(a.png) 1x)",
            "-webkit-linear-gradient(red, blue), -webkit-image-set(url(a.png) 1x)",
            "linear-gradient(red, blue), image-set(url(a.png) 1x)",
          )
        )
        assertEq(
          AV(background, "linear-gradient(red, blue), image-set(url(a.png) 1x)")(Env.empty).map(_.value),
          Vector(
            "-o-linear-gradient(red, blue), image-set(url(a.png) 1x)",
            "-webkit-linear-gradient(red, blue), -webkit-image-set(url(a.png) 1x)",
            "linear-gradient(red, blue), image-set(url(a.png) 1x)",
          )
        )
        assertEq(
          AV(backgroundImage, "linear-gradient(red, blue), repeating-linear-gradient(red, blue)")(Env.empty).map(_.value),
          Vector(
            "-o-linear-gradient(red, blue), -o-repeating-linear-gradient(red, blue)",
            "-webkit-linear-gradient(red, blue), -webkit-repeating-linear-gradient(red, blue)",
            "linear-gradient(red, blue), repeating-linear-gradient(red, blue)",
          )
        )
      }
    }

    "valuePrefixes" - {
      def test(av: AV, exp: String*)(implicit q: Line): Unit = testGen(CssKV.value)(av.attr, av.value, exp: _*)
      "textAlign.left"          - test(textAlign.left,          "left")
      "textAlign.justify"       - test(textAlign.justify,       "justify")
      "float.inlineStart"       - test(float.inlineStart,       "inline-start")
      "float.inlineEnd"         - test(float.inlineEnd,         "inline-end")
      "clear.inlineStart"       - test(clear.inlineStart,       "inline-start")
      "clear.inlineEnd"         - test(clear.inlineEnd,         "inline-end")
      "captionSide.blockStart"  - test(captionSide.blockStart,  "block-start")
      "captionSide.inlineStart" - test(captionSide.inlineStart, "inline-start")
      "resize.block"            - test(resize.block,            "block")
      "resize.inline"           - test(resize.inline,           "inline")
      "width.available"         - test(width.available,         "available", "-webkit-available")
      "width.fillAvailable"     - test(width.fillAvailable,     "fill-available", "-webkit-fill-available")
      "cursor.pointer"          - test(cursor.pointer,          "pointer")
      "cursor.zoomIn"           - test(cursor.zoomIn,           "zoom-in", "-webkit-zoom-in")
      "cursor.grab"             - test(cursor.grab,             "grab", "-webkit-grab")
      "cursor.grabbing"         - test(cursor.grabbing,         "grabbing", "-webkit-grabbing")
      "alignContent.spaceEvenly" - {
        assertEq(
          alignContent.spaceEvenly(Env.empty),
          Vector(
            CssKV("-ms-align-content", "space-evenly"),
            CssKV("-webkit-align-content", "space-evenly"),
            CssKV("align-content", "space-evenly"),
          )
        )
      }

      "resize.block" - {
        val expected = Vector(
          CssKV("resize", "block"),
        )
        assertEq(resize.block(Env.empty), expected)
      }

      "important" - {
        test(textAlign.left.important, "left !important")
        test(cursor.pointer.important, "pointer !important")
        test(cursor.zoomIn.important,  "zoom-in !important", "-webkit-zoom-in !important")

        "avApply" - {
          val expected = Vector(
            CssKV("cursor", "-webkit-zoom-in !important"),
            CssKV("cursor", "zoom-in !important"),
          )
          assertEq(cursor.zoomIn.important(Env.empty), expected)
        }
      }
    }

    "content" - {
      "e" - assertEq(content.string("").value, "''")
      "s" - assertEq(content.string("abc 123").value, "'abc 123'")
      "q" - assertEq(content.string("""ok " ' \ Δ cool""").value, """'ok " \000027 \00005C \000394 cool'""")
      "u" - assertEq(content.url("http://www.example.com/test.png").value, "url('http://www.example.com/test.png')")

      "hexFollowedByHexChar" - {
        assertEq(content.string("'a").value, "'\\000027a'")
        assertEq(content.string("\\a").value, "'\\00005Ca'")
        assertEq(content.string("Δa").value, "'\\000394a'")
      }

      "newlines" - {
        assertNotContains(content.string("hello\nworld").value, "\n")
        assertNotContains(content.string("hello\rworld").value, "\r")
        assertNotContains(content.string("hello\fworld").value, "\f")
        assertEq(content.string("hello\nworld").value, "'hello\\00000Aworld'")
      }

      "surrogatePairs" - {
        val v = content.string("🎉").value
        assertEq(v, "'\\01F389'")
      }
    }

    "border" - {
      assertEq(border(length).value, "3px")
      assertEq(border(length, style).value, "3px dashed")
      assertEq(border(length, style, colour).value, "3px dashed green")
      compileError("border(pct)")
      compileError("outline(pct)")
      compileError("columnRule(pct)")
    }

    "textIndent" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit =
        assertEq(av.value, exp)
      import Literal.Typed._
      test(textIndent(length)                   , "3px")
      test(textIndent(length, hanging)          , "3px hanging")
      test(textIndent(length, eachLine)         , "3px each-line")
      test(textIndent(length, hanging, eachLine), "3px hanging each-line")
      test(textIndent(length, eachLine, hanging), "3px each-line hanging")
    }

    "backgroundClip" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
      test(backgroundClip.paddingBox                     , "padding-box")
      test(backgroundClip.borderBox                      , "border-box")
      test(backgroundClip.contentBox                     , "content-box")
      test(backgroundClip.text                           , "text")
      test(backgroundClip.borderBox.text                 , "border-box,text")
      test(backgroundClip.contentBox.paddingBox          , "content-box,padding-box")
      test(backgroundClip.contentBox.paddingBox.important, "content-box,padding-box !important")

      "prefixes" - {
        val samsungEnv = Env(Env.Platform.empty(None).copy(name = Some("Samsung Internet")))
        val chromeEnv  = Env(Env.Platform.empty(None).copy(name = Some("Chrome")))
        assertEq(backgroundClip.text.av(Env.empty).map(_.key),       Vector("-webkit-background-clip", "background-clip"))
        assertEq(backgroundClip.text.av(samsungEnv).map(_.key),      Vector("-webkit-background-clip", "background-clip"))
        assertEq(backgroundClip.borderBox.av(samsungEnv).map(_.key), Vector("background-clip"))
        assertEq(backgroundClip.text.av(chromeEnv).map(_.key),       Vector("background-clip"))
      }
    }

    "borderRadius" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
      test(borderRadius(px(3)), "3px")
      test(borderRadius(px(3)).important, "3px !important")
      test(borderRadius(px(3))(px(5)), "3px / 5px")
      test(borderRadius(px(3))(px(5)).important, "3px / 5px !important")
      test(borderRadius(px(1), px(2), px(3), px(4))(px(9), px(8), px(7), px(6)), "1px 2px 3px 4px / 9px 8px 7px 6px")
    }

    "quotes" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
      test(quotes("[", "]"), "'[' ']'")
      test(quotes("[", "]").important, "'[' ']' !important")
      test(quotes("[", "]")("<", ">").important, "'[' ']' '<' '>' !important")
    }

    "textDecorationLine" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
      test(textDecorationLine.underline, "underline")
      test(textDecorationLine.underline.important, "underline !important")
      test(textDecorationLine.underline.overline.important, "underline overline !important")
    }

    "PrefixApplyWords" - {
      val pa = CanIUse2.PrefixApply.keywords("abc", "def")
      def test(i: String, eo: String = null)(implicit q: Line): Unit = {
        val o = pa(i).fold(i)(_(CanIUse.Prefix.o))
        assertEq(o, Option(eo) getOrElse i)
      }
      test("abc", "-o-abc")
      test("def", "-o-def")
      test("abc-")
      test("abc0")
      test("abcd")
      test("-abc")
      test("0abc")
      test("aabc")
      test(" abc", " -o-abc")
      test("abc ", "-o-abc ")
      test("abc 123", "-o-abc 123")
      test("qwe abc", "qwe -o-abc")
      test("abc abc 123 def", "-o-abc -o-abc 123 -o-def")
      test("-o-abc")
      test("def(12)", "-o-def(12)")
      test("abc\n123", "-o-abc\n123")
      test("123\ndef", "123\n-o-def")
      test("hello\nabc\nworld", "hello\n-o-abc\nworld")
      test("multi\nline\ndef(12)", "multi\nline\n-o-def(12)")
      test("$abc", "$-o-abc")
      test("$1 abc", "$1 -o-abc")
      test("\\abc", "\\-o-abc")
      test("foo\\def", "foo\\-o-def")
      test("url(abc.png)")
      test("url('abc.png')")
      test("url(\"abc.png\")")
      test("url(foo/abc/bar.png)")
      test("url(abc.png), def(1)", "url(abc.png), -o-def(1)")
      test("'abc'")
      test("\"def\"")
      test("'abc' abc", "'abc' -o-abc")
      test("\"def\" def(1)", "\"def\" -o-def(1)")
      assertEq(pa("url(abc.png)").isDefined, false)
      assertEq(pa("'abc'").isDefined, false)
    }

    "envDepPrefixes1" - {
      def test(name: String)(exp: String*): Unit = {
        val env = Env(Env.Platform.empty(None).copy(name = Option(name)))
        val a = cursor.zoomIn(env).map(_.value).sorted
        assertEq(a, exp.toVector.sorted)
      }
      "chrome"  - test("Chrome") ("zoom-in")
      "firefox" - test("Firefox")("zoom-in")
      "opera"   - test("Opera")  ("zoom-in")
      "ie"      - test("IE")     (/*"-ms-zoom-in", Unsupported */  "zoom-in")
      "safari"  - test("Safari") ("zoom-in")
      "all"     - test(null)     ("-webkit-zoom-in", "zoom-in")
    }

    "envDepPrefixes2" - {
      def test(name: String)(exp: String*)(implicit q: Line): Unit = {
        val env = Env(Env.Platform.empty(None).copy(name = Option(name)))
        val a = AV(userSelect, "none")(env).map(_.key).sorted
        assertEq(a, exp.toVector.sorted)
      }
      "chrome"  - test("Chrome") ("user-select")
      "firefox" - test("Firefox")("user-select")
      "opera"   - test("Opera"  )("user-select")
      "ie"      - test("IE")     ("-ms-user-select")
      "safari"  - test("Safari") ("-webkit-user-select", "user-select")
      "all"     - test(null)     ("-ms-user-select", "-webkit-user-select", "user-select")
    }

    "gridTemplateAreas" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp.trim)
      test(gridTemplateAreas.none, "none")
      test(gridTemplateAreas("a b"), """ "a b" """)
      test(gridTemplateAreas("a b", "c d"), """ "a b" "c d" """)
    }

    "rowGap" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp.trim)
      test(rowGap.normal, "normal")
      test(rowGap(Literal.Typed.normal), "normal")
      test(rowGap.`0`, "0")
      test(rowGap(pct), "5%")
      test(rowGap(length), "3px")
    }

    "gap" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp.trim)
      test(gap.normal, "normal")
      test(gap(Literal.Typed.normal, Literal.Typed.`0`), "normal 0")
      test(gap(pct, length), "5% 3px")
    }

    "pointerEvents" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp.trim)
      test(pointerEvents.auto,    "auto")
      test(pointerEvents.none,    "none")
      test(pointerEvents.inherit, "inherit")
      test(pointerEvents.initial, "initial")
      test(pointerEvents.unset,   "unset")
    }
    "justifyContent" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp.trim)
      test(justifyContent.center       , "center")
      test(justifyContent.start        , "start")
      test(justifyContent.end          , "end")
      test(justifyContent.flexStart    , "flex-start")
      test(justifyContent.flexEnd      , "flex-end")
      test(justifyContent.left         , "left")
      test(justifyContent.right        , "right")
      test(justifyContent.spaceBetween , "space-between")
      test(justifyContent.spaceAround  , "space-around")
      test(justifyContent.spaceEvenly  , "space-evenly")
      test(justifyContent.stretch      , "stretch")
      test(justifyContent.safeCenter   , "safe center")
      test(justifyContent.unsafeCenter , "unsafe center")
      test(justifyContent.inherit      , "inherit")
      test(justifyContent.initial      , "initial")
      test(justifyContent.unset        , "unset")
    }
    "userSelect" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit =
        assertEq(av.value, exp.trim)
      test(userSelect.auto, "auto")
      test(userSelect.text, "text")
      test(userSelect.none, "none")
      test(userSelect.contain, "contain")
      test(userSelect.all, "all")
      test(userSelect.inherit, "inherit")
      test(userSelect.initial, "initial")
      test(userSelect.unset, "unset")
    }
    "appearance" - {
      assertEq(appearance.auto.value,           "auto")
      assertEq(appearance.base.value,           "base")
      assertEq(appearance.baseSelect.value,     "base-select")
      assertEq(appearance.button.value,         "button")
      assertEq(appearance.checkbox.value,       "checkbox")
      assertEq(appearance.listbox.value,        "listbox")
      assertEq(appearance.menulist.value,       "menulist")
      assertEq(appearance.menulistButton.value, "menulist-button")
      assertEq(appearance.meter.value,          "meter")
      assertEq(appearance.none.value,           "none")
      assertEq(appearance.progressBar.value,    "progress-bar")
      assertEq(appearance.radio.value,          "radio")
      assertEq(appearance.searchfield.value,    "searchfield")
      assertEq(appearance.textarea.value,       "textarea")
      assertEq(appearance.textfield.value,      "textfield")
      val bbEnv     = Env(Env.Platform.empty(None).copy(name = Some("BlackBerry")))
      val chromeEnv = Env(Env.Platform.empty(None).copy(name = Some("Chrome")))
      assertEq(appearance.none(Env.empty).map(_.key), Vector("-webkit-appearance", "appearance"))
      assertEq(appearance.none(bbEnv).map(_.key),     Vector("-webkit-appearance"))
      assertEq(appearance.none(chromeEnv).map(_.key), Vector("appearance"))
    }
    "printColorAdjust" - {
      assertEq(printColorAdjust.economy.value, "economy")
      assertEq(printColorAdjust.exact.value,   "exact")
      val chromeEnv  = Env(Env.Platform.empty(None).copy(name = Some("Chrome")))
      val firefoxEnv = Env(Env.Platform.empty(None).copy(name = Some("Firefox")))
      assertEq(printColorAdjust.exact(Env.empty).map(_.key),  Vector("-o-print-color-adjust", "-webkit-print-color-adjust", "print-color-adjust"))
      assertEq(printColorAdjust.exact(chromeEnv).map(_.key),  Vector("-webkit-print-color-adjust"))
      assertEq(printColorAdjust.exact(firefoxEnv).map(_.key), Vector("print-color-adjust"))
    }
    "wiredAttrs" - {
      assertEq(position.sticky(Env.empty),                     Vector(CssKV("position", "sticky")))
      assertEq(AV(backgroundBlendMode, "multiply")(Env.empty), Vector(CssKV("background-blend-mode", "multiply")))
      assertEq(backgroundRepeat.round(Env.empty),              Vector(CssKV("background-repeat", "round")))
      assertEq(justifyContent.spaceEvenly(Env.empty),
        Vector(
          CssKV("-ms-justify-content", "space-evenly"),
          CssKV("-webkit-justify-content", "space-evenly"),
          CssKV("justify-content", "space-evenly")))
      assertEq(overflow.hidden(Env.empty),                     Vector(CssKV("overflow", "hidden")))
      assertEq(AV(fontVariantNumeric, "tabular-nums")(Env.empty), Vector(CssKV("font-variant-numeric", "tabular-nums")))
      assertEq(AV(imageOrientation, "from-image")(Env.empty),  Vector(CssKV("image-orientation", "from-image")))
    }
    "inset" - {
      assertEq(insetBlock(length).value,       "3px")
      assertEq(insetBlock(length, pct).value,  "3px 5%")
      assertEq(insetBlock.auto.value,          "auto")
      assertEq(insetBlock.`0`.value,           "0")
      assertEq(insetInline(length).value,      "3px")
      assertEq(insetInline(length, pct).value, "3px 5%")
      assertEq(insetInline.auto.value,         "auto")
      assertEq(insetInline.`0`.value,          "0")
      assertEq(insetBlockStart(length).value,  "3px")
      assertEq(insetBlockEnd(pct).value,       "5%")
      assertEq(insetInlineStart.auto.value,    "auto")
      assertEq(insetInlineEnd.`0`.value,       "0")
    }
    "animation" - {
      import Dsl._
      "animationDirection" - {
        assertEq(animationDirection.normal.av.value,                   "normal")
        assertEq(animationDirection.normal.reverse.alternate.av.value, "normal,reverse,alternate")
        assertEq(animationDirection.alternateReverse.important.value,  "alternate-reverse !important")
      }
      "animationFillMode" - {
        assertEq(animationFillMode.none.av.value,                    "none")
        assertEq(animationFillMode.forwards.backwards.both.av.value, "forwards,backwards,both")
        assertEq(animationFillMode.forwards.important.value,         "forwards !important")
      }
      "animationPlayState" - {
        assertEq(animationPlayState.running.av.value,               "running")
        assertEq(animationPlayState.running.paused.av.value,        "running,paused")
        assertEq(animationPlayState.running.paused.important.value, "running,paused !important")
      }
      "animationName" - {
        val kf1 = Keyframes(ClassName("kf1"), Nil)
        val kf2 = Keyframes(ClassName("kf2"), Nil)
        val kf3 = Keyframes(ClassName("kf3"), Nil)
        assertEq(animationName.none.value,           "none")
        assertEq(animationName(kf1).value,           "kf1")
        assertEq(animationName(kf1, kf2, kf3).value, "kf1,kf2,kf3")
      }
      "animationIterationCount" - {
        assertEq(animationIterationCount.infinite.value,   "infinite")
        assertEq(animationIterationCount.count(3).value,   "3")
        assertEq(animationIterationCount.count(2.5).value, "2.5")
        assertEq(animationIterationCount(1, 2.5, count(3), Literal.Typed.infinite).value, "1,2.5,3,infinite")
      }
      "animationTimingFunction" - {
        assertEq(animationTimingFunction.ease.value,                     "ease")
        assertEq(animationTimingFunction(easeIn, easeOut, linear).value, "ease-in,ease-out,linear")
        assertEq(animationTimingFunction(cubicBezier(0.1, 0.2, 0.3, 0.4), steps(4, start)).value,
          "cubic-bezier(0.1, 0.2, 0.3, 0.4),steps(4, start)")
      }
      "transitionTimingFunction" - {
        assertEq(transitionTimingFunction.easeInOut.value,      "ease-in-out")
        assertEq(transitionTimingFunction(ease, stepEnd).value, "ease,step-end")
      }
    }
    "revert" - {
      import Dsl.revert
      assertEq(margin.revert.value,     "revert")
      assertEq(margin(revert).value,    "revert")
      assertEq(color.revert.value,      "revert")
      assertEq(width.revert.value,      "revert")
      assertEq(cursor.revert.value,     "revert")
      assertEq(userSelect.revert.value, "revert")
    }
    "overflow" - {
      import Dsl.{auto, hidden}
      assertEq(overflow.auto.value,                        "auto")
      assertEq(overflow.clip.value,                        "clip")
      assertEq(overflow.hidden.value,                      "hidden")
      assertEq(overflow.scroll.value,                      "scroll")
      assertEq(overflow.visible.value,                     "visible")
      assertEq(overflow(auto).value,                       "auto")
      assertEq(overflow(auto, hidden).value,               "auto hidden")
      assertEq(overflow(Literal.Typed.clip, Literal.Typed.scroll).value, "clip scroll")
      assertEq(overflowX.clip.value,                       "clip")
      assertEq(overflowY.clip.value,                       "clip")
      assertEq(overflowX(auto).value,                      "auto")
    }
    "columnCount" - {
      assertEq(columnCount(3).value, "3")
      assertEq(columnCount.auto.value, "auto")
      assertEq(columnCount.inherit.value, "inherit")
      compileError("columnCount(2.5)")
      compileError("columnCount(1.0)")
    }
    "outlineStyle" - {
      assertEq(outlineStyle.auto.value,    "auto")
      assertEq(outlineStyle.none.value,    "none")
      assertEq(outlineStyle.dotted.value,  "dotted")
      assertEq(outlineStyle.dashed.value,  "dashed")
      assertEq(outlineStyle.solid.value,   "solid")
      assertEq(outlineStyle.double.value,  "double")
      assertEq(outlineStyle.groove.value,  "groove")
      assertEq(outlineStyle.ridge.value,   "ridge")
      assertEq(outlineStyle.inset.value,   "inset")
      assertEq(outlineStyle.outset.value,  "outset")
    }
    "textAlign" - {
      assertEq(textAlign.justifyAll.value, "justify-all")
    }
    "placeSelf" - {
      def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
      test(placeSelf.auto,                    "auto")
      test(placeSelf.normal,                  "normal")
      test(placeSelf.stretch,                 "stretch")
      test(placeSelf.center,                  "center")
      test(placeSelf.start,                   "start")
      test(placeSelf.end,                     "end")
      test(placeSelf.flexStart,               "flex-start")
      test(placeSelf.flexEnd,                 "flex-end")
      test(placeSelf.selfStart,               "self-start")
      test(placeSelf.selfEnd,                 "self-end")
      test(placeSelf.baseline,                "baseline")
      test(placeSelf.firstBaseline,           "first baseline")
      test(placeSelf.lastBaseline,            "last baseline")
      test(placeSelf.safeCenter,              "safe center")
      test(placeSelf.unsafeCenter,            "unsafe center")
      test(placeSelf.center.important,        "center !important")
      test(placeSelf.center.start,            "center start")
      test(placeSelf.auto.left,               "auto left")
      test(placeSelf.stretch.right.important, "stretch right !important")
      test(placeSelf.firstBaseline.flexEnd,   "first baseline flex-end")
      test(placeSelf.autoCenter,              "auto center")
      compileError("placeSelf.left")
      compileError("placeSelf.right")
    }
    "typedAttrs" - {
      import Dsl._
      "flexBasis" - {
        assertEq(flexBasis.auto.value,       "auto")
        assertEq(flexBasis.`0`.value,        "0")
        assertEq(flexBasis.content.value,    "content")
        assertEq(flexBasis.fitContent.value, "fit-content")
        assertEq(flexBasis.maxContent.value, "max-content")
        assertEq(flexBasis.minContent.value, "min-content")
        assertEq(flexBasis(length).value,    "3px")
        assertEq(flexBasis(pct).value,       "5%")
        assertEq(flexBasis.inherit.value,    "inherit")
      }
      "svgFill" - {
        assertEq(svgFill.none.value,         "none")
        assertEq(svgFill.red.value,          "red")
        assertEq(svgFill(c"#123").value,     "#123")
        assertEq(svgFill.currentColor.value, "currentColor")
      }
      "svgFloodColor" - {
        assertEq(svgFloodColor.red.value,      "red")
        assertEq(svgFloodColor(c"#123").value, "#123")
      }
      "textStrokeColor" - {
        assertEq(textStrokeColor.red.value,      "red")
        assertEq(textStrokeColor(c"#123").value, "#123")
      }
      "textStrokeWidth" - {
        assertEq(textStrokeWidth.thin.value,     "thin")
        assertEq(textStrokeWidth.medium.value,   "medium")
        assertEq(textStrokeWidth.thick.value,    "thick")
        assertEq(textStrokeWidth.`0`.value,      "0")
        assertEq(textStrokeWidth(length).value,  "3px")
      }
    }
    "fontFamily" - {
      val ff = FontFace[FontFamily](FontFamily("My Font"), NonEmptyVector("url(font.woff)"))
      assertEq(fontFamily.serif.value,       "serif")
      assertEq(fontFamily.sansSerif.value,   "sans-serif")
      assertEq(fontFamily.monospace.value,   "monospace")
      assertEq(fontFamily.cursive.value,     "cursive")
      assertEq(fontFamily.fantasy.value,     "fantasy")
      assertEq(fontFamily.systemUI.value,    "system-ui")
      assertEq(fontFamily.uiSerif.value,     "ui-serif")
      assertEq(fontFamily.uiSansSerif.value, "ui-sans-serif")
      assertEq(fontFamily.uiMonospace.value, "ui-monospace")
      assertEq(fontFamily.uiRounded.value,   "ui-rounded")
      assertEq(fontFamily(ff).value,                                    "'My Font'")
      assertEq(fontFamily("Helvetica Neue", "Arial", "sans-serif").value, "'Helvetica Neue', Arial, sans-serif")
      assertEq(fontFamily(ff, "Helvetica Neue", "sans-serif").value,      "'My Font', 'Helvetica Neue', sans-serif")
    }
    "modernLiteralsAndAttrs" - {
      import Dsl._
      "revertLayer" - {
        assertEq(margin.revertLayer.value,  "revert-layer")
        assertEq(margin(revertLayer).value, "revert-layer")
        assertEq(color.revertLayer.value,   "revert-layer")
      }
      "stepJumpTerms" - {
        assertEq(animationTimingFunction.steps(4, jumpStart).value, "steps(4, jump-start)")
        assertEq(animationTimingFunction.steps(4, jumpEnd).value,   "steps(4, jump-end)")
        assertEq(animationTimingFunction.steps(4, jumpNone).value,  "steps(4, jump-none)")
        assertEq(animationTimingFunction.steps(4, jumpBoth).value,  "steps(4, jump-both)")
        assertEq(transitionTimingFunction(steps(2, jumpNone)).value, "steps(2, jump-none)")
      }
      "whiteSpaceBreakSpaces" - {
        assertEq(whiteSpace.breakSpaces.value, "break-spaces")
      }
      "accentColor" - {
        assertEq(accentColor.auto.value,       "auto")
        assertEq(accentColor.red.value,        "red")
        assertEq(accentColor(c"#09f").value,   "#09f")
      }
      "aspectRatio" - {
        assertEq(aspectRatio.auto.value,               "auto")
        assertEq(aspectRatio(16 :/: 9).value,          "16/9")
        assertEq(aspectRatio(1.5).value,               "1.5")
        assertEq(aspectRatio(16, 9).value,             "16/9")
        assertEq(aspectRatio(auto, 16 :/: 9).value,    "auto 16/9")
        assertEq(aspectRatio(16 :/: 9, auto).value,    "16/9 auto")
      }
      "backdropFilter" - {
        assertEq((backdropFilter := "blur(10px)").value, "blur(10px)")
      }
      "colorScheme" - {
        assertEq(colorScheme.normal.value,    "normal")
        assertEq(colorScheme.light.value,     "light")
        assertEq(colorScheme.dark.value,      "dark")
        assertEq(colorScheme.lightDark.value, "light dark")
        assertEq(colorScheme.darkLight.value, "dark light")
        assertEq(colorScheme.onlyLight.value, "only light")
        assertEq(colorScheme.onlyDark.value,  "only dark")
      }
      "containAndContainer" - {
        assertEq(contain.none.value,                  "none")
        assertEq(contain.strict.value,                "strict")
        assertEq(contain.content.value,               "content")
        assertEq(contain.size.value,                  "size")
        assertEq(contain.inlineSize.value,            "inline-size")
        assertEq(contain.layout.value,                "layout")
        assertEq(contain.style.value,                 "style")
        assertEq(contain.paint.value,                 "paint")
        assertEq(overscrollBehavior(contain, none).value, "contain none")
        assertEq(containerName.none.value,            "none")
        assertEq(containerName("sidebar").value,      "sidebar")
        assertEq(containerName("a", "b").value,       "a b")
        assertEq(containerType.normal.value,          "normal")
        assertEq(containerType.size.value,            "size")
        assertEq(containerType.inlineSize.value,      "inline-size")
        assertEq((container := "sidebar / inline-size").value, "sidebar / inline-size")
      }
      "placeContentAndItems" - {
        def test(av: AV, exp: String)(implicit q: Line): Unit = assertEq(av.value, exp)
        test(placeContent.center,                   "center")
        test(placeContent.spaceBetween.center,      "space-between center")
        test(placeContent.start.right.important,    "start right !important")
        compileError("placeContent.left")
        test(placeItems.center,                     "center")
        test(placeItems.stretch.start,              "stretch start")
        test(placeItems.center.auto,                "center auto")
        test(placeItems.baseline.left.important,    "baseline left !important")
        compileError("placeItems.left")
      }
      "individualTransforms" - {
        assertEq(translate.none.value,                "none")
        assertEq(translate.`0`.value,                 "0")
        assertEq(translate(10.px).value,              "10px")
        assertEq(translate(10.px, 50.%%).value,       "10px 50%")
        assertEq(translate(10.px, 20.px, 30.px).value, "10px 20px 30px")
        assertEq(rotate.none.value,                   "none")
        assertEq(rotate(45.deg).value,                "45deg")
        assertEq(rotate.x(90.deg).value,              "x 90deg")
        assertEq(rotate.y(0.5.turn).value,            "y 0.5turn")
        assertEq(rotate.z(1.rad).value,               "z 1rad")
        assertEq(rotate(1, 1, 0, 45.deg).value,       "1 1 0 45deg")
        assertEq(scale.none.value,                    "none")
        assertEq(scale(2).value,                      "2")
        assertEq(scale(1.5, 50.%%).value,             "1.5 50%")
        assertEq(scale(1, 2, 0.5).value,              "1 2 0.5")
      }
      "textWrap" - {
        assertEq(textWrap.wrap.value,    "wrap")
        assertEq(textWrap.nowrap.value,  "nowrap")
        assertEq(textWrap.balance.value, "balance")
        assertEq(textWrap.pretty.value,  "pretty")
        assertEq(textWrap.stable.value,  "stable")
      }
      "mask" - {
        assertEq(maskClip.noClip.value,            "no-clip")
        assertEq(maskClip.borderBox.value,         "border-box")
        assertEq(maskComposite.exclude.value,      "exclude")
        assertEq((maskImage := "url(m.svg)").value, "url(m.svg)")
        assertEq(maskMode.luminance.value,         "luminance")
        assertEq(maskMode.matchSource.value,       "match-source")
        assertEq(maskOrigin.fillBox.value,         "fill-box")
        assertEq((maskPosition := "center").value, "center")
        assertEq(maskRepeat.noRepeat.value,        "no-repeat")
        assertEq(maskRepeat(repeat, space).value,  "repeat space")
        assertEq(maskSize.cover.value,             "cover")
        assertEq(maskSize.contain.value,           "contain")
        assertEq(maskSize(50.%%, auto).value,      "50% auto")
      }
      "logicalShorthands" - {
        assertEq(inset.`0`.value,                    "0")
        assertEq(inset.auto.value,                   "auto")
        assertEq(inset(10.px).value,                 "10px")
        assertEq(inset(10.px, 20.px).value,          "10px 20px")
        assertEq(inset(1.px, 2.px, 3.px, 4.px).value, "1px 2px 3px 4px")
        assertEq(border(1.px, inset, black).value,   "1px inset black")
        assertEq(marginBlock.auto.value,             "auto")
        assertEq(marginBlock.`0`.value,              "0")
        assertEq(marginBlock(10.px).value,           "10px")
        assertEq(marginBlock(10.px, 20.px).value,    "10px 20px")
        assertEq(marginInline(auto).value,           "auto")
        assertEq(marginInline(10.px, 5.%%).value,    "10px 5%")
        assertEq(paddingBlock.`0`.value,             "0")
        assertEq(paddingBlock(10.px, 20.px).value,   "10px 20px")
        assertEq(paddingInline(10.px).value,         "10px")
        assertEq(paddingInline(10.px, 5.%%).value,   "10px 5%")
      }
    }
  }
}
