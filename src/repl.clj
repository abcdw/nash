(ns repl
  (:require [clojure.tools.nrepl.server :refer [start-server]]
            [refactor-nrepl.middleware :as refactor.nrepl]
            [cider.nrepl :refer [cider-nrepl-handler]]))

(defn -main [args]
  (let [port (or (some-> (first *command-line-args*)
                         (java.lang.Long/parseLong))
                 7888)]
    (start-server :port port
                  :handler (refactor.nrepl/wrap-refactor cider-nrepl-handler))
    (println "Started nREPL on port" port)))
