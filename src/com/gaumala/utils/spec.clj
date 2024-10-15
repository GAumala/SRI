(ns com.gaumala.utils.spec
  (:require [clojure.spec.alpha :as s]))

(defn validate [spec input]
  "Devuelve `true` si `input` se conforma al `spec`.
  De lo contrario arroja un error el resultado de `explain`"
  {:doc/format :markdown}
  (if (s/valid? spec input)
    true
    (throw (ex-info (s/explain-str spec input) {:input input}))))
