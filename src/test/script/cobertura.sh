#! /bin/sh


#Script to launch to test cobertura coverage : we uses this because using with mvn causes issues.
#Indeed, cobertura alters what our test script writes, and as our script compares its output with
#references files, this causes tests to fail where they should not.

cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script:"$PATH"

mvn clean
mvn test-compile
mvn cobertura:instrument

src/test/script/basic-context.sh
src/test/script/basic-gencode.sh
src/test/script/basic-lex.sh
src/test/script/basic-synt.sh
src/test/script/test-codegen.sh
src/test/script/test-codegen-nocheck.sh
src/test/script/test-codegen-interactive.sh
src/test/script/test-decompil-cobertura.sh
src/test/script/test-parallel.sh
src/test/script/test-synt-cobertura.sh
src/test/script/test-context-only.sh
src/test/script/test_decac_options.sh

src/test/script/cobertura-report.sh