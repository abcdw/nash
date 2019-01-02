(ns nash.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [seesaw.core :as ss :refer :all]
            [seesaw.font :refer :all]))



(def db (atom {}))

(swap! db assoc :gui/keybindings {:stop-action "C-g"})

(def general-mode
  #:mode{:desription  "General keybindings and actions"
         :type        :command
         :actions     {:default #(print "hello")
                       :print   print
                       :pprint  clojure.pprint/pprint}
         :keybindings {\a :default}})


(defn -main [args]
  (println "ok"))


[#:task{:title    "Experiment with seesaw"
        :progress 50}
 #:task{:title "Create #deftask reader macro"
        :descriptin "It may help to write \"plain\" (markup) text inside code
Think about making git tasks plugin"}]

;; (listbox :popup (fn [e] []))

;; ;;;;;;;;;;;;;;;;;; General project thoughts ;;;;;;;;;;;;;;;;;;
"Formulate a problem to solve"

"Don't make an easier dsl like bash, make it possible to generate a proper
clojure with good interfaces, create a spec, which describes arguments type and
for each type make autocompleter (gui + algorithm), which will create a
\"/home/abcdw/Downloads\" from h/a/d + hotkey to autocomplete in second position
of (ls |)"

"Think about necessary interface elements"

;; ;;;;;;;;;;;;;;;;;; Dev repeatative actions ;;;;;;;;;;;;;;;;;;
"When you write function and use not yet defined fn it will be cool to have
ability easily create a definition of such function."

;; ;;;;;;;;;;;;;;;;;; Helpful projects and libs ;;;;;;;;;;;;;;;;;;
;; https://github.com/franks42/clj-ns-browser

(s/def ::letter (set (map char (range (int \a) (inc (int \z))))))

(s/def ::test-spec
  (s/with-gen string?
    #(gen/fmap (fn [letters] (apply str letters))
               (s/gen (s/coll-of ::letter :min-count 1)))))

;; (s/def ::test-spec (s/coll-of ::letter :min-count 1))

;; (s/conform ::test-spec "test")
(gen/sample (s/gen ::test-spec))


"Edward Rofl Tufte pioneer in the field of data visualisation
The Visual Display of Quantative information"
