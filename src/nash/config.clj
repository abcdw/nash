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

(defshell :shell/ranger
  "alacritty" "-e" "ranger")

(defshell :windowmanager/detach-hdmi
  "xrandr" "--output" "HDMI1" "--off")

(defshell :windowmanager/attach-hdmi
  "xrandr" "--output" "HDMI1" "--primary"
  "--mode" "1920x1200" "--right-of" "eDP1")

(runfn :windowmanager/detach-hdmi)
(runfn :windowmanager/attach-hdmi)

(runfn :shell/ranger)

