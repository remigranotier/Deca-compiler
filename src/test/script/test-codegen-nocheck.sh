#! /bin/sh

# Auteur : gl01
# Version initiale : 15/01/2020

# Script testing the execution of files compiled with the option -n

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

#function to compare the result of test_synt with the expected result :
compare_with_ref(){

    if [ -f "${1%.deca}".ref ]; then
        DIFF=$(diff "${1%.deca}".res "${1%.deca}".ref)
        if [ -n "$DIFF" ]
        then
            echo "Result different than expected for file $1. Here are the diffs with the expected.assult :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "no reference for this test yet : $1. Here is the output of the test : "
        cat "${1%.deca}".res
        echo ""
        echo "Do you want to add this as a reference ? y : add, anything else : ignore"
        read ans
        if [ "$ans" == "y" ]; then
            cp "${1%.deca}".res "${1%.deca}".ref
            echo "added"
        fi

    fi
}


#First we check that using nocheck on valid tests does not break anything : as they are valid tests, using -n
#should give us the same output.

# TESTS VALIDES
test_script_valide(){
  decac -n "$1"
  if decac -n "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 : OK"
      (ima "${1%.deca}".ass) > "${1%.deca}".res
      if (cat "${1%.deca}".res) | grep -q "ERROR"
      then
        echo "Erreur : $1 a une erreur a l'exécution"
      else
        compare_with_ref "$1"
      fi
  fi
}



for test_case in src/test/deca/codegen/valid/homemade/*.deca
do
  test_script_valide "$test_case"
done

#Then we check that nocheck has the correct behaviour for invalid tests


# TESTS INVALIDES
test_script_invalide(){
  decac -n "$1"
  if decac -n "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 : OK"
      (ima "${1%.deca}".ass) > "${1%.deca}"_nocheck.res
      compare_with_ref "${1%.deca}"_nocheck.deca
  fi
}

for test_case in src/test/deca/codegen/invalid/homemade/*.deca
do
  test_script_invalide "$test_case"
done


test_overflow(){
  decac -r 4 -n "$1"
  if decac -r 4 -n "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 avec -r 4 OK"
      (ima -p 001 "${1%.deca}".ass) > "${1%.deca}".res
      if (cat "${1%.deca}".res) | grep -q "ERROR : stack overflow"
      then
        echo "Erreur : stack overflow a été détectée malgré l'option -n"
        exit 1
      else
        echo "ok"
      fi
  fi
}

test_overflow src/test/deca/codegen/valid/homemade/variables_simples.deca
