# Features


cond
Conditional CSS
  * Pseudo selectors.
  * Media query AST.

nesting
Nested styles
* Unsafe children
* StyleC

stroingngly-typed attributes

prefixes
Browser prefixes
* Automatic browser prefixes for properties (using caniuse.com data).
  * Ability to dynamically tailor CSS to environment. (Able but as yet, unused.)
  * Automatic browser prefixes for _values_ (using caniuse.com data).
  * JsEnv

conflict
Conflict detection
* Attribute conflict detection. (Transitive using reachability of underlying properties. Eg. `grid-area` affects `grid-row` which affects `grid-row-start`.)

reuse
* Reusable styles (modules).

classnames
Taming Bootstrap and other CSS libraries
* Styles can add class names, for example to apply Bootstrap classes like `btn btn-default`.

reset
* CSS Reset.
