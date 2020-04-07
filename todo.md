* allow users full control over postcss
  * stylelint rule config / off
  * autoprefixer config / off
  * provide two built-in defaults: dev & prod










## stylelint rules

color-no-invalid-hex: Disallow invalid hex colors.
font-family-no-duplicate-names: Disallow duplicate font family names.
function-calc-no-invalid: Disallow an invalid expression within calc functions.
function-linear-gradient-no-nonstandard-direction: Disallow direction values in linear-gradient() calls that are not valid according to the standard syntax.
string-no-newline: Disallow (unescaped) newlines in strings.
unit-no-unknown: Disallow unknown units.
property-no-unknown: Disallow unknown properties.
declaration-block-no-duplicate-properties: Disallow duplicate properties within declaration blocks.
declaration-block-no-shorthand-property-overrides: Disallow shorthand properties that override related longhand properties.
selector-pseudo-class-no-unknown: Disallow unknown pseudo-class selectors.
selector-pseudo-element-no-unknown: Disallow unknown pseudo-element selectors.
selector-type-no-unknown: Disallow unknown type selectors.
media-feature-name-no-unknown: Disallow unknown media feature names.
at-rule-no-unknown: Disallow unknown at-rules.
shorthand-property-no-redundant-values: Disallow redundant values in shorthand properties (Autofixable).
value-no-vendor-prefix: Disallow vendor prefixes for values.
selector-no-vendor-prefix: Disallow vendor prefixes for selectors.
media-feature-name-no-vendor-prefix: Disallow vendor prefixes for media feature names.
no-unknown-animations: Disallow unknown animations.
length-zero-no-unit: Disallow units for zero lengths (Autofixable).


#### CASE

unit-case: Specify lowercase or uppercase for units (Autofixable).
value-keyword-case: Specify lowercase or uppercase for keywords values (Autofixable).
property-case: Specify lowercase or uppercase for properties (Autofixable).
function-name-case: Specify lowercase or uppercase for function names (Autofixable).
color-hex-case: Specify lowercase or uppercase for hex colors (Autofixable).
at-rule-name-case: Specify lowercase or uppercase for at-rules names (Autofixable).
media-feature-name-case: Specify lowercase or uppercase for media feature names (Autofixable).
selector-pseudo-class-case: Specify lowercase or uppercase for pseudo-class selectors (Autofixable).
selector-pseudo-element-case: Specify lowercase or uppercase for pseudo-element selectors (Autofixable).
selector-type-case: Specify lowercase or uppercase for type selectors (Autofixable).
