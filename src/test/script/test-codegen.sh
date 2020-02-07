#! /bin/sh

# Auteur : gl01
# Version initiale : 14/01/2020

#script testing code generation.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

#function to compare the result of decac with the expected result :
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


# TESTS VALIDES
test_script_valide(){
  decac "$1"
  if decac "$1"\
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

# test if the output is still the same if we use the option -r 4 : it should always be the case.
# It also checks that we never use R4 with -r 4 activated.
test_script_valide_4_registers(){
  decac -r 4 "$1"
  if decac -r 4 "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 avec -r 4 : OK"
      if (cat "${1%.deca}".ass) | grep -q "R4"; then
          echo "Erreur : R4 utilisé sur $1 alors qu'il a été compilé avec l'option -r 4"
          exit 1
      fi

      (ima "${1%.deca}".ass) > "${1%.deca}".res
      if (cat "${1%.deca}".res) | grep -q "ERROR"
      then
        echo "Erreur : $1 a une erreur a l'exécution"
      else
        compare_with_ref "$1"
      fi
  fi
}

#Test if the file throws an overflow error. executed with ima -p 001 so it is easy to overflow.
test_overflow(){
  decac -r 4 "$1"
  if decac -r 4 "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 : OK"
      (ima -p 001 "${1%.deca}".ass) > "${1%.deca}".res
      if (cat "${1%.deca}".res) | grep -q "ERROR : stack overflow"
      then
        echo "Stack overflow bien levée pour $1 : OK"
      else
        echo "Erreur : $1 n'a pas de stack overflow a l'exécution"
        exit 1
      fi
  fi
}

for test_case in src/test/deca/codegen/valid/homemade/*.deca
do
  test_script_valide "$test_case"
  test_script_valide_4_registers "$test_case"
done

for test_case in src/test/deca/codegen/valid/homemade/object/*.deca
do
  test_script_valide "$test_case"
  test_script_valide_4_registers "$test_case"
done

test_overflow src/test/deca/codegen/valid/homemade/variables_simples.deca






# TESTS INVALIDES
test_script_invalide(){
  decac "$1"
  if decac "$1"\
      | grep -q "$1:[0-9]"
  then
      echo "Erreur de compilation pour $1"
      exit 1
  else
      echo "Compilation de $1 : OK"
      (ima "${1%.deca}".ass) > "${1%.deca}".res
      if (cat "${1%.deca}".res) | grep -q "ERROR"
      then
        compare_with_ref "$1"
      else
        echo "Erreur : $1 s'exécute sans erreur."
        exit 1
      fi
  fi
}

for test_case in src/test/deca/codegen/invalid/homemade/*.deca
do
  test_script_invalide "$test_case"
done