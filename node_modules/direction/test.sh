#!/bin/sh
#!/bin/bash

typeset -i tests=0

function it {
    let tests+=1;
    description="$1";
}

function assert {
    if [[ "$1" == "$2" ]]; then
        printf "\033[32m.\033[0m";
    else
        printf "\033[31m\nFAIL: $description\033[0m: '$1' != '$2'\n";
        exit 1
    fi
}

it "Should accept a value"
    result=`./cli.js "direction"` 2> /dev/null
    assert $result "ltr"

it "Should accept a right-to-left value"
    result=`./cli.js "لانجليزية"` 2> /dev/null
    assert $result "rtl"

it "Should accept a neutral value"
    result=`./cli.js "@!"` 2> /dev/null
    assert $result "neutral"

it "Should accept multiple values"
    result=`./cli.js "direction another"` 2> /dev/null
    assert $result "ltr"

it "Should accept multiple arguments"
    result=`./cli.js "direction" "another"` 2> /dev/null
    assert $result "ltr"

it "Should accept a value over stdin"
    result=`echo "direction" | ./cli.js` 2> /dev/null
    assert $result "ltr"

it "Should accept multiple values over stdin"
    result=`echo "direction another" | ./cli.js` 2> /dev/null
    assert $result "ltr"

it "Should accept \`--help\`"
    code=0
    ./cli.js --help > /dev/null 2>&1 || code=$?
    assert $code 0

it "Should accept \`-h\`"
    code=0
    ./cli.js -h > /dev/null 2>&1 || code=$?
    assert $code 0

it "Should accept \`--version\`"
    code=0
    ./cli.js --version > /dev/null 2>&1 || code=$?
    assert $code 0

it "Should accept \`-v\`"
    code=0
    ./cli.js -v > /dev/null 2>&1 || code=$?
    assert $code 0

printf "\033[32m\n(✓) Passed $tests assertions without errors\033[0m\n";

exit 0
