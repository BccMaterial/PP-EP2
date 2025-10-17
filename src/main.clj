; Pode virar uma partial
(defn build_where [acc item]
    ; Se é o primeiro, não tem AND
    (if (empty? acc)
        (str (:field item) " = " (:value item))
        (str acc " AND " (:field item) " = " (:value item))
    )
)

(defn ands 
    ([filter_list] (reduce build_where "" filter_list))
)

; Aqui vai ter q passar uma função (pra ser usada no lugar do build_where)
(defn filters [filters_list]
    (str " WHERE " filters_list)
)

(defn table 
    ([table_name] (str "SELECT * FROM ", table_name))
    ([table_name filters] (str "SELECT * FROM ", table_name, filters))
)

(println 
    (table
        "usuarios"
        (filters 
            (ands [
                { :field "id", :value 1 },
                { :field "idade", :value 25 }
            ])
        )
    )
)
