class Ouaip extends Object {
	int x = 3;
}
class Ouaipp extends Ouaip {
	int y = 4;
}
{
	Ouaipp ouaipp = new Ouaipp();
	Ouaip ouaip = (Ouaip) (ouaipp);
	int a = 2;
	float b = (float) (a);
	if ((ouaipp  instanceof  Ouaip)) {
		println("yoyoyo");
	}
}
