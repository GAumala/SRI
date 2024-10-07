(ns com.gaumala.resources
  (:require [clojure.string :refer [replace]]))

(defn load-xml [path]
  (-> (slurp path)
      (replace #"\n[ \t]*" "")))
