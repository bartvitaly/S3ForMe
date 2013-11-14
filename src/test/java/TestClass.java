package test.java;

import org.testng.annotations.Test;

public class TestClass {

	@Test
	public void method_Test() {

		System.out.println(9/2);

	}

	String[] insert(String[] array, String item) {

		int arrayLength = array.length;

		for (int i = 0; i < arrayLength; i++) {

			if (array[i].equals(item)) {

				for (int k = arrayLength; k > i; k--) {
					array[k] = array[k - 1];
				}

				arrayLength++;

			}

		}

		String[] arrayNew = new String[arrayLength];
		for (int k = 0; k < arrayLength; k++) {
			arrayNew[k] = array[k];
		}

		return arrayNew;
	}
	
	String[] delete(String[] array, String item) {

		int arrayLength = array.length;

		for (int i = 0; i < arrayLength; i++) {

			if (array[i].equals(item)) {

				for (int k = i; k < arrayLength - 1; k++) {
					array[k] = array[k + 1];
				}

				arrayLength--;

			}

		}

		String[] arrayNew = new String[arrayLength];
		for (int k = 0; k < arrayLength; k++) {
			arrayNew[k] = array[k];
		}

		return arrayNew;
	}

}
