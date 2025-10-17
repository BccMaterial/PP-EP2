(defn table [tabela] (str "SELECT * FROM ", tabela))

(print (table "usuarios"))
