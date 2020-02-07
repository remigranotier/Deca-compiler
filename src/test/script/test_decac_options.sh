#! /bin/sh

# decac
# decac -b
# decac -p src/test/deca/syntax/valid/vide.deca
# decac -v src/test/deca/syntax/valid/vide.deca
# decac -p -v
# decac -r 3 src/test/deca/syntax/valid/vide.deca
# decac -r 8 src/test/deca/syntax/valid/vide.deca
# decac -r
# decac -n
# decac -r -d
# decac -P src/test/deca/syntax/valid/vide.deca src/test/deca/syntax/valid/vide.deca


decac_moins_b=$(decac -b)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac -b a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_moins_b" = "" ]; then
    echo "ERREUR: decac -b n'a produit aucune sortie"
    exit 1
fi

if echo "$decac_moins_b" | grep -i -e "erreur" -e "error"; then
    echo "ERREUR: La sortie de decac -b contient erreur ou error"
    exit 1
fi

echo "Pas de probleme detecte avec decac -b"

decac_vide=$(decac)

if [ "$?" -ne 0 ]; then
    echo "ERREUR: decac a termine avec un status different de zero."
    exit 1
fi

if [ "$decac_vide" = "" ]; then
    echo "ERREUR: decac n'a produit aucune sortie"
    exit 1
fi

echo "Pas de problème détécté avec decac sans argument"

decac_pv=$(decac -p -v 2>&1)

if [ -z "$?" ]; then
    echo "ERREUR: sortie vide"
    exit 1
fi

echo "Pas de problème détécté avec decac -p -v"

decac_bP=$(decac -b -P 2>&1)

if [ "$?" = "$decac_b" ]; then
    echo "ERREUR : decac -b -P a sorti la bannière en présence d'un autre argument"
    exit 1
fi

if [ -z "$?" ]; then
    echo "ERREUR: sortie vide"
    exit 1
fi

echo "Pas de problème détecté avec decac -b -P"

decac_r3=$(decac -r 3 2>&1)

if [ -z "$?" ]; then
    echo "ERREUR: sortie vide"
    exit 1
fi

echo "Pas de problème détecté avec decac -r 3"

decac_r5_r5=$(decac -r 5 -r 5 2>&1)

if [ -z "$?" ]; then
    echo "ERREUR: sortie vide"
    exit 1
fi

echo "Pas de problème detecté avec decac -r 5 -r 5"

decac_r30=$(decac -r 30 2>&1)

if [ -z "$?" ]; then
    echo "ERREUR: sortie vide"
    exit 1
fi

echo "Pas de problème détecté avec decac -r 30"