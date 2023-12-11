import org.apache.commons.dbcp2.BasicDataSource
import org.springframework.jdbc.core.JdbcTemplate

object Conexao {

    var jdbcTemplate: JdbcTemplate? = null
        get() {
            if (field == null){
                val dataSource = BasicDataSource()
                dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
                dataSource.url= "jdbc:mysql://localhost:3306/nocline"
                dataSource.username = "noc_line"
                dataSource.password = "noc_line134#"
                val novoJdbcTemplate = JdbcTemplate(dataSource)
                field = novoJdbcTemplate
            }
            return  field
        }

    var jdbcTemplate_server: JdbcTemplate? = null
        get() {
            if (field == null){
                val dataSource = BasicDataSource()
                dataSource.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"
                dataSource.url = "jdbc:sqlserver://52.22.58.174:1433;database=nocline;encrypt=false;trustServerCertificate=false"
                dataSource.username = "sa"
                dataSource.password = "urubu100"
                val novoJdbcTemplate = JdbcTemplate(dataSource)
                field = novoJdbcTemplate
            }
            return  field
        }

    fun criarTabelas() {
        val jdbcTemplate = Conexao.jdbcTemplate ?: throw IllegalStateException("O jdbcTemplate não foi inicializado corretamente.")

        // Criação da tabela janelas
        jdbcTemplate.execute("""
        CREATE TABLE IF NOT EXISTS janela (
                  id_janela INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
                  nome_janela VARCHAR(150) NULL,
                  status_abertura TINYINT NULL,
                  data_hora DATETIME NULL,
                  fk_maquinaJ INT NOT NULL,
                  fk_empresaJ INT NOT NULL,
                  CONSTRAINT fk_maq_empJ
                    FOREIGN KEY (fk_maquinaJ, fk_empresaJ)
                    REFERENCES maquina (id_maquina, fk_empresaM)
    )
    """)



    }

}