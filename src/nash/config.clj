(ns ^{:doc    "Documentation of nash.config namespace"
      :author "Andrew Tropin"}
    nash.config
  (:require [clojure.java.shell :as shell]
            [manifold.stream :as stream]
            [nash.nativehook]
            [manifold.bus :as bus]
            [clojure.spec.alpha :as s]))

;; KEYBINDINGS
;; t for top-level, n for nested

;; colorschemes
;; TODO: create converter from vim/emacs/terminal colorschemes

;; documentation
;; TODO: add doc autogen and so on
;; docstring formatting rule: use `x` to hilight var/fn and make a link
;; see also vim style of documentation

"This function shows a good doc string.

It accepts a `name` as an argument and returns a pretty-printed doc.
"

;; https://github.com/eggsyntax/datawalk
;; https://github.com/mooz/xkeysnail

(char-escape-string \t)
(def nip #(println "not implemented"))

(def default-mapping
  {"C-u" ::to-previous-hotkey-list
   "C-f" ::toggle-transient-mode
   "C-c" ::to-root-hotkey-list})

(def keywords->keycodes
  {[:fn :esc \1 \2 \3 \4 \5 \6 \7 \8 \9 \0 \[ \] :bspc]              (range 0 15)
   [:tab \' \, \. \p \y \f \g \c \r \l \/ \= :enter]                 (range 15 29)
   [:ctrl \a \o \e \u \i \d \h \t \n \s \- \`]                       (range 29 42)
   [:shift \\ \; \q \j \k \x \b \m \w \v \z]                         (range 42 54)
   [:alt :space :caps :f1 :f2 :f3 :f4 :f5 :f6 :f7 :f8 :f9 :f10 :num] (range 56 69)})

(def _keyword->keycode
  {:printscreen 3639
   :home        3655
   :end         3663
   :pageup      3657
   :pagedown    3665
   :meta        3675
   :rshift      3638
   :up          57416
   :down        57424
   :left        57419
   :right       57421
   :insert      3666
   :delete      3667})

(def _keycode->keyword
  (->>
   _keyword->keycode
   (map (fn [[k v]] [v k]))
   (into (sorted-map))))

(def keycode->keyword
  (->> keywords->keycodes
       (map (fn [[k v]] (zipmap v k)) )
       (into (sorted-map))
       (merge _keycode->keyword)))




(def default-db
  {::cwd "/home/abcdw"
   ::current-window {:class "Emacs"}

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

(stream/consume (fn [x] (println @key-down (map keycode->keyword @key-down))) prn-stream)

(stream/consume (fn [x] (if (= #{3675 24} @key-down)
                         (future (runfn :shell/mpc-toggle)))) prn-stream)
;; (stream/close! prn-stream)

(stream/connect
 (bus/subscribe key-event-stream "pressed")
 prn-stream)


(defshell :shell/ranger
  "alacritty" "-e" "ranger")

(defshell :shell/mpc-toggle
  "mpc" "toggle")

(defshell :windowmanager/detach-hdmi
  "xrandr" "--output" "HDMI1" "--off")

(defshell :windowmanager/attach-hdmi
  "xrandr" "--output" "HDMI1" "--primary"
  "--mode" "1920x1200" "--right-of" "eDP1")

;; (runfn :windowmanager/detach-hdmi)
;; (runfn :windowmanager/attach-hdmi)

;; (runfn :shell/ranger)

