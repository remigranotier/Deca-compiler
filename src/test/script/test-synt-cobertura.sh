#! /bin/sh

echo "Test syntaxe pour cobertura en cours (pas de vérification ici)"

for test_case in src/test/deca/syntax/valid/homemade/*.deca
do
  var=$(test_synt "$test_case" 2<&1 >/dev/null)
done

for test_case in src/test/deca/syntax/invalid/homemade/*.deca
do
  var=$(test_synt "$test_case" 2>&1 >/dev/null)
done