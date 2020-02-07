#! /bin/sh

echo "Test decompilation pour cobertura en cours (pas de vérification ici)"

for test_case in src/test/deca/syntax/valid/homemade/*.deca
do
  var=$(decac -p "$test_case" 2>&1 >/dev/null)
done

for test_case in src/test/deca/context/valid/homemade/*.deca
do
  var=$(decac -p "$test_case" 2>&1 >/dev/null)
done

for test_case in src/test/deca/context/invalid/homemade/*.deca
do
  var=$(decac -p "$test_case" 2>&1 >/dev/null)
done
