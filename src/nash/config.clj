(ns nash.config
  (:require [clojure.java.shell :as shell]
            [manifold.stream :as stream]
            [nash.nativehook]
            [manifold.bus :as bus]
            [clojure.spec.alpha :as s]))

;; KEYBINDINGS
;; t for top-level, n for nested

;; https://github.com/mooz/xkeysnail

(def nip #(println "not implemented"))

(def default-mapping
  {"C-u" ::to-previous-hotkey-list
   "C-f" ::toggle-transient-mode
   "C-c" ::to-root-hotkey-list})

(def default-db
  {::cwd "/home/abcdw"

   ::functions
   {:shell/ranger nip}

   ::components
   {::nativehook true}

   ::streams
   {:nash.keyboard/key-events (bus/event-bus)}})

(def db (atom default-db))

(defn defshell [k & more]
  (println more)
  (swap! db assoc-in [::functions k]
         #(apply shell/sh more ;; :dir (:shell/cwd db)
                 )))

(defn runfn [k]
  ((get-in @db [::functions k])))

(def nhook (nash.nativehook/init))
(def key-event-stream (get-in nhook [:nash.plugin/streams :jnativehook/key-events]))

(def key-down (atom #{}))
(def p-stream (stream/stream))
(stream/consume #(swap! key-down conj %) p-stream)
(def r-stream (stream/stream))
(stream/consume #(swap! key-down disj %) r-stream)

(stream/connect
 (bus/subscribe key-event-stream "pressed")
 p-stream)

(stream/connect
 (bus/subscribe key-event-stream "released")
 r-stream)

(def prn-stream (stream/stream))

(stream/consume (fn [x] (println key-down)) prn-stream)

(stream/consume (fn [x] (if (= #{3675 24} @key-down)
                         (future (runfn :shell/ranger)))) prn-stream)

(stream/connect
 (bus/subscribe key-event-stream "pressed")
 prn-stream)


(defshell :shell/ranger
  "alacritty" "-e" "ranger")

(defshell :windowmanager/detach-hdmi
  "xrandr" "--output" "HDMI1" "--off")

(defshell :windowmanager/attach-hdmi
  "xrandr" "--output" "HDMI1" "--primary"
  "--mode" "1920x1200" "--right-of" "eDP1")

;; (runfn :windowmanager/detach-hdmi)
;; (runfn :windowmanager/attach-hdmi)

;; (runfn :shell/ranger)

