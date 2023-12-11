import com.github.britooo.looca.api.core.Looca
import com.github.britooo.looca.api.group.discos.DiscoGrupo
import com.github.britooo.looca.api.group.janelas.Janela


fun main() {
    //Cria uma instância da classe `Looca`.
    val looca: Looca = Looca()

    //Declara duas variáveis do tipo `Janela` e `Rede`.
    val janela: Janela

    //Obtém o objeto `dadosJanelas` da classe `Looca`.
    val dadosJanelas = looca.grupoDeJanelas

    //Obtém uma lista de janelas do objeto `dadosJanelas`.
    val listaJanelas = dadosJanelas.janelas

    //Obtém a quantidade de janelas na lista.
    val quantidadeJanelas = listaJanelas.size

    // Cria uma lista de nomes de janelas a partir da lista de janelas.
    // map -> é uma função que transforma uma lista em outra lista e a { it.getTitulo() } é uma função que é usada para transformar cada janela em seu nome.
    val nomesJanelas = listaJanelas.map { it.getTitulo() }

    //Imprime a quantidade de janelas e o nome de cada uma delas na saída do console.
    println("Quantidade de janelas: $quantidadeJanelas")
    println("Nomes das janelas: $nomesJanelas")


}