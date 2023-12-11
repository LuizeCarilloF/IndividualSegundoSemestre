import com.github.britooo.looca.api.core.Looca
import com.github.britooo.looca.api.group.janelas.Janela
import com.github.britooo.looca.api.group.rede.Rede


fun main() {

    val looca: Looca = Looca()
    val janela: Janela
    val rede: Rede
    val dadosJanelas = looca.grupoDeJanelas
    val listaJanelas = dadosJanelas.janelas
    val quantidadeJanelas = listaJanelas.size

    val nomesJanelas = listaJanelas.map { it.getTitulo() }
    println("Quantidade de janelas: $quantidadeJanelas")
    println("Nomes das janelas: $nomesJanelas")

    val redes = looca.rede.grupoDeInterfaces.interfaces
    println(redes)
    val listaBytesRecebidos = mutableListOf<Long>()
    val listaBytesEnviados = mutableListOf<Long>()

    val processos = looca.grupoDeProcessos

    var listaProcessos = processos.processos
    for (p in listaProcessos) {
        println("""
           nome ${p.nome}
           pid ${p.pid}
           uso ${p.usoCpu}
           memoria ${p.usoMemoria}
           bytes ${p.bytesUtilizados}
           memoria virtual ${p.memoriaVirtualUtilizada}
       """.trimIndent())
    }
}