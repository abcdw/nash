(ns nash.nativehook
  (:require [clojure.reflect :as cr]
            [manifold.bus :as bus]
            [manifold.stream :as stream]
            [clojure.string :as cstr])
  (:import (org.jnativehook GlobalScreen NativeHookException)
           (java.util.logging Logger Level)
           (org.jnativehook.keyboard NativeKeyEvent NativeKeyListener)))

;; TODO: think about different layouts hotkeys

(defn init []
  (let [key-event-stream (bus/event-bus)

        klistener
        (reify NativeKeyListener
          (nativeKeyPressed [this e]
            (bus/publish! key-event-stream "pressed" (.getKeyCode e)))
          (nativeKeyReleased [this e]
            (bus/publish! key-event-stream "released" (.getKeyCode e)))
          (nativeKeyTyped [this e]))

        jn-logger
        (Logger/getLogger "org.jnativehook")]
    (try
      (. jn-logger setLevel (. Level WARNING))
      (GlobalScreen/registerNativeHook)
      (GlobalScreen/addNativeKeyListener klistener)
      (catch NativeHookException e
        (println (.getMessage e))))
    {:nash.plugin/deinit (fn [] (future (GlobalScreen/unregisterNativeHook)))
     :nash.plugin/streams {:jnativehook/key-events key-event-stream}}))

(comment
  (init)
  (def jn-logger (Logger/getLogger "org.jnativehook"))
  (. jn-logger setLevel (. Level ALL))
  (. jn-logger setLevel (. Level WARNING))
  (GlobalScreen/addNativeKeyListener klistener)
  (GlobalScreen/removeNativeKeyListener klistener)
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

  )

