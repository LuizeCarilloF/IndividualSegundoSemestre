import com.fasterxml.jackson.databind.ObjectMapper
import com.github.britooo.looca.api.core.Looca
import com.github.britooo.looca.api.group.janelas.Janela
import com.github.britooo.looca.api.group.processos.Processo
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForList
import org.springframework.jdbc.core.queryForObject
import java.time.LocalDate
import java.time.LocalDateTime

class DadosRepositorios {

    lateinit var jdbcTemplate: JdbcTemplate
    lateinit var jdbcTemplate_server: JdbcTemplate

    fun iniciar() {
        jdbcTemplate = Conexao.jdbcTemplate!!
    }

    fun iniciar_server() {
        jdbcTemplate_server = Conexao.jdbcTemplate_server!!
    }

    fun cadastrarJanela(novaJanela: MutableList<Janela>?, id_maquina: Int, fk_empresa: Int) {
        val janelasNoBanco = jdbcTemplate.queryForList(
            "SELECT nome_janela FROM janela where fk_maquinaJ = $id_maquina and fk_empresaJ = $fk_empresa",
            String::class.java
        )

        val janelasNoBancoServer = jdbcTemplate_server.queryForList(
            "SELECT nome_janela FROM janela where fk_maquinaJ = $id_maquina and fk_empresaJ = $fk_empresa",
            String::class.java
        )

        val janelasListadas = novaJanela?.filter { it.titulo != null && it.titulo.isNotBlank() }?.map { it.titulo }

        novaJanela?.forEach { janela ->
            if (janela.titulo != null && janela.titulo.isNotBlank()) {
                val janelaExisteNoBanco = janelasNoBanco.contains(janela.titulo)

                if (janelaExisteNoBanco) {
                    // A janela existe no banco, atualize-a definindo status_abertura como verdadeiro.
                    jdbcTemplate.update(
                        """
                UPDATE janela
                SET data_hora = ?,
                    status_abertura = ?
                WHERE nome_janela = ? AND fk_maquinaJ = $id_maquina AND fk_empresaJ = $fk_empresa
                """,
                        LocalDateTime.now(),
                        true,
                        janela.titulo
                    )
                } else {
                    // A janela não existe no banco, insira-a com status_abertura como verdadeiro.
                    jdbcTemplate.update(
                        """
                INSERT INTO janela (nome_janela, data_hora, status_abertura, fk_maquinaJ, fk_empresaJ)
                VALUES (?, ?, ?, $id_maquina, $fk_empresa)
                """,
                        janela.titulo,
                        LocalDateTime.now(),
                        true
                    )
                }
                val janelaExisteNoBancoServer = janelasNoBancoServer.contains(janela.titulo)

                if (janelaExisteNoBancoServer) {
                    // A janela existe no banco, atualize-a definindo status_abertura como verdadeiro.
                    jdbcTemplate_server.update(
                        """
                UPDATE janela
                SET data_hora = ?,
                    status_abertura = ?
                WHERE nome_janela = ? AND fk_maquinaJ = $id_maquina AND fk_empresaJ = $fk_empresa
                """,
                        LocalDateTime.now(),
                        true,
                        janela.titulo
                    )
                } else {
                    // A janela não existe no banco, insira-a com status_abertura como verdadeiro.
                    jdbcTemplate_server.update(
                        """
                INSERT INTO janela (nome_janela, data_hora, status_abertura, fk_maquinaJ, fk_empresaJ)
                VALUES (?, ?, ?, $id_maquina, $fk_empresa)
                """,
                        janela.titulo,
                        LocalDateTime.now(),
                        true
                    )
                }
            }
        }

        if (janelasListadas != null && janelasListadas.isNotEmpty()) {
            val placeholders = janelasListadas.map { "?" }.joinToString(", ")
            val updateQuery = "UPDATE janela SET status_abertura = ? WHERE nome_janela NOT IN ($placeholders)"
            val updateQueryServer = "UPDATE janela SET status_abertura = ? WHERE nome_janela NOT IN ($placeholders)"
            val params = arrayOf(false, *janelasListadas.toTypedArray())
            val queryJanela = jdbcTemplate.update(updateQuery, *params)
            val queryJanelaServer = jdbcTemplate.update(updateQueryServer, *params)
            println("${queryJanela + queryJanelaServer} registros atualizados na tabela de janelas")
        }

    }






    fun capturarDadosJ(looca: Looca): MutableList<Janela>? {
        val janela = looca.grupoDeJanelas
        var janelasVisiveis = janela.janelasVisiveis

        return janelasVisiveis
    }


    }






