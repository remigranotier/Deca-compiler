class A extends Object {
	int x() {
		return 4;
	}
}
{
	A a = new A();
	(a.x = 2);
}
