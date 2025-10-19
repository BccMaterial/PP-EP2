(require '[clojure.string :as str])

(def ops {
    :maior ">"
    :menor "<"
    :menor_igual "<="
    :maior_igual ">="
    :igual "="
    :in "IN"
    :notin "!IN"
    :diff "<>"
})

; ------- KV HANDLERS -------

(defn format_by_type [item]
    (cond
        (string? item)  (str "\"" item "\"")
        (keyword? item) (str ":" item)
        (number? item)  (str item)
        (boolean? item) (if item "true" "false")
        (vector? item)  (str "[" (str/join ", " (map format_by_type item)) "]")
        (list? item)    (str "(" (str/join ", " (map format_by_type item)) ")")
        :else           (str item))
)

(defn retrieve_non_kv [[k _v]]
    (not= k :field)
)

(defn handle_condition [item]
    (
        (first (first 
            (filter 
                retrieve_non_kv 
                (seq item)
            )
        )) 
        ops
    )
)

(defn handle_value [item]
    (format_by_type 
        (first (rest (first 
            (filter 
                retrieve_non_kv 
                (seq item)
            )
        )))
    )
)

; ------- WHERE BUILDER -------

(defn handle_where_set [operator acc item]
    (println item)
    (if (empty? acc)
        (str (:field item) " " (handle_condition item) " " (handle_value item))
        (str acc " " operator " " (:field item) " " (handle_condition item) " " (handle_value item))
    )   
)

(defn handle_where_set_with_operator [acc item] 
    (str acc " " (:operator item) " " (:query item))
)

(defn build_where [operator acc item]
    ; Se é o primeiro, não tem "operator"
    (if (contains? item :operator)
        (handle_where_set_with_operator acc item)
        (handle_where_set operator acc item)
    )
)

(def build_ands (partial build_where "AND"))
(def build_ors (partial build_where "OR"))

(defn ands [filter_list] 
    { :operator "AND" :query (reduce build_ands "" filter_list) }
)

(defn ors [filter_list] 
    { :operator "OR" :query (reduce build_ors "" filter_list) }
)

; Aqui vai ter q passar uma função (pra ser usada no lugar do build_where)
(defn filters [filters_query]
    (str " WHERE " (:query filters_query))
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
                    { :field "id", :notin [1, 2, 3, 4] },
                    { :field "idade", :maior 25 }
                    { :field "nome", :in ["eduarda", "josias", "vinicius", "thiago"] }
                    (ors [
                        { :field "email" :igual "thiagopls1@homail.com" }
                    ])
                ])
            )
        )
    )
)
