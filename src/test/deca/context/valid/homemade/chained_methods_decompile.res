class A extends Object {
	int x = 20;
	int getX() {
		return this.x;
	}
}
class B extends A {
	A a = new A();
	A getA() {
		return this.a;
	}
}
{
	B b = new B();
	println(b.getA().getX());
}
