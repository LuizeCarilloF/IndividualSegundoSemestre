import psutil
import threading
import time
import keyboard
import socket
import mysql.connector
#import pymssql
from cred import usr, pswd

event = threading.Event()
print(event)

def stop():
    event.set()
    print("\nFinalizando monitoramento")
    print(event)

keyboard.add_hotkey("esc", stop)

while not event.is_set():
    cpu = psutil.cpu_times()
    processador = psutil.cpu_percent(interval=1)
    memoria = psutil.virtual_memory()
    hostname = socket.gethostname()

    # CPU
    cpu_percentual = processador

    # Memória
    memoria_disponivel = memoria.available
    memoria_total = memoria.total

    mydb = mysql.connector.connect(host='localhost', user=usr, password=pswd, database='nocline')
    
    try:
        if mydb.is_connected():
            db_info = mydb.get_server_info()
                
            mycursor = mydb.cursor()
        
            sql_query = "SELECT id_maquina, fk_empresaM FROM maquina WHERE hostname = %s;"
            mycursor.execute(sql_query, (hostname,))
            
            result = mycursor.fetchone()
            
            if result:
                id_maquina, fk_empresaM = result
                
                sql_query = """
                INSERT INTO monitoramento (dado_coletado, data_hora, descricao, fk_componentes_monitoramento, fk_maquina_monitoramento, fk_empresa_monitoramento, fk_unidade_medida)
                VALUES (%s, now(), 'uso cpu', (SELECT id_componente from componente WHERE nome_componente = 'CPU' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s)),
                                             (%s, now(), 'memoria disponivel', (SELECT id_componente from componente WHERE nome_componente = 'RAM' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s)),
                       (%s, now(), 'memoria total', (SELECT id_componente from componente WHERE nome_componente = 'RAM' and fk_maquina_componente = %s), %s, %s, (SELECT id_unidade FROM unidade_medida WHERE representacao = %s));
                """
                val = [cpu_percentual, id_maquina, id_maquina, fk_empresaM, '%',
                       memoria_disponivel, id_maquina, id_maquina, fk_empresaM, 'B',
                       memoria_total, id_maquina, id_maquina, fk_empresaM, 'B']
                
                mycursor.execute(sql_query, val)
                mydb.commit()
                print(mycursor.rowcount, "registros inseridos no banco")
                print("\r\n")
            else:
                print("A máquina " + hostname + " não foi cadastrada no site. Cadastre-a para fazer a captura!")
    
    except mysql.connector.Error as e:
        print("Erro ao conectar com o MySQL:", e)
    
    finally:
        if mydb.is_connected():
            mycursor.close()
            mydb.close()
            time.sleep(5)

