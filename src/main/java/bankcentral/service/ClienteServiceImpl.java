package bankcentral.service;

import bankcentral.domain.Cliente;
import bankcentral.domain.Movimiento;
import bankcentral.domain.TarjetaCredito;
import bankcentral.domain.validations.ValidationRules;
import bankcentral.enums.TipoCuenta;
import bankcentral.enums.TipoEstado;
import bankcentral.service.outputport.ClientePersistencePort;
import bankcentral.util.FormRuleValidator;

import java.util.List;
import java.util.Optional;

public class ClienteServiceImpl implements ClienteService {

    // Puerto de salida: dependencia hacia la capa de persistencia
    private final ClientePersistencePort clienteRepository;

    public ClienteServiceImpl(ClientePersistencePort clienteRepository) {
        this.clienteRepository = clienteRepository; // Inyección de dependencias
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------
    @Override
    public Cliente createClienteService() {

        Cliente cliente = new Cliente();

        // Uso de FormRuleValidator con ValidationRules (patron del docente)
        cliente.setId(FormRuleValidator.readInt(
                "Ingrese el id del cliente",
                ValidationRules.POSITIVE_NUMBER,
                "El id debe ser un número positivo."));

        cliente.setIdentificacion(FormRuleValidator.readString(
                "Ingrese la identificación (7-12 dígitos)",
                ValidationRules.VALID_IDENTIFICACION,
                "La identificación debe tener entre 7 y 12 dígitos numéricos."));

        cliente.setNombre(FormRuleValidator.readString(
                "Ingrese el nombre",
                ValidationRules.VALID_NOMBRE,
                "El nombre debe tener al menos 3 letras y no contener números."));

        cliente.setApellido(FormRuleValidator.readString(
                "Ingrese el apellido",
                ValidationRules.VALID_NOMBRE,
                "El apellido debe tener al menos 3 letras y no contener números."));

        cliente.setCelular(FormRuleValidator.readString(
                "Ingrese el celular (10 dígitos)",
                ValidationRules.VALID_CELULAR,
                "El celular debe tener exactamente 10 dígitos."));

        cliente.setUsuario(FormRuleValidator.readString(
                "Ingrese el usuario (mínimo 3 caracteres, sin espacios)",
                ValidationRules.VALID_USUARIO,
                "El usuario debe tener al menos 3 caracteres y no contener espacios."));

        cliente.setContrasena(FormRuleValidator.readString(
                "Ingrese la contraseña (mínimo 4 caracteres)",
                ValidationRules.VALID_CONTRASENA,
                "La contraseña debe tener al menos 4 caracteres."));

        cliente.setEstado(TipoEstado.ACTIVO);
        cliente.setIntentosFallidos(0);

        System.out.println("Seleccione el tipo de cuenta:");
        System.out.println("1. AHORROS   2. CORRIENTE   3. CREDITO");
        int opcion = FormRuleValidator.readInt("Opcion: ");

        TipoCuenta tipoCuenta;
        switch (opcion) {
            case 2:  tipoCuenta = TipoCuenta.CORRIENTE; break;
            case 3:  tipoCuenta = TipoCuenta.CREDITO;   break;
            default: tipoCuenta = TipoCuenta.AHORROS;   break;
        }

        Cliente nuevoCliente = new Cliente(
            cliente.getId(),
            cliente.getIdentificacion(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getCelular(),
            cliente.getUsuario(),
            cliente.getContrasena(),
            tipoCuenta
        );

        return clienteRepository.saveCliente(nuevoCliente);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    @Override
    public Cliente updateClienteService(int id) {

        Optional<Cliente> opt = clienteRepository.findClienteById(id);

        if (opt.isEmpty()) {
            System.out.println("Cliente no encontrado.");
            return null;
        }

        Cliente cliente = opt.get();

        System.out.println("Seleccione el dato a actualizar:");
        System.out.println("1. Nombre");
        System.out.println("2. Apellido");
        System.out.println("3. Identificación");
        System.out.println("4. Celular");
        System.out.println("5. Usuario");
        System.out.println("6. Estado (ACTIVO/BLOQUEADO)");

        int option = FormRuleValidator.readInt("Opcion: ");

        switch (option) {
            case 1:
                cliente.setNombre(FormRuleValidator.readString(
                        "Nuevo nombre",
                        ValidationRules.VALID_NOMBRE,
                        "El nombre debe tener al menos 3 letras y no contener números."));
                break;
            case 2:
                cliente.setApellido(FormRuleValidator.readString(
                        "Nuevo apellido",
                        ValidationRules.VALID_NOMBRE,
                        "El apellido debe tener al menos 3 letras y no contener números."));
                break;
            case 3:
                cliente.setIdentificacion(FormRuleValidator.readString(
                        "Nueva identificación",
                        ValidationRules.VALID_IDENTIFICACION,
                        "La identificación debe tener entre 7 y 12 dígitos."));
                break;
            case 4:
                cliente.setCelular(FormRuleValidator.readString(
                        "Nuevo celular",
                        ValidationRules.VALID_CELULAR,
                        "El celular debe tener exactamente 10 dígitos."));
                break;
            case 5:
                cliente.setUsuario(FormRuleValidator.readString(
                        "Nuevo usuario",
                        ValidationRules.VALID_USUARIO,
                        "El usuario debe tener al menos 3 caracteres y no contener espacios."));
                break;
            case 6:
                String est = FormRuleValidator.readString("Nuevo estado (ACTIVO / BLOQUEADO)");
                cliente.setEstado(est.equalsIgnoreCase("BLOQUEADO")
                        ? TipoEstado.BLOQUEADO : TipoEstado.ACTIVO);
                break;
            default:
                System.out.println("Seleccione una opcion valida.");
        }

        return clienteRepository.updateCliente(cliente);
    }

    // -------------------------------------------------------
    // GET BY ID
    // -------------------------------------------------------
    @Override
    public Optional<Cliente> getClienteById(int id) {

        Optional<Cliente> opt = clienteRepository.findClienteById(id);

        if (opt.isPresent()) {
            Cliente c = opt.get();
            System.out.println("Id:             " + c.getId());
            System.out.println("Identificación: " + c.getIdentificacion());
            System.out.println("Nombre:         " + c.getNombre() + " " + c.getApellido());
            System.out.println("Celular:        " + c.getCelular());
            System.out.println("Usuario:        " + c.getUsuario());
            System.out.println("Estado:         " + c.getEstado());
            System.out.println("Cuenta:         " + c.getCuenta());
        } else {
            System.out.println("Cliente no encontrado.");
        }

        return opt;
    }

    // -------------------------------------------------------
    // GET ALL
    // -------------------------------------------------------
    @Override
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAllClientes();
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    @Override
    public void deleteCliente(int id) {
        System.out.println("Estoy en el service de Cliente");
        clienteRepository.deleteCliente(id);
    }

    // -------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------
    @Override
    public Cliente login(String usuario, String contrasena) {

        Optional<Cliente> opt = clienteRepository.findClienteByUsuario(usuario);

        if (opt.isEmpty()) {
            System.out.println("Usuario no encontrado.");
            return null;
        }

        Cliente cliente = opt.get();

        if (cliente.getEstado() == TipoEstado.BLOQUEADO) {
            System.out.println("Cuenta bloqueada. Contacta con soporte.");
            return null;
        }

        if (!cliente.autenticar(contrasena)) {
            cliente.setIntentosFallidos(cliente.getIntentosFallidos() + 1);
            int restantes = 3 - cliente.getIntentosFallidos();

            if (cliente.getIntentosFallidos() >= 3) {
                cliente.setEstado(TipoEstado.BLOQUEADO);
                System.out.println("Cuenta bloqueada por demasiados intentos fallidos.");
            } else {
                System.out.println("Contraseña incorrecta. Intentos restantes: " + restantes);
            }
            return null;
        }

        // Login exitoso: resetear intentos fallidos
        cliente.setIntentosFallidos(0);
        return cliente;
    }

    // -------------------------------------------------------
    // OPERACIONES BANCARIAS
    // -------------------------------------------------------

    @Override
    public String consignar(Cliente cliente, double monto) {
        String resultado = cliente.getCuenta().consignar(monto);
        System.out.println(resultado);
        return resultado;
    }

    @Override
    public String retirar(Cliente cliente, double monto) {
        String resultado = cliente.getCuenta().retirar(monto);
        System.out.println(resultado);
        return resultado;
    }

    @Override
    public String transferir(Cliente origen, String usuarioDestino, double monto) {

        Optional<Cliente> opt = clienteRepository.findClienteByUsuario(usuarioDestino);

        if (opt.isEmpty())
            return "ERROR: Usuario destino no encontrado.";
        if (origen.getUsuario().equalsIgnoreCase(usuarioDestino))
            return "ERROR: No puedes transferirte a ti mismo.";
        if (origen.getCuenta().getTipo() == TipoCuenta.CREDITO)
            return "ERROR: La tarjeta de crédito no puede hacer transferencias.";
        if (monto > origen.getCuenta().getSaldo())
            return "ERROR: Saldo insuficiente.";

        Cliente destino = opt.get();

        origen.getCuenta().setSaldo(origen.getCuenta().getSaldo() - monto);
        origen.getCuenta().getMovimientos().add(0,
            new Movimiento("Transferencia a " + usuarioDestino, monto));

        destino.getCuenta().setSaldo(destino.getCuenta().getSaldo() + monto);
        destino.getCuenta().getMovimientos().add(0,
            new Movimiento("Transferencia recibida de " + origen.getUsuario(), monto));

        return "Transferencia exitosa. Nuevo saldo: $" +
               String.format("%,.0f", origen.getCuenta().getSaldo());
    }

    @Override
    public String comprarCredito(Cliente cliente, double monto, int cuotas) {
        if (!(cliente.getCuenta() instanceof TarjetaCredito)) {
            return "ERROR: El cliente no tiene tarjeta de crédito.";
        }
        TarjetaCredito tc = (TarjetaCredito) cliente.getCuenta();
        return tc.comprar(monto, cuotas);
    }

    @Override
    public String pagarCredito(Cliente cliente, double monto) {
        if (!(cliente.getCuenta() instanceof TarjetaCredito)) {
            return "ERROR: El cliente no tiene tarjeta de crédito.";
        }
        TarjetaCredito tc = (TarjetaCredito) cliente.getCuenta();
        return tc.pagarDeuda(monto);
    }

    @Override
    public String cambiarContrasena(Cliente cliente, String actual, String nueva, String confirmar) {
        return cliente.cambiarContrasena(actual, nueva, confirmar);
    }
}
