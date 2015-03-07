* Styles are vals/traits/classes/object/defs.
* Style is applied by reference at tag.
* Style can be a function with tag as arg.
* Style can be a value like `<.div(myStyle, “Cool”)`
* scalajs-react can be modified to pass the element to the style. (ie. applied like a value but is a fn that take the element)
* scalajs-react can be modified to allow traversal of the element's children so styles can apply sub-styles (at runtime).
* Styles can apply a classname directly to an element, and output traditional static CSS with the same classname. (i.e fake inline)
* Styles can be made to be traits, and all merge into one object that reflects exactly how overrides/cascades will happen in the browser.
* We could just write a LESS or SASS in Scala.
* If a style expected three children it could be written as a (Parent, C₁, C₂, C₃ → Parent)
* User definable attribute override policy?  Maybe using implicits. CanOverride[Margin] allows MarginLeft Can also provide custom merge strategies too.
* Use Shapeless keyed records, use implicits for merging.
