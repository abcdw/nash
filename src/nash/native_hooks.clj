(ns nash.native-hooks
  (:require [clojure.reflect :as cr]
            [clojure.string :as cstr])
  (:import (org.jnativehook GlobalScreen NativeHookException)
           (java.util.logging Logger Level)
           (org.jnativehook.keyboard NativeKeyEvent NativeKeyListener)))

;; TODO: think about different layouts hotkeys

(defn get-static-field-value [field]
  (.get (.getField NativeKeyEvent field) nil))

(defn convert-key-to-keyword [key]
  (keyword "key" (cstr/lower-case (cstr/replace key #"VC_" ""))))

(def key+code
  (->> (cr/reflect NativeKeyEvent :ancestors true)
       :members
       (filter #(cstr/starts-with? (:name %1) "VC_"))
       (map :name)
       (map str)
       (map (fn [name] [(convert-key-to-keyword name) (get-static-field-value name)]))
       (sort-by second)))

(def key->code (into {} key+code))

(defn is-pressed? [e key]
  (= (key key->code) (.getKeyCode e)))

;; (.getModifiers @last-event)


(defonce last-event (atom nil))
(defn update-event! [e]
  (reset! last-event e))
;; @last-event
;; (update-event! "test")

(def jn-logger (Logger/getLogger "org.jnativehook"))
(. jn-logger setLevel (. Level ALL))
(. jn-logger setLevel (. Level WARNING))

(defmulti key-typed (fn [e] (.getModifiers e)))

;; (remove-method key-typed :default)
(methods key-typed)

(def VC_META_L_MASK 4)

;; ((get (methods key-typed) 4) {})

(defmethod key-typed 4
  [e]
  (println "meta pressed" (.getKeyChar e)))

(defmethod key-typed :default
  [e]
  ;; (println "wow")
  ;; (println "ok: " (.getModifiers e))
  )



(def klistener
  (reify NativeKeyListener
    (nativeKeyPressed [this e]
      (println (.getKeyCode e))

      ;; (println "key pressed: " e)
      ;; (when (= (.getKeyChar e) \w)
      ;;   (println "^^^^^^^^^^^^^^^^^^^triggered")
      ;;   (update-event! e))
      ;; (println "is pressed: " (is-pressed? e :key/w))
      ;; (println "pressed: " (.getKeyCode e))
      )
    (nativeKeyReleased [this e]
      ;; (println "key released: " (NativeKeyEvent/getKeyText (.getKeyCode e)))
      )
    (nativeKeyTyped [this e]
      (key-typed e)
      ;; (print "key typed:" (type (.getModifiers e)) " ")
      ;; (println "key typed:" (.getKeyChar e))
      ;; (println "key typed: " (NativeKeyEvent/getKeyText (.getKeyCode e)))
      )))

;; (NativeKeyEvent/getModifiersText
;;  (.getModifiers @last-event))
;; (.paramString @last-event)

(GlobalScreen/addNativeKeyListener klistener)
(GlobalScreen/removeNativeKeyListener klistener)
(:key/w key->code)

;; (def membs (:members (cr/reflect @last-event)))
;; (clojure.pprint/pprint
;;  (->> membs
;;      (remove #(contains? (:flags %) :final))
;;      (map :name)))

;; (.paramString @last-event)

(try
  (GlobalScreen/registerNativeHook)
  (catch NativeHookException e
    (println (.getMessage e))))

;; (.getKeyChar @last-event)
;; (GlobalScreen/unregisterNativeHook)

