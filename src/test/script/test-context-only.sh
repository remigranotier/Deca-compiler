#! /bin/sh

for i in src/test/deca/context/invalid/homemade/*.deca
do
	var=$(test_context "$i" 2>&1)
	if "$var" 2>&1 | grep -q "$i"
	then
		echo "Erreur OK pour $i"
	else
		echo "$i a marché (non prévu)"
		exit 1
	fi
done

for i in src/test/deca/context/valid/homemade/*.deca
do
	var=$(test_context "$i" 2>&1)
	if "$var" 2>&1 | grep -q "$i"
	then
		echo "ERROR : sortie :"
		echo
		echo "$var"
		exit 1
	else
		echo "Vérification OK pour $i"
	fi
done

for i in src/test/deca/codegen/valid/homemade/*.deca
do
	var=$(test_context "$i" 2>&1)
	if "$var" 2>&1 | grep -q "$i"
	then
		echo "ERROR : sortie :"
		echo
		echo "$var"
		exit 1
	else
		echo "Vérification OK pour $i"
	fi
done

for i in src/test/deca/codegen/valid/homemade/objet/*.deca
do
	var=$(test_context "$i" 2>&1)
	if "$var" 2>&1 | grep -q "$i"
	then
		echo "ERROR : sortie :"
		echo
		echo "$var"
		exit 1
	else
		echo "Vérification OK pour $i"
	fi
done