#! /bin/sh

# Auteur : gl01
# Version initiale : 18/01/2020

#script pour supprimer l'ensemble des .res et des .ass générés par les autres scripts de tests

# On se place dans le répertoire du projet (quel que soit le
# répertoire d'où est lancé le script) :
cd "$(dirname "$0")"/../../.. || exit 1

PATH=./src/test/script/launchers:"$PATH"

rm src/test/deca/syntax/valid/homemade/*.res
rm src/test/deca/syntax/invalid/homemade/*.res

rm src/test/deca/codegen/valid/homemade/*.res
rm src/test/deca/codegen/invalid/homemade/*.res
rm src/test/deca/codegen/valid/homemade/*.ass
rm src/test/deca/codegen/invalid/homemade/*.ass
rm src/test/deca/codegen/interactive/homemade/*.res
rm src/test/deca/codegen/interactive/homemade/*.ass
rm src/test/deca/context/valid/homemade/*.res
rm src/test/deca/context/invalid/homemade/*.res
rm src/test/deca/context/valid/homemade/*.ass
rm src/test/deca/context/invalid/homemade/*.ass
