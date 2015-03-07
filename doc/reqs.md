#### Constraints
* CO-01: `:visited` can *only* be applied via CSS selectors, not inline.
* CO-02: Composition errors based on attribute *values* aren't checkable at compile time.
* CO-03: The type-system of Scala compiler must be leveraged as much as possible.
* CO-04: Where a style is referenced or applied, the dev shall be able to navigate to its definition in a single action in IntelliJ/Eclipse.
* CO-05: Solution shall not impose compliance to the CSS spec. (ie. it shall not prevent CSS hacks.)
* CO-06: Browsers cascade styles (even inline styles) to children and there is nothing we can do about it.

#### Functional Requirements
* FR-01: Dev shall be able to define a style that requires a specific configuration of children such that the compiler will enforce that the children are styled.
* FR-02: Dev shall be able to define a style that depends on required input. (ie. a function)
* FR-03: Dev shall be able to define a style that depends on browser/page state (eg. `:hover`, `:visited`)
* FR-04: Dev shall be able to define a style that depends on the current plaform (eg. IE 8, mobile)
* FR-05: Dev shall be able to compose styles to form a new style.
* FR-06: Dev shall be able to define a style that extends an existing style.
* FR-07: Dev shall be able to define custom CSS attributes.
* FR-08: Scalac shall prevent compilation without proof that a referenced style exists.
* FR-09: Scalac shall prevent compilation without proof that attributes used in a style are either part of the CSS spec or custom as per FR-7.
* FR-10: Dev shall be able to specify a composition strategy when merging styles.
* FR-11: Solution shall provide a composition stategy that prevents composition.
* FR-12: Solution shall provide a composition stategy that overrides (i.e. discards one side and chooses the other).
* FR-13: Dev shall be able to create a composition stategy that merges attribute values.
* FR-14: Dev shall be able to specify different composition strategies per attribute type. (i.e. `border-top`, the `margin` family)
* FR-15: Dev shall be able to apply a baseline style (a “css reset”) to a whole page.
* FR-16: Solution shall provide one or more “css resets” that devs can override to customise, then apply to their whole page. **[pri=low]**
* FR-17: Dev shall be able to define a style that affects unspecified, optionally existant children. (Must like & in LESS. Required for FR-15.)
* FR-18: When looking at a style definition, Dev shall be able to passively understand if any attributes are being overridden.
* FR-19: When looking at a style definition, Dev shall be able to passively understand which attributes are being overridden. **[pri=low]**

#### Preferences
* PR-01: It would be better not to push psuedo-selector logic upon devs and have them write code for what the browser already does.
* PR-02: It would be nice to edit a logical style in the browser directly. If styles are inline then one can only modify a single tag's style at a time.
* PR-03: Solution should not require scalajs-react. scalajs-react should depend on Solution with provide a small bridge.

#### Quality Requirements
* QR-01: A page of 1000 DOM elements with 400 different styles, 20% of elements having 3 styles, 10% having 5, should render in ≤ 2 sec.
* QR-02: The time between changing the styles in Scala, and seeing the result in the browser should be ideally ≤ 3 sec.

