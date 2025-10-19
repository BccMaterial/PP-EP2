(require '[clojure.string :as str])

(def ops {
    :maior ">"
    :menor "<"
    :menor_igual ">"
    :igual "="
    :in "IN"
    :diff "<>"
})

; ------- KV HANDLERS -------

(defn retrieve_non_kv [[k _]]
    (not= k :field)
)

(defn handle_item [item]
    ((first (first 
        (filter 
            retrieve_non_kv 
            (seq item)
        )
    )) ops)
)

(defn handle_value [item]
    (first (rest (first 
        (filter 
            retrieve_non_kv 
            (seq item)
        )
    )))
)

; ------- WHERE BUILDER -------

(defn build_where [operator acc item]
    ; Se é o primeiro, não tem "operator"
    (if (empty? acc)
        (str (:field item) " " (handle_item item) " " (handle_value item))
        (str acc " " operator " " (:field item) " " (handle_item item) " " (handle_value item))
    )
)

(def build_ands (partial build_where "AND"))
(def build_ors (partial build_where "OR"))

(defn ands 
    ([filter_list] (reduce build_ands "" filter_list))
)

(defn ors
    ([filter_list] (reduce build_ors "" filter_list))
)

; Aqui vai ter q passar uma função (pra ser usada no lugar do build_where)
(defn filters [filters_list]
    (str " WHERE " filters_list)
)

; ------- FUNÇÕES PRINCIPAIS -------

(defn table 
    ([table_name] (str "SELECT * FROM ", table_name))
    ([table_name filters] (str "SELECT * FROM ", table_name, filters))
)

(defn with_columns [columns query]
    (str/replace query "*" 
        (reduce (fn [acc value] 
                    (str acc ", " value)
                ) columns)
    )
)


; ------- USO DA FUNÇÃO -------

(println 
    (with_columns ["id", "nome", "idade", "email"]
        (table
            "usuarios"
            (filters 
                (ands [
                    { :field "id", :diff 1 },
                    { :field "idade", :maior 25 }
                ])
            )
        )
    )
)
