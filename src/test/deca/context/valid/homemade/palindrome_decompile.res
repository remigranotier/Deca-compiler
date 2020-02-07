{
	int r;
	int sum = 0;
	int temp;
	int n = 454;
	(temp = n);
	while ((n > 0)) {
		(r = (n % 10));
		(sum = ((sum * 10) + r));
		(n = (n / 10));
	}
	if ((temp == sum)) {
		println("palindrome number ");
	} else {
		println("not palindrome");
	}
}
