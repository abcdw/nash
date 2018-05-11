(ns nash.gui
  (:require
   [clojure.string :as cstr]
   [seesaw.core :as ss :refer :all]
   [seesaw.font :refer :all]))

(defonce f (ss/frame :title "Get to know Seesaw"))

(native!)

(def lbl (label :id :my-label
                :text "I'm another label"
                :halign :center))
(def b (button :text "Click Me"))

(defn display [content]
  (config! f :content content)
  content)

(def my-font
  (font :name "Iosevka Nerd Font"
        :size 18))

(def text-panel (text :text "test"
                      :margin 10
                      :multi-line? true
                      :font my-font))

(def bpanel (border-panel :north lbl
                          :west b
                          :center text-panel
                          :border 5
                          :vgap 5
                          :hgap 5))
(display bpanel)

(defn show-form []
  (-> f ss/pack! ss/show!))

;; (config! text-panel :text
;;          (cstr/join "\n" (font-families)))

(comment
  (show-form))


;; (print (eval (read-string (input "What's your favorite color?"))))

;; (def lb (listbox :model (-> 'seesaw.core ns-publics keys sort)))
;; (display b)
;; (display lbl)
;; (display lb)
;; (display (scrollable lb))
;; (show-form)
;; (listen b :action (fn [e] (alert e "Thanks!")))
;; (listen b :mouse-entered #(config! % :foreground :blue)
;;         :mouse-exited #(config! % :foreground :red))
;; (selection lb {:multi? true})

;; (listen lb :selection (fn [e] (println "Selection is " (selection e))))

;; (all-frames)
