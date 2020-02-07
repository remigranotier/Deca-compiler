#! /bin/sh

# Auteur : gl01
# Version initiale : 13/01/2020

# Script testing the output of decac.
# No longer used : we instead compare the output at the execution of the compiled file.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"


#function to compare the result of decac with the expected result :
compare_with_ref(){

    if [ -f "${1%.deca}".ref ]; then
        DIFF=$(diff "${1%.deca}".ass "${1%.deca}".ref)
        if [ -n "$DIFF" ]
        then
            echo "Result different than expected for file $1. Here are the diffs with the expected.assult :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "no reference for this test yet : $1. Here is the output of the test : "
        cat "${1%.deca}".ass
        echo ""
        echo "Do you want to add this as a reference ? y : add, anything else : ignore"
        read ans
        if [ $ans == "y" ]; then
            cp "${1%.deca}".ass "${1%.deca}".ref
            echo "added"
        fi

    fi
}


# TESTS VALIDES
test_script_valide(){
  decac "$1"
  if decac "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Echec inattendu de decac sur $1"
      exit 1
  else
      compare_with_ref "$1"
      echo "OK"
  fi
}

for test_case in src/test/deca/codegen/valid/homemade/*.deca
do
  test_script_valide "$test_case"
done

# TESTS INVALIDES
test_script_invalide(){
  decac "$1"
  if decac "$1" \
      | grep -q "src/test/deca/codegen/invalid/homemade/"
  then
      echo "OK"
      compare_with_ref "$1"
  else
      echo "Reussite innatendue de decac sur $1"
      exit 1
  fi
}

for test_case in src/test/deca/codegen/invalid/homemade/*.deca
do
  test_script_invalide "$test_case"
done