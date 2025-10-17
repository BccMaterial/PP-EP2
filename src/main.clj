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

(defn table 
    ([col_name] (str "SELECT * FROM ", col_name))
    ([col_name filters] (str "SELECT * FROM ", col_name, filters))
)

(println (table "usuarios"))

(println 
    (table
        "usuarios"
        (filters 
            [
                { :field "id", :value 1 },
                { :field "idade", :value 25 }
            ]
        )
    )
)
