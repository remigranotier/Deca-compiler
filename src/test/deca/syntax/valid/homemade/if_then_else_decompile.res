{
	if (true) {
		println("ok");
	} else {
		if (true) {
			println("should not get there");
		} else {
			println("this should definitely not show up");
		}
	}
	if (true) {
		println("ok");
	}
	if (false) {
	} else {
		println("ok");
	}
}
