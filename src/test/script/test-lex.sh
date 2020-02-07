#! /bin/sh

# Auteur : gl01
# Version initiale : 09/01/2020

# Base pour un script de test de la lexicographie.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

#function to compare the result of test_lex with the expected result :
#It allows us to detect if the lexer returned the correct tokens.
compare_with_ref(){

    if [ -f "${1%.deca}".ref ]; then
        DIFF=$(diff "${1%.deca}".res "${1%.deca}".ref)
        if [ -n "$DIFF" ]
        then
            echo "Result different than expected for file $1. Here are the diffs with the expected result :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "no reference for this test yet : $1. Here is the output of the test : "
        cat "${1%.deca}".res
        echo ""
        echo "Do you want to add this as a reference ? y : add, anything else : ignore"
        read ans
        if [ $ans == "y" ]; then
            cp "${1%.deca}".res "${1%.deca}".ref
            echo "added"
        fi

    fi
}


# TESTS VALIDES
test_script_valide(){
  (test_lex "$1" 2>&1) > "${1%.deca}".res
  if test_lex "$1" 2>&1 \
      | grep -q "$1:[0-9]"
  then
      echo "Echec inattendu de test_lex sur $1"
      exit 1
  else
      compare_with_ref "$1"
      echo "OK"
  fi
}

for test_case in src/test/deca/syntax/valid/homemade/*.deca
do
  test_script_valide "$test_case"
done

# TESTS INVALIDES
test_script_invalide(){
  (test_lex "$1" 2>&1) > "${1%.deca}".res
  if test_lex "$1" 2>&1 \
      | grep -q "$1:[0-9]"
  then
      echo "OK"
      compare_with_ref "$1"
  else
      if "$1" 2>&1 \
      | grep -q "src/test/deca/syntax/invalid/homemade/early-closed_commentary.deca"
      then
          echo "OK"
          compare_with_ref "$1"
          #this test is valid for the lexer, but invalid for the parser
      else
        echo "Reussite innatendue de test_lex sur $1"
        exit 1
      fi
  fi
}

for test_case in src/test/deca/syntax/invalid/homemade/*.deca
do
  test_script_invalide "$test_case"
done