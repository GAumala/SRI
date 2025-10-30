(ns com.gaumala.utils.spec
  (:require [clojure.spec.alpha :as s]))

(defn validate
  "Devuelve `true` si `input` se conforma al `spec`.
  De lo contrario arroja un `ExceptionInfo` con la siguiente data (`ex-data`): 

  | key      | valor |
  | ---------|-------|
  | `:type`  | `:spec-validation`
  | `:data`  | El resultado de `clojure.spec.alpha/explain-data`
  | `:explain` | El resultado de `clojure.spec.alpha/explain-str` (string con la explicación)
  | `:input` | `input`"
  {:doc/format :markdown}
  [spec input]
  (if (s/valid? spec input)
    true
    (throw (ex-info (s/explain-str spec input)
                    {:type :spec-validation
                     :data (s/explain-data spec input)
                     :explain (s/explain-str spec input)
                     :input input}))))
