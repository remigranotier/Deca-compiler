{
	int year = 1900;
	boolean leap = false;
	if (((year % 4) == 0)) {
		if (((year % 100) == 0)) {
			if (((year % 400) == 0)) {
				(leap = true);
			} else {
				(leap = false);
			}
		} else {
			(leap = true);
		}
	} else {
		(leap = false);
	}
	if (leap) {
		println(year);
		println(" is a leap year.");
	} else {
		println(year);
		println(" is not a leap year.");
	}
}
