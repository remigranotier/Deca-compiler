class A extends Object {
	protected B b;
	void setB(B newB) {
		(this.b = newB);
	}
}
class B extends Object {
	protected A a;
	void setA(A newA) {
		(this.a = newA);
	}
}
{
	A a = new A();
	B b = new B();
	a.setB(b);
	b.setA(a);
}
