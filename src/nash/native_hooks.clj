(ns nash.native-hooks
  (:import (org.jnativehook GlobalScreen NativeHookException)
           (java.util.logging Logger Level)
           (org.jnativehook.keyboard NativeKeyEvent NativeKeyListener)))


(def jn-logger (Logger/getLogger "org.jnativehook"))
(. jn-logger setLevel (. Level OFF))
(. jn-logger setLevel (. Level ALL))

(def klistener
  (reify NativeKeyListener
    (nativeKeyPressed [this e]
      (println "key pressed: " e))
    (nativeKeyReleased [this e]
      ;; (println "key released: " (NativeKeyEvent/getKeyText (.getKeyCode e)))
      )
    (nativeKeyTyped [this e]
      (println "key typed:" e)
      ;; (println "key typed: " (NativeKeyEvent/getKeyText (.getKeyCode e)))
      )))

(try
  (GlobalScreen/registerNativeHook)
  (catch NativeHookException e
    (println (.getMessage e))))

(GlobalScreen/unregisterNativeHook)
(GlobalScreen/addNativeKeyListener klistener)
(GlobalScreen/removeNativeKeyListener klistener)
