(defn build_where [acc item]
    ; Se é o primeiro, não tem AND
    (if (empty? acc)
        (str (:field item) " = " (:value item))
        (str acc " AND " (:field item) " = " (:value item))
    )
)

(defn filters [filter_list]
    (str " WHERE " (reduce build_where "" filter_list))
)

(defn table [tabela] (str "SELECT * FROM ", tabela))
(print (table "usuarios"))

(print 
    (filters [{:field "id", :value 1}]
    )
)
