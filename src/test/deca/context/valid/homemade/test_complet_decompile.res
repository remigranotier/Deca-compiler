class A extends Object {
	protected int x;
	int getX() {
		return x;
	}
	void setX(int x) {
		(this.x = x);
	}
}
{
	A a = new A();
	a.setX(1);
	println("a.getX() = ", a.getX());
}
