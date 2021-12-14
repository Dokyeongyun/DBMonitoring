package JavaFx;

import java.io.File;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) {
		File file = new File("./config/connectioninfo");
		System.out.println(Arrays.toString(file.list()));
	}
}
