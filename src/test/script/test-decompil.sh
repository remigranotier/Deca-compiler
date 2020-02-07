#! /bin/sh

# Auteur : gl01
# Version initiale : 12/01/2020

# script testing decac -p
# It compares its output with a reference file, and then uses decac -p again to check that the output remains the same.

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

#function to compare the result of decac -p with the expected result :
compare_with_ref(){

    if [ -f "${1%.deca}"_decompile.ref ]; then
        DIFF=$(diff "${1%.deca}"_decompile.res "${1%.deca}"_decompile.ref)
        if [ -n "$DIFF" ]; then
            echo "Result different than expected for file $1. Here are the diffs with the expected result :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "current test ;"
        echo ""
        cat "$1"
        echo "no reference for this test yet : $1. Here is the output of the test : "
        cat "${1%.deca}"_decompile.res
        echo ""
        echo "Do you want to add this as a reference ? y : add, anything else : ignore"
        read ans
        if [ $ans == "y" ]; then
            cp "${1%.deca}"_decompile.res "${1%.deca}"_decompile.ref
            echo "added"
        fi

    fi
}


check_idempotency(){

    if [ -f "${1%.deca}"_decompile.ref ]; then
        #copying the file as a .deca file so that decac -p accept it. It will be removed later.
        cp "${1%.deca}"_decompile.ref tmp.deca

        (decac -p tmp.deca) > "${1%.deca}"_decompile.res

        #We no longer need tmp.deca : deleting it
        rm tmp.deca

        DIFF=$(diff "${1%.deca}"_decompile.res "${1%.deca}"_decompile.ref)
        if [ -n "$DIFF" ]; then
            echo "File $1 decompilation does not respect idempotence. Here are the diffs with the expected result :"
            echo "$DIFF"
            exit 1
        fi
    else
        echo "ERROR ON $1 : no ref file for testing idempotency "
        exit 1

    fi
}

#Only testing on valid directory : we can't decompile something we can't even compile.

test_script_valide(){
  (decac -p "$1" 2>&1) > "${1%.deca}"_decompile.res
  if decac -p "$1" 2>&1 \
      | grep -q "$1:[0-9]"
  then
      echo "Echec inattendu de decac -p sur $1"
      exit 1
  else
      compare_with_ref "$1"
      check_idempotency "$1"
      echo "Décompilation $1 et idempotence : OK"
  fi
}

for test_case in src/test/deca/syntax/valid/homemade/*.deca
do
  test_script_valide "$test_case"
done

for test_case in src/test/deca/context/valid/homemade/*.deca
do
  test_script_valide "$test_case"
done

for test_case in src/test/deca/context/invalid/homemade/*.deca
do
  test_script_valide "$test_case"
done

