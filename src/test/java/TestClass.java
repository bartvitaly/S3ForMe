package test.java;

import org.testng.annotations.Test;

public class TestClass {

	@Test
	public void method_Test() {

		int[] unsortedArray = { 46, 1, 4, 45, 2, 0 };
		int[] sortedArray = bubbleSort(unsortedArray);

		for (int i = 0; i < sortedArray.length; i++) {
			System.out.println(sortedArray[i]);
		}

	}

	int[] bubbleSort(int[] array) {

		for (int i = 0; i < array.length - 1; i++) {
			for (int j = 0; j < array.length - 1; j++) {
				int current = array[j];
				int next = array[j + 1];
				if (current > next) {
					array[j] = next;
					array[j + 1] = current;
				}
			}
		}
		return array;

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
