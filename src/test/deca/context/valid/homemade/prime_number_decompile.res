{
	int num = 29;
	boolean flag = false;
	int i = 2;
	boolean out = false;
	while (((i != (num / 2)) && (out == false))) {
		if (((num % i) == 0)) {
			(flag = true);
			(out = true);
		}
	}
	if (!flag) {
		println(num);
		println(" is a prime number.");
	} else {
		println(num);
		println(" is not a prime number.");
	}
}
