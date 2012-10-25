(ns sqlingvo.core
  (:refer-clojure :exclude [group-by replace])
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :refer [join replace split]]
            [sqlingvo.compiler :refer [compile-sql]]
            [sqlingvo.util :refer [parse-expr parse-exprs parse-table]]))

(deftype Node [content]

  clojure.lang.IPersistentMap
  (assoc [_ k v]
    (Node. (.assoc content k v)))

  (assocEx [_ k v]
    (Node. (.assocEx content k v)))

  (without [_ k]
    (Node. (.without content k)))

  java.lang.Iterable
  (iterator [this]
    (.iterator content))

  clojure.lang.Associative
  (containsKey [_ k]
    (.containsKey content k))

  (entryAt [_ k]
    (.entryAt content k))

  clojure.lang.IPersistentCollection
  (count [this]
    (jdbc/with-query-results results
      (let [exprs [(parse-expr '(count *))]]
        (compile-sql (assoc this :exprs {:op :exprs :children exprs})))
      (:count (first results))))

  (equiv [this other]
    (and (isa? (class other) Node)
         (= (. this content)
            (. other content))))

  clojure.lang.Seqable
  (seq [this]
    (jdbc/with-query-results results
      (compile-sql this)
      (doall results)))

  clojure.lang.ILookup
  (valAt [_ k]
    (.valAt content k))

  (valAt [_ k not-found]
    (.valAt content k not-found))

  (toString [this]
    (first (compile-sql this))))

(defmethod print-method Node [node writer]
  (print-dup (compile-sql node) writer))

(defmethod print-dup Node [node writer]
  (print-dup (compile-sql node) writer))

(defn node
  "Make a new AST node for `op`."
  [op & {:as opts}]
  (assoc (Node. (or opts {})) :op op))

(defn- parse-from [forms]
  (cond
   (keyword? forms)
   (parse-table forms)
   (and (map? forms) (= :select (:op forms)))
   forms
   :else (throw (IllegalArgumentException. (str "Can't parse FROM form: " forms)))))

(defn- wrap-seq [s]
  (if (sequential? s) s [s]))

(defn sql
  "Compile `stmt` into a vector, where the first element is the
  SQL stmt and the rest are the prepared stmt arguments."
  [stmt] (compile-sql stmt))

(defn as
  "Add an AS clause to the SQL statement."
  [stmt as]
  (assoc (parse-expr stmt) :as as))

(defn except
  "Select the SQL set difference between `stmt-1` and `stmt-2`."
  [stmt-1 stmt-2 & {:keys [all]}]
  {:op :except :children [stmt-1 stmt-2] :all all})

(defn drop-table
  "Drop the database `tables`."
  [tables & {:as opts}]
  (assoc opts
    :op :drop-table
    :tables (map parse-table (wrap-seq tables))))

(defn from
  "Add the FROM item to the SQL statement."
  [stmt & from]
  (assoc stmt
    :from {:op :from :from (map parse-from from)}))

(defn group-by
  "Add the GROUP BY clause to the SQL statement."
  [stmt & exprs]
  (assoc stmt
    :group-by {:op :group-by :exprs (parse-exprs exprs)}))

(defn intersect
  "Select the SQL set intersection between `stmt-1` and `stmt-2`."
  [stmt-1 stmt-2 & {:keys [all]}]
  {:op :intersect :children [stmt-1 stmt-2] :all all})

(defn limit
  "Add the LIMIT clause to the SQL statement."
  [stmt count]
  (assoc stmt :limit {:op :limit :count count}))

(defn offset
  "Add the OFFSET clause to the SQL statement."
  [stmt start]
  (assoc stmt :offset {:op :offset :start start}))

(defn order-by
  "Add the ORDER BY clause to the SQL statement."
  [stmt exprs & {:as opts}]
  (assoc stmt
    :order-by
    (assoc opts
      :op :order-by
      :exprs (parse-exprs (wrap-seq exprs)))))

(defn select
  "Select `exprs` from the database."
  [& exprs]
  {:op :select :exprs (parse-exprs exprs)})

(defn truncate
  "Truncate the database `tables`."
  [tables & {:as opts}]
  (assoc opts
    :op :truncate
    :tables (map parse-table (wrap-seq tables))))

(defn union
  "Select the SQL set union between `stmt-1` and `stmt-2`."
  [stmt-1 stmt-2 & {:keys [all]}]
  {:op :union :children [stmt-1 stmt-2] :all all})

(defn where
  "Add the WHERE `condition` to the SQL statement."
  [stmt condition]
  (assoc stmt
    :condition
    {:op :condition
     :condition (parse-expr condition)}))

(defmulti run
  "Run the SQL statement `stmt`."
  (fn [stmt] (:op stmt)))

(defmethod run :select [stmt]
  (jdbc/with-query-results  results
    (sql stmt)
    (doall results)))

(defmethod run :default [stmt]
  (apply jdbc/do-prepared (sql stmt)))
