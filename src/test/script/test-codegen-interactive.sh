#! /bin/sh

# Auteur : gl01
# Version initiale : 17/01/2020

# Script testing different inputs on interactive tests.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

#function to compare the result of test_synt with the expected result :
compare_with_ref(){

    if [ -f "${1%.deca}$2".ref ]; then
        DIFF=$(diff "${1%.deca}$2".res "${1%.deca}$2".ref)
        if [ -n "$DIFF" ]
        then
            echo "Result different than expected for file $1. with imput $2. Here are the diffs with the expected.assult :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "no reference for this test yet : $1 with input $2. Here is the output of the test : "
        cat "${1%.deca}$2".res
        echo ""
        echo "Do you want to add this as a reference ? y : add, anything else : ignore"
        read ans
        if [ "$ans" == "y" ]; then
            cp "${1%.deca}$2".res "${1%.deca}$2".ref
            echo "added"
        fi

    fi
}


test_script_with_input(){
  (echo $2 | ima "${1%.deca}".ass) > "${1%.deca}$2".res
  compare_with_ref "$1" "$2"
  echo "Test de $1 OK pour l'entrée $2"
  }


compile() {
  decac "$1"
  if decac "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 OK"
  fi
}

#Due to the need of a set of input, we need to test the files one by one

compile src/test/deca/codegen/interactive/homemade/read_float.deca
test_script_with_input src/test/deca/codegen/interactive/homemade/read_float.deca 0.0
test_script_with_input src/test/deca/codegen/interactive/homemade/read_float.deca 4
test_script_with_input src/test/deca/codegen/interactive/homemade/read_float.deca "\"UwU"
test_script_with_input src/test/deca/codegen/interactive/homemade/read_float.deca 1.0e-200
test_script_with_input src/test/deca/codegen/interactive/homemade/read_float.deca 1.0e200

compile src/test/deca/codegen/interactive/homemade/read_int.deca
test_script_with_input src/test/deca/codegen/interactive/homemade/read_int.deca 4
test_script_with_input src/test/deca/codegen/interactive/homemade/read_int.deca 0.0
test_script_with_input src/test/deca/codegen/interactive/homemade/read_int.deca "\"UwU"
test_script_with_input src/test/deca/codegen/interactive/homemade/read_int.deca 999999999999


