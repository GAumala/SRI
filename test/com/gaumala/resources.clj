(ns com.gaumala.resources
  (:require [clojure.string :as str]))

(defn load-xml [path]
  (-> (slurp path)))
