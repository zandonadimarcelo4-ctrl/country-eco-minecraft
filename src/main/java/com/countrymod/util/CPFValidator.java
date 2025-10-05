package com.countrymod.util;

/**
 * Validates and formats Brazilian CPF (tax ID) numbers.
 */
public class CPFValidator {
	public static boolean isValidCPF(String cpf) {
		if (cpf == null) return false;
		
		String cleanCpf = cpf.replaceAll("[^0-9]", "");
		
		if (cleanCpf.length() != 11) return false;
		
		// Check if all digits are the same
		if (cleanCpf.chars().allMatch(c -> c == cleanCpf.charAt(0))) return false;
		
		try {
			// Validate first check digit
			int sum = 0;
			for (int i = 0; i < 9; i++) {
				sum += Character.getNumericValue(cleanCpf.charAt(i)) * (10 - i);
			}
			int firstVerifier = 11 - (sum % 11);
			if (firstVerifier >= 10) firstVerifier = 0;
			
			if (Character.getNumericValue(cleanCpf.charAt(9)) != firstVerifier) return false;
			
			// Validate second check digit
			sum = 0;
			for (int i = 0; i < 10; i++) {
				sum += Character.getNumericValue(cleanCpf.charAt(i)) * (11 - i);
			}
			int secondVerifier = 11 - (sum % 11);
			if (secondVerifier >= 10) secondVerifier = 0;
			
			return Character.getNumericValue(cleanCpf.charAt(10)) == secondVerifier;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String formatCPF(String cpf) {
		String cleanCpf = cpf.replaceAll("[^0-9]", "");
		if (cleanCpf.length() == 11) {
			return String.format("%s.%s.%s-%s",
				cleanCpf.substring(0, 3),
				cleanCpf.substring(3, 6),
				cleanCpf.substring(6, 9),
				cleanCpf.substring(9, 11));
		}
		return cpf;
	}
}
