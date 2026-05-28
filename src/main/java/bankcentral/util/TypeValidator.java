package bankcentral.util;

import java.util.Scanner;

public class TypeValidator {

    static Scanner sc = new Scanner(System.in);

    public static int validateInt(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } catch (Exception e) {
                System.out.println("Ingrese un numero entero valido.");
                sc.nextLine();
            }
        }
    }

    public static double validateDouble(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                double value = sc.nextDouble();
                sc.nextLine();
                return value;
            } catch (Exception e) {
                System.out.println("Ingrese un numero decimal valido.");
                sc.nextLine();
            }
        }
    }

    public static boolean validateBoolean(String prompt) {
        while (true) {
            try {
                System.out.println(prompt);
                boolean value = sc.nextBoolean();
                sc.nextLine();
                return value;
            } catch (Exception e) {
                System.out.println("Ingrese un valor logico (true/false).");
                sc.nextLine();
            }
        }
    }

    public static String validateString(String prompt) {
        while (true) {
            System.out.println(prompt);
            String value = sc.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("El campo no puede estar vacio.");
        }
    }
}
