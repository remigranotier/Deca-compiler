class Animal extends Object {
	protected int size;
	int getSize() {
		return this.size;
	}
	int returns180() {
		return 1000;
	}
	void setSize(int value) {
		(this.size = value);
	}
	void printsJudgement() {
		this.printsSize();
		if ((this.size <= 50)) {
			println("C'est petit quand meme...");
		} else {
			println("Faudrait penser a maigrir...");
		}
		this.printsLungs();
		this.printsTail();
	}
	void printsSize() {
		println("Je mesure ", this.size, " cm !");
	}
	void printsLungs() {
	}
	void printsTail() {
	}
}
class LandAnimal extends Animal {
	boolean hasLungs = true;
	boolean hasTail() {
		return false;
	}
	void printsLungs() {
		if (hasLungs) {
			println("J'ai des poumons!!!! Incroyable !!!!!");
		} else {
			println("Par contre j'ai pas de poumons, rip l'air en dehors de l'eau ahah");
		}
	}
}
class LandAnimalWithTail extends LandAnimal {
	boolean hasTail() {
		return true;
	}
}
class SeaAnimal extends Animal {
	boolean hasLungs = false;
}
class Dog extends LandAnimalWithTail {
	protected int size = 100;
}
class Human extends LandAnimal {
	protected int size = this.returns180();
	int returns180() {
		return 180;
	}
}
class Fish extends SeaAnimal {
	protected int size = 20;
}
{
	Human jacky = new Human();
	Dog fido = new Dog();
	Fish poissonDuCul = new Fish();
	jacky.printsSize();
	fido.printsSize();
	poissonDuCul.printsSize();
}
