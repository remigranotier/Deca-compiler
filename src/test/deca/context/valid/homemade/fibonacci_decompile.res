{
	int n = 10;
	int t1 = 0;
	int t2 = 1;
	int i = 1;
	int sum;
	println("First ");
	print(n);
	print(" terms: ");
	while ((i != n)) {
		print(t1);
		println(" + ");
		(sum = (t1 + t2));
		(t1 = t2);
		(t2 = sum);
		(i = (i + 1));
	}
}
