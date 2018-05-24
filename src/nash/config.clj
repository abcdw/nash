(ns nash.config
  (:require [clojure.java.shell :as shell]))

(def nip #(println "not implemented"))

(def default-db
  {:nash/functions
   {:shell/ranger nip}

   :shell/cwd "/home/abcdw"})

(def db (atom default-db))

(defn defshell [k & more]
  (println more)
  (swap! db assoc-in [:nash/functions k]
         #(apply shell/sh more ;; :dir (:shell/cwd db)
                 )))

(defn runfn [k]
  ((get-in @db [:nash/functions k])))

(defshell :shell/ranger "alacritty" "-e" "ranger")

(runfn :shell/ranger)

