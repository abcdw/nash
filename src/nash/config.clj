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
;; Hold modkey to reduce number of items in which-key

;; colorschemes
;; TODO: create converter from vim/emacs/terminal colorschemes

;; documentation
;; TODO: add doc autogen and so on
;; docstring formatting rule: use `x` to hilight var/fn and make a link
;; see also vim style of documentation

;; two modes:
;; meta a m for mpc transient
;; meta f m for mpc oneshot command

;; Conflict free hotkeys

;; navigation in insert mode same as in normal, but with modifier (ctrl for example)
;; Tap win to show which-key, hold win to show hotkey+action icon/window name

;; Shorter than `spc w m` series hotkey (tab for example)
;; Windows layout, which describes which windows placed in which containers

;; Fastcall: save series of hotkey to one of few fastcolls (C-1/2/3/4/a/b/c)

;; Global mode set until other mode chosen
;; Window mode set after exiting from global mode
;; Minor mode ???

;; Get list of functions based on object "type" git fns for git-url for example

;; How to understand what have to be called on SPC f f ? Last file-browser fn?

;; TODO: Only one field can be edited other things are related only to navigation

;; Description of the form -> transformer from formdata to storage data

;; Show items on the same level grouped-by hight-level key, also come throught
;; navigation for sexps (can go in different high-level sexp on the same nesting
;; level)

;; One-hand key bindings

"This function shows a good doc string.

It accepts a `name` as an argument and returns a pretty-printed doc.
"

;; https://github.com/eggsyntax/datawalk
;; https://github.com/mooz/xkeysnail

{:key \p
 :modifiers #{:ctrl :shift}}

(def modifiers
  #{:ctrl :shift :rshift :alt :caps})
;; TODO: Define modifiers area of responsibility
;; :ctrl for autocomplete
;; :meta for windows

(def nip #(println "not implemented"))


(def modi->str
  {:ctrl "C"
   :alt  "A"
   :meta "M"})

(defn keyset-to-str [s k]
  (str (if (s :ctrl) "C-") k))

(keyset-to-str #{:ctrl :alt} \a)

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
  {:f11         87
   :f12         88
   :printscreen 3639
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

   ::state
   {}

   ::functions
   {}

   ::effect-handlers
   {:shell/ranger nip}

   ::components
   {::nativehook true}

   ::streams
   {:nash.keyboard/key-events (bus/event-bus)}})

(def db (atom default-db))

(defn init-db []
  (reset! db default-db))

(defn defshell [k & more]
  (println more)
  (swap! db assoc-in [::effect-handlers k]
         #(apply shell/sh more ;; :dir (:shell/cwd db)
                 )))

(defn runef [k]
  ((get-in @db [::effect-handlers k] (fn []))))

(def nhook (nash.nativehook/init))
(def key-event-stream (get-in nhook [:nash.plugin/streams :jnativehook/key-events]))

(def key-down (atom #{}))
(def e-stream (stream/stream))

(defn update-keydown [e]
  (cond
    (= :pressed (:type e)) (swap! key-down conj (:keycode e))
    (= :released (:type e)) (swap! key-down disj (:keycode e)))
  e)

(def after-update (stream/map update-keydown e-stream))

(stream/connect
 (bus/subscribe key-event-stream "keyevents")
 e-stream)

(stream/consume
 (fn [x] (if (= :pressed (:type x)) (println @key-down (map keycode->keyword @key-down))))
 after-update)

(defn to-ef [key-down k]
  (cond
    (clojure.set/superset? key-down #{3675 24}) :shell/mpc-toggle))

(runef (to-ef ;; @key-down x
        #{3675 24} 24
        ))

(stream/consume (fn [x] (if (= #{3675 24} @key-down)
                         (future (runef :shell/mpc-toggle)))) after-update)

;; (stream/close! e-stream)

(defshell :shell/ranger
  "alacritty" "-e" "ranger")

(defshell :shell/mpc-toggle
  "mpc" "toggle")

(defshell :windowmanager/detach-hdmi
  "xrandr" "--output" "HDMI1" "--off")

(defshell :windowmanager/attach-hdmi
  "xrandr" "--output" "HDMI1" "--primary"
  "--mode" "1920x1200" "--right-of" "eDP1")

;; TODO: add cwd to i3status
