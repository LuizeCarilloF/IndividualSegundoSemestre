import psutil
import threading
import time
import keyboard
import socket
import mysql.connector
from cred import usr, pswd
import json
import requests

# Defina as variáveis de conexão do MySQL aqui

event = threading.Event()
print(event)

id_maquina = None
mydb = None

def stop():
    event.set()
    print("\nFinalizando monitoramento")
    print(event)

keyboard.add_hotkey("esc", stop)

while not event.is_set():
    try:
        if mydb is None or not mydb.is_connected():
            mydb = mysql.connector.connect(host='localhost', user=usr, password=pswd, database='nocLine', auth_plugin='mysql_native_password')

        cpu_percentual = psutil.cpu_percent(interval=1)
        memoria_usada_percentual = psutil.virtual_memory().percent
        hostname = socket.gethostname()

        # CPU
        print("Verificando condições de alerta CPU")
        print("Valor atual de cpu_percentual:", cpu_percentual)
        if cpu_percentual > 0 and cpu_percentual < 4:
            print("Condição de alerta CPU atendida (Risco)")
        elif cpu_percentual > 5:
            print("Condição de alerta CPU atendida (Perigo)")
        else:
            print("Nenhuma condição de alerta CPU atendida")

        # Memória
        print("Verificando condições de alerta RAM")
        print("Valor atual de memoria_usada_percentual:", memoria_usada_percentual)
        if memoria_usada_percentual > 80 and memoria_usada_percentual < 90:
            print("Condição de alerta RAM atendida (Risco)")
        elif memoria_usada_percentual > 90:
            print("Condição de alerta RAM atendida (Perigo)")
        else:
            print("Nenhuma condição de alerta RAM atendida")

        # Restante do código

        sql_query = "SELECT id_maquina, fk_empresaM FROM maquina WHERE hostname = %s;"
        mycursor = mydb.cursor()
        mycursor.execute(sql_query, (hostname,))

        result = mycursor.fetchone()

        if result:
            id_maquina, fk_empresaM = result

            # Restante do código

            sql_query = """
                INSERT INTO monitoramento (dado_coletado, data_hora, descricao, fk_componentes_monitoramento, fk_maquina_monitoramento, fk_empresa_monitoramento, fk_unidade_medida)
                VALUES (%s, now(), 'uso de cpu py', (SELECT id_componente from componente WHERE nome_componente = 'CPU' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s)),
                       (%s, now(), 'memoria disponivel', (SELECT id_componente from componente WHERE nome_componente = 'RAM' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s)),
                       (%s, now(), 'memoria total', (SELECT id_componente from componente WHERE nome_componente = 'RAM' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s));
                """
            val = [cpu_percentual, id_maquina, id_maquina, fk_empresaM, '%',
                   psutil.virtual_memory().available, id_maquina, id_maquina, fk_empresaM, 'B',
                   psutil.virtual_memory().total, id_maquina, id_maquina, fk_empresaM, 'B']

            mycursor.execute(sql_query, val)
            mydb.commit()
            print(mycursor.rowcount, "registros inseridos no banco")
            print("\r\n")

    except mysql.connector.Error as e:
        print("Erro ao conectar com o MySQL:", e)

    finally:
        if mydb and mydb.is_connected():
            mycursor.close()

        time.sleep(5)  # Adicionado aqui se você desejar um atraso antes da próxima iteração
