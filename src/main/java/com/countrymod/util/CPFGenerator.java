package com.countrymod.util;

import java.util.Random;

/**
 * Generates valid Brazilian CPF (tax ID) numbers.
 */
public class CPFGenerator {
	private static final Random random = new Random();
	
	public static String generateCPF() {
		StringBuilder base = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			base.append(random.nextInt(10));
		}
		
		String d1 = calculateDigit(base.toString(), new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2});
		String d2 = calculateDigit(base.toString() + d1, new int[]{11, 10, 9, 8, 7, 6, 5, 4, 3, 2});
		
		String cpf = base.toString() + d1 + d2;
		return CPFValidator.formatCPF(cpf);
	}
	
	private static String calculateDigit(String numbers, int[] weights) {
		int sum = 0;
		for (int i = 0; i < numbers.length(); i++) {
			sum += Character.getNumericValue(numbers.charAt(i)) * weights[i];
		}
		int remainder = sum % 11;
		return remainder < 2 ? "0" : String.valueOf(11 - remainder);
	}
}
