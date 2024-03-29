package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import model.Sala;

/**
 *
 * @author Sabrina
 * @since 02/2021
 * @version 1.0
 *
 */
public class SalaDAO {

    //Variável apenas para teste
    public String nameTest;

    /**
     * @deprecated
     * Metodo não utilizado. Método de consulta rápida.
     */
    public void consulta() {
        Connect conexao = new Connect();
        Connection conn = conexao.getConectionMySQL();
        try {
            String consulta = "SELECT * FROM sala";

            Statement stm = conn.createStatement();
            ResultSet resultado = stm.executeQuery(consulta);

            while (resultado.next()) {
                System.out.println(resultado.getInt("id_sala"));
                System.out.println(resultado.getString("nome"));
                System.out.println(resultado.getInt("lotacao"));
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conexao.closeConec();
        }
    }

    /**
     * Metodo responsável pela consulta e retorno de ArrayList de sala(s)
     *
     * @return ArrayList de sala(s)
     */
    public ArrayList<Sala> queryByController() {
        ArrayList<Sala> pr = new ArrayList();

        //Consulta ao método da Classe de conexão
        Connect conexao = new Connect();
        Connection conn = conexao.getConectionMySQL();

        //Acessando e atribuindo valores
        try {
            String consulta = "SELECT * FROM SALA";
            Statement stm = conn.createStatement();
            ResultSet resultado = stm.executeQuery(consulta);

            while (resultado.next()) {
                Sala p = new Sala();
                p.setId(resultado.getInt("id_sala"));
                p.setNome(resultado.getString("NOME"));
                p.setLotacao(resultado.getInt("lotacao"));
                pr.add(p);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conexao.closeConec();
        }
        return pr;
    }

    /**
     * Classe responsável pela consulta por nome
     * @param nome Pois o método faz a busca nominal
     */
    public void search(String nome) {
        //Consulta ao método da Classe de conexão
        Connect conexao = new Connect();
        Connection conn = conexao.getConectionMySQL();

        int i = 1, j = 0;
        PessoaDAO pd = new PessoaDAO();

        //Percorre o método query.. e abstrai os nomes iguais
        while (queryByController().size() > j) {
            if (queryByController().get(j).getNome().equalsIgnoreCase(nome)) {
                j = queryByController().get(j).getId();
                break;
            } else {
                j++;
            }
        }

        //Consultando e atribuindo valores
        try {
            String consulta = "SELECT id_pessoa, nome,sobrenome, fk_sala from pessoa where fk_sala=" + j + ";";
            Statement stm = conn.createStatement();
            ResultSet resultado = stm.executeQuery(consulta);

            while (resultado.next()) {

                while (!pd.queryByController().isEmpty()) {
                    this.nameTest = queryByController().get(i).getNome();
                    String msg = "Id: " + resultado.getInt("id_pessoa")
                            + "\nNome Completo: " + resultado.getString("NOME") + " "
                            + resultado.getString("sobrenome")
                            + "\nNome da sala: " + queryByController().get(i).getNome();
                    JOptionPane optionPane = new JOptionPane();
                    optionPane.setMessage(msg);
                    JDialog dialog = optionPane.createDialog(null, "Pessoa encontrada!!!");
                    dialog.setVisible(true);
                    break;
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } finally {
            conexao.closeConec();
        }
    }

    /**
     * Classe responsável pela consulta do maior ID inserido no banco para uma
     * nova inserção
     *
     * @throws SQLException pois chama o método query que faz consultas
     * @return int com o maior valor de ID
     */
    public int maxId() throws SQLException {
        ArrayList<Sala> sala = new ArrayList(queryByController());
        ArrayList<Integer> maxId = new ArrayList();
        for (int i = 0; i < sala.size(); i++) {
            maxId.add(sala.get(i).getId());
        }
        return Collections.max(maxId) + 1;
    }

    /**
     * Classe responsável pela adição de salas, consulta o maior id
     *
     * @throws SQLException pois chama o método query que faz consultas
     * @return int com o maior valor de ID
     * @param sala faz a atribuição através de um valor da classe
     */
    public boolean add(Sala sala) throws SQLException {
        //Consulta ao método da Classe de conexão
        Connect conexao = new Connect();
        Connection conn = conexao.getConectionMySQL();
        try {

            String insert = "INSERT INTO sala (id_sala, nome, lotacao) VALUES (?, ?, ?);";

            try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                stmt.setInt(1, maxId());
                stmt.setString(2, sala.getNome());
                stmt.setInt(3, sala.getLotacao());

                stmt.execute();
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
