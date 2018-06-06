(ns repl
  (:require cider-nrepl.main))

(defn -main [args]
  (cider-nrepl.main/init
   ["refactor-nrepl.middleware/wrap-refactor"
    "cider.nrepl/cider-middleware"]))


