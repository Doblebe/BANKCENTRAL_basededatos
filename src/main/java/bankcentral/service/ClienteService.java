package bankcentral.service;

import bankcentral.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {

    // Contratos - solo la firma de los metodos (puerto de entrada)

    Cliente           createClienteService();
    Cliente           updateClienteService(int id);
    Optional<Cliente> getClienteById(int id);
    List<Cliente>     getAllClientes();
    void              deleteCliente(int id);
    Cliente           login(String usuario, String contrasena);

    // Operaciones bancarias
    String consignar(Cliente cliente, double monto);
    String retirar(Cliente cliente, double monto);
    String transferir(Cliente origen, String usuarioDestino, double monto);
    String comprarCredito(Cliente cliente, double monto, int cuotas);
    String pagarCredito(Cliente cliente, double monto);
    String cambiarContrasena(Cliente cliente, String actual, String nueva, String confirmar);
}
