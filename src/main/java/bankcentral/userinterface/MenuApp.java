package bankcentral.userinterface;

import bankcentral.domain.Cliente;
import bankcentral.domain.Movimiento;
import bankcentral.domain.TarjetaCredito;
import bankcentral.domain.validations.ValidationRules;
import bankcentral.service.ClienteServiceImpl;
import bankcentral.util.FormRuleValidator;
import bankcentral.util.TypeValidator;
import bankcentral.view.ClienteView;

import java.util.List;
import java.util.Scanner;

public class MenuApp {

    Scanner sc = new Scanner(System.in);

    private final ClienteView clienteView;
    private final ClienteServiceImpl clienteServiceImpl;

    public MenuApp(ClienteView clienteView, ClienteServiceImpl clienteServiceImpl) {
        this.clienteView        = clienteView;
        this.clienteServiceImpl = clienteServiceImpl;
    }


    // -------------------------------------------------------
    // MENU PRINCIPAL
    // -------------------------------------------------------
    public void mainMenu() {

        System.out.println("Presione 1 para iniciar BankCentral");
        int init = sc.nextInt();
        sc.nextLine();

        while (init != 0) {
            System.out.println("\n==============================");
            System.out.println("     BIENVENIDO A BANKCENTRAL");
            System.out.println("==============================");
            System.out.println("1. Registrarse");
            System.out.println("2. Iniciar Sesión");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    System.out.println("\n--- Registro de Nuevo Cliente ---");
                    clienteView.createCliente();
                    break;
                case 2:
                    System.out.println("\n--- Inicio de Sesión ---");
                    iniciarSesion();
                    break;
                case 3:
                    System.out.println("Gracias por usar BankCentral. Hasta pronto.");
                    init = 0;
                    break;
                default:
                    System.out.println("Ingrese una opción válida.");
            }
        }
    }


    // -------------------------------------------------------
    // INICIO DE SESION
    // -------------------------------------------------------
    private void iniciarSesion() {

        String usuario    = FormRuleValidator.readString("Usuario: ");
        String contrasena = FormRuleValidator.readString("Contraseña: ");

        Cliente cliente = clienteView.login(usuario, contrasena);

        if (cliente == null) {
            System.out.println("No se pudo iniciar sesión. Volviendo al menú principal.");
            return;
        }

        System.out.println("\nBienvenido, " + cliente.getNombre() + " " + cliente.getApellido() + "!");

        // Verificar si es admin para mostrar opciones adicionales
        if (cliente.getUsuario().equalsIgnoreCase("admin")) {
            menuAdmin(cliente);
        } else {
            menuDashboard(cliente);
        }
    }


    // -------------------------------------------------------
    // DASHBOARD DEL CLIENTE
    // -------------------------------------------------------
    private void menuDashboard(Cliente cliente) {

        boolean activo = true;

        while (activo) {
            System.out.println("\n==============================");
            System.out.println("  DASHBOARD - " + cliente.getNombre().toUpperCase());
            System.out.println("==============================");
            System.out.println("Cuenta: " + cliente.getCuenta().getTipo());

            if (cliente.getCuenta() instanceof TarjetaCredito) {
                TarjetaCredito tc = (TarjetaCredito) cliente.getCuenta();
                System.out.println("Deuda: $" + String.format("%,.0f", tc.getSaldo()));
                System.out.println("Cupo disponible: $" + String.format("%,.0f", tc.getCupoDisponible()));
            } else {
                System.out.println("Saldo: $" + String.format("%,.0f", cliente.getCuenta().getSaldo()));
            }

            System.out.println("------------------------------");
            System.out.println("1. Consignar");
            System.out.println("2. Retirar");
            System.out.println("3. Transferir");
            System.out.println("4. Movimientos");
            System.out.println("5. Tarjeta de Crédito");
            System.out.println("6. Mi Perfil");
            System.out.println("7. Cerrar Sesión");
            System.out.print("Seleccione una opción: ");

            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1: menuConsignar(cliente);    break;
                case 2: menuRetirar(cliente);      break;
                case 3: menuTransferir(cliente);   break;
                case 4: menuMovimientos(cliente);  break;
                case 5: menuCredito(cliente);      break;
                case 6: menuPerfil(cliente);       break;
                case 7:
                    System.out.println("Sesión cerrada. Hasta pronto, " + cliente.getNombre() + ".");
                    activo = false;
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        }
    }


    // -------------------------------------------------------
    // CONSIGNAR
    // -------------------------------------------------------
    private void menuConsignar(Cliente cliente) {
        System.out.println("\n--- Consignar ---");
        double monto = FormRuleValidator.readDouble(
                "Ingrese el monto a consignar: $",
                ValidationRules.POSITIVE_AMOUNT,
                "El monto debe ser mayor a cero.");
        System.out.println(clienteServiceImpl.consignar(cliente, monto));
    }


    // -------------------------------------------------------
    // RETIRAR
    // -------------------------------------------------------
    private void menuRetirar(Cliente cliente) {
        System.out.println("\n--- Retirar ---");
        double monto = FormRuleValidator.readDouble(
                "Ingrese el monto a retirar: $",
                ValidationRules.POSITIVE_AMOUNT,
                "El monto debe ser mayor a cero.");
        System.out.println(clienteServiceImpl.retirar(cliente, monto));
    }


    // -------------------------------------------------------
    // TRANSFERIR
    // -------------------------------------------------------
    private void menuTransferir(Cliente cliente) {
        System.out.println("\n--- Transferir ---");
        String usuarioDestino = FormRuleValidator.readString("Ingrese el usuario destino: ");
        double monto = FormRuleValidator.readDouble(
                "Ingrese el monto a transferir: $",
                ValidationRules.POSITIVE_AMOUNT,
                "El monto debe ser mayor a cero.");
        System.out.println(clienteServiceImpl.transferir(cliente, usuarioDestino, monto));
    }


    // -------------------------------------------------------
    // MOVIMIENTOS
    // -------------------------------------------------------
    private void menuMovimientos(Cliente cliente) {
        System.out.println("\n--- Historial de Movimientos ---");
        List<Movimiento> movimientos = cliente.getCuenta().getMovimientos();

        if (movimientos.isEmpty()) {
            System.out.println("No tienes movimientos registrados.");
            return;
        }

        System.out.printf("%-22s %-45s %15s%n", "Fecha", "Tipo", "Valor");
        System.out.println("-".repeat(84));
        for (Movimiento m : movimientos) {
            System.out.printf("%-22s %-45s %15s%n",
                m.getFecha(),
                m.getTipo(),
                "$" + String.format("%,.0f", m.getValor()));
        }
    }


    // -------------------------------------------------------
    // TARJETA DE CREDITO
    // -------------------------------------------------------
    private void menuCredito(Cliente cliente) {

        if (!(cliente.getCuenta() instanceof TarjetaCredito)) {
            System.out.println("No tienes tarjeta de crédito.");
            return;
        }

        TarjetaCredito tc = (TarjetaCredito) cliente.getCuenta();
        boolean activo = true;

        while (activo) {
            System.out.println("\n--- Tarjeta de Crédito ---");
            System.out.println("Cupo total:       $" + String.format("%,.0f", tc.getCupo()));
            System.out.println("Deuda actual:     $" + String.format("%,.0f", tc.getSaldo()));
            System.out.println("Cupo disponible:  $" + String.format("%,.0f", tc.getCupoDisponible()));
            System.out.println("------------------------------");
            System.out.println("1. Realizar Compra");
            System.out.println("2. Pagar Deuda");
            System.out.println("3. Volver");
            System.out.print("Seleccione una opción: ");

            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1:
                    double monto = FormRuleValidator.readDouble(
                            "Monto de la compra: $",
                            ValidationRules.POSITIVE_AMOUNT,
                            "El monto debe ser mayor a cero.");
                    int cuotas = FormRuleValidator.readInt(
                            "Número de cuotas: ",
                            ValidationRules.VALID_CUOTAS,
                            "Las cuotas deben estar entre 1 y 36.");
                    System.out.println(clienteServiceImpl.comprarCredito(cliente, monto, cuotas));
                    break;
                case 2:
                    double pago = FormRuleValidator.readDouble(
                            "Monto a pagar: $",
                            ValidationRules.POSITIVE_AMOUNT,
                            "El monto debe ser mayor a cero.");
                    System.out.println(clienteServiceImpl.pagarCredito(cliente, pago));
                    break;
                case 3:
                    activo = false;
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        }
    }


    // -------------------------------------------------------
    // PERFIL DEL CLIENTE
    // -------------------------------------------------------
    private void menuPerfil(Cliente cliente) {

        boolean activo = true;

        while (activo) {
            System.out.println("\n--- Mi Perfil ---");
            System.out.println("Nombre:         " + cliente.getNombre() + " " + cliente.getApellido());
            System.out.println("Identificación: " + cliente.getIdentificacion());
            System.out.println("Celular:        " + cliente.getCelular());
            System.out.println("Usuario:        " + cliente.getUsuario());
            System.out.println("Estado:         " + cliente.getEstado());
            System.out.println("------------------------------");
            System.out.println("1. Actualizar Nombre");
            System.out.println("2. Actualizar Celular");
            System.out.println("3. Cambiar Contraseña");
            System.out.println("4. Volver");
            System.out.print("Seleccione una opción: ");

            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1:
                    cliente.setNombre(FormRuleValidator.readString(
                            "Nuevo nombre: ",
                            ValidationRules.VALID_NOMBRE,
                            "El nombre debe tener al menos 3 letras y no contener números."));
                    System.out.println("Nombre actualizado correctamente.");
                    break;
                case 2:
                    cliente.setCelular(FormRuleValidator.readString(
                            "Nuevo celular: ",
                            ValidationRules.VALID_CELULAR,
                            "El celular debe tener exactamente 10 dígitos."));
                    System.out.println("Celular actualizado correctamente.");
                    break;
                case 3:
                    String actual    = FormRuleValidator.readString("Contraseña actual: ");
                    String nueva     = FormRuleValidator.readString(
                            "Nueva contraseña: ",
                            ValidationRules.VALID_CONTRASENA,
                            "La contraseña debe tener al menos 4 caracteres.");
                    String confirmar = FormRuleValidator.readString("Confirmar nueva contraseña: ");
                    System.out.println(clienteServiceImpl.cambiarContrasena(cliente, actual, nueva, confirmar));
                    break;
                case 4:
                    activo = false;
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        }
    }


    // -------------------------------------------------------
    // PANEL DE ADMINISTRACION
    // -------------------------------------------------------
    private void menuAdmin(Cliente admin) {

        boolean activo = true;

        while (activo) {
            System.out.println("\n==============================");
            System.out.println("     PANEL DE ADMINISTRACIÓN");
            System.out.println("==============================");
            System.out.println("1. Ver todos los clientes");
            System.out.println("2. Buscar cliente por Id");
            System.out.println("3. Crear nuevo cliente");
            System.out.println("4. Actualizar cliente");
            System.out.println("5. Eliminar cliente");
            System.out.println("6. Cerrar Sesión");
            System.out.print("Seleccione una opción: ");

            int opt = sc.nextInt();
            sc.nextLine();

            switch (opt) {
                case 1:
                    System.out.println("\n--- Todos los Clientes ---");
                    clienteView.getAllClientes();
                    break;
                case 2:
                    clienteView.getClienteById(FormRuleValidator.readInt(
                            "Ingrese el id del cliente",
                            ValidationRules.POSITIVE_NUMBER,
                            "El id debe ser un número positivo."));
                    break;
                case 3:
                    System.out.println("\n--- Crear Nuevo Cliente ---");
                    clienteView.createCliente();
                    break;
                case 4:
                    System.out.println("\n--- Actualizar Cliente ---");
                    clienteView.updateCliente(FormRuleValidator.readInt(
                            "Ingrese el id del cliente a actualizar",
                            ValidationRules.POSITIVE_NUMBER,
                            "El id debe ser un número positivo."));
                    break;
                case 5:
                    System.out.println("\n--- Eliminar Cliente ---");
                    clienteView.deleteCliente(FormRuleValidator.readInt(
                            "Ingrese el id del cliente a eliminar",
                            ValidationRules.POSITIVE_NUMBER,
                            "El id debe ser un número positivo."));
                    break;
                case 6:
                    System.out.println("Sesión cerrada. Hasta pronto, " + admin.getNombre() + ".");
                    activo = false;
                    break;
                default:
                    System.out.println("Seleccione una opción válida.");
            }
        }
    }
}
