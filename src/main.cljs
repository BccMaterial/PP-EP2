(defn busca_tabela [tabela] (str "SELECT * FROM ", tabela))

(print (busca_tabela "usuarios"))
