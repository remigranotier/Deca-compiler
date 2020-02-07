class Cellule extends Object {
	int value;
	Cellule suiv;
	int getLastValue() {
		Cellule current = this;
		while (!suiv.equals(null)) {
			(current = current.suiv);
		}
		return current.value;
	}
	void setSuiv(Cellule newSuiv) {
		(this.suiv = newSuiv);
	}
	void setValue(int newValue) {
		(this.value = newValue);
	}
}
{
	Cellule cell_3 = new Cellule();
	Cellule cell_2 = new Cellule();
	Cellule cell_1 = new Cellule();
	cell_3.setSuiv(null);
	cell_3.setValue(420);
	cell_2.setSuiv(cell_3);
	cell_2.setValue(2);
	cell_1.setSuiv(cell_2);
	cell_1.setValue(1);
	println(cell_1.getLastValue());
	println(cell_2.value);
	println(cell_3.suiv.value);
}
